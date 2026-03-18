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
import com.hmdm.persistence.DevicePhotoDAO;
import com.hmdm.persistence.domain.Device;
import com.hmdm.persistence.domain.DevicePhoto;
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
import java.util.regex.Pattern;

/**
 * Device photo upload API
 *
 * Functionality:
 * - Receive photo files uploaded from devices
 * - Store photos to server filesystem
 * - Record photo metadata to database
 * - Support batch upload and single upload
 */
@Api(tags = {"Device Photo"}, authorizations = {@Authorization("apiKey")})
@Singleton
@Path("/plugins/devicephoto/public")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DevicePhotoUploadResource {

    private static final Logger log = LoggerFactory.getLogger(DevicePhotoUploadResource.class);

    // Whitelist for allowed file extensions
    private static final Pattern ALLOWED_EXTENSIONS = Pattern.compile(
            "\\.(jpg|jpeg|png|gif|bmp|webp)$",
            Pattern.CASE_INSENSITIVE
    );

    // Maximum file size: 50MB
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024;

    private final DevicePhotoDAO devicePhotoDAO;
    private final DeviceDAO deviceDAO;

    /**
     * Constructor - injected by Guice
     */
    @Inject
    public DevicePhotoUploadResource(DevicePhotoDAO devicePhotoDAO, DeviceDAO deviceDAO) {
        this.devicePhotoDAO = devicePhotoDAO;
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
     * Upload device photo
     *
     * @param number Device number
     * @param fileName File name
     * @param photoData Photo data (Base64 encoded or raw bytes)
     * @return Operation result
     */
    @POST
    @Path("/upload/{number}")
    @ApiOperation(
            value = "Upload device photo",
            notes = "Receive and save photo uploaded from device"
    )
    public Response uploadPhoto(
            @ApiParam("Device number") @PathParam("number") String number,
            @ApiParam("File name") @QueryParam("filename") String fileName,
            @ApiParam("Photo data") String photoData) {
        try {
            // Validate filename
            if (fileName == null || fileName.trim().isEmpty()) {
                return Response.ERROR("validation.error", "Filename is required");
            }

            // Validate file extension
            if (!ALLOWED_EXTENSIONS.matcher(fileName).find()) {
                log.warn("Invalid file extension for device {}: {}", number, fileName);
                return Response.ERROR("validation.error", "Invalid file type. Allowed: jpg, jpeg, png, gif, bmp, webp");
            }

            // Validate file size if photoData is provided
            if (photoData != null && photoData.length() > MAX_FILE_SIZE) {
                log.warn("File too large for device {}: {} bytes", number, photoData.length());
                return Response.ERROR("validation.error", "File too large. Maximum size: 50MB");
            }

            Integer deviceId = getDeviceIdByNumber(number);
            if (deviceId == null) {
                log.warn("Device not found: {}", number);
                return Response.ERROR("device.not.found", "Device not found");
            }

            DevicePhoto photo = new DevicePhoto();
            photo.setDeviceId(deviceId);
            photo.setFileName(fileName.trim());
            photo.setUploadTime(new Date());
            photo.setStatus(DevicePhoto.STATUS_UPLOADED);

            // TODO: Save photo data to filesystem with UUID filename
            // For now, just save metadata to database

            devicePhotoDAO.insertDevicePhoto(photo);

            log.info("Photo uploaded for device {}: {}", number, fileName);
            return Response.OK(photo);
        } catch (Exception e) {
            log.error("Failed to upload photo for device {}", number, e);
            return Response.ERROR("error.internal.server", "Failed to upload photo");
        }
    }

    /**
     * Get device photo list
     *
     * @param number Device number
     * @return Photo list
     */
    @GET
    @Path("/list/{number}")
    @ApiOperation(
            value = "Get device photo list",
            notes = "Get list of photos uploaded by specified device"
    )
    public Response getPhotoList(
            @ApiParam("Device number") @PathParam("number") String number) {
        try {
            Integer deviceId = getDeviceIdByNumber(number);
            if (deviceId == null) {
                log.warn("Device not found: {}", number);
                return Response.ERROR("device.not.found", "Device not found");
            }

            List<DevicePhoto> photos = devicePhotoDAO.getDevicePhotosByDeviceId(deviceId);
            log.info("Retrieved {} photos for device {}", photos.size(), number);
            return Response.OK(photos);
        } catch (Exception e) {
            log.error("Failed to get photo list for device {}", number, e);
            return Response.ERROR("error.internal.server", "Failed to get photo list");
        }
    }

    /**
     * Delete device photo
     *
     * @param number Device number
     * @param photoId Photo ID
     * @return Operation result
     */
    @DELETE
    @Path("/{number}/{photoId}")
    @ApiOperation(
            value = "Delete device photo",
            notes = "Delete uploaded photo for specified device"
    )
    public Response deletePhoto(
            @ApiParam("Device number") @PathParam("number") String number,
            @ApiParam("Photo ID") @PathParam("photoId") Integer photoId) {
        try {
            Integer deviceId = getDeviceIdByNumber(number);
            if (deviceId == null) {
                log.warn("Device not found: {}", number);
                return Response.ERROR("device.not.found", "Device not found");
            }

            DevicePhoto photo = devicePhotoDAO.getDevicePhotoById(photoId);
            if (photo == null || !photo.getDeviceId().equals(deviceId)) {
                log.warn("Photo {} not found or not owned by device {}", photoId, number);
                return Response.OBJECT_NOT_FOUND_ERROR();
            }

            // TODO: Delete actual file from filesystem

            devicePhotoDAO.deleteDevicePhoto(photoId);
            log.info("Photo {} deleted for device {}", photoId, number);
            return Response.OK();
        } catch (Exception e) {
            log.error("Failed to delete photo {} for device {}", photoId, number, e);
            return Response.ERROR("error.internal.server", "Failed to delete photo");
        }
    }
}