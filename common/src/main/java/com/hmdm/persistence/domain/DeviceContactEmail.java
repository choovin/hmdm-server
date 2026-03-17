/*
 * Headwind MDM: Open Source Android MDM Software
 */
package com.hmdm.persistence.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceContactEmail implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String TYPE_HOME = "HOME";
    public static final String TYPE_WORK = "WORK";
    public static final String TYPE_OTHER = "OTHER";

    private Integer id;
    private Integer contactId;
    private String emailType;
    private String email;
    private Boolean isPrimary;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getContactId() { return contactId; }
    public void setContactId(Integer contactId) { this.contactId = contactId; }

    public String getEmailType() { return emailType; }
    public void setEmailType(String emailType) { this.emailType = emailType; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Boolean getIsPrimary() { return isPrimary; }
    public void setIsPrimary(Boolean isPrimary) { this.isPrimary = isPrimary; }
}
