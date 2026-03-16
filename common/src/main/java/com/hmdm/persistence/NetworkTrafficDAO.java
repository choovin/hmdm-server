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
import com.hmdm.persistence.domain.DeviceNetworkSettings;
import com.hmdm.persistence.domain.NetworkTrafficLog;
import com.hmdm.persistence.domain.NetworkTrafficRule;
import com.hmdm.persistence.mapper.NetworkTrafficMapper;
import com.hmdm.security.SecurityContext;
import org.mybatis.guice.transactional.Transactional;

import java.util.Date;
import java.util.List;

/**
 * <p>A DAO for managing network traffic filtering.</p>
 *
 * @author Igor Pikin (ipikin@h-mdm.com)
 */
public class NetworkTrafficDAO {

    private final NetworkTrafficMapper networkTrafficMapper;

    @Inject
    public NetworkTrafficDAO(NetworkTrafficMapper networkTrafficMapper) {
        this.networkTrafficMapper = networkTrafficMapper;
    }

    // ==================== Traffic Rules ====================

    @Transactional
    public NetworkTrafficRule createTrafficRule(NetworkTrafficRule rule) {
        rule.setCustomerId(SecurityContext.get().getCurrentUser().get().getCustomerId());
        networkTrafficMapper.insertTrafficRule(rule);
        return rule;
    }

    public NetworkTrafficRule getTrafficRuleById(Integer id) {
        return networkTrafficMapper.getTrafficRuleById(id,
                SecurityContext.get().getCurrentUser().get().getCustomerId());
    }

    public List<NetworkTrafficRule> getTrafficRulesByCustomer() {
        return networkTrafficMapper.getTrafficRulesByCustomerId(
                SecurityContext.get().getCurrentUser().get().getCustomerId());
    }

    public List<NetworkTrafficRule> getTrafficRulesByConfiguration(Integer configurationId) {
        return networkTrafficMapper.getTrafficRulesByConfigurationId(configurationId,
                SecurityContext.get().getCurrentUser().get().getCustomerId());
    }

    public List<NetworkTrafficRule> getEnabledTrafficRulesByConfiguration(Integer configurationId) {
        return networkTrafficMapper.getEnabledTrafficRulesByConfigurationId(configurationId,
                SecurityContext.get().getCurrentUser().get().getCustomerId());
    }

    @Transactional
    public int updateTrafficRule(NetworkTrafficRule rule) {
        rule.setCustomerId(SecurityContext.get().getCurrentUser().get().getCustomerId());
        return networkTrafficMapper.updateTrafficRule(rule);
    }

    @Transactional
    public int updateTrafficRuleEnabled(Integer id, Boolean enabled) {
        return networkTrafficMapper.updateTrafficRuleEnabled(id, enabled,
                SecurityContext.get().getCurrentUser().get().getCustomerId());
    }

    @Transactional
    public int deleteTrafficRule(Integer id) {
        return networkTrafficMapper.deleteTrafficRule(id,
                SecurityContext.get().getCurrentUser().get().getCustomerId());
    }

    @Transactional
    public int deleteOldTrafficRules(int daysToKeep) {
        Date beforeDate = new Date(System.currentTimeMillis() - (daysToKeep * 24L * 60L * 60L * 1000L));
        return networkTrafficMapper.deleteOldTrafficRules(beforeDate,
                SecurityContext.get().getCurrentUser().get().getCustomerId());
    }

    // ==================== Traffic Logs ====================

    @Transactional
    public NetworkTrafficLog logTraffic(NetworkTrafficLog log) {
        log.setCustomerId(SecurityContext.get().getCurrentUser().get().getCustomerId());
        networkTrafficMapper.insertTrafficLog(log);
        return log;
    }

    public NetworkTrafficLog getTrafficLogById(Integer id) {
        return networkTrafficMapper.getTrafficLogById(id,
                SecurityContext.get().getCurrentUser().get().getCustomerId());
    }

    public List<NetworkTrafficLog> getTrafficLogsByDevice(Integer deviceId) {
        return networkTrafficMapper.getTrafficLogsByDeviceId(deviceId,
                SecurityContext.get().getCurrentUser().get().getCustomerId());
    }

    public List<NetworkTrafficLog> getTrafficLogsByCustomer() {
        return networkTrafficMapper.getTrafficLogsByCustomerId(
                SecurityContext.get().getCurrentUser().get().getCustomerId());
    }

    public List<NetworkTrafficLog> getTrafficLogsByFilter(Integer deviceId, String action, String domain,
                                                          Date startDate, Date endDate) {
        return networkTrafficMapper.getTrafficLogsByFilter(deviceId,
                SecurityContext.get().getCurrentUser().get().getCustomerId(),
                action, domain, startDate, endDate);
    }

    @Transactional
    public int deleteOldTrafficLogs(int daysToKeep) {
        Date beforeDate = new Date(System.currentTimeMillis() - (daysToKeep * 24L * 60L * 60L * 1000L));
        return networkTrafficMapper.deleteOldTrafficLogs(beforeDate,
                SecurityContext.get().getCurrentUser().get().getCustomerId());
    }

    // ==================== Statistics ====================

    public Long getBlockedTrafficCountByDevice(Integer deviceId) {
        return networkTrafficMapper.getBlockedTrafficCountByDevice(deviceId,
                SecurityContext.get().getCurrentUser().get().getCustomerId());
    }

    public Long getTotalTrafficCountByDevice(Integer deviceId) {
        return networkTrafficMapper.getTotalTrafficCountByDevice(deviceId,
                SecurityContext.get().getCurrentUser().get().getCustomerId());
    }

    public Long getBlockedTrafficBytesByDevice(Integer deviceId) {
        return networkTrafficMapper.getBlockedTrafficBytesByDevice(deviceId,
                SecurityContext.get().getCurrentUser().get().getCustomerId());
    }

    // ==================== Device Network Settings ====================

    @Transactional
    public DeviceNetworkSettings createDeviceNetworkSettings(DeviceNetworkSettings settings) {
        settings.setCustomerId(SecurityContext.get().getCurrentUser().get().getCustomerId());
        networkTrafficMapper.insertDeviceNetworkSettings(settings);
        return settings;
    }

    public DeviceNetworkSettings getDeviceNetworkSettings(Integer deviceId) {
        return networkTrafficMapper.getDeviceNetworkSettings(deviceId,
                SecurityContext.get().getCurrentUser().get().getCustomerId());
    }

    @Transactional
    public int updateDeviceNetworkSettings(DeviceNetworkSettings settings) {
        settings.setCustomerId(SecurityContext.get().getCurrentUser().get().getCustomerId());
        return networkTrafficMapper.updateDeviceNetworkSettings(settings);
    }

    @Transactional
    public int deleteDeviceNetworkSettings(Integer deviceId) {
        return networkTrafficMapper.deleteDeviceNetworkSettings(deviceId,
                SecurityContext.get().getCurrentUser().get().getCustomerId());
    }

    @Transactional
    public DeviceNetworkSettings getOrCreateDeviceNetworkSettings(Integer deviceId) {
        DeviceNetworkSettings settings = getDeviceNetworkSettings(deviceId);
        if (settings == null) {
            settings = new DeviceNetworkSettings();
            settings.setDeviceId(deviceId);
            settings.setFilteringEnabled(false);
            settings.setLoggingEnabled(true);
            settings.setStrictMode(false);
            settings.setProxyEnabled(false);
            settings.setDnsOverHttpsEnabled(false);
            settings = createDeviceNetworkSettings(settings);
        }
        return settings;
    }
}
