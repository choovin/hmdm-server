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
import com.hmdm.persistence.DeviceLocationDAO;
import com.hmdm.persistence.domain.Device;
import com.hmdm.persistence.domain.DeviceLocation;
import com.hmdm.rest.json.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Device location data upload API
 *
 * Functionality:
 * - Receive location information from devices (latitude, longitude, speed, altitude, etc.)
 * - Store location data to database
 * - Support batch upload of location data from devices
 */
@Api(tags = {"Device Location"}, authorizations = {@Authorization("apiKey")})
@Singleton
@Path("/plugins/devicelocations/public")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DeviceLocationResource {

    private static final Logger log = LoggerFactory.getLogger(DeviceLocationResource.class);

    private final DeviceLocationDAO deviceLocationDAO;
    private final DeviceDAO deviceDAO;

    /**
     * Constructor - injected by Guice
     */
    @Inject
    public DeviceLocationResource(DeviceLocationDAO deviceLocationDAO, DeviceDAO deviceDAO) {
        this.deviceLocationDAO = deviceLocationDAO;
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
     * Upload device location data
     *
     * @param number Device number
     * @param locations List of location data
     * @return Operation result
     */
    @PUT
    @Path("/update/{number}")
    @ApiOperation(
            value = "Update device location",
            notes = "Receive and save location information from device"
    )
    public Response updateDeviceLocation(
            @ApiParam("Device number") @PathParam("number") String number,
            @ApiParam("Location data list") List<DeviceLocation> locations) {
        try {
            if (locations == null || locations.isEmpty()) {
                return Response.OK();
            }

            Integer deviceId = getDeviceIdByNumber(number);
            if (deviceId == null) {
                log.warn("Device not found: {}", number);
                return Response.ERROR("device.not.found", "Device not found");
            }

            // Batch insert location data
            for (DeviceLocation location : locations) {
                location.setDeviceId(deviceId);
                deviceLocationDAO.insertLocation(location);
            }

            log.info("Device {} location updated, count: {}", number, locations.size());
            return Response.OK();
        } catch (Exception e) {
            log.error("Failed to save location data for device {}", number, e);
            return Response.ERROR("error.internal.server", "Failed to save location data");
        }
    }

    /**
     * Get latest location of device
     *
     * @param number Device number
     * @return Latest device location
     */
    @GET
    @Path("/latest/{number}")
    @ApiOperation(
            value = "Get device latest location",
            notes = "Get the latest location information for specified device"
    )
    public Response getLatestLocation(
            @ApiParam("Device number") @PathParam("number") String number) {
        try {
            Integer deviceId = getDeviceIdByNumber(number);
            if (deviceId == null) {
                log.warn("Device not found: {}", number);
                return Response.ERROR("device.not.found", "Device not found");
            }

            DeviceLocation location = deviceLocationDAO.getLatestDeviceLocation(deviceId);
            if (location == null) {
                return Response.OBJECT_NOT_FOUND_ERROR();
            }

            return Response.OK(location);
        } catch (Exception e) {
            log.error("Failed to get location for device {}", number, e);
            return Response.ERROR("error.internal.server", "Failed to get location data");
        }
    }
}