/*
 * Headwind MDM: Open Source Android MDM Software
 */
package com.hmdm.persistence;

import com.google.inject.Inject;
import com.hmdm.persistence.domain.DeviceContact;
import com.hmdm.persistence.domain.DeviceContactPhone;
import com.hmdm.persistence.domain.DeviceContactEmail;
import com.hmdm.persistence.mapper.DeviceContactMapper;
import com.hmdm.security.SecurityContext;
import org.mybatis.guice.transactional.Transactional;
import java.util.List;

public class DeviceContactDAO {
    private final DeviceContactMapper mapper;

    @Inject
    public DeviceContactDAO(DeviceContactMapper mapper) {
        this.mapper = mapper;
    }

    @Transactional
    public DeviceContact createContact(DeviceContact contact) {
        contact.setCustomerId(SecurityContext.get().getCurrentUser().get().getCustomerId());
        mapper.insertContact(contact);
        // Insert phones
        if (contact.getPhones() != null) {
            for (DeviceContactPhone phone : contact.getPhones()) {
                phone.setContactId(contact.getId());
                mapper.insertContactPhone(phone);
            }
        }
        // Insert emails
        if (contact.getEmails() != null) {
            for (DeviceContactEmail email : contact.getEmails()) {
                email.setContactId(contact.getId());
                mapper.insertContactEmail(email);
            }
        }
        return contact;
    }

    public DeviceContact getContactById(Integer id) {
        DeviceContact contact = mapper.getContactById(id, SecurityContext.get().getCurrentUser().get().getCustomerId());
        if (contact != null) {
            contact.setPhones(mapper.getPhonesByContactId(id));
            contact.setEmails(mapper.getEmailsByContactId(id));
        }
        return contact;
    }

    public List<DeviceContact> getContactsByDevice(Integer deviceId) {
        List<DeviceContact> contacts = mapper.getContactsByDeviceId(deviceId, SecurityContext.get().getCurrentUser().get().getCustomerId());
        for (DeviceContact contact : contacts) {
            contact.setPhones(mapper.getPhonesByContactId(contact.getId()));
            contact.setEmails(mapper.getEmailsByContactId(contact.getId()));
        }
        return contacts;
    }

    public List<DeviceContact> searchContacts(Integer deviceId, String query) {
        return mapper.searchContacts(deviceId, SecurityContext.get().getCurrentUser().get().getCustomerId(), query);
    }

    @Transactional
    public int updateContact(DeviceContact contact) {
        contact.setCustomerId(SecurityContext.get().getCurrentUser().get().getCustomerId());
        int updated = mapper.updateContact(contact);
        // Update phones
        if (contact.getPhones() != null) {
            mapper.deletePhonesByContactId(contact.getId());
            for (DeviceContactPhone phone : contact.getPhones()) {
                phone.setContactId(contact.getId());
                mapper.insertContactPhone(phone);
            }
        }
        // Update emails
        if (contact.getEmails() != null) {
            mapper.deleteEmailsByContactId(contact.getId());
            for (DeviceContactEmail email : contact.getEmails()) {
                email.setContactId(contact.getId());
                mapper.insertContactEmail(email);
            }
        }
        return updated;
    }

    @Transactional
    public int deleteContact(Integer id) {
        mapper.deletePhonesByContactId(id);
        mapper.deleteEmailsByContactId(id);
        return mapper.deleteContact(id, SecurityContext.get().getCurrentUser().get().getCustomerId());
    }
}
