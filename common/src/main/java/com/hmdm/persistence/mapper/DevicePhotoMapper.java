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

package com.hmdm.persistence.mapper;

import com.hmdm.persistence.domain.DevicePhoto;
import com.hmdm.persistence.domain.PhotoUploadRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>MyBatis mapper for device photo operations.</p>
 */
public interface DevicePhotoMapper {

    /**
     * Inserts a new device photo record.
     */
    void insertDevicePhoto(DevicePhoto photo);

    /**
     * Gets a device photo by ID.
     */
    DevicePhoto getDevicePhotoById(@Param("id") Integer id);

    /**
     * Gets all photos for a device.
     */
    List<DevicePhoto> getDevicePhotosByDeviceId(@Param("deviceId") Integer deviceId);

    /**
     * Gets all photos for a customer.
     */
    List<DevicePhoto> getDevicePhotosByCustomerId(@Param("customerId") Integer customerId);

    /**
     * Searches device photos with filters.
     */
    List<DevicePhoto> searchDevicePhotos(@Param("customerId") Integer customerId,
                                          @Param("deviceId") Integer deviceId,
                                          @Param("search") String search);

    /**
     * Updates a device photo.
     */
    void updateDevicePhoto(DevicePhoto photo);

    /**
     * Deletes a device photo.
     */
    void deleteDevicePhoto(@Param("id") Integer id);

    /**
     * Deletes old photos older than specified days.
     */
    void deleteOldPhotos(@Param("days") int days);

    // Photo Upload Request methods

    /**
     * Inserts a new photo upload request.
     */
    void insertPhotoUploadRequest(PhotoUploadRequest request);

    /**
     * Gets a photo upload request by ID.
     */
    PhotoUploadRequest getPhotoUploadRequestById(@Param("id") Integer id);

    /**
     * Gets all pending photo upload requests for a device.
     */
    List<PhotoUploadRequest> getPendingPhotoUploadRequests(@Param("deviceId") Integer deviceId);

    /**
     * Updates a photo upload request status.
     */
    void updatePhotoUploadRequestStatus(@Param("id") Integer id,
                                        @Param("status") String status,
                                        @Param("photoCount") Integer photoCount,
                                        @Param("errorMessage") String errorMessage);

    /**
     * Gets all photo upload requests for a customer.
     */
    List<PhotoUploadRequest> getPhotoUploadRequestsByCustomerId(@Param("customerId") Integer customerId);

    /**
     * Deletes old completed photo upload requests.
     */
    void deleteOldPhotoUploadRequests(@Param("days") int days);
}
