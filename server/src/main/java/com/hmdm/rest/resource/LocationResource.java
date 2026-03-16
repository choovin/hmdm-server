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

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.hmdm.persistence.DeviceLocationDAO;
import com.hmdm.persistence.domain.DeviceLocation;
import com.hmdm.persistence.domain.Geofence;
import com.hmdm.rest.json.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>REST API resource for device location tracking and geofencing.</p>
 *
 * @author Headwind Solutions LLC
 */
@Api(tags = {"Location"}, authorizations = {@Authorization("Bearer Token")})
@Singleton
@Path("/private/locations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LocationResource {

    private static final Logger log = LoggerFactory.getLogger(LocationResource.class);

    private final DeviceLocationDAO deviceLocationDAO;

    /**
     * <p>A constructor required by Swagger.</p>
     */
    public LocationResource() {
        this.deviceLocationDAO = null;
    }

    @Inject
    public LocationResource(DeviceLocationDAO deviceLocationDAO) {
        this.deviceLocationDAO = deviceLocationDAO;
    }

    // =================================================================================================================
    // Device Location APIs
    // =================================================================================================================

    @ApiOperation(
            value = "Get device location history",
            notes = "Get the location history for a specific device with optional time filtering",
            response = DeviceLocation.class,
            responseContainer = "List"
    )
    @GET
    @Path("/device/{deviceId}/history")
    public Response getDeviceLocationHistory(
            @ApiParam("Device ID") @PathParam("deviceId") Integer deviceId,
            @ApiParam("Start time (Unix timestamp)") @QueryParam("from") Long fromTime,
            @ApiParam("End time (Unix timestamp)") @QueryParam("to") Long toTime,
            @ApiParam("Maximum records to return") @QueryParam("limit") Integer limit) {
        try {
            List<DeviceLocation> locations = deviceLocationDAO.getDeviceLocationHistory(deviceId, fromTime, toTime, limit);
            return Response.OK(locations);
        } catch (Exception e) {
            log.error("Error getting device location history: {}", e.getMessage(), e);
            return Response.ERROR("error.internal.server");
        }
    }

    @ApiOperation(
            value = "Get latest device location",
            notes = "Get the most recent location for a specific device",
            response = DeviceLocation.class
    )
    @GET
    @Path("/device/{deviceId}/latest")
    public Response getLatestDeviceLocation(
            @ApiParam("Device ID") @PathParam("deviceId") Integer deviceId) {
        try {
            DeviceLocation location = deviceLocationDAO.getLatestDeviceLocation(deviceId);
            if (location == null) {
                return Response.OBJECT_NOT_FOUND_ERROR();
            }
            return Response.OK(location);
        } catch (Exception e) {
            log.error("Error getting latest device location: {}", e.getMessage(), e);
            return Response.ERROR("error.internal.server");
        }
    }

    @ApiOperation(
            value = "Get all latest locations",
            notes = "Get the most recent location for all devices of the current customer",
            response = DeviceLocation.class,
            responseContainer = "List"
    )
    @GET
    @Path("/latest")
    public Response getAllLatestLocations() {
        try {
            List<DeviceLocation> locations = deviceLocationDAO.getAllLatestLocations();
            return Response.OK(locations);
        } catch (Exception e) {
            log.error("Error getting all latest locations: {}", e.getMessage(), e);
            return Response.ERROR("error.internal.server");
        }
    }

    // =================================================================================================================
    // Geofence APIs
    // =================================================================================================================

    @ApiOperation(
            value = "Get all geofences",
            notes = "Get all geofences for the current customer",
            response = Geofence.class,
            responseContainer = "List"
    )
    @GET
    @Path("/geofences")
    public Response getAllGeofences(
            @ApiParam("Return only active geofences") @QueryParam("activeOnly") Boolean activeOnly) {
        try {
            List<Geofence> geofences = deviceLocationDAO.getAllGeofences(activeOnly);
            return Response.OK(geofences);
        } catch (Exception e) {
            log.error("Error getting geofences: {}", e.getMessage(), e);
            return Response.ERROR("error.internal.server");
        }
    }

    @ApiOperation(
            value = "Get geofence by ID",
            notes = "Get a specific geofence by its ID",
            response = Geofence.class
    )
    @GET
    @Path("/geofences/{id}")
    public Response getGeofenceById(
            @ApiParam("Geofence ID") @PathParam("id") Integer id) {
        try {
            Geofence geofence = deviceLocationDAO.getGeofenceById(id);
            if (geofence == null) {
                return Response.OBJECT_NOT_FOUND_ERROR();
            }
            return Response.OK(geofence);
        } catch (Exception e) {
            log.error("Error getting geofence: {}", e.getMessage(), e);
            return Response.ERROR("error.internal.server");
        }
    }

    @ApiOperation(
            value = "Create geofence",
            notes = "Create a new geofence",
            response = Geofence.class
    )
    @POST
    @Path("/geofences")
    public Response createGeofence(Geofence geofence) {
        try {
            if (geofence.getName() == null || geofence.getName().trim().isEmpty()) {
                return Response.ERROR("error.geofence.name.required");
            }
            if (geofence.getLatitude() == null || geofence.getLongitude() == null) {
                return Response.ERROR("error.geofence.coordinates.required");
            }
            if (geofence.getRadius() == null || geofence.getRadius() <= 0) {
                return Response.ERROR("error.geofence.radius.invalid");
            }

            Geofence created = deviceLocationDAO.insertGeofence(geofence);
            return Response.OK(created);
        } catch (Exception e) {
            log.error("Error creating geofence: {}", e.getMessage(), e);
            return Response.ERROR("error.internal.server");
        }
    }

    @ApiOperation(
            value = "Update geofence",
            notes = "Update an existing geofence",
            response = Response.class
    )
    @PUT
    @Path("/geofences/{id}")
    public Response updateGeofence(
            @ApiParam("Geofence ID") @PathParam("id") Integer id,
            Geofence geofence) {
        try {
            geofence.setId(id);
            int updated = deviceLocationDAO.updateGeofence(geofence);
            if (updated == 0) {
                return Response.OBJECT_NOT_FOUND_ERROR();
            }
            return Response.OK();
        } catch (Exception e) {
            log.error("Error updating geofence: {}", e.getMessage(), e);
            return Response.ERROR("error.internal.server");
        }
    }

    @ApiOperation(
            value = "Delete geofence",
            notes = "Delete a geofence by ID",
            response = Response.class
    )
    @DELETE
    @Path("/geofences/{id}")
    public Response deleteGeofence(
            @ApiParam("Geofence ID") @PathParam("id") Integer id) {
        try {
            int deleted = deviceLocationDAO.deleteGeofence(id);
            if (deleted == 0) {
                return Response.OBJECT_NOT_FOUND_ERROR();
            }
            return Response.OK();
        } catch (Exception e) {
            log.error("Error deleting geofence: {}", e.getMessage(), e);
            return Response.ERROR("error.internal.server");
        }
    }

    @ApiOperation(
            value = "Assign devices to geofence",
            notes = "Assign a list of devices to a geofence",
            response = Response.class
    )
    @POST
    @Path("/geofences/{id}/devices")
    public Response assignDevicesToGeofence(
            @ApiParam("Geofence ID") @PathParam("id") Integer geofenceId,
            List<Integer> deviceIds) {
        try {
            deviceLocationDAO.assignDevicesToGeofence(geofenceId, deviceIds);
            return Response.OK();
        } catch (Exception e) {
            log.error("Error assigning devices to geofence: {}", e.getMessage(), e);
            return Response.ERROR("error.internal.server");
        }
    }

    @ApiOperation(
            value = "Get geofence device assignments",
            notes = "Get the list of device IDs assigned to a geofence",
            response = Integer.class,
            responseContainer = "List"
    )
    @GET
    @Path("/geofences/{id}/devices")
    public Response getGeofenceDeviceIds(
            @ApiParam("Geofence ID") @PathParam("id") Integer geofenceId) {
        try {
            List<Integer> deviceIds = deviceLocationDAO.getGeofenceDeviceIds(geofenceId);
            return Response.OK(deviceIds);
        } catch (Exception e) {
            log.error("Error getting geofence device assignments: {}", e.getMessage(), e);
            return Response.ERROR("error.internal.server");
        }
    }
}
