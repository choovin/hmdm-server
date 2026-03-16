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

package com.hmdm.persistence.domain;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * <p>A domain object representing a device location record.</p>
 *
 * @author Headwind Solutions LLC
 */
@ApiModel(description = "A device location record")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceLocation implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("An ID of location record")
    private Integer id;

    @ApiModelProperty("An ID of device")
    private Integer deviceId;

    @ApiModelProperty("Device number")
    private String deviceNumber;

    @ApiModelProperty("Latitude coordinate")
    private Double latitude;

    @ApiModelProperty("Longitude coordinate")
    private Double longitude;

    @ApiModelProperty("Location accuracy in meters")
    private Double accuracy;

    @ApiModelProperty("Altitude in meters")
    private Double altitude;

    @ApiModelProperty("Speed in meters per second")
    private Double speed;

    @ApiModelProperty("Battery level percentage")
    private Integer batteryLevel;

    @ApiModelProperty("Human-readable address")
    private String address;

    @ApiModelProperty("Location provider (gps, network, passive)")
    private String provider;

    @ApiModelProperty("Timestamp when location was recorded on device")
    private Long timestamp;

    @ApiModelProperty("Record creation time on server")
    private Long createdAt;

    public DeviceLocation() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceNumber() {
        return deviceNumber;
    }

    public void setDeviceNumber(String deviceNumber) {
        this.deviceNumber = deviceNumber;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Double accuracy) {
        this.accuracy = accuracy;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Integer getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(Integer batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Calculate distance from this location to another location in meters
     */
    public double distanceTo(DeviceLocation other) {
        if (this.latitude == null || this.longitude == null ||
            other.latitude == null || other.longitude == null) {
            return -1;
        }

        double R = 6371000; // Earth radius in meters
        double lat1Rad = Math.toRadians(this.latitude);
        double lat2Rad = Math.toRadians(other.latitude);
        double deltaLat = Math.toRadians(other.latitude - this.latitude);
        double deltaLon = Math.toRadians(other.longitude - this.longitude);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    /**
     * Check if this location is inside a geofence
     */
    public boolean isInsideGeofence(Geofence geofence) {
        if (this.latitude == null || this.longitude == null ||
            geofence.getLatitude() == null || geofence.getLongitude() == null) {
            return false;
        }

        DeviceLocation center = new DeviceLocation();
        center.setLatitude(geofence.getLatitude());
        center.setLongitude(geofence.getLongitude());

        double distance = distanceTo(center);
        return distance <= geofence.getRadius();
    }
}
