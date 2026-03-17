/*
 * Headwind MDM: Open Source Android MDM Software
 */
package com.hmdm.persistence.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceContactPhone implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String TYPE_HOME = "HOME";
    public static final String TYPE_WORK = "WORK";
    public static final String TYPE_MOBILE = "MOBILE";
    public static final String TYPE_OTHER = "OTHER";

    private Integer id;
    private Integer contactId;
    private String phoneType;
    private String phoneNumber;
    private String phoneNumberNormalized;
    private Boolean isPrimary;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getContactId() { return contactId; }
    public void setContactId(Integer contactId) { this.contactId = contactId; }

    public String getPhoneType() { return phoneType; }
    public void setPhoneType(String phoneType) { this.phoneType = phoneType; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getPhoneNumberNormalized() { return phoneNumberNormalized; }
    public void setPhoneNumberNormalized(String phoneNumberNormalized) { this.phoneNumberNormalized = phoneNumberNormalized; }

    public Boolean getIsPrimary() { return isPrimary; }
    public void setIsPrimary(Boolean isPrimary) { this.isPrimary = isPrimary; }
}
