/*
 * Headwind MDM: Open Source Android MDM Software
 */
package com.hmdm.persistence.mapper;

import com.hmdm.persistence.domain.EmailTemplate;
import com.hmdm.persistence.domain.RebrandingSettings;
import com.hmdm.persistence.domain.WhiteLabelBuild;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface WhiteLabelMapper {
    // Rebranding Settings
    RebrandingSettings getRebrandingSettings(@Param("customerId") Integer customerId);
    int updateRebrandingSettings(RebrandingSettings settings);
    int insertRebrandingSettings(RebrandingSettings settings);

    // White Label Builds
    List<WhiteLabelBuild> getAllBuilds(@Param("customerId") Integer customerId);
    WhiteLabelBuild getBuildById(@Param("id") Integer id, @Param("customerId") Integer customerId);
    WhiteLabelBuild getBuildByPackageName(@Param("packageName") String packageName, @Param("customerId") Integer customerId);
    int insertBuild(WhiteLabelBuild build);
    int updateBuild(WhiteLabelBuild build);
    int updateBuildStatus(WhiteLabelBuild build);
    int deleteBuild(@Param("id") Integer id, @Param("customerId") Integer customerId);

    // Email Templates
    List<EmailTemplate> getAllEmailTemplates(@Param("customerId") Integer customerId);
    EmailTemplate getEmailTemplate(@Param("customerId") Integer customerId, @Param("templateType") String templateType);
    int insertEmailTemplate(EmailTemplate template);
    int updateEmailTemplate(EmailTemplate template);
    int deleteEmailTemplate(@Param("id") Integer id, @Param("customerId") Integer customerId);
}
