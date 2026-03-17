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
 * <p>A domain object representing a remote control session.</p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RemoteControlSession implements Serializable, CustomerData {

    private static final long serialVersionUID = 1L;

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_CONNECTING = "CONNECTING";
    public static final String STATUS_CONNECTED = "CONNECTED";
    public static final String STATUS_DISCONNECTED = "DISCONNECTED";
    public static final String STATUS_ERROR = "ERROR";

    public static final String TYPE_VIEW = "VIEW";
    public static final String TYPE_CONTROL = "CONTROL";

    private Integer id;
    private Integer deviceId;
    private int customerId;
    private Integer userId;
    private String deviceNumber;
    private String userName;
    private String sessionToken;
    private String status;
    private String sessionType;
    private Date startedAt;
    private Date connectedAt;
    private Date endedAt;
    private Integer durationSeconds;
    private String remoteAddress;
    private String errorMessage;

    // WebRTC signaling data (not exposed in JSON)
    private transient String webrtcOffer;
    private transient String webrtcAnswer;
    private transient String iceCandidates;

    public RemoteControlSession() {
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
    public int getCustomerId() {
        return customerId;
    }

    @Override
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getDeviceNumber() {
        return deviceNumber;
    }

    public void setDeviceNumber(String deviceNumber) {
        this.deviceNumber = deviceNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Date startedAt) {
        this.startedAt = startedAt;
    }

    public Date getConnectedAt() {
        return connectedAt;
    }

    public void setConnectedAt(Date connectedAt) {
        this.connectedAt = connectedAt;
    }

    public Date getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(Date endedAt) {
        this.endedAt = endedAt;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getWebrtcOffer() {
        return webrtcOffer;
    }

    public void setWebrtcOffer(String webrtcOffer) {
        this.webrtcOffer = webrtcOffer;
    }

    public String getWebrtcAnswer() {
        return webrtcAnswer;
    }

    public void setWebrtcAnswer(String webrtcAnswer) {
        this.webrtcAnswer = webrtcAnswer;
    }

    public String getIceCandidates() {
        return iceCandidates;
    }

    public void setIceCandidates(String iceCandidates) {
        this.iceCandidates = iceCandidates;
    }

    /**
     * Checks if the session is active.
     */
    public boolean isActive() {
        return STATUS_PENDING.equals(status) || STATUS_CONNECTING.equals(status) || STATUS_CONNECTED.equals(status);
    }

    /**
     * Gets formatted duration string.
     */
    public String getFormattedDuration() {
        if (durationSeconds == null || durationSeconds == 0) {
            return "0:00";
        }
        int minutes = durationSeconds / 60;
        int seconds = durationSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
}
