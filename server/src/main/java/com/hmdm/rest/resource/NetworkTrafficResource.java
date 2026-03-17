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
import com.hmdm.persistence.NetworkTrafficDAO;
import com.hmdm.persistence.domain.DeviceNetworkSettings;
import com.hmdm.persistence.domain.NetworkTrafficLog;
import com.hmdm.persistence.domain.NetworkTrafficRule;
import com.hmdm.rest.json.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.List;

/**
 * <p>REST API resource for network traffic filtering.</p>
 *
 * @author Igor Pikin (ipikin@h-mdm.com)
 */
@Api(value = "Network Traffic", authorizations = {@Authorization(value = "Bearer")})
@Path("/private/network-traffic")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NetworkTrafficResource {

    private final NetworkTrafficDAO networkTrafficDAO;

    @Inject
    public NetworkTrafficResource(NetworkTrafficDAO networkTrafficDAO) {
        this.networkTrafficDAO = networkTrafficDAO;
    }

    // ==================== Traffic Rules ====================

    @ApiOperation(value = "Get all traffic rules for current customer")
    @GET
    @Path("/rules")
    public Response getAllTrafficRules() {
        List<NetworkTrafficRule> rules = networkTrafficDAO.getTrafficRulesByCustomer();
        return Response.OK(rules);
    }

    @ApiOperation(value = "Get traffic rules by configuration ID")
    @GET
    @Path("/rules/configuration/{configId}")
    public Response getTrafficRulesByConfiguration(
            @ApiParam("Configuration ID") @PathParam("configId") Integer configId) {
        List<NetworkTrafficRule> rules = networkTrafficDAO.getTrafficRulesByConfiguration(configId);
        return Response.OK(rules);
    }

    @ApiOperation(value = "Get enabled traffic rules by configuration ID")
    @GET
    @Path("/rules/configuration/{configId}/enabled")
    public Response getEnabledTrafficRulesByConfiguration(
            @ApiParam("Configuration ID") @PathParam("configId") Integer configId) {
        List<NetworkTrafficRule> rules = networkTrafficDAO.getEnabledTrafficRulesByConfiguration(configId);
        return Response.OK(rules);
    }

    @ApiOperation(value = "Get traffic rule by ID")
    @GET
    @Path("/rules/{id}")
    public Response getTrafficRuleById(
            @ApiParam("Rule ID") @PathParam("id") Integer id) {
        NetworkTrafficRule rule = networkTrafficDAO.getTrafficRuleById(id);
        if (rule == null) {
            return Response.ERROR("Not found", "Traffic rule not found");
        }
        return Response.OK(rule);
    }

    @ApiOperation(value = "Create a new traffic rule")
    @POST
    @Path("/rules")
    public Response createTrafficRule(
            @ApiParam("Traffic rule data") NetworkTrafficRule rule) {
        if (rule == null || rule.getRuleName() == null || rule.getRuleName().isEmpty()) {
            return Response.ERROR("Validation error", "Rule name is required");
        }
        if (rule.getRuleType() == null || rule.getRuleType().isEmpty()) {
            return Response.ERROR("Validation error", "Rule type is required");
        }
        if (rule.getTrafficType() == null || rule.getTrafficType().isEmpty()) {
            return Response.ERROR("Validation error", "Traffic type is required");
        }
        if (rule.getPattern() == null || rule.getPattern().isEmpty()) {
            return Response.ERROR("Validation error", "Pattern is required");
        }
        NetworkTrafficRule created = networkTrafficDAO.createTrafficRule(rule);
        return Response.OK(created);
    }

    @ApiOperation(value = "Update a traffic rule")
    @PUT
    @Path("/rules/{id}")
    public Response updateTrafficRule(
            @ApiParam("Rule ID") @PathParam("id") Integer id,
            @ApiParam("Traffic rule data") NetworkTrafficRule rule) {
        if (rule == null) {
            return Response.ERROR("Validation error", "Rule data is required");
        }
        rule.setId(id);
        int updated = networkTrafficDAO.updateTrafficRule(rule);
        if (updated > 0) {
            return Response.OK();
        }
        return Response.ERROR("Not found", "Traffic rule not found");
    }

    @ApiOperation(value = "Update traffic rule enabled status")
    @PUT
    @Path("/rules/{id}/enabled")
    public Response updateTrafficRuleEnabled(
            @ApiParam("Rule ID") @PathParam("id") Integer id,
            @ApiParam("Enabled status") @QueryParam("enabled") Boolean enabled) {
        if (enabled == null) {
            return Response.ERROR("Validation error", "Enabled status is required");
        }
        int updated = networkTrafficDAO.updateTrafficRuleEnabled(id, enabled);
        if (updated > 0) {
            return Response.OK();
        }
        return Response.ERROR("Not found", "Traffic rule not found");
    }

    @ApiOperation(value = "Delete a traffic rule")
    @DELETE
    @Path("/rules/{id}")
    public Response deleteTrafficRule(
            @ApiParam("Rule ID") @PathParam("id") Integer id) {
        int deleted = networkTrafficDAO.deleteTrafficRule(id);
        if (deleted > 0) {
            return Response.OK();
        }
        return Response.ERROR("Not found", "Traffic rule not found");
    }

    // ==================== Traffic Logs ====================

    @ApiOperation(value = "Get traffic logs by device ID")
    @GET
    @Path("/logs/device/{deviceId}")
    public Response getTrafficLogsByDevice(
            @ApiParam("Device ID") @PathParam("deviceId") Integer deviceId) {
        List<NetworkTrafficLog> logs = networkTrafficDAO.getTrafficLogsByDevice(deviceId);
        return Response.OK(logs);
    }

    @ApiOperation(value = "Get all traffic logs for current customer")
    @GET
    @Path("/logs")
    public Response getAllTrafficLogs() {
        List<NetworkTrafficLog> logs = networkTrafficDAO.getTrafficLogsByCustomer();
        return Response.OK(logs);
    }

    @ApiOperation(value = "Get traffic logs with filter")
    @GET
    @Path("/logs/filter")
    public Response getTrafficLogsByFilter(
            @ApiParam("Device ID") @QueryParam("deviceId") Integer deviceId,
            @ApiParam("Action (ALLOWED/BLOCKED)") @QueryParam("action") String action,
            @ApiParam("Domain filter") @QueryParam("domain") String domain,
            @ApiParam("Start date") @QueryParam("startDate") Long startDateTimestamp,
            @ApiParam("End date") @QueryParam("endDate") Long endDateTimestamp) {
        Date startDate = startDateTimestamp != null ? new Date(startDateTimestamp) : null;
        Date endDate = endDateTimestamp != null ? new Date(endDateTimestamp) : null;
        List<NetworkTrafficLog> logs = networkTrafficDAO.getTrafficLogsByFilter(deviceId, action, domain, startDate, endDate);
        return Response.OK(logs);
    }

    @ApiOperation(value = "Log a traffic event")
    @POST
    @Path("/logs")
    public Response logTrafficEvent(
            @ApiParam("Traffic log data") NetworkTrafficLog log) {
        if (log == null || log.getDeviceId() == null) {
            return Response.ERROR("Validation error", "Device ID is required");
        }
        if (log.getAction() == null || log.getAction().isEmpty()) {
            return Response.ERROR("Validation error", "Action is required");
        }
        NetworkTrafficLog created = networkTrafficDAO.logTraffic(log);
        return Response.OK(created);
    }

    // ==================== Statistics ====================

    @ApiOperation(value = "Get traffic statistics for a device")
    @GET
    @Path("/stats/device/{deviceId}")
    public Response getDeviceTrafficStats(
            @ApiParam("Device ID") @PathParam("deviceId") Integer deviceId) {
        Long blockedCount = networkTrafficDAO.getBlockedTrafficCountByDevice(deviceId);
        Long totalCount = networkTrafficDAO.getTotalTrafficCountByDevice(deviceId);
        Long blockedBytes = networkTrafficDAO.getBlockedTrafficBytesByDevice(deviceId);

        TrafficStats stats = new TrafficStats();
        stats.blockedCount = blockedCount != null ? blockedCount : 0L;
        stats.totalCount = totalCount != null ? totalCount : 0L;
        stats.blockedBytes = blockedBytes != null ? blockedBytes : 0L;

        return Response.OK(stats);
    }

    // ==================== Device Network Settings ====================

    @ApiOperation(value = "Get device network settings")
    @GET
    @Path("/settings/device/{deviceId}")
    public Response getDeviceNetworkSettings(
            @ApiParam("Device ID") @PathParam("deviceId") Integer deviceId) {
        DeviceNetworkSettings settings = networkTrafficDAO.getOrCreateDeviceNetworkSettings(deviceId);
        return Response.OK(settings);
    }

    @ApiOperation(value = "Update device network settings")
    @PUT
    @Path("/settings/device/{deviceId}")
    public Response updateDeviceNetworkSettings(
            @ApiParam("Device ID") @PathParam("deviceId") Integer deviceId,
            @ApiParam("Network settings") DeviceNetworkSettings settings) {
        if (settings == null) {
            return Response.ERROR("Validation error", "Settings are required");
        }
        settings.setDeviceId(deviceId);

        // Check if settings exist
        DeviceNetworkSettings existing = networkTrafficDAO.getDeviceNetworkSettings(deviceId);
        if (existing == null) {
            DeviceNetworkSettings created = networkTrafficDAO.createDeviceNetworkSettings(settings);
            return Response.OK(created);
        } else {
            int updated = networkTrafficDAO.updateDeviceNetworkSettings(settings);
            if (updated > 0) {
                return Response.OK();
            }
            return Response.ERROR("Unknown error", "Failed to update settings");
        }
    }

    @ApiOperation(value = "Delete old traffic data")
    @DELETE
    @Path("/cleanup")
    public Response cleanupOldData(
            @ApiParam("Days to keep logs") @QueryParam("logDays") Integer logDays,
            @ApiParam("Days to keep rules") @QueryParam("ruleDays") Integer ruleDays) {
        int logsDeleted = 0;
        int rulesDeleted = 0;

        if (logDays != null && logDays > 0) {
            logsDeleted = networkTrafficDAO.deleteOldTrafficLogs(logDays);
        }
        if (ruleDays != null && ruleDays > 0) {
            rulesDeleted = networkTrafficDAO.deleteOldTrafficRules(ruleDays);
        }

        CleanupResult result = new CleanupResult();
        result.logsDeleted = logsDeleted;
        result.rulesDeleted = rulesDeleted;

        return Response.OK(result);
    }

    // Helper classes for responses
    public static class TrafficStats {
        public Long blockedCount;
        public Long totalCount;
        public Long blockedBytes;
    }

    public static class CleanupResult {
        public int logsDeleted;
        public int rulesDeleted;
    }
}
