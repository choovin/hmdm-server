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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>A domain object representing device-specific network settings.</p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceNetworkSettings implements Serializable, CustomerData {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer deviceId;
    private Integer customerId;
    private String deviceNumber;
    private Boolean filteringEnabled;
    private Boolean loggingEnabled;
    private Boolean strictMode;
    private String proxyHost;
    private Integer proxyPort;
    private Boolean proxyEnabled;
    private Boolean dnsOverHttpsEnabled;
    private String dnsOverHttpsUrl;
    private Date updatedAt;

    public DeviceNetworkSettings() {
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

    @Override
    public Integer getCustomerId() {
        return customerId;
    }

    @Override
    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getDeviceNumber() {
        return deviceNumber;
    }

    public void setDeviceNumber(String deviceNumber) {
        this.deviceNumber = deviceNumber;
    }

    public Boolean getFilteringEnabled() {
        return filteringEnabled;
    }

    public void setFilteringEnabled(Boolean filteringEnabled) {
        this.filteringEnabled = filteringEnabled;
    }

    public Boolean getLoggingEnabled() {
        return loggingEnabled;
    }

    public void setLoggingEnabled(Boolean loggingEnabled) {
        this.loggingEnabled = loggingEnabled;
    }

    public Boolean getStrictMode() {
        return strictMode;
    }

    public void setStrictMode(Boolean strictMode) {
        this.strictMode = strictMode;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public Integer getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(Integer proxyPort) {
        this.proxyPort = proxyPort;
    }

    public Boolean getProxyEnabled() {
        return proxyEnabled;
    }

    public void setProxyEnabled(Boolean proxyEnabled) {
        this.proxyEnabled = proxyEnabled;
    }

    public Boolean getDnsOverHttpsEnabled() {
        return dnsOverHttpsEnabled;
    }

    public void setDnsOverHttpsEnabled(Boolean dnsOverHttpsEnabled) {
        this.dnsOverHttpsEnabled = dnsOverHttpsEnabled;
    }

    public String getDnsOverHttpsUrl() {
        return dnsOverHttpsUrl;
    }

    public void setDnsOverHttpsUrl(String dnsOverHttpsUrl) {
        this.dnsOverHttpsUrl = dnsOverHttpsUrl;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Gets the full proxy URL.
     */
    public String getProxyUrl() {
        if (proxyHost == null || proxyHost.isEmpty() || proxyPort == null) {
            return null;
        }
        return proxyHost + ":" + proxyPort;
    }
}
