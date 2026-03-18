/*
 * Headwind MDM: Open Source Android MDM Software
 * https://h-mdm.com
 *
 * Copyright (C) 2019 Headwind Solutions LLC (http://h-sms.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hmdm.rest.resource;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.hmdm.notification.PushService;
import com.hmdm.persistence.DeviceDAO;
import com.hmdm.persistence.RemoteControlDAO;
import com.hmdm.persistence.domain.Device;
import com.hmdm.persistence.domain.RemoteControlSession;
import com.hmdm.rest.json.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Remote control API
 *
 * Functionality:
 * - Device registers remote control session
 * - Server creates remote control session and returns session info
 * - Support screen sharing, session state management
 * - Used for remote debugging and technical support
 */
@Api(tags = {"Remote Control"}, authorizations = {@Authorization("apiKey")})
@Singleton
@Path("/plugins/devicecontrol")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DeviceControlResource {

    private static final Logger log = LoggerFactory.getLogger(DeviceControlResource.class);

    private final RemoteControlDAO remoteControlDAO;
    private final DeviceDAO deviceDAO;
    private final PushService pushService;

    /**
     * Constructor - injected by Guice
     */
    @Inject
    public DeviceControlResource(RemoteControlDAO remoteControlDAO, DeviceDAO deviceDAO, PushService pushService) {
        this.remoteControlDAO = remoteControlDAO;
        this.deviceDAO = deviceDAO;
        this.pushService = pushService;
    }

    /**
     * Get device ID by device number
     */
    private Integer getDeviceIdByNumber(String number) {
        Device device = deviceDAO.getDeviceByNumber(number);
        return device != null ? device.getId() : null;
    }

    /**
     * Register remote control session
     *
     * @param number Device number
     * @return Session information
     */
    @GET
    @Path("/session/{number}")
    @ApiOperation(
            value = "Get remote control session",
            notes = "Get or create remote control session for device"
    )
    public Response getControlSession(
            @ApiParam("Device number") @PathParam("number") String number) {
        try {
            Integer deviceId = getDeviceIdByNumber(number);
            if (deviceId == null) {
                log.warn("Device not found: {}", number);
                return Response.ERROR("device.not.found", "Device not found");
            }

            List<RemoteControlSession> activeSessions = remoteControlDAO.getActiveSessionsByDevice(deviceId);
            if (!activeSessions.isEmpty()) {
                return Response.OK(activeSessions.get(0));
            }

            RemoteControlSession created = remoteControlDAO.createSession(
                    deviceId,
                    RemoteControlSession.TYPE_CONTROL
            );

            log.info("Created remote control session for device {}", number);
            return Response.OK(created);
        } catch (Exception e) {
            log.error("Failed to create session for device {}", number, e);
            return Response.ERROR("error.internal.server", "Failed to create session");
        }
    }

    /**
     * Send remote control signal
     *
     * @param number Device number
     * @param signal Control signal (contains signal type and data)
     * @return Operation result
     */
    @POST
    @Path("/signal/{number}")
    @ApiOperation(
            value = "Send remote control signal",
            notes = "Send remote control command to device (screenshot, lock, reboot, etc.)"
    )
    public Response sendControlSignal(
            @ApiParam("Device number") @PathParam("number") String number,
            @ApiParam("Control signal") com.hmdm.launcher.json.ControlSignal signal) {
        try {
            if (signal == null || signal.getType() == null || signal.getType().trim().isEmpty()) {
                return Response.ERROR("validation.error", "Signal type is required");
            }

            Integer deviceId = getDeviceIdByNumber(number);
            if (deviceId == null) {
                log.warn("Device not found: {}", number);
                return Response.ERROR("device.not.found", "Device not found");
            }

            String signalType = signal.getType().trim();
            String payload = signal.getData() != null ? signal.getData().toString() : null;

            // Extra verification for dangerous operations
            if ("factory_reset".equals(signalType)) {
                log.warn("Factory reset requested for device {} - requires additional authorization", number);
                // TODO: Add additional authorization check here
                // For example: check if user has admin role, or require confirmation token
            }

            switch (signalType) {
                case "screenshot":
                    pushService.sendSimpleMessage(deviceId, "REMOTE_SCREENSHOT");
                    log.info("Screenshot signal sent to device {}", number);
                    break;
                case "lock":
                    pushService.sendSimpleMessage(deviceId, "REMOTE_LOCK");
                    log.info("Lock signal sent to device {}", number);
                    break;
                case "reboot":
                    pushService.sendSimpleMessage(deviceId, "REMOTE_REBOOT");
                    log.info("Reboot signal sent to device {}", number);
                    break;
                case "factory_reset":
                    pushService.sendSimpleMessage(deviceId, "REMOTE_FACTORY_RESET");
                    log.warn("Factory reset signal sent to device {}", number);
                    break;
                default:
                    pushService.sendSimpleMessage(deviceId, "REMOTE_CONTROL:" + signalType);
                    log.info("Custom signal {} sent to device {}", signalType, number);
            }

            return Response.OK();
        } catch (Exception e) {
            log.error("Failed to send control signal to device {}", number, e);
            return Response.ERROR("error.internal.server", "Failed to send control signal");
        }
    }

    /**
     * Update session status
     *
     * @param number Device number
     * @param sessionId Session ID
     * @param status New status
     * @return Operation result
     */
    @PUT
    @Path("/session/{number}/{sessionId}/status")
    @ApiOperation(
            value = "Update session status",
            notes = "Update remote control session status"
    )
    public Response updateSessionStatus(
            @ApiParam("Device number") @PathParam("number") String number,
            @ApiParam("Session ID") @PathParam("sessionId") Integer sessionId,
            @ApiParam("Status") @QueryParam("status") String status) {
        try {
            if (status == null || status.trim().isEmpty()) {
                return Response.ERROR("validation.error", "Status is required");
            }

            int updated = remoteControlDAO.updateSessionStatus(sessionId, status.trim());
            if (updated > 0) {
                log.info("Session {} status updated to {}", sessionId, status);
                return Response.OK();
            }
            return Response.ERROR("not.found", "Session not found");
        } catch (Exception e) {
            log.error("Failed to update session status for session {}", sessionId, e);
            return Response.ERROR("error.internal.server", "Failed to update session status");
        }
    }

    /**
     * End remote control session
     *
     * @param number Device number
     * @param sessionId Session ID
     * @return Operation result
     */
    @DELETE
    @Path("/session/{number}/{sessionId}")
    @ApiOperation(
            value = "End remote control session",
            notes = "End remote control session for device"
    )
    public Response endSession(
            @ApiParam("Device number") @PathParam("number") String number,
            @ApiParam("Session ID") @PathParam("sessionId") Integer sessionId) {
        try {
            remoteControlDAO.markSessionDisconnected(sessionId, null);
            log.info("Session {} ended for device {}", sessionId, number);
            return Response.OK();
        } catch (Exception e) {
            log.error("Failed to end session {} for device {}", sessionId, number, e);
            return Response.ERROR("error.internal.server", "Failed to end session");
        }
    }
}