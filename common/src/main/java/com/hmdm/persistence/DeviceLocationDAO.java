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
import com.hmdm.persistence.domain.DeviceLocation;
import com.hmdm.persistence.domain.Geofence;
import com.hmdm.persistence.mapper.DeviceLocationMapper;
import com.hmdm.security.SecurityContext;
import org.mybatis.guice.transactional.Transactional;

import java.util.List;

/**
 * <p>DAO for device location tracking and geofencing operations.</p>
 *
 * @author Headwind Solutions LLC
 */
@Singleton
public class DeviceLocationDAO {

    private final DeviceLocationMapper mapper;

    @Inject
    public DeviceLocationDAO(DeviceLocationMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * <p>Inserts a new device location record.</p>
     *
     * @param location the location record to insert.
     * @return the inserted location record with generated ID.
     */
    @Transactional
    public DeviceLocation insertDeviceLocation(DeviceLocation location) {
        mapper.insertDeviceLocation(location);
        return location;
    }

    /**
     * <p>Gets the location history for a specific device.</p>
     *
     * @param deviceId the ID of device.
     * @param fromTime optional start time filter.
     * @param toTime optional end time filter.
     * @param limit maximum number of records to return.
     * @return a list of location records.
     */
    public List<DeviceLocation> getDeviceLocationHistory(
            Integer deviceId,
            Long fromTime,
            Long toTime,
            Integer limit) {
        return mapper.getDeviceLocationHistory(deviceId, fromTime, toTime, limit);
    }

    /**
     * <p>Gets the latest location for a device.</p>
     *
     * @param deviceId the ID of device.
     * @return the latest location record or null if not found.
     */
    public DeviceLocation getLatestDeviceLocation(Integer deviceId) {
        return mapper.getLatestDeviceLocation(deviceId);
    }

    /**
     * <p>Gets the latest locations for all devices of the current customer.</p>
     *
     * @return a list of latest location records.
     */
    public List<DeviceLocation> getAllLatestLocations() {
        return mapper.getAllLatestLocations(SecurityContext.get().getCurrentUser().get().getCustomerId());
    }

    /**
     * <p>Gets the latest locations for all devices of a specific customer.</p>
     *
     * @param customerId the ID of customer.
     * @return a list of latest location records.
     */
    public List<DeviceLocation> getAllLatestLocations(Integer customerId) {
        return mapper.getAllLatestLocations(customerId);
    }

    /**
     * <p>Deletes old location records for a device.</p>
     *
     * @param deviceId the ID of device.
     * @param olderThan timestamp before which records should be deleted.
     * @return the number of deleted records.
     */
    @Transactional
    public int deleteOldLocations(Integer deviceId, Long olderThan) {
        return mapper.deleteOldLocations(deviceId, olderThan);
    }

    // Geofence methods

    /**
     * <p>Creates a new geofence.</p>
     *
     * @param geofence the geofence to create.
     * @return the created geofence with generated ID.
     */
    @Transactional
    public Geofence insertGeofence(Geofence geofence) {
        geofence.setCustomerId(SecurityContext.get().getCurrentUser().get().getCustomerId());
        mapper.insertGeofence(geofence);
        return geofence;
    }

    /**
     * <p>Creates a new geofence for a specific customer.</p>
     *
     * @param geofence the geofence to create.
     * @param customerId the customer ID.
     * @return the created geofence with generated ID.
     */
    @Transactional
    public Geofence insertGeofence(Geofence geofence, Integer customerId) {
        geofence.setCustomerId(customerId);
        mapper.insertGeofence(geofence);
        return geofence;
    }

    /**
     * <p>Updates an existing geofence.</p>
     *
     * @param geofence the geofence to update.
     * @return the number of updated records.
     */
    @Transactional
    public int updateGeofence(Geofence geofence) {
        geofence.setCustomerId(SecurityContext.get().getCurrentUser().get().getCustomerId());
        return mapper.updateGeofence(geofence);
    }

    /**
     * <p>Deletes a geofence by ID.</p>
     *
     * @param id the ID of geofence.
     * @return the number of deleted records.
     */
    @Transactional
    public int deleteGeofence(Integer id) {
        Integer customerId = SecurityContext.get().getCurrentUser().get().getCustomerId();
        return mapper.deleteGeofence(id, customerId);
    }

    /**
     * <p>Gets a geofence by ID.</p>
     *
     * @param id the ID of geofence.
     * @return the geofence or null if not found.
     */
    public Geofence getGeofenceById(Integer id) {
        Integer customerId = SecurityContext.get().getCurrentUser().get().getCustomerId();
        return mapper.getGeofenceById(id, customerId);
    }

    /**
     * <p>Gets all geofences for the current customer.</p>
     *
     * @param activeOnly if true, return only active geofences.
     * @return a list of geofences.
     */
    public List<Geofence> getAllGeofences(Boolean activeOnly) {
        Integer customerId = SecurityContext.get().getCurrentUser().get().getCustomerId();
        return mapper.getAllGeofences(customerId, activeOnly);
    }

    /**
     * <p>Gets all geofences for the current customer.</p>
     *
     * @return a list of geofences.
     */
    public List<Geofence> getAllGeofences() {
        return getAllGeofences(null);
    }

    /**
     * <p>Assigns devices to a geofence.</p>
     *
     * @param geofenceId the ID of geofence.
     * @param deviceIds list of device IDs.
     * @return the number of inserted records.
     */
    @Transactional
    public int assignDevicesToGeofence(Integer geofenceId, List<Integer> deviceIds) {
        // First remove existing assignments
        mapper.removeDevicesFromGeofence(geofenceId);
        // Then add new assignments
        if (deviceIds != null && !deviceIds.isEmpty()) {
            return mapper.assignDevicesToGeofence(geofenceId, deviceIds);
        }
        return 0;
    }

    /**
     * <p>Gets device IDs assigned to a geofence.</p>
     *
     * @param geofenceId the ID of geofence.
     * @return a list of device IDs.
     */
    public List<Integer> getGeofenceDeviceIds(Integer geofenceId) {
        return mapper.getGeofenceDeviceIds(geofenceId);
    }

    /**
     * <p>Updates the geofence status for a device.</p>
     *
     * @param geofenceId the ID of geofence.
     * @param deviceId the ID of device.
     * @param status the new status (inside, outside, unknown).
     * @return the number of updated records.
     */
    @Transactional
    public int updateGeofenceDeviceStatus(Integer geofenceId, Integer deviceId, String status) {
        return mapper.updateGeofenceDeviceStatus(geofenceId, deviceId, status);
    }
}
