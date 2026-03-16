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
 * <p>A domain object representing LDAP synchronization log.</p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LDAPSyncLog implements Serializable, CustomerData {

    private static final long serialVersionUID = 1L;

    public static final String TYPE_FULL = "FULL";
    public static final String TYPE_INCREMENTAL = "INCREMENTAL";
    public static final String TYPE_TEST = "TEST";

    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_FAILED = "FAILED";
    public static final String STATUS_PARTIAL = "PARTIAL";

    private Integer id;
    private Integer customerId;
    private String syncType;
    private String status;
    private Integer usersSynced;
    private Integer usersCreated;
    private Integer usersUpdated;
    private Integer usersFailed;
    private String errorMessage;
    private Date startedAt;
    private Date completedAt;

    public LDAPSyncLog() {
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

    public String getSyncType() {
        return syncType;
    }

    public void setSyncType(String syncType) {
        this.syncType = syncType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getUsersSynced() {
        return usersSynced;
    }

    public void setUsersSynced(Integer usersSynced) {
        this.usersSynced = usersSynced;
    }

    public Integer getUsersCreated() {
        return usersCreated;
    }

    public void setUsersCreated(Integer usersCreated) {
        this.usersCreated = usersCreated;
    }

    public Integer getUsersUpdated() {
        return usersUpdated;
    }

    public void setUsersUpdated(Integer usersUpdated) {
        this.usersUpdated = usersUpdated;
    }

    public Integer getUsersFailed() {
        return usersFailed;
    }

    public void setUsersFailed(Integer usersFailed) {
        this.usersFailed = usersFailed;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Date startedAt) {
        this.startedAt = startedAt;
    }

    public Date getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Date completedAt) {
        this.completedAt = completedAt;
    }

    /**
     * Gets the duration of the sync in seconds.
     */
    public Long getDurationSeconds() {
        if (startedAt == null || completedAt == null) {
            return null;
        }
        return (completedAt.getTime() - startedAt.getTime()) / 1000;
    }

    /**
     * Checks if the sync was successful.
     */
    public boolean isSuccessful() {
        return STATUS_SUCCESS.equals(status);
    }

    /**
     * Gets a summary of the sync.
     */
    public String getSummary() {
        return String.format("Synced: %d, Created: %d, Updated: %d, Failed: %d",
                usersSynced != null ? usersSynced : 0,
                usersCreated != null ? usersCreated : 0,
                usersUpdated != null ? usersUpdated : 0,
                usersFailed != null ? usersFailed : 0);
    }
}
