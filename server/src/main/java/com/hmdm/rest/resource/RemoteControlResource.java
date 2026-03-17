/*
 *
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
 *
 */

package com.hmdm.rest.resource;

import com.google.inject.Inject;
import com.hmdm.persistence.RemoteControlDAO;
import com.hmdm.persistence.domain.RemoteControlSession;
import com.hmdm.rest.json.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;


/**
 * <p>REST API resource for remote control sessions.</p>
 *
 * @author Igor Pikin (ipikin@h-mdm.com)
 */
@Api(value = "Remote Control", authorizations = {@Authorization(value = "Bearer")})
@Path("/private/remote-control")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RemoteControlResource {

    private final RemoteControlDAO remoteControlDAO;

    @Inject
    public RemoteControlResource(RemoteControlDAO remoteControlDAO) {
        this.remoteControlDAO = remoteControlDAO;
    }

    @ApiOperation(value = "Get all remote control sessions")
    @GET
    @Path("/sessions")
    public Response getAllSessions() {
        List<RemoteControlSession> sessions = remoteControlDAO.getAllSessions();
        return Response.OK(sessions);
    }

    @ApiOperation(value = "Get sessions by device ID")
    @GET
    @Path("/sessions/device/{deviceId}")
    public Response getSessionsByDevice(
            @ApiParam("Device ID") @PathParam("deviceId") Integer deviceId) {
        List<RemoteControlSession> sessions = remoteControlDAO.getSessionsByDevice(deviceId);
        return Response.OK(sessions);
    }

    @ApiOperation(value = "Get active sessions by device ID")
    @GET
    @Path("/sessions/device/{deviceId}/active")
    public Response getActiveSessionsByDevice(
            @ApiParam("Device ID") @PathParam("deviceId") Integer deviceId) {
        List<RemoteControlSession> sessions = remoteControlDAO.getActiveSessionsByDevice(deviceId);
        return Response.OK(sessions);
    }

    @ApiOperation(value = "Get session by ID")
    @GET
    @Path("/sessions/{id}")
    public Response getSessionById(
            @ApiParam("Session ID") @PathParam("id") Integer id) {
        RemoteControlSession session = remoteControlDAO.getSessionById(id);
        if (session == null) {
            return Response.ERROR("Not found", "Session not found");
        }
        return Response.OK(session);
    }

    @ApiOperation(value = "Get session by token")
    @GET
    @Path("/sessions/token/{token}")
    public Response getSessionByToken(
            @ApiParam("Session Token") @PathParam("token") String token) {
        RemoteControlSession session = remoteControlDAO.getSessionByToken(token);
        if (session == null) {
            return Response.ERROR("Not found", "Session not found");
        }
        return Response.OK(session);
    }

    @ApiOperation(value = "Create a new remote control session")
    @POST
    @Path("/sessions")
    public Response createSession(
            @ApiParam("Session data") RemoteControlSession session) {
        if (session == null || session.getDeviceId() == null) {
            return Response.ERROR("Validation error", "Device ID is required");
        }
        if (session.getSessionType() == null) {
            session.setSessionType(RemoteControlSession.TYPE_VIEW);
        }
        RemoteControlSession created = remoteControlDAO.createSession(
                session.getDeviceId(), session.getSessionType());
        return Response.OK(created);
    }

    @ApiOperation(value = "Update a session")
    @PUT
    @Path("/sessions/{id}")
    public Response updateSession(
            @ApiParam("Session ID") @PathParam("id") Integer id,
            @ApiParam("Session data") RemoteControlSession session) {
        if (session == null) {
            return Response.ERROR("Validation error", "Session data is required");
        }
        session.setId(id);
        int updated = remoteControlDAO.updateSession(session);
        if (updated > 0) {
            return Response.OK();
        }
        return Response.ERROR("Not found", "Session not found");
    }

    @ApiOperation(value = "Update session status")
    @PUT
    @Path("/sessions/{id}/status")
    public Response updateSessionStatus(
            @ApiParam("Session ID") @PathParam("id") Integer id,
            @ApiParam("Status") @QueryParam("status") String status) {
        if (status == null || status.isEmpty()) {
            return Response.ERROR("Validation error", "Status is required");
        }
        int updated = remoteControlDAO.updateSessionStatus(id, status);
        if (updated > 0) {
            return Response.OK();
        }
        return Response.ERROR("Not found", "Session not found");
    }

    @ApiOperation(value = "Update session WebRTC data")
    @PUT
    @Path("/sessions/{id}/webrtc")
    public Response updateSessionWebRTCData(
            @ApiParam("Session ID") @PathParam("id") Integer id,
            @ApiParam("WebRTC offer") @QueryParam("offer") String offer,
            @ApiParam("WebRTC answer") @QueryParam("answer") String answer,
            @ApiParam("ICE candidates") @QueryParam("candidates") String candidates) {
        int updated = remoteControlDAO.updateSessionWebRTCData(id, offer, answer, candidates);
        if (updated > 0) {
            return Response.OK();
        }
        return Response.ERROR("Not found", "Session not found");
    }

    @ApiOperation(value = "Mark session as connecting")
    @PUT
    @Path("/sessions/{id}/connecting")
    public Response markSessionConnecting(
            @ApiParam("Session ID") @PathParam("id") Integer id) {
        int updated = remoteControlDAO.updateSessionStatus(id, RemoteControlSession.STATUS_CONNECTING);
        if (updated > 0) {
            remoteControlDAO.logAudit(id, null, "SESSION_CONNECTING", null);
            return Response.OK();
        }
        return Response.ERROR("Not found", "Session not found");
    }

    @ApiOperation(value = "Mark session as connected")
    @PUT
    @Path("/sessions/{id}/connected")
    public Response markSessionConnected(
            @ApiParam("Session ID") @PathParam("id") Integer id,
            @ApiParam("Remote address") @QueryParam("remoteAddress") String remoteAddress) {
        int updated = remoteControlDAO.markSessionConnected(id, remoteAddress);
        if (updated > 0) {
            remoteControlDAO.logAudit(id, null, "SESSION_CONNECTED", "Remote address: " + remoteAddress);
            return Response.OK();
        }
        return Response.ERROR("Not found", "Session not found");
    }

    @ApiOperation(value = "Mark session as disconnected")
    @PUT
    @Path("/sessions/{id}/disconnected")
    public Response markSessionDisconnected(
            @ApiParam("Session ID") @PathParam("id") Integer id,
            @ApiParam("Duration in seconds") @QueryParam("duration") Integer durationSeconds) {
        int updated = remoteControlDAO.markSessionDisconnected(id, durationSeconds);
        if (updated > 0) {
            remoteControlDAO.logAudit(id, null, "SESSION_DISCONNECTED", "Duration: " + durationSeconds + "s");
            return Response.OK();
        }
        return Response.ERROR("Not found", "Session not found");
    }

    @ApiOperation(value = "Mark session as error")
    @PUT
    @Path("/sessions/{id}/error")
    public Response markSessionError(
            @ApiParam("Session ID") @PathParam("id") Integer id,
            @ApiParam("Error message") @QueryParam("message") String errorMessage) {
        int updated = remoteControlDAO.markSessionError(id, errorMessage);
        if (updated > 0) {
            remoteControlDAO.logAudit(id, null, "SESSION_ERROR", errorMessage);
            return Response.OK();
        }
        return Response.ERROR("Not found", "Session not found");
    }

    @ApiOperation(value = "Delete a session")
    @DELETE
    @Path("/sessions/{id}")
    public Response deleteSession(
            @ApiParam("Session ID") @PathParam("id") Integer id) {
        int deleted = remoteControlDAO.deleteSession(id);
        if (deleted > 0) {
            return Response.OK();
        }
        return Response.ERROR("Not found", "Session not found");
    }

    @ApiOperation(value = "Check if device has active sessions")
    @GET
    @Path("/sessions/device/{deviceId}/has-active")
    public Response hasActiveSessions(
            @ApiParam("Device ID") @PathParam("deviceId") Integer deviceId) {
        boolean hasActive = remoteControlDAO.hasActiveSessions(deviceId);
        return Response.OK(hasActive);
    }

    @ApiOperation(value = "Delete old sessions")
    @DELETE
    @Path("/sessions/old")
    public Response deleteOldSessions(
            @ApiParam("Days to keep") @QueryParam("days") Integer daysToKeep) {
        if (daysToKeep == null || daysToKeep < 1) {
            daysToKeep = 30;
        }
        int deleted = remoteControlDAO.deleteOldSessions(daysToKeep);
        return Response.OK(deleted);
    }

    @ApiOperation(value = "Log audit action")
    @POST
    @Path("/audit")
    public Response logAudit(
            @ApiParam("Session ID") @QueryParam("sessionId") Integer sessionId,
            @ApiParam("Device ID") @QueryParam("deviceId") Integer deviceId,
            @ApiParam("Action") @QueryParam("action") String action,
            @ApiParam("Action details") @QueryParam("details") String details) {
        if (action == null || action.isEmpty()) {
            return Response.ERROR("Validation error", "Action is required");
        }
        remoteControlDAO.logAudit(sessionId, deviceId, action, details);
        return Response.OK();
    }
}
