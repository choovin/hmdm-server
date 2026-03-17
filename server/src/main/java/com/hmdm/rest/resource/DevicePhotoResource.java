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
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

import com.hmdm.notification.PushService;
import com.hmdm.notification.persistence.domain.PushMessage;
import com.hmdm.persistence.DeviceDAO;
import com.hmdm.persistence.DevicePhotoDAO;
import com.hmdm.persistence.domain.Device;
import com.hmdm.persistence.domain.DevicePhoto;
import com.hmdm.persistence.domain.PhotoUploadRequest;
import com.hmdm.rest.json.Response;
import com.hmdm.security.SecurityContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Resource exposing REST API for device photo management.</p>
 */
@Api(tags = {"Device Photos"}, authorizations = {@Authorization("Bearer Token")})
@Singleton
@Path("/private/photos")
public class DevicePhotoResource {

    private static final Logger log = LoggerFactory.getLogger(DevicePhotoResource.class);

    public static final String TYPE_UPLOAD_PHOTOS = "uploadPhotos";

    private final DevicePhotoDAO devicePhotoDAO;
    private final DeviceDAO deviceDAO;
    private final PushService pushService;

    /**
     * <p>A constructor required by Swagger.</p>
     */
    public DevicePhotoResource() {
        this.devicePhotoDAO = null;
        this.deviceDAO = null;
        this.pushService = null;
    }

    @Inject
    public DevicePhotoResource(DevicePhotoDAO devicePhotoDAO,
                               DeviceDAO deviceDAO,
                               PushService pushService) {
        this.devicePhotoDAO = devicePhotoDAO;
        this.deviceDAO = deviceDAO;
        this.pushService = pushService;
    }

    // =================================================================================================================
    @ApiOperation(
            value = "Get all photos for customer",
            notes = "Get all photos uploaded by devices for current customer",
            response = DevicePhoto.class,
            responseContainer = "List"
    )
    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllPhotos() {
        try {
            List<DevicePhoto> photos = this.devicePhotoDAO.getDevicePhotosByCustomerId();
            return Response.OK(photos);
        } catch (Exception e) {
            log.error("Failed to retrieve device photos", e);
            return Response.INTERNAL_ERROR();
        }
    }

    // =================================================================================================================
    @ApiOperation(
            value = "Get photos by device",
            notes = "Get all photos uploaded by a specific device",
            response = DevicePhoto.class,
            responseContainer = "List"
    )
    @GET
    @Path("/device/{deviceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPhotosByDevice(@PathParam("deviceId") @ApiParam("Device ID") Integer deviceId) {
        try {
            List<DevicePhoto> photos = this.devicePhotoDAO.getDevicePhotosByDeviceId(deviceId);
            return Response.OK(photos);
        } catch (Exception e) {
            log.error("Failed to retrieve device photos for device #{}", deviceId, e);
            return Response.INTERNAL_ERROR();
        }
    }

    // =================================================================================================================
    @ApiOperation(
            value = "Search photos",
            notes = "Search photos by keyword",
            response = DevicePhoto.class,
            responseContainer = "List"
    )
    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchPhotos(@QueryParam("deviceId") Integer deviceId,
                                 @QueryParam("search") String search) {
        try {
            List<DevicePhoto> photos = this.devicePhotoDAO.searchDevicePhotos(deviceId, search);
            return Response.OK(photos);
        } catch (Exception e) {
            log.error("Failed to search device photos", e);
            return Response.INTERNAL_ERROR();
        }
    }

    // =================================================================================================================
    @ApiOperation(
            value = "Get photo by ID",
            notes = "Get a specific photo by its ID",
            response = DevicePhoto.class
    )
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPhotoById(@PathParam("id") @ApiParam("Photo ID") Integer id) {
        try {
            DevicePhoto photo = this.devicePhotoDAO.getDevicePhotoById(id);
            if (photo != null) {
                return Response.OK(photo);
            } else {
                return Response.ERROR();
            }
        } catch (Exception e) {
            log.error("Failed to retrieve device photo #{}", id, e);
            return Response.INTERNAL_ERROR();
        }
    }

    // =================================================================================================================
    @ApiOperation(
            value = "Update photo",
            notes = "Update photo description and tags"
    )
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePhoto(DevicePhoto photo) {
        try {
            final boolean canEdit = SecurityContext.get().hasPermission("edit_devices");

            if (!canEdit) {
                log.error("Unauthorized attempt to update photo",
                        com.hmdm.security.SecurityException.onCustomerDataAccessViolation(photo.getId(), "photo"));
                return Response.PERMISSION_DENIED();
            }

            this.devicePhotoDAO.updateDevicePhoto(photo);
            return Response.OK();
        } catch (Exception e) {
            log.error("Failed to update device photo", e);
            return Response.INTERNAL_ERROR();
        }
    }

