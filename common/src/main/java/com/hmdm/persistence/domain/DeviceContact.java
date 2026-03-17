/*
 * Headwind MDM: Open Source Android MDM Software
 */
package com.hmdm.persistence.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceContact implements Serializable, CustomerData {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer deviceId;
    private int customerId;
    private String deviceNumber;
    private String contactId;
    private String displayName;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String phoneNumberNormalized;
    private String email;
    private String company;
    private String jobTitle;
    private String department;
    private String notes;
    private String photoUri;
    private Boolean isManaged;
    private Boolean isSynced;
    private Date createdAt;
    private Date updatedAt;
    private Date lastSyncedAt;

    // Related data
    private List<DeviceContactPhone> phones;
    private List<DeviceContactEmail> emails;

    // Getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getDeviceId() { return deviceId; }
    public void setDeviceId(Integer deviceId) { this.deviceId = deviceId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getDeviceNumber() { return deviceNumber; }
    public void setDeviceNumber(String deviceNumber) { this.deviceNumber = deviceNumber; }

    public String getContactId() { return contactId; }
    public void setContactId(String contactId) { this.contactId = contactId; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getPhoneNumberNormalized() { return phoneNumberNormalized; }
    public void setPhoneNumberNormalized(String phoneNumberNormalized) { this.phoneNumberNormalized = phoneNumberNormalized; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getPhotoUri() { return photoUri; }
    public void setPhotoUri(String photoUri) { this.photoUri = photoUri; }

    public Boolean getIsManaged() { return isManaged; }
    public void setIsManaged(Boolean isManaged) { this.isManaged = isManaged; }

    public Boolean getIsSynced() { return isSynced; }
    public void setIsSynced(Boolean isSynced) { this.isSynced = isSynced; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public Date getLastSyncedAt() { return lastSyncedAt; }
    public void setLastSyncedAt(Date lastSyncedAt) { this.lastSyncedAt = lastSyncedAt; }

    public List<DeviceContactPhone> getPhones() { return phones; }
    public void setPhones(List<DeviceContactPhone> phones) { this.phones = phones; }

    public List<DeviceContactEmail> getEmails() { return emails; }
    public void setEmails(List<DeviceContactEmail> emails) { this.emails = emails; }

    public String getFullName() {
        if (displayName != null && !displayName.isEmpty()) {
            return displayName;
        }
        StringBuilder sb = new StringBuilder();
        if (firstName != null) sb.append(firstName);
        if (lastName != null) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(lastName);
        }
        return sb.toString();
    }
}
