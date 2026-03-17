/*
 * Headwind MDM: Open Source Android MDM Software
 */
package com.hmdm.persistence.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(description = "Rebranding settings for a customer")
@JsonIgnoreProperties(ignoreUnknown = true)
public class RebrandingSettings implements CustomerData, Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("Customer ID")
    private int customerId;

    @ApiModelProperty("Custom logo URL")
    private String customLogo;

    @ApiModelProperty("Custom favicon URL")
    private String customFavicon;

    @ApiModelProperty("Custom CSS")
    private String customCss;

    @ApiModelProperty("Primary color (hex)")
    private String primaryColor;

    @ApiModelProperty("Secondary color (hex)")
    private String secondaryColor;

    @ApiModelProperty("Accent color (hex)")
    private String accentColor;

    @ApiModelProperty("Custom email header HTML")
    private String customEmailHeader;

    @ApiModelProperty("Custom email footer HTML")
    private String customEmailFooter;

    @ApiModelProperty("Custom email subject prefix")
    private String customEmailSubjectPrefix;

    @ApiModelProperty("Custom login page HTML")
    private String customLoginPageHtml;

    @ApiModelProperty("Hide powered by text")
    private Boolean hidePoweredBy;

    @ApiModelProperty("Custom copyright text")
    private String customCopyrightText;

    @ApiModelProperty("Enable custom domain")
    private Boolean enableCustomDomain;

    @ApiModelProperty("Custom domain")
    private String customDomain;

    @Override
    public Integer getId() { return null; }

    @Override
    public int getCustomerId() { return customerId; }
    @Override
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getCustomLogo() { return customLogo; }
    public void setCustomLogo(String customLogo) { this.customLogo = customLogo; }

    public String getCustomFavicon() { return customFavicon; }
    public void setCustomFavicon(String customFavicon) { this.customFavicon = customFavicon; }

    public String getCustomCss() { return customCss; }
    public void setCustomCss(String customCss) { this.customCss = customCss; }

    public String getPrimaryColor() { return primaryColor; }
    public void setPrimaryColor(String primaryColor) { this.primaryColor = primaryColor; }

    public String getSecondaryColor() { return secondaryColor; }
    public void setSecondaryColor(String secondaryColor) { this.secondaryColor = secondaryColor; }

    public String getAccentColor() { return accentColor; }
    public void setAccentColor(String accentColor) { this.accentColor = accentColor; }

    public String getCustomEmailHeader() { return customEmailHeader; }
    public void setCustomEmailHeader(String customEmailHeader) { this.customEmailHeader = customEmailHeader; }

    public String getCustomEmailFooter() { return customEmailFooter; }
    public void setCustomEmailFooter(String customEmailFooter) { this.customEmailFooter = customEmailFooter; }

    public String getCustomEmailSubjectPrefix() { return customEmailSubjectPrefix; }
    public void setCustomEmailSubjectPrefix(String customEmailSubjectPrefix) { this.customEmailSubjectPrefix = customEmailSubjectPrefix; }

    public String getCustomLoginPageHtml() { return customLoginPageHtml; }
    public void setCustomLoginPageHtml(String customLoginPageHtml) { this.customLoginPageHtml = customLoginPageHtml; }

    public Boolean getHidePoweredBy() { return hidePoweredBy; }
    public void setHidePoweredBy(Boolean hidePoweredBy) { this.hidePoweredBy = hidePoweredBy; }

    public String getCustomCopyrightText() { return customCopyrightText; }
    public void setCustomCopyrightText(String customCopyrightText) { this.customCopyrightText = customCopyrightText; }

    public Boolean getEnableCustomDomain() { return enableCustomDomain; }
    public void setEnableCustomDomain(Boolean enableCustomDomain) { this.enableCustomDomain = enableCustomDomain; }

    public String getCustomDomain() { return customDomain; }
    public void setCustomDomain(String customDomain) { this.customDomain = customDomain; }
}
