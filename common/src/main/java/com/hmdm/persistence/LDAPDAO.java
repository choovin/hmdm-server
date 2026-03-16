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
import com.hmdm.persistence.domain.LDAPConfiguration;
import com.hmdm.persistence.domain.LDAPGroupMapping;
import com.hmdm.persistence.domain.LDAPSyncLog;
import com.hmdm.persistence.mapper.LDAPMapper;
import com.hmdm.security.SecurityContext;
import org.mybatis.guice.transactional.Transactional;

import java.util.Date;
import java.util.List;

/**
 * <p>A DAO for managing LDAP integration.</p>
 *
 * @author Igor Pikin (ipikin@h-mdm.com)
 */
public class LDAPDAO {

    private final LDAPMapper ldapMapper;

    @Inject
    public LDAPDAO(LDAPMapper ldapMapper) {
        this.ldapMapper = ldapMapper;
    }

    // ==================== LDAP Configuration ====================

    @Transactional
    public LDAPConfiguration createLDAPConfiguration(LDAPConfiguration config) {
        config.setCustomerId(SecurityContext.get().getCurrentUser().get().getCustomerId());
        ldapMapper.insertLDAPConfiguration(config);
        return config;
    }

    public LDAPConfiguration getLDAPConfigurationById(Integer id) {
        return ldapMapper.getLDAPConfigurationById(id,
                SecurityContext.get().getCurrentUser().get().getCustomerId());
    }

    public LDAPConfiguration getLDAPConfigurationByCustomer() {
        return ldapMapper.getLDAPConfigurationByCustomerId(
                SecurityContext.get().getCurrentUser().get().getCustomerId());
    }

    @Transactional
    public int updateLDAPConfiguration(LDAPConfiguration config) {
        config.setCustomerId(SecurityContext.get().getCurrentUser().get().getCustomerId());
        return ldapMapper.updateLDAPConfiguration(config);
    }

    @Transactional
    public int updateLastSyncTime(Integer id) {
        return ldapMapper.updateLastSyncTime(id,
                SecurityContext.get().getCurrentUser().get().getCustomerId());
    }

    @Transactional
    public int deleteLDAPConfiguration(Integer id) {
        // Delete group mappings first
        ldapMapper.deleteGroupMappingsByConfigId(id);
        return ldapMapper.deleteLDAPConfiguration(id,
                SecurityContext.get().getCurrentUser().get().getCustomerId());
    }

    // ==================== LDAP Group Mappings ====================

    @Transactional
    public LDAPGroupMapping createGroupMapping(LDAPGroupMapping mapping) {
        ldapMapper.insertGroupMapping(mapping);
        return mapping;
    }

    public LDAPGroupMapping getGroupMappingById(Integer id) {
        return ldapMapper.getGroupMappingById(id);
    }

    public List<LDAPGroupMapping> getGroupMappingsByConfigId(Integer ldapConfigId) {
        return ldapMapper.getGroupMappingsByConfigId(ldapConfigId);
    }

    @Transactional
    public int updateGroupMapping(LDAPGroupMapping mapping) {
        return ldapMapper.updateGroupMapping(mapping);
    }

    @Transactional
    public int deleteGroupMapping(Integer id) {
        return ldapMapper.deleteGroupMapping(id);
    }

    @Transactional
    public int deleteGroupMappingsByConfigId(Integer ldapConfigId) {
        return ldapMapper.deleteGroupMappingsByConfigId(ldapConfigId);
    }

    // ==================== LDAP Sync Logs ====================

    @Transactional
    public LDAPSyncLog createSyncLog(LDAPSyncLog log) {
        log.setCustomerId(SecurityContext.get().getCurrentUser().get().getCustomerId());
        ldapMapper.insertSyncLog(log);
        return log;
    }

    @Transactional
    public int updateSyncLog(LDAPSyncLog log) {
        if (log.getCompletedAt() == null) {
            log.setCompletedAt(new Date());
        }
        return ldapMapper.updateSyncLog(log);
    }

    public LDAPSyncLog getSyncLogById(Integer id) {
        return ldapMapper.getSyncLogById(id,
                SecurityContext.get().getCurrentUser().get().getCustomerId());
    }

    public List<LDAPSyncLog> getSyncLogsByCustomer() {
        return ldapMapper.getSyncLogsByCustomerId(
                SecurityContext.get().getCurrentUser().get().getCustomerId());
    }

    public List<LDAPSyncLog> getRecentSyncLogs(Integer limit) {
        if (limit == null || limit < 1) {
            limit = 10;
        }
        return ldapMapper.getRecentSyncLogsByCustomerId(
                SecurityContext.get().getCurrentUser().get().getCustomerId(), limit);
    }

    // ==================== User LDAP Fields ====================

    @Transactional
    public int updateUserLdapFields(Integer userId, Boolean ldapUser, String ldapDn) {
        return ldapMapper.updateUserLdapFields(userId, ldapUser, ldapDn);
    }

    @Transactional
    public int markUserAsLdap(Integer userId, String ldapDn) {
        return ldapMapper.markUserAsLdap(userId, ldapDn);
    }

    // ==================== Helper Methods ====================

    @Transactional
    public LDAPConfiguration getOrCreateLDAPConfiguration() {
        LDAPConfiguration config = getLDAPConfigurationByCustomer();
        if (config == null) {
            config = new LDAPConfiguration();
            config.setEnabled(false);
            config.setServerPort(389);
            config.setUseSsl(false);
            config.setUseTls(false);
            config.setUserSearchFilter("(uid={0})");
            config.setUserNameAttribute("uid");
            config.setUserEmailAttribute("mail");
            config.setUserFirstNameAttribute("givenName");
            config.setUserLastNameAttribute("sn");
            config.setGroupNameAttribute("cn");
            config.setAutoCreateUsers(true);
            config.setConnectionTimeout(5000);
            config.setReadTimeout(10000);
            config.setSyncEnabled(false);
            config = createLDAPConfiguration(config);
        }
        return config;
    }
}
