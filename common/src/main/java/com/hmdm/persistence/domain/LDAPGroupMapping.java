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
 * <p>A domain object representing LDAP group mapping.</p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LDAPGroupMapping implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_OPERATOR = "OPERATOR";
    public static final String ROLE_VIEWER = "VIEWER";

    private Integer id;
    private Integer ldapConfigId;
    private String ldapGroupDn;
    private String localRole;
    private Integer localConfigurationId;
    private Integer priority;
    private Date createdAt;

    // Transient fields for display
    private String ldapGroupName;
    private String configurationName;

    public LDAPGroupMapping() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLdapConfigId() {
        return ldapConfigId;
    }

    public void setLdapConfigId(Integer ldapConfigId) {
        this.ldapConfigId = ldapConfigId;
    }

    public String getLdapGroupDn() {
        return ldapGroupDn;
    }

    public void setLdapGroupDn(String ldapGroupDn) {
        this.ldapGroupDn = ldapGroupDn;
    }

    public String getLocalRole() {
        return localRole;
    }

    public void setLocalRole(String localRole) {
        this.localRole = localRole;
    }

    public Integer getLocalConfigurationId() {
        return localConfigurationId;
    }

    public void setLocalConfigurationId(Integer localConfigurationId) {
        this.localConfigurationId = localConfigurationId;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getLdapGroupName() {
        return ldapGroupName;
    }

    public void setLdapGroupName(String ldapGroupName) {
        this.ldapGroupName = ldapGroupName;
    }

    public String getConfigurationName() {
        return configurationName;
    }

    public void setConfigurationName(String configurationName) {
        this.configurationName = configurationName;
    }

    /**
     * Gets the LDAP group name from DN.
     */
    public String extractGroupName() {
        if (ldapGroupDn == null || ldapGroupDn.isEmpty()) {
            return null;
        }
        // Extract CN from DN (e.g., "CN=Admins,OU=Groups,DC=example,DC=com" -> "Admins")
        String[] parts = ldapGroupDn.split(",");
        for (String part : parts) {
            String trimmed = part.trim();
            if (trimmed.toUpperCase().startsWith("CN=")) {
                return trimmed.substring(3);
            }
        }
        return ldapGroupDn;
    }
}
