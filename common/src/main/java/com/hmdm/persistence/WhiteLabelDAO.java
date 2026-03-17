/*
 * Headwind MDM: Open Source Android MDM Software
 */
package com.hmdm.persistence;

import com.google.inject.Inject;
import com.hmdm.persistence.domain.EmailTemplate;
import com.hmdm.persistence.domain.RebrandingSettings;
import com.hmdm.persistence.domain.WhiteLabelBuild;
import com.hmdm.persistence.mapper.WhiteLabelMapper;
import com.hmdm.security.SecurityContext;
import org.mybatis.guice.transactional.Transactional;

import java.util.List;

public class WhiteLabelDAO {
    private final WhiteLabelMapper mapper;

    @Inject
    public WhiteLabelDAO(WhiteLabelMapper mapper) {
        this.mapper = mapper;
    }

    // Rebranding Settings
    public RebrandingSettings getRebrandingSettings() {
        return mapper.getRebrandingSettings(SecurityContext.get().getCurrentUser().get().getCustomerId());
    }

    @Transactional
    public int saveRebrandingSettings(RebrandingSettings settings) {
        settings.setCustomerId(SecurityContext.get().getCurrentUser().get().getCustomerId());
        RebrandingSettings existing = mapper.getRebrandingSettings(settings.getCustomerId());
        if (existing != null) {
            return mapper.updateRebrandingSettings(settings);
        } else {
            return mapper.insertRebrandingSettings(settings);
        }
    }

    // White Label Builds
    public List<WhiteLabelBuild> getAllBuilds() {
        return mapper.getAllBuilds(SecurityContext.get().getCurrentUser().get().getCustomerId());
    }

    public WhiteLabelBuild getBuildById(Integer id) {
        return mapper.getBuildById(id, SecurityContext.get().getCurrentUser().get().getCustomerId());
    }

    public WhiteLabelBuild getBuildByPackageName(String packageName) {
        return mapper.getBuildByPackageName(packageName, SecurityContext.get().getCurrentUser().get().getCustomerId());
    }

    @Transactional
    public WhiteLabelBuild createBuild(WhiteLabelBuild build) {
        build.setCustomerId(SecurityContext.get().getCurrentUser().get().getCustomerId());
        build.setBuildStatus(WhiteLabelBuild.STATUS_PENDING);
        mapper.insertBuild(build);
        return build;
    }

    @Transactional
    public int updateBuild(WhiteLabelBuild build) {
        build.setCustomerId(SecurityContext.get().getCurrentUser().get().getCustomerId());
        return mapper.updateBuild(build);
    }

    @Transactional
    public int updateBuildStatus(WhiteLabelBuild build) {
        build.setCustomerId(SecurityContext.get().getCurrentUser().get().getCustomerId());
        return mapper.updateBuildStatus(build);
    }

    @Transactional
    public int deleteBuild(Integer id) {
        return mapper.deleteBuild(id, SecurityContext.get().getCurrentUser().get().getCustomerId());
    }

    // Email Templates
    public List<EmailTemplate> getAllEmailTemplates() {
        return mapper.getAllEmailTemplates(SecurityContext.get().getCurrentUser().get().getCustomerId());
    }

    public EmailTemplate getEmailTemplate(String templateType) {
        return mapper.getEmailTemplate(SecurityContext.get().getCurrentUser().get().getCustomerId(), templateType);
    }

    @Transactional
    public EmailTemplate saveEmailTemplate(EmailTemplate template) {
        template.setCustomerId(SecurityContext.get().getCurrentUser().get().getCustomerId());
        template.setIsCustom(true);
        if (template.getId() != null) {
            mapper.updateEmailTemplate(template);
        } else {
            mapper.insertEmailTemplate(template);
        }
        return template;
    }

    @Transactional
    public int deleteEmailTemplate(Integer id) {
        return mapper.deleteEmailTemplate(id, SecurityContext.get().getCurrentUser().get().getCustomerId());
    }
}
