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
 * <p>A domain object representing LDAP configuration.</p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LDAPConfiguration implements Serializable, CustomerData {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer customerId;
    private Boolean enabled;
    private String serverHost;
    private Integer serverPort;
    private Boolean useSsl;
    private Boolean useTls;
    private String bindDn;
    private String bindPassword;
    private String baseDn;
    private String userSearchFilter;
    private String userNameAttribute;
    private String userEmailAttribute;
    private String userFirstNameAttribute;
    private String userLastNameAttribute;
    private String groupSearchFilter;
    private String groupNameAttribute;
    private String adminGroupDn;
    private Boolean autoCreateUsers;
    private Integer autoAssignConfigurationId;
    private Integer connectionTimeout;
    private Integer readTimeout;
    private Date lastSyncAt;
    private Boolean syncEnabled;
    private Date createdAt;
    private Date updatedAt;

    public LDAPConfiguration() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getCustomerId() {
        return customerId;
    }

    @Override
    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }

    public Boolean getUseSsl() {
        return useSsl;
    }

    public void setUseSsl(Boolean useSsl) {
        this.useSsl = useSsl;
    }

    public Boolean getUseTls() {
        return useTls;
    }

    public void setUseTls(Boolean useTls) {
        this.useTls = useTls;
    }

    public String getBindDn() {
        return bindDn;
    }

    public void setBindDn(String bindDn) {
        this.bindDn = bindDn;
    }

    public String getBindPassword() {
        return bindPassword;
    }

    public void setBindPassword(String bindPassword) {
        this.bindPassword = bindPassword;
    }

    public String getBaseDn() {
        return baseDn;
    }

    public void setBaseDn(String baseDn) {
        this.baseDn = baseDn;
    }

    public String getUserSearchFilter() {
        return userSearchFilter;
    }

    public void setUserSearchFilter(String userSearchFilter) {
        this.userSearchFilter = userSearchFilter;
    }

    public String getUserNameAttribute() {
        return userNameAttribute;
    }

    public void setUserNameAttribute(String userNameAttribute) {
        this.userNameAttribute = userNameAttribute;
    }

    public String getUserEmailAttribute() {
        return userEmailAttribute;
    }

    public void setUserEmailAttribute(String userEmailAttribute) {
        this.userEmailAttribute = userEmailAttribute;
    }

    public String getUserFirstNameAttribute() {
        return userFirstNameAttribute;
    }

    public void setUserFirstNameAttribute(String userFirstNameAttribute) {
        this.userFirstNameAttribute = userFirstNameAttribute;
    }

    public String getUserLastNameAttribute() {
        return userLastNameAttribute;
    }

    public void setUserLastNameAttribute(String userLastNameAttribute) {
        this.userLastNameAttribute = userLastNameAttribute;
    }

    public String getGroupSearchFilter() {
        return groupSearchFilter;
    }

    public void setGroupSearchFilter(String groupSearchFilter) {
        this.groupSearchFilter = groupSearchFilter;
    }

    public String getGroupNameAttribute() {
        return groupNameAttribute;
    }

    public void setGroupNameAttribute(String groupNameAttribute) {
        this.groupNameAttribute = groupNameAttribute;
    }

    public String getAdminGroupDn() {
        return adminGroupDn;
    }

    public void setAdminGroupDn(String adminGroupDn) {
        this.adminGroupDn = adminGroupDn;
    }

    public Boolean getAutoCreateUsers() {
        return autoCreateUsers;
    }

    public void setAutoCreateUsers(Boolean autoCreateUsers) {
        this.autoCreateUsers = autoCreateUsers;
    }

    public Integer getAutoAssignConfigurationId() {
        return autoAssignConfigurationId;
    }

    public void setAutoAssignConfigurationId(Integer autoAssignConfigurationId) {
        this.autoAssignConfigurationId = autoAssignConfigurationId;
    }

    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Integer getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Integer readTimeout) {
        this.readTimeout = readTimeout;
    }

    public Date getLastSyncAt() {
        return lastSyncAt;
    }

    public void setLastSyncAt(Date lastSyncAt) {
        this.lastSyncAt = lastSyncAt;
    }

    public Boolean getSyncEnabled() {
        return syncEnabled;
    }

    public void setSyncEnabled(Boolean syncEnabled) {
        this.syncEnabled = syncEnabled;
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
     * Gets the LDAP server URL.
     */
    public String getLdapUrl() {
        String protocol = (useSsl != null && useSsl) ? "ldaps" : "ldap";
        int port = serverPort != null ? serverPort : (useSsl != null && useSsl ? 636 : 389);
        return protocol + "://" + serverHost + ":" + port;
    }

    /**
     * Checks if configuration is valid.
     */
    public boolean isValid() {
        return serverHost != null && !serverHost.isEmpty()
                && baseDn != null && !baseDn.isEmpty();
    }
}
