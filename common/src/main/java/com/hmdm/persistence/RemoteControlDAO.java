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
import com.hmdm.persistence.domain.RemoteControlSession;
import com.hmdm.persistence.mapper.RemoteControlMapper;
import com.hmdm.security.SecurityContext;
import com.hmdm.security.SecurityException;
import org.mybatis.guice.transactional.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * <p>A DAO for managing remote control sessions.</p>
 *
 * @author Igor Pikin (ipikin@h-mdm.com)
 */
public class RemoteControlDAO {

    private final RemoteControlMapper remoteControlMapper;

    @Inject
    public RemoteControlDAO(RemoteControlMapper remoteControlMapper) {
        this.remoteControlMapper = remoteControlMapper;
    }

    /**
     * <p>Creates a new remote control session.</p>
     *
     * @param deviceId the device ID.
     * @param sessionType the session type (VIEW or CONTROL).
     * @return the created session.
     */
    @Transactional
    public RemoteControlSession createSession(Integer deviceId, String sessionType) {
        RemoteControlSession session = new RemoteControlSession();
        session.setDeviceId(deviceId);
        session.setCustomerId(SecurityContext.get().getCurrentUser().get().getCustomerId());
        session.setUserId(SecurityContext.get().getCurrentUser().get().getId());
        session.setSessionToken(UUID.randomUUID().toString().replace("-", ""));
        session.setStatus(RemoteControlSession.STATUS_PENDING);
        session.setSessionType(sessionType);
        session.setStartedAt(new Date());

        remoteControlMapper.insertRemoteControlSession(session);
        return session;
    }

    /**
     * <p>Gets a session by ID.</p>
     *
     * @param id the session ID.
     * @return the session.
     * @throws SecurityException if access is denied.
     */
    public RemoteControlSession getSessionById(Integer id) {
        return remoteControlMapper.getRemoteControlSessionById(id,
                SecurityContext.get().getCurrentUser().get().getCustomerId());
    }

    /**
     * <p>Gets a session by token.</p>
     *
     * @param sessionToken the session token.
     * @return the session.
     * @throws SecurityException if access is denied.
     */
    public RemoteControlSession getSessionByToken(String sessionToken) {
        return remoteControlMapper.getRemoteControlSessionByToken(sessionToken,
                SecurityContext.get().getCurrentUser().get().getCustomerId());
    }

    /**
     * <p>Gets active sessions for a device.</p>
     *
     * @param deviceId the device ID.
     * @return list of active sessions.
     */
    public List<RemoteControlSession> getActiveSessionsByDevice(Integer deviceId) {
        return remoteControlMapper.getActiveSessionsByDeviceId(deviceId,
                SecurityContext.get().getCurrentUser().get().getCustomerId());
    }

    /**
     * <p>Gets all sessions for a device.</p>
     *
     * @param deviceId the device ID.
     * @return list of sessions.
     */
    public List<RemoteControlSession> getSessionsByDevice(Integer deviceId) {
        return remoteControlMapper.getRemoteControlSessionsByDeviceId(deviceId,
                SecurityContext.get().getCurrentUser().get().getCustomerId());
    }

    /**
     * <p>Gets all sessions for the current customer.</p>
     *
     * @return list of sessions.
     */
    public List<RemoteControlSession> getAllSessions() {
        return remoteControlMapper.getRemoteControlSessionsByCustomerId(
                SecurityContext.get().getCurrentUser().get().getCustomerId());
    }

    /**
     * <p>Updates a session.</p>
     *
     * @param session the session to update.
     * @return the number of updated records.
     */
    @Transactional
    public int updateSession(RemoteControlSession session) {
        session.setCustomerId(SecurityContext.get().getCurrentUser().get().getCustomerId());
        return remoteControlMapper.updateRemoteControlSession(session);
    }

