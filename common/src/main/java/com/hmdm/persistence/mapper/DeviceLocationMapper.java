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

import java.util.List;

import com.hmdm.persistence.domain.DeviceLocation;
import com.hmdm.persistence.domain.Geofence;
import org.apache.ibatis.annotations.Param;

/**
 * <p>Mapper interface for device location data.</p>
 *
 * @author Headwind Solutions LLC
 */
public interface DeviceLocationMapper {

    /**
     * <p>Inserts a new device location record.</p>
     *
     * @param location the location record to insert.
     * @return the number of inserted records.
     */
    int insertDeviceLocation(DeviceLocation location);

    /**
     * <p>Gets the location history for a specific device.</p>
     *
     * @param deviceId the ID of device.
     * @param fromTime optional start time filter.
     * @param toTime optional end time filter.
     * @param limit maximum number of records to return.
     * @return a list of location records.
     */
    List<DeviceLocation> getDeviceLocationHistory(
            @Param("deviceId") Integer deviceId,
            @Param("fromTime") Long fromTime,
            @Param("toTime") Long toTime,
            @Param("limit") Integer limit);

    /**
     * <p>Gets the latest location for a device.</p>
     *
     * @param deviceId the ID of device.
     * @return the latest location record or null if not found.
     */
    DeviceLocation getLatestDeviceLocation(@Param("deviceId") Integer deviceId);

    /**
     * <p>Gets the latest locations for all devices of a customer.</p>
     *
     * @param customerId the ID of customer.
     * @return a list of latest location records.
     */
    List<DeviceLocation> getAllLatestLocations(@Param("customerId") Integer customerId);

    /**
     * <p>Deletes old location records for a device.</p>
     *
     * @param deviceId the ID of device.
     * @param olderThan timestamp before which records should be deleted.
     * @return the number of deleted records.
     */
    int deleteOldLocations(
            @Param("deviceId") Integer deviceId,
            @Param("olderThan") Long olderThan);

    // Geofence methods

    /**
     * <p>Inserts a new geofence.</p>
     *
     * @param geofence the geofence to insert.
     * @return the number of inserted records.
     */
    int insertGeofence(Geofence geofence);

    /**
     * <p>Updates an existing geofence.</p>
     *
     * @param geofence the geofence to update.
     * @return the number of updated records.
     */
    int updateGeofence(Geofence geofence);

    /**
     * <p>Deletes a geofence by ID.</p>
     *
     * @param id the ID of geofence.
     * @param customerId the ID of customer for security check.
     * @return the number of deleted records.
     */
    int deleteGeofence(
            @Param("id") Integer id,
            @Param("customerId") Integer customerId);

    /**
     * <p>Gets a geofence by ID.</p>
     *
     * @param id the ID of geofence.
     * @param customerId the ID of customer for security check.
     * @return the geofence or null if not found.
     */
    Geofence getGeofenceById(
            @Param("id") Integer id,
            @Param("customerId") Integer customerId);

    /**
     * <p>Gets all geofences for a customer.</p>
     *
     * @param customerId the ID of customer.
     * @param activeOnly if true, return only active geofences.
     * @return a list of geofences.
     */
    List<Geofence> getAllGeofences(
            @Param("customerId") Integer customerId,
            @Param("activeOnly") Boolean activeOnly);

    /**
     * <p>Assigns devices to a geofence.</p>
     *
     * @param geofenceId the ID of geofence.
     * @param deviceIds list of device IDs.
     * @return the number of inserted records.
     */
    int assignDevicesToGeofence(
            @Param("geofenceId") Integer geofenceId,
            @Param("deviceIds") List<Integer> deviceIds);

    /**
     * <p>Removes all device assignments from a geofence.</p>
     *
     * @param geofenceId the ID of geofence.
     * @return the number of deleted records.
     */
    int removeDevicesFromGeofence(@Param("geofenceId") Integer geofenceId);

    /**
     * <p>Gets device IDs assigned to a geofence.</p>
     *
     * @param geofenceId the ID of geofence.
     * @return a list of device IDs.
     */
    List<Integer> getGeofenceDeviceIds(@Param("geofenceId") Integer geofenceId);

    /**
     * <p>Updates the geofence status for a device.</p>
     *
     * @param geofenceId the ID of geofence.
     * @param deviceId the ID of device.
     * @param status the new status (inside, outside, unknown).
     * @return the number of updated records.
     */
    int updateGeofenceDeviceStatus(
            @Param("geofenceId") Integer geofenceId,
            @Param("deviceId") Integer deviceId,
            @Param("status") String status);
}
