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

package com.hmdm.persistence;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.hmdm.persistence.domain.DevicePhoto;
import com.hmdm.persistence.domain.PhotoUploadRequest;
import com.hmdm.persistence.mapper.DevicePhotoMapper;
import com.hmdm.security.SecurityContext;
import org.mybatis.guice.transactional.Transactional;

import java.util.List;

/**
 * <p>A DAO for device photo management.</p>
 */
@Singleton
public class DevicePhotoDAO {

    private final DevicePhotoMapper devicePhotoMapper;

    @Inject
    public DevicePhotoDAO(DevicePhotoMapper devicePhotoMapper) {
        this.devicePhotoMapper = devicePhotoMapper;
    }

    /**
     * Inserts a new device photo record.
     */
    @Transactional
    public void insertDevicePhoto(DevicePhoto photo) {
        if (photo.getCustomerId() == null) {
            photo.setCustomerId(SecurityContext.get().getCurrentCustomerId().orElse(null));
        }
        this.devicePhotoMapper.insertDevicePhoto(photo);
    }

    /**
     * Gets a device photo by ID.
     */
    public DevicePhoto getDevicePhotoById(Integer id) {
        DevicePhoto photo = this.devicePhotoMapper.getDevicePhotoById(id);
        if (photo != null) {
            SecurityContext.get().checkCurrentCustomerAccess(photo.getCustomerId());
        }
        return photo;
    }

    /**
     * Gets all photos for a device.
     */
    public List<DevicePhoto> getDevicePhotosByDeviceId(Integer deviceId) {
        return this.devicePhotoMapper.getDevicePhotosByDeviceId(deviceId);
    }

    /**
     * Gets all photos for current customer.
     */
    public List<DevicePhoto> getDevicePhotosByCustomerId() {
        Integer customerId = SecurityContext.get().getCurrentCustomerId().orElse(null);
        return this.devicePhotoMapper.getDevicePhotosByCustomerId(customerId);
    }

    /**
     * Searches device photos with filters.
     */
    public List<DevicePhoto> searchDevicePhotos(Integer deviceId, String search) {
        Integer customerId = SecurityContext.get().getCurrentCustomerId().orElse(null);
        if (search != null && !search.isEmpty()) {
            search = "%" + search + "%";
        }
        return this.devicePhotoMapper.searchDevicePhotos(customerId, deviceId, search);
    }

    /**
     * Updates a device photo.
     */
    @Transactional
    public void updateDevicePhoto(DevicePhoto photo) {
        DevicePhoto existing = getDevicePhotoById(photo.getId());
        if (existing != null) {
            this.devicePhotoMapper.updateDevicePhoto(photo);
        }
    }

    /**
     * Deletes a device photo.
     */
    @Transactional
    public void deleteDevicePhoto(Integer id) {
        DevicePhoto photo = getDevicePhotoById(id);
        if (photo != null) {
            this.devicePhotoMapper.deleteDevicePhoto(id);
        }
    }

    /**
     * Deletes old photos older than specified days.
     */
    @Transactional
    public void deleteOldPhotos(int days) {
        this.devicePhotoMapper.deleteOldPhotos(days);
    }

    // Photo Upload Request methods

    /**
     * Creates a new photo upload request.
     */
    @Transactional
    public PhotoUploadRequest createPhotoUploadRequest(PhotoUploadRequest request) {
        if (request.getCustomerId() == null) {
            request.setCustomerId(SecurityContext.get().getCurrentCustomerId().orElse(null));
        }
        request.setStatus(PhotoUploadRequest.STATUS_PENDING);
        this.devicePhotoMapper.insertPhotoUploadRequest(request);
        return request;
    }

    /**
     * Gets a photo upload request by ID.
     */
    public PhotoUploadRequest getPhotoUploadRequestById(Integer id) {
        PhotoUploadRequest request = this.devicePhotoMapper.getPhotoUploadRequestById(id);
        if (request != null) {
            SecurityContext.get().checkCurrentCustomerAccess(request.getCustomerId());
        }
        return request;
    }

    /**
     * Gets pending photo upload requests for a device.
     */
    public List<PhotoUploadRequest> getPendingPhotoUploadRequests(Integer deviceId) {
        return this.devicePhotoMapper.getPendingPhotoUploadRequests(deviceId);
    }

    /**
     * Updates a photo upload request status.
     */
    @Transactional
    public void updatePhotoUploadRequestStatus(Integer id, String status, Integer photoCount, String errorMessage) {
        PhotoUploadRequest request = getPhotoUploadRequestById(id);
        if (request != null) {
            this.devicePhotoMapper.updatePhotoUploadRequestStatus(id, status, photoCount, errorMessage);
        }
    }

    /**
     * Gets all photo upload requests for current customer.
     */
    public List<PhotoUploadRequest> getPhotoUploadRequestsByCustomerId() {
        Integer customerId = SecurityContext.get().getCurrentCustomerId().orElse(null);
        return this.devicePhotoMapper.getPhotoUploadRequestsByCustomerId(customerId);
    }

    /**
     * Deletes old completed photo upload requests.
     */
    @Transactional
    public void deleteOldPhotoUploadRequests(int days) {
        this.devicePhotoMapper.deleteOldPhotoUploadRequests(days);
    }
}
