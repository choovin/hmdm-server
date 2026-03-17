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
import com.hmdm.persistence.LDAPDAO;
import com.hmdm.persistence.domain.LDAPConfiguration;
import com.hmdm.persistence.domain.LDAPGroupMapping;
import com.hmdm.persistence.domain.LDAPSyncLog;
import com.hmdm.rest.json.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * <p>REST API resource for LDAP integration.</p>
 *
 * @author Igor Pikin (ipikin@h-mdm.com)
 */
@Api(value = "LDAP", authorizations = {@Authorization(value = "Bearer")})
@Path("/private/ldap")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LDAPResource {

    private final LDAPDAO ldapDAO;

    @Inject
    public LDAPResource(LDAPDAO ldapDAO) {
        this.ldapDAO = ldapDAO;
    }

    // ==================== LDAP Configuration ====================

    @ApiOperation(value = "Get LDAP configuration for current customer")
    @GET
    @Path("/config")
    public Response getLDAPConfiguration() {
        LDAPConfiguration config = ldapDAO.getOrCreateLDAPConfiguration();
        return Response.OK(config);
    }

    @ApiOperation(value = "Update LDAP configuration")
    @PUT
    @Path("/config")
    public Response updateLDAPConfiguration(
            @ApiParam("LDAP configuration") LDAPConfiguration config) {
        if (config == null) {
            return Response.ERROR("Validation error", "Configuration is required");
        }
        LDAPConfiguration existing = ldapDAO.getLDAPConfigurationByCustomer();
        if (existing == null) {
            LDAPConfiguration created = ldapDAO.createLDAPConfiguration(config);
            return Response.OK(created);
        } else {
            config.setId(existing.getId());
            int updated = ldapDAO.updateLDAPConfiguration(config);
            if (updated > 0) {
                return Response.OK();
            }
            return Response.ERROR("Unknown error", "Failed to update configuration");
        }
    }

    @ApiOperation(value = "Delete LDAP configuration")
    @DELETE
    @Path("/config/{id}")
    public Response deleteLDAPConfiguration(
            @ApiParam("Configuration ID") @PathParam("id") Integer id) {
        int deleted = ldapDAO.deleteLDAPConfiguration(id);
        if (deleted > 0) {
            return Response.OK();
        }
        return Response.ERROR("Not found", "Configuration not found");
    }

    // ==================== LDAP Group Mappings ====================

    @ApiOperation(value = "Get LDAP group mappings")
    @GET
    @Path("/mappings")
    public Response getGroupMappings(
            @ApiParam("LDAP Config ID") @QueryParam("configId") Integer configId) {
        if (configId == null) {
            return Response.ERROR("Validation error", "Config ID is required");
        }
        List<LDAPGroupMapping> mappings = ldapDAO.getGroupMappingsByConfigId(configId);
        return Response.OK(mappings);
    }

    @ApiOperation(value = "Create LDAP group mapping")
    @POST
    @Path("/mappings")
    public Response createGroupMapping(
            @ApiParam("Group mapping") LDAPGroupMapping mapping) {
        if (mapping == null || mapping.getLdapGroupDn() == null || mapping.getLdapGroupDn().isEmpty()) {
            return Response.ERROR("Validation error", "LDAP group DN is required");
        }
        if (mapping.getLocalRole() == null || mapping.getLocalRole().isEmpty()) {
            return Response.ERROR("Validation error", "Local role is required");
        }
        LDAPGroupMapping created = ldapDAO.createGroupMapping(mapping);
        return Response.OK(created);
    }

    @ApiOperation(value = "Update LDAP group mapping")
    @PUT
    @Path("/mappings/{id}")
    public Response updateGroupMapping(
            @ApiParam("Mapping ID") @PathParam("id") Integer id,
            @ApiParam("Group mapping") LDAPGroupMapping mapping) {
        if (mapping == null) {
            return Response.ERROR("Validation error", "Mapping is required");
        }
        mapping.setId(id);
        int updated = ldapDAO.updateGroupMapping(mapping);
        if (updated > 0) {
            return Response.OK();
        }
        return Response.ERROR("Not found", "Mapping not found");
    }

    @ApiOperation(value = "Delete LDAP group mapping")
    @DELETE
    @Path("/mappings/{id}")
    public Response deleteGroupMapping(
            @ApiParam("Mapping ID") @PathParam("id") Integer id) {
        int deleted = ldapDAO.deleteGroupMapping(id);
        if (deleted > 0) {
            return Response.OK();
        }
        return Response.ERROR("Not found", "Mapping not found");
    }

    // ==================== LDAP Sync Logs ====================

    @ApiOperation(value = "Get LDAP sync logs")
    @GET
    @Path("/sync-logs")
    public Response getSyncLogs() {
        List<LDAPSyncLog> logs = ldapDAO.getSyncLogsByCustomer();
        return Response.OK(logs);
    }

    @ApiOperation(value = "Get recent LDAP sync logs")
    @GET
    @Path("/sync-logs/recent")
    public Response getRecentSyncLogs(
            @ApiParam("Limit") @QueryParam("limit") Integer limit) {
        List<LDAPSyncLog> logs = ldapDAO.getRecentSyncLogs(limit);
        return Response.OK(logs);
    }

    @ApiOperation(value = "Create LDAP sync log")
    @POST
    @Path("/sync-logs")
    public Response createSyncLog(
            @ApiParam("Sync log") LDAPSyncLog log) {
        if (log == null || log.getSyncType() == null) {
            return Response.ERROR("Validation error", "Sync type is required");
        }
        LDAPSyncLog created = ldapDAO.createSyncLog(log);
        return Response.OK(created);
    }

    @ApiOperation(value = "Update LDAP sync log")
    @PUT
    @Path("/sync-logs/{id}")
    public Response updateSyncLog(
            @ApiParam("Log ID") @PathParam("id") Integer id,
            @ApiParam("Sync log") LDAPSyncLog log) {
        if (log == null) {
            return Response.ERROR("Validation error", "Sync log is required");
        }
        log.setId(id);
        int updated = ldapDAO.updateSyncLog(log);
        if (updated > 0) {
            return Response.OK();
        }
        return Response.ERROR("Not found", "Sync log not found");
    }

    // ==================== LDAP Test Connection ====================

    @ApiOperation(value = "Test LDAP connection")
    @POST
    @Path("/test-connection")
    public Response testConnection(
            @ApiParam("LDAP configuration") LDAPConfiguration config) {
        if (config == null || config.getServerHost() == null || config.getServerHost().isEmpty()) {
            return Response.ERROR("Validation error", "Server host is required");
        }
        if (config.getBaseDn() == null || config.getBaseDn().isEmpty()) {
            return Response.ERROR("Validation error", "Base DN is required");
        }

        // This is a simplified test - in production, this would actually try to connect to LDAP
        // For now, just validate the configuration
        if (!config.isValid()) {
            return Response.ERROR("Validation error", "Invalid configuration");
        }

        return Response.OK("Connection test passed (simulated)");
    }
}
