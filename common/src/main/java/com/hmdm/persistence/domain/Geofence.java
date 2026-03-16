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
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * <p>A domain object representing a geofence (geographic boundary).</p>
 *
 * @author Headwind Solutions LLC
 */
@ApiModel(description = "A geographic boundary (geofence)")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Geofence implements CustomerData, Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("An ID of geofence")
    private Integer id;

    @ApiModelProperty(hidden = true)
    private Integer customerId;

    @ApiModelProperty("Name of the geofence")
    private String name;

    @ApiModelProperty("Description of the geofence")
    private String description;

    @ApiModelProperty("Latitude of geofence center")
    private Double latitude;

    @ApiModelProperty("Longitude of geofence center")
    private Double longitude;

    @ApiModelProperty("Radius of geofence in meters")
    private Integer radius;

    @ApiModelProperty("Send notification when device enters geofence")
    private Boolean enterNotification;

    @ApiModelProperty("Send notification when device exits geofence")
    private Boolean exitNotification;

    @ApiModelProperty("Whether this geofence is active")
    private Boolean active;

    @ApiModelProperty("List of device IDs assigned to this geofence")
    private List<Integer> deviceIds;

    @ApiModelProperty("Creation timestamp")
    private Long createdAt;

    public Geofence() {
    }

    @Override
    public Integer getCustomerId() {
        return customerId;
    }

    @Override
    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Integer getRadius() {
        return radius;
    }

    public void setRadius(Integer radius) {
        this.radius = radius;
    }

    public Boolean getEnterNotification() {
        return enterNotification;
    }

    public void setEnterNotification(Boolean enterNotification) {
        this.enterNotification = enterNotification;
    }

    public Boolean getExitNotification() {
        return exitNotification;
    }

    public void setExitNotification(Boolean exitNotification) {
        this.exitNotification = exitNotification;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<Integer> getDeviceIds() {
        return deviceIds;
    }

    public void setDeviceIds(List<Integer> deviceIds) {
        this.deviceIds = deviceIds;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }
}
