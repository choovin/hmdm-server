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
import com.hmdm.persistence.DeviceDAO;
import com.hmdm.persistence.NetworkTrafficDAO;
import com.hmdm.persistence.domain.Device;
import com.hmdm.persistence.domain.DeviceNetworkSettings;
import com.hmdm.persistence.domain.NetworkTrafficLog;
import com.hmdm.persistence.domain.NetworkTrafficRule;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Network filter API
 *
 * Functionality:
 * - Device gets network filter rules from server
 * - Upload network traffic logs to server
 * - Implement application-level network access control
 */
@Api(tags = {"Network Filter"}, authorizations = {@Authorization("apiKey")})
@Singleton
@Path("/plugins/networkfilter")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NetworkFilterResource {

    private static final Logger log = LoggerFactory.getLogger(NetworkFilterResource.class);

    private final NetworkTrafficDAO networkTrafficDAO;
    private final DeviceDAO deviceDAO;

    /**
     * Constructor - injected by Guice
     */
    @Inject
    public NetworkFilterResource(NetworkTrafficDAO networkTrafficDAO, DeviceDAO deviceDAO) {
        this.networkTrafficDAO = networkTrafficDAO;
        this.deviceDAO = deviceDAO;
    }

    /**
     * Get device ID by device number
     */
    private Integer getDeviceIdByNumber(String number) {
        Device device = deviceDAO.getDeviceByNumber(number);
        return device != null ? device.getId() : null;
    }

    /**
     * Get device network filter rules
     *
     * @param number Device number
     * @return Network filter rule list
     */
    @GET
    @Path("/rules/{number}")
    @ApiOperation(
            value = "Get network filter rules",
            notes = "Get network filter rules for specified device"
    )
    public Response getNetworkRules(
            @ApiParam("Device number") @PathParam("number") String number) {
        try {
            Integer deviceId = getDeviceIdByNumber(number);
            if (deviceId == null) {
                log.warn("Device not found: {}", number);
                return Response.ERROR("device.not.found", "Device not found");
            }

            Device device = deviceDAO.getDeviceByNumber(number);
            Integer configId = device != null ? device.getConfigurationId() : null;
            if (configId == null) {
                return Response.OK(new java.util.ArrayList<>());
            }

            List<NetworkTrafficRule> rules = networkTrafficDAO.getEnabledTrafficRulesByConfiguration(configId);
            log.info("Retrieved {} rules for device {}", rules.size(), number);
            return Response.OK(rules);
        } catch (Exception e) {
            log.error("Failed to get network rules for device {}", number, e);
            return Response.ERROR("error.internal.server", "Failed to get network rules");
        }
    }

    /**
     * Upload network traffic logs
     *
     * @param number Device number
     * @param logs Traffic log list
     * @return Operation result
     */
    @POST
    @Path("/logs/{number}")
    @ApiOperation(
            value = "Upload network traffic logs",
            notes = "Receive and save network traffic logs from device"
    )
    public Response uploadTrafficLogs(
            @ApiParam("Device number") @PathParam("number") String number,
            @ApiParam("Traffic log list") List<NetworkTrafficLog> logs) {
        try {
            if (logs == null || logs.isEmpty()) {
                return Response.OK();
            }

            Integer deviceId = getDeviceIdByNumber(number);
            if (deviceId == null) {
                log.warn("Device not found: {}", number);
                return Response.ERROR("device.not.found", "Device not found");
            }

            // Limit batch size to prevent abuse
            if (logs.size() > 1000) {
                log.warn("Traffic log batch too large for device {}: {} logs", number, logs.size());
                return Response.ERROR("validation.error", "Too many logs in one request (max 1000)");
            }

            int savedCount = 0;
            for (NetworkTrafficLog trafficLog : logs) {
                trafficLog.setDeviceId(deviceId);
                if (trafficLog.getTimestamp() == null) {
                    trafficLog.setTimestamp(new Date());
                }
                networkTrafficDAO.logTraffic(trafficLog);
                savedCount++;
            }

            log.info("Saved {} traffic logs for device {}", savedCount, number);
            return Response.OK(savedCount + " logs saved");
        } catch (Exception e) {
            log.error("Failed to upload traffic logs for device {}", number, e);
            return Response.ERROR("error.internal.server", "Failed to upload traffic logs");
        }
    }

    /**
     * Get device network settings
     *
     * @param number Device number
     * @return Device network settings
     */
    @GET
    @Path("/settings/{number}")
    @ApiOperation(
            value = "Get device network settings",
            notes = "Get network filter settings for device"
    )
    public Response getNetworkSettings(
            @ApiParam("Device number") @PathParam("number") String number) {
        try {
            Integer deviceId = getDeviceIdByNumber(number);
            if (deviceId == null) {
                log.warn("Device not found: {}", number);
                return Response.ERROR("device.not.found", "Device not found");
            }

            DeviceNetworkSettings settings = networkTrafficDAO.getOrCreateDeviceNetworkSettings(deviceId);
            return Response.OK(settings);
        } catch (Exception e) {
            log.error("Failed to get network settings for device {}", number, e);
            return Response.ERROR("error.internal.server", "Failed to get network settings");
        }
    }

    /**
     * Update device network settings
     *
     * @param number Device number
     * @param settings Network settings
     * @return Operation result
     */
    @PUT
    @Path("/settings/{number}")
    @ApiOperation(
            value = "Update device network settings",
            notes = "Update network filter settings for device"
    )
    public Response updateNetworkSettings(
            @ApiParam("Device number") @PathParam("number") String number,
            @ApiParam("Network settings") DeviceNetworkSettings settings) {
        try {
            if (settings == null) {
                return Response.ERROR("validation.error", "Settings cannot be null");
            }

            Integer deviceId = getDeviceIdByNumber(number);
            if (deviceId == null) {
                log.warn("Device not found: {}", number);
                return Response.ERROR("device.not.found", "Device not found");
            }

            settings.setDeviceId(deviceId);

            DeviceNetworkSettings existing = networkTrafficDAO.getDeviceNetworkSettings(deviceId);

            if (existing == null) {
                networkTrafficDAO.createDeviceNetworkSettings(settings);
            } else {
                networkTrafficDAO.updateDeviceNetworkSettings(settings);
            }

            log.info("Updated network settings for device {}", number);
            return Response.OK();
        } catch (Exception e) {
            log.error("Failed to update network settings for device {}", number, e);
            return Response.ERROR("error.internal.server", "Failed to update network settings");
        }
    }

    /**
     * Get device traffic statistics
     *
     * @param number Device number
     * @return Traffic statistics data
     */
    @GET
    @Path("/stats/{number}")
    @ApiOperation(
            value = "Get device traffic statistics",
            notes = "Get network traffic statistics for device"
    )
    public Response getTrafficStats(
            @ApiParam("Device number") @PathParam("number") String number) {
        try {
            Integer deviceId = getDeviceIdByNumber(number);
            if (deviceId == null) {
                log.warn("Device not found: {}", number);
                return Response.ERROR("device.not.found", "Device not found");
            }

            Long blockedCount = networkTrafficDAO.getBlockedTrafficCountByDevice(deviceId);
            Long totalCount = networkTrafficDAO.getTotalTrafficCountByDevice(deviceId);
            Long blockedBytes = networkTrafficDAO.getBlockedTrafficBytesByDevice(deviceId);

            Map<String, Object> stats = new HashMap<>();
            stats.put("blockedCount", blockedCount != null ? blockedCount : 0L);
            stats.put("totalCount", totalCount != null ? totalCount : 0L);
            stats.put("blockedBytes", blockedBytes != null ? blockedBytes : 0L);

            return Response.OK(stats);
        } catch (Exception e) {
            log.error("Failed to get traffic stats for device {}", number, e);
            return Response.ERROR("error.internal.server", "Failed to get traffic statistics");
        }
    }
}