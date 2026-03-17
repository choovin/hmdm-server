/*
 * Headwind MDM: Open Source Android MDM Software
 */
package com.hmdm.persistence.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(description = "An email template customization")
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailTemplate implements CustomerData, Serializable {
    private static final long serialVersionUID = 1L;

    public static final String TYPE_PASSWORD_RESET = "password_reset";
    public static final String TYPE_SIGNUP_CONFIRMATION = "signup_confirmation";
    public static final String TYPE_DEVICE_ENROLLMENT = "device_enrollment";
    public static final String TYPE_ALERT_NOTIFICATION = "alert_notification";

    @ApiModelProperty("Template ID")
    private Integer id;

    @ApiModelProperty(hidden = true)
    private int customerId;

    @ApiModelProperty("Template type")
    private String templateType;

    @ApiModelProperty("Email subject")
    private String subject;

    @ApiModelProperty("HTML body")
    private String bodyHtml;

    @ApiModelProperty("Plain text body")
    private String bodyText;

    @ApiModelProperty("Is custom template")
    private Boolean isCustom;

    @ApiModelProperty("Creation time")
    private Long createdAt;

    @ApiModelProperty("Last update time")
    private Long updatedAt;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    @Override
    public int getCustomerId() { return customerId; }
    @Override
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getTemplateType() { return templateType; }
    public void setTemplateType(String templateType) { this.templateType = templateType; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getBodyHtml() { return bodyHtml; }
    public void setBodyHtml(String bodyHtml) { this.bodyHtml = bodyHtml; }

    public String getBodyText() { return bodyText; }
    public void setBodyText(String bodyText) { this.bodyText = bodyText; }

    public Boolean getIsCustom() { return isCustom; }
    public void setIsCustom(Boolean isCustom) { this.isCustom = isCustom; }

    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }

    public Long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Long updatedAt) { this.updatedAt = updatedAt; }
}
