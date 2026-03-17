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
 * <p>A domain object representing a network traffic filtering rule.</p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NetworkTrafficRule implements Serializable, CustomerData {

    private static final long serialVersionUID = 1L;

    public static final String TYPE_ALLOW = "ALLOW";
    public static final String TYPE_BLOCK = "BLOCK";
    public static final String TYPE_WHITELIST = "WHITELIST";
    public static final String TYPE_BLACKLIST = "BLACKLIST";

    public static final String TRAFFIC_TYPE_URL = "URL";
    public static final String TRAFFIC_TYPE_DOMAIN = "DOMAIN";
    public static final String TRAFFIC_TYPE_IP = "IP";
    public static final String TRAFFIC_TYPE_PORT = "PORT";
    public static final String TRAFFIC_TYPE_APP = "APP";

    private Integer id;
    private int customerId;
    private Integer configurationId;
    private String ruleName;
    private String ruleType;
    private String trafficType;
    private String pattern;
    private String description;
    private Integer priority;
    private Boolean enabled;
    private Date createdAt;
    private Date updatedAt;

    public NetworkTrafficRule() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public int getCustomerId() {
        return customerId;
    }

    @Override
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public Integer getConfigurationId() {
        return configurationId;
    }

    public void setConfigurationId(Integer configurationId) {
        this.configurationId = configurationId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public String getTrafficType() {
        return trafficType;
    }

    public void setTrafficType(String trafficType) {
        this.trafficType = trafficType;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Checks if this is a blocking rule.
     */
    public boolean isBlocking() {
        return TYPE_BLOCK.equals(ruleType) || TYPE_BLACKLIST.equals(ruleType);
    }

    /**
     * Checks if this is an allowing rule.
     */
    public boolean isAllowing() {
        return TYPE_ALLOW.equals(ruleType) || TYPE_WHITELIST.equals(ruleType);
    }
}
