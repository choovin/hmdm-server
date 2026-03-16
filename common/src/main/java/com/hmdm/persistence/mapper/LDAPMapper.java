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

import com.hmdm.persistence.domain.LDAPConfiguration;
import com.hmdm.persistence.domain.LDAPGroupMapping;
import com.hmdm.persistence.domain.LDAPSyncLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>Mapper interface for LDAP integration.</p>
 */
public interface LDAPMapper {

    // ==================== LDAP Configuration ====================

    int insertLDAPConfiguration(LDAPConfiguration config);

    LDAPConfiguration getLDAPConfigurationById(@Param("id") Integer id,
                                               @Param("customerId") Integer customerId);

    LDAPConfiguration getLDAPConfigurationByCustomerId(@Param("customerId") Integer customerId);

    int updateLDAPConfiguration(LDAPConfiguration config);

    int updateLastSyncTime(@Param("id") Integer id,
                           @Param("customerId") Integer customerId);

    int deleteLDAPConfiguration(@Param("id") Integer id,
                                @Param("customerId") Integer customerId);

    // ==================== LDAP Group Mappings ====================

    int insertGroupMapping(LDAPGroupMapping mapping);

    LDAPGroupMapping getGroupMappingById(@Param("id") Integer id);

    List<LDAPGroupMapping> getGroupMappingsByConfigId(@Param("ldapConfigId") Integer ldapConfigId);

    int updateGroupMapping(LDAPGroupMapping mapping);

    int deleteGroupMapping(@Param("id") Integer id);

    int deleteGroupMappingsByConfigId(@Param("ldapConfigId") Integer ldapConfigId);

    // ==================== LDAP Sync Logs ====================

    int insertSyncLog(LDAPSyncLog log);

    int updateSyncLog(LDAPSyncLog log);

    LDAPSyncLog getSyncLogById(@Param("id") Integer id,
                               @Param("customerId") Integer customerId);

    List<LDAPSyncLog> getSyncLogsByCustomerId(@Param("customerId") Integer customerId);

    List<LDAPSyncLog> getRecentSyncLogsByCustomerId(@Param("customerId") Integer customerId,
                                                     @Param("limit") Integer limit);

    // ==================== User LDAP Fields ====================

    int updateUserLdapFields(@Param("userId") Integer userId,
                             @Param("ldapUser") Boolean ldapUser,
                             @Param("ldapDn") String ldapDn);

    int markUserAsLdap(@Param("userId") Integer userId,
                       @Param("ldapDn") String ldapDn);
}
