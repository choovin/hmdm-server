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

import com.hmdm.persistence.domain.DeviceNetworkSettings;
import com.hmdm.persistence.domain.NetworkTrafficLog;
import com.hmdm.persistence.domain.NetworkTrafficRule;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>Mapper interface for network traffic filtering.</p>
 */
public interface NetworkTrafficMapper {

    // ==================== Traffic Rules ====================

    int insertTrafficRule(NetworkTrafficRule rule);

    NetworkTrafficRule getTrafficRuleById(@Param("id") Integer id,
                                          @Param("customerId") Integer customerId);

    List<NetworkTrafficRule> getTrafficRulesByCustomerId(@Param("customerId") Integer customerId);

    List<NetworkTrafficRule> getTrafficRulesByConfigurationId(@Param("configurationId") Integer configurationId,
                                                               @Param("customerId") Integer customerId);

    List<NetworkTrafficRule> getEnabledTrafficRulesByConfigurationId(@Param("configurationId") Integer configurationId,
                                                                      @Param("customerId") Integer customerId);

    int updateTrafficRule(NetworkTrafficRule rule);

    int updateTrafficRuleEnabled(@Param("id") Integer id,
                                 @Param("enabled") Boolean enabled,
                                 @Param("customerId") Integer customerId);

    int deleteTrafficRule(@Param("id") Integer id,
                          @Param("customerId") Integer customerId);

    int deleteOldTrafficRules(@Param("beforeDate") Date beforeDate,
                              @Param("customerId") Integer customerId);

    // ==================== Traffic Logs ====================

    int insertTrafficLog(NetworkTrafficLog log);

    NetworkTrafficLog getTrafficLogById(@Param("id") Integer id,
                                        @Param("customerId") Integer customerId);

    List<NetworkTrafficLog> getTrafficLogsByDeviceId(@Param("deviceId") Integer deviceId,
                                                      @Param("customerId") Integer customerId);

    List<NetworkTrafficLog> getTrafficLogsByCustomerId(@Param("customerId") Integer customerId);

    List<NetworkTrafficLog> getTrafficLogsByFilter(@Param("deviceId") Integer deviceId,
                                                    @Param("customerId") Integer customerId,
                                                    @Param("action") String action,
                                                    @Param("domain") String domain,
                                                    @Param("startDate") Date startDate,
                                                    @Param("endDate") Date endDate);

    int deleteOldTrafficLogs(@Param("beforeDate") Date beforeDate,
                             @Param("customerId") Integer customerId);

    // ==================== Statistics ====================

    Long getBlockedTrafficCountByDevice(@Param("deviceId") Integer deviceId,
                                        @Param("customerId") Integer customerId);

    Long getTotalTrafficCountByDevice(@Param("deviceId") Integer deviceId,
                                      @Param("customerId") Integer customerId);

    Long getBlockedTrafficBytesByDevice(@Param("deviceId") Integer deviceId,
                                        @Param("customerId") Integer customerId);

    // ==================== Device Network Settings ====================

    int insertDeviceNetworkSettings(DeviceNetworkSettings settings);

    DeviceNetworkSettings getDeviceNetworkSettings(@Param("deviceId") Integer deviceId,
                                                   @Param("customerId") Integer customerId);

    int updateDeviceNetworkSettings(DeviceNetworkSettings settings);

    int deleteDeviceNetworkSettings(@Param("deviceId") Integer deviceId,
                                    @Param("customerId") Integer customerId);
}
