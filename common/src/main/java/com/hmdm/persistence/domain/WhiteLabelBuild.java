/*
 * Headwind MDM: Open Source Android MDM Software
 */
package com.hmdm.persistence.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(description = "A white-label APK build configuration")
@JsonIgnoreProperties(ignoreUnknown = true)
public class WhiteLabelBuild implements CustomerData, Serializable {
    private static final long serialVersionUID = 1L;

    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_BUILDING = "building";
    public static final String STATUS_COMPLETED = "completed";
    public static final String STATUS_FAILED = "failed";

    @ApiModelProperty("Build ID")
    private Integer id;

    @ApiModelProperty(hidden = true)
    private int customerId;

    @ApiModelProperty("Build name")
    private String buildName;

    @ApiModelProperty("Application name")
    private String appName;

    @ApiModelProperty("Package name")
    private String packageName;

    @ApiModelProperty("Version code")
    private Integer versionCode;

    @ApiModelProperty("Version name")
    private String versionName;

    @ApiModelProperty("Icon file path")
    private String iconPath;

    @ApiModelProperty("Splash screen path")
    private String splashScreenPath;

    @ApiModelProperty("Custom strings (JSON)")
    private String customStrings;

    @ApiModelProperty("Custom colors (JSON)")
    private String customColors;

    @ApiModelProperty("Server URL")
    private String serverUrl;

    @ApiModelProperty("Build status")
    private String buildStatus;

    @ApiModelProperty("Build log")
    private String buildLog;

    @ApiModelProperty("Download URL")
    private String downloadUrl;

    @ApiModelProperty("Creation time")
    private Long createdAt;

    @ApiModelProperty("Last update time")
    private Long updatedAt;

    @ApiModelProperty("Build completion time")
    private Long builtAt;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    @Override
    public int getCustomerId() { return customerId; }
    @Override
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getBuildName() { return buildName; }
    public void setBuildName(String buildName) { this.buildName = buildName; }

    public String getAppName() { return appName; }
    public void setAppName(String appName) { this.appName = appName; }

    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }

    public Integer getVersionCode() { return versionCode; }
    public void setVersionCode(Integer versionCode) { this.versionCode = versionCode; }

    public String getVersionName() { return versionName; }
    public void setVersionName(String versionName) { this.versionName = versionName; }

    public String getIconPath() { return iconPath; }
    public void setIconPath(String iconPath) { this.iconPath = iconPath; }

    public String getSplashScreenPath() { return splashScreenPath; }
    public void setSplashScreenPath(String splashScreenPath) { this.splashScreenPath = splashScreenPath; }

    public String getCustomStrings() { return customStrings; }
    public void setCustomStrings(String customStrings) { this.customStrings = customStrings; }

    public String getCustomColors() { return customColors; }
    public void setCustomColors(String customColors) { this.customColors = customColors; }

    public String getServerUrl() { return serverUrl; }
    public void setServerUrl(String serverUrl) { this.serverUrl = serverUrl; }

    public String getBuildStatus() { return buildStatus; }
    public void setBuildStatus(String buildStatus) { this.buildStatus = buildStatus; }

    public String getBuildLog() { return buildLog; }
    public void setBuildLog(String buildLog) { this.buildLog = buildLog; }

    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }

    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }

    public Long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Long updatedAt) { this.updatedAt = updatedAt; }

    public Long getBuiltAt() { return builtAt; }
    public void setBuiltAt(Long builtAt) { this.builtAt = builtAt; }
}