    // =================================================================================================================
    @ApiOperation(
            value = "Delete photo",
            notes = "Delete a photo by ID"
    )
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deletePhoto(@PathParam("id") @ApiParam("Photo ID") Integer id) {
        try {
            final boolean canEdit = SecurityContext.get().hasPermission("edit_devices");

            if (!canEdit) {
                log.error("Unauthorized attempt to delete photo",
                        com.hmdm.security.SecurityException.onCustomerDataAccessViolation(id, "photo"));
                return Response.PERMISSION_DENIED();
            }

            this.devicePhotoDAO.deleteDevicePhoto(id);
            return Response.OK();
        } catch (Exception e) {
            log.error("Failed to delete device photo #{}", id, e);
            return Response.INTERNAL_ERROR();
        }
    }

    // =================================================================================================================
    @ApiOperation(
            value = "Request photo upload",
            notes = "Send a request to device to upload photos"
    )
    @POST
    @Path("/request/{deviceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response requestPhotoUpload(@PathParam("deviceId") @ApiParam("Device ID") Integer deviceId,
                                       @QueryParam("description") String description) {
        try {
            final boolean canEdit = SecurityContext.get().hasPermission("edit_devices");

            if (!canEdit) {
                log.error("Unauthorized attempt to request photo upload",
                        com.hmdm.security.SecurityException.onCustomerDataAccessViolation(deviceId, "device"));
                return Response.PERMISSION_DENIED();
            }

            Device device = this.deviceDAO.getDeviceById(deviceId);
            if (device == null) {
                return Response.DEVICE_NOT_FOUND_ERROR();
            }

            // Create a photo upload request record
            PhotoUploadRequest request = new PhotoUploadRequest();
            request.setDeviceId(deviceId);
            request.setDescription(description);
            this.devicePhotoDAO.createPhotoUploadRequest(request);

            // Send push notification to device
            this.pushService.sendSimpleMessage(deviceId, TYPE_UPLOAD_PHOTOS);

            return Response.OK(request);
        } catch (Exception e) {
            log.error("Failed to request photo upload for device #{}", deviceId, e);
            return Response.INTERNAL_ERROR();
        }
    }

    // =================================================================================================================
    @ApiOperation(
            value = "Get photo upload requests",
            notes = "Get all photo upload requests for current customer",
            response = PhotoUploadRequest.class,
            responseContainer = "List"
    )
    @GET
    @Path("/requests")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPhotoUploadRequests() {
        try {
            List<PhotoUploadRequest> requests = this.devicePhotoDAO.getPhotoUploadRequestsByCustomerId();
            return Response.OK(requests);
        } catch (Exception e) {
            log.error("Failed to retrieve photo upload requests", e);
            return Response.INTERNAL_ERROR();
        }
    }

    // =================================================================================================================
    @ApiOperation(
            value = "Get pending upload requests for device",
            notes = "Get pending photo upload requests for a specific device (called by device)",
            response = PhotoUploadRequest.class,
            responseContainer = "List"
    )
    @GET
    @Path("/pending/{deviceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPendingUploadRequests(@PathParam("deviceId") @ApiParam("Device ID") Integer deviceId) {
        try {
            List<PhotoUploadRequest> requests = this.devicePhotoDAO.getPendingPhotoUploadRequests(deviceId);
            return Response.OK(requests);
        } catch (Exception e) {
            log.error("Failed to retrieve pending photo upload requests for device #{}", deviceId, e);
            return Response.INTERNAL_ERROR();
        }
    }

    // =================================================================================================================
    @ApiOperation(
            value = "Update upload request status",
            notes = "Update the status of a photo upload request (called by device)"
    )
    @POST
    @Path("/request/{id}/status")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUploadRequestStatus(@PathParam("id") @ApiParam("Request ID") Integer id,
                                              @QueryParam("status") String status,
                                              @QueryParam("photoCount") Integer photoCount,
                                              @QueryParam("errorMessage") String errorMessage) {
        try {
            this.devicePhotoDAO.updatePhotoUploadRequestStatus(id, status, photoCount, errorMessage);
            return Response.OK();
        } catch (Exception e) {
            log.error("Failed to update photo upload request status #{}", id, e);
            return Response.INTERNAL_ERROR();
        }
    }
}
