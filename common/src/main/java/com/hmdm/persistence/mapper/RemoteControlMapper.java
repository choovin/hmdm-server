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

package com.hmdm.persistence.mapper;

import com.hmdm.persistence.domain.RemoteControlSession;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>Mapper interface for remote control sessions.</p>
 */
public interface RemoteControlMapper {

    /**
     * <p>Inserts a new remote control session record.</p>
     *
     * @param session the session to insert.
     * @return the number of inserted records.
     */
    int insertRemoteControlSession(RemoteControlSession session);

    /**
     * <p>Gets a remote control session by ID.</p>
     *
     * @param id the session ID.
     * @param customerId the customer ID for security check.
     * @return the session or null if not found.
     */
    RemoteControlSession getRemoteControlSessionById(@Param("id") Integer id,
                                                      @Param("customerId") Integer customerId);

    /**
     * <p>Gets a remote control session by token.</p>
     *
     * @param sessionToken the session token.
     * @param customerId the customer ID for security check.
     * @return the session or null if not found.
     */
    RemoteControlSession getRemoteControlSessionByToken(@Param("sessionToken") String sessionToken,
                                                         @Param("customerId") Integer customerId);

    /**
     * <p>Gets active sessions for a device.</p>
     *
     * @param deviceId the device ID.
     * @param customerId the customer ID for security check.
     * @return list of active sessions.
     */
    List<RemoteControlSession> getActiveSessionsByDeviceId(@Param("deviceId") Integer deviceId,
                                                            @Param("customerId") Integer customerId);

    /**
     * <p>Gets all remote control sessions for a device.</p>
     *
     * @param deviceId the device ID.
     * @param customerId the customer ID for security check.
     * @return list of sessions.
     */
    List<RemoteControlSession> getRemoteControlSessionsByDeviceId(@Param("deviceId") Integer deviceId,
                                                                   @Param("customerId") Integer customerId);

    /**
     * <p>Gets all remote control sessions for a customer.</p>
     *
     * @param customerId the customer ID.
     * @return list of sessions.
     */
    List<RemoteControlSession> getRemoteControlSessionsByCustomerId(@Param("customerId") Integer customerId);

    /**
     * <p>Updates a remote control session.</p>
     *
     * @param session the session to update.
     * @return the number of updated records.
     */
    int updateRemoteControlSession(RemoteControlSession session);

    /**
     * <p>Updates session status.</p>
     *
     * @param id the session ID.
     * @param status the new status.
     * @param customerId the customer ID for security check.
     * @return the number of updated records.
     */
    int updateSessionStatus(@Param("id") Integer id,
                            @Param("status") String status,
                            @Param("customerId") Integer customerId);

    /**
     * <p>Updates session WebRTC data.</p>
     *
     * @param id the session ID.
     * @param webrtcOffer the WebRTC offer.
     * @param webrtcAnswer the WebRTC answer.
     * @param iceCandidates the ICE candidates.
     * @param customerId the customer ID for security check.
     * @return the number of updated records.
     */
    int updateSessionWebRTCData(@Param("id") Integer id,
                                 @Param("webrtcOffer") String webrtcOffer,
                                 @Param("webrtcAnswer") String webrtcAnswer,
                                 @Param("iceCandidates") String iceCandidates,
                                 @Param("customerId") Integer customerId);

    /**
     * <p>Marks session as connected.</p>
     *
     * @param id the session ID.
     * @param connectedAt the connection timestamp.
     * @param remoteAddress the remote address.
     * @param customerId the customer ID for security check.
     * @return the number of updated records.
     */
    int markSessionConnected(@Param("id") Integer id,
                              @Param("connectedAt") Date connectedAt,
                              @Param("remoteAddress") String remoteAddress,
                              @Param("customerId") Integer customerId);

    /**
     * <p>Marks session as disconnected.</p>
     *
     * @param id the session ID.
     * @param endedAt the end timestamp.
     * @param durationSeconds the session duration.
     * @param customerId the customer ID for security check.
     * @return the number of updated records.
     */
    int markSessionDisconnected(@Param("id") Integer id,
                                 @Param("endedAt") Date endedAt,
                                 @Param("durationSeconds") Integer durationSeconds,
                                 @Param("customerId") Integer customerId);

    /**
     * <p>Marks session as error.</p>
     *
     * @param id the session ID.
     * @param errorMessage the error message.
     * @param endedAt the end timestamp.
     * @param customerId the customer ID for security check.
     * @return the number of updated records.
     */
    int markSessionError(@Param("id") Integer id,
                          @Param("errorMessage") String errorMessage,
                          @Param("endedAt") Date endedAt,
                          @Param("customerId") Integer customerId);

    /**
     * <p>Deletes a remote control session.</p>
     *
     * @param id the session ID.
     * @param customerId the customer ID for security check.
     * @return the number of deleted records.
     */
    int deleteRemoteControlSession(@Param("id") Integer id,
                                    @Param("customerId") Integer customerId);

    /**
     * <p>Deletes old completed sessions.</p>
     *
     * @param beforeDate the date before which to delete.
     * @param customerId the customer ID for security check.
     * @return the number of deleted records.
     */
    int deleteOldSessions(@Param("beforeDate") Date beforeDate,
                          @Param("customerId") Integer customerId);

    /**
     * <p>Inserts an audit log entry.</p>
     *
     * @param sessionId the session ID.
     * @param deviceId the device ID.
     * @param customerId the customer ID.
     * @param userId the user ID.
     * @param action the action.
     * @param actionDetails the action details.
     * @return the number of inserted records.
     */
    int insertAuditLog(@Param("sessionId") Integer sessionId,
                       @Param("deviceId") Integer deviceId,
                       @Param("customerId") Integer customerId,
                       @Param("userId") Integer userId,
                       @Param("action") String action,
                       @Param("actionDetails") String actionDetails);
}