    /**
     * <p>Updates session status.</p>
     *
     * @param id the session ID.
     * @param status the new status.
     * @return the number of updated records.
     */
    @Transactional
    public int updateSessionStatus(Integer id, String status) {
        return remoteControlMapper.updateSessionStatus(id, status,
                SecurityContext.get().getCurrentUser().get().getCustomerId());
    }

    /**
     * <p>Updates session WebRTC data.</p>
     *
     * @param id the session ID.
     * @param webrtcOffer the WebRTC offer.
     * @param webrtcAnswer the WebRTC answer.
     * @param iceCandidates the ICE candidates.
     * @return the number of updated records.
     */
    @Transactional
    public int updateSessionWebRTCData(Integer id, String webrtcOffer, String webrtcAnswer, String iceCandidates) {
        return remoteControlMapper.updateSessionWebRTCData(id, webrtcOffer, webrtcAnswer, iceCandidates,
                SecurityContext.get().getCurrentUser().get().getCustomerId());
    }

    /**
     * <p>Marks session as connected.</p>
     *
     * @param id the session ID.
     * @param remoteAddress the remote address.
     * @return the number of updated records.
     */
    @Transactional
    public int markSessionConnected(Integer id, String remoteAddress) {
        return remoteControlMapper.markSessionConnected(id, new Date(), remoteAddress,
                SecurityContext.get().getCurrentUser().get().getCustomerId());
    }

    /**
     * <p>Marks session as disconnected.</p>
     *
     * @param id the session ID.
     * @param durationSeconds the session duration.
     * @return the number of updated records.
     */
    @Transactional
    public int markSessionDisconnected(Integer id, Integer durationSeconds) {
        return remoteControlMapper.markSessionDisconnected(id, new Date(), durationSeconds,
                SecurityContext.get().getCurrentUser().get().getCustomerId());
    }

    /**
     * <p>Marks session as error.</p>
     *
     * @param id the session ID.
     * @param errorMessage the error message.
     * @return the number of updated records.
     */
    @Transactional
    public int markSessionError(Integer id, String errorMessage) {
        return remoteControlMapper.markSessionError(id, errorMessage, new Date(),
                SecurityContext.get().getCurrentUser().get().getCustomerId());
    }

    /**
     * <p>Deletes a session.</p>
     *
     * @param id the session ID.
     * @return the number of deleted records.
     */
    @Transactional
    public int deleteSession(Integer id) {
        return remoteControlMapper.deleteRemoteControlSession(id,
                SecurityContext.get().getCurrentUser().get().getCustomerId());
    }

    /**
     * <p>Deletes old completed sessions.</p>
     *
     * @param daysToKeep the number of days to keep sessions.
     * @return the number of deleted records.
     */
    @Transactional
    public int deleteOldSessions(int daysToKeep) {
        Date beforeDate = new Date(System.currentTimeMillis() - (daysToKeep * 24L * 60L * 60L * 1000L));
        return remoteControlMapper.deleteOldSessions(beforeDate,
                SecurityContext.get().getCurrentUser().get().getCustomerId());
    }

    /**
     * <p>Logs an audit entry.</p>
     *
     * @param sessionId the session ID.
     * @param deviceId the device ID.
     * @param action the action.
     * @param actionDetails the action details.
     * @return the number of inserted records.
     */
    @Transactional
    public int logAudit(Integer sessionId, Integer deviceId, String action, String actionDetails) {
        return remoteControlMapper.insertAuditLog(sessionId, deviceId,
                SecurityContext.get().getCurrentUser().get().getCustomerId(),
                SecurityContext.get().getCurrentUser().get().getId(),
                action, actionDetails);
    }

    /**
     * <p>Checks if there are any active sessions for a device.</p>
     *
     * @param deviceId the device ID.
     * @return true if there are active sessions.
     */
    public boolean hasActiveSessions(Integer deviceId) {
        List<RemoteControlSession> activeSessions = getActiveSessionsByDevice(deviceId);
        return activeSessions != null && !activeSessions.isEmpty();
    }
}
