/*
 * Headwind MDM: Open Source Android MDM Software
 */
package com.hmdm.persistence.mapper;

import com.hmdm.persistence.domain.DeviceContact;
import com.hmdm.persistence.domain.DeviceContactEmail;
import com.hmdm.persistence.domain.DeviceContactPhone;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface DeviceContactMapper {
    int insertContact(DeviceContact contact);
    DeviceContact getContactById(@Param("id") Integer id, @Param("customerId") Integer customerId);
    List<DeviceContact> getContactsByDeviceId(@Param("deviceId") Integer deviceId, @Param("customerId") Integer customerId);
    List<DeviceContact> searchContacts(@Param("deviceId") Integer deviceId, @Param("customerId") Integer customerId, @Param("query") String query);
    int updateContact(DeviceContact contact);
    int deleteContact(@Param("id") Integer id, @Param("customerId") Integer customerId);

    int insertContactPhone(DeviceContactPhone phone);
    List<DeviceContactPhone> getPhonesByContactId(@Param("contactId") Integer contactId);
    int deletePhonesByContactId(@Param("contactId") Integer contactId);

    int insertContactEmail(DeviceContactEmail email);
    List<DeviceContactEmail> getEmailsByContactId(@Param("contactId") Integer contactId);
    int deleteEmailsByContactId(@Param("contactId") Integer contactId);
}
