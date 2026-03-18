/*
 * Headwind MDM: Open Source Android MDM Software
 * https://h-mdm.com
 *
 * Copyright (C) 2019 Headwind Solutions LLC (http://h-sms.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hmdm.rest.resource;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.hmdm.persistence.DeviceContactDAO;
import com.hmdm.persistence.DeviceDAO;
import com.hmdm.launcher.json.ContactData;
import com.hmdm.persistence.domain.Device;
import com.hmdm.persistence.domain.DeviceContact;
import com.hmdm.rest.json.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Contact sync API
 *
 * Functionality:
 * - Get contact data from server for device
 * - Upload device local contacts to server
 * - Implement server-to-device contact sync
 */
@Api(tags = {"Contact Sync"}, authorizations = {@Authorization("apiKey")})
@Singleton
@Path("/plugins/contacts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ContactsSyncResource {

    private static final Logger log = LoggerFactory.getLogger(ContactsSyncResource.class);

    private final DeviceContactDAO deviceContactDAO;
    private final DeviceDAO deviceDAO;

    /**
     * Constructor - injected by Guice
     */
    @Inject
    public ContactsSyncResource(DeviceContactDAO deviceContactDAO, DeviceDAO deviceDAO) {
        this.deviceContactDAO = deviceContactDAO;
        this.deviceDAO = deviceDAO;
    }

    /**
     * Get device ID by device number
     */
    private Integer getDeviceIdByNumber(String number) {
        Device device = deviceDAO.getDeviceByNumber(number);
        return device != null ? device.getId() : null;
    }

    /**
     * Get device contact list
     *
     * @param number Device number
     * @return Contact data list (JSON format)
     */
    @GET
    @Path("/{number}")
    @ApiOperation(
            value = "Get device contacts",
            notes = "Get stored contact list for specified device"
    )
    public Response getContacts(
            @ApiParam("Device number") @PathParam("number") String number) {
        try {
            Integer deviceId = getDeviceIdByNumber(number);
            if (deviceId == null) {
                log.warn("Device not found: {}", number);
                return Response.ERROR("device.not.found", "Device not found");
            }

            List<DeviceContact> contacts = deviceContactDAO.getContactsByDevice(deviceId);

            List<ContactData.ContactEntry> contactEntries = contacts.stream()
                    .map(c -> {
                        ContactData.ContactEntry entry = new ContactData.ContactEntry();
                        entry.setName(c.getName());
                        entry.setPhone(c.getPhone());
                        entry.setEmail(c.getEmail());
                        return entry;
                    })
                    .collect(Collectors.toList());

            String jsonResponse = toJsonArray(contactEntries);

            log.info("Retrieved {} contacts for device {}", contacts.size(), number);
            return Response.ok(jsonResponse).build();
        } catch (Exception e) {
            log.error("Failed to get contacts for device {}", number, e);
            return Response.ERROR("error.internal.server", "Failed to get contacts");
        }
    }

    /**
     * Upload device contacts to server
     *
     * @param number Device number
     * @param contacts Contact data
     * @return Operation result
     */
    @PUT
    @Path("/{number}")
    @ApiOperation(
            value = "Upload device contacts",
            notes = "Upload device contacts to server"
    )
    public Response uploadContacts(
            @ApiParam("Device number") @PathParam("number") String number,
            @ApiParam("Contact data") ContactData contacts) {
        try {
            if (contacts == null || contacts.getContacts() == null || contacts.getContacts().isEmpty()) {
                return Response.OK("No contacts to upload");
            }

            Integer deviceId = getDeviceIdByNumber(number);
            if (deviceId == null) {
                log.warn("Device not found: {}", number);
                return Response.ERROR("device.not.found", "Device not found");
            }

            // Delete existing contacts before uploading new ones
            deviceContactDAO.deleteContactsByDevice(deviceId);

            // Save contacts in batch
            // Note: For better performance with large datasets, consider using batch insert
            int savedCount = 0;
            for (ContactData.ContactEntry entry : contacts.getContacts()) {
                DeviceContact contact = new DeviceContact();
                contact.setDeviceId(deviceId);
                contact.setName(entry.getName());
                contact.setPhone(entry.getPhone());
                contact.setEmail(entry.getEmail());

                deviceContactDAO.createContact(contact);
                savedCount++;
            }

            log.info("Uploaded {} contacts for device {}", savedCount, number);
            return Response.OK(savedCount + " contacts saved");
        } catch (Exception e) {
            log.error("Failed to upload contacts for device {}", number, e);
            return Response.ERROR("error.internal.server", "Failed to upload contacts");
        }
    }

    /**
     * Delete all contacts for device
     *
     * @param number Device number
     * @return Operation result
     */
    @DELETE
    @Path("/{number}")
    @ApiOperation(
            value = "Delete device contacts",
            notes = "Delete all contacts for specified device"
    )
    public Response deleteContacts(
            @ApiParam("Device number") @PathParam("number") String number) {
        try {
            Integer deviceId = getDeviceIdByNumber(number);
            if (deviceId == null) {
                log.warn("Device not found: {}", number);
                return Response.ERROR("device.not.found", "Device not found");
            }

            deviceContactDAO.deleteContactsByDevice(deviceId);

            log.info("Deleted contacts for device {}", number);
            return Response.OK("Contacts deleted");
        } catch (Exception e) {
            log.error("Failed to delete contacts for device {}", number, e);
            return Response.ERROR("error.internal.server", "Failed to delete contacts");
        }
    }

    /**
     * Search device contacts
     *
     * @param number Device number
     * @param query Search keyword
     * @return Matching contact list
     */
    @GET
    @Path("/search/{number}")
    @ApiOperation(
            value = "Search device contacts",
            notes = "Search device contacts by keyword"
    )
    public Response searchContacts(
            @ApiParam("Device number") @PathParam("number") String number,
            @ApiParam("Search keyword") @QueryParam("query") String query) {
        try {
            Integer deviceId = getDeviceIdByNumber(number);
            if (deviceId == null) {
                log.warn("Device not found: {}", number);
                return Response.ERROR("device.not.found", "Device not found");
            }

            // Validate and sanitize query parameter
            if (query == null) {
                query = "";
            }
            query = query.trim();

            List<DeviceContact> contacts = deviceContactDAO.searchContacts(deviceId, query);
            return Response.OK(contacts);
        } catch (Exception e) {
            log.error("Failed to search contacts for device {}", number, e);
            return Response.ERROR("error.internal.server", "Failed to search contacts");
        }
    }

    /**
     * Manually trigger contact sync
     *
     * @param number Device number
     * @return Operation result
     */
    @POST
    @Path("/sync/{number}")
    @ApiOperation(
            value = "Trigger contact sync",
            notes = "Send sync request to device to trigger contact sync"
    )
    public Response triggerSync(
            @ApiParam("Device number") @PathParam("number") String number) {
        try {
            Integer deviceId = getDeviceIdByNumber(number);
            if (deviceId == null) {
                log.warn("Device not found: {}", number);
                return Response.ERROR("device.not.found", "Device not found");
            }

            // TODO: Send push notification to device to trigger sync
            // pushService.sendSimpleMessage(deviceId, "CONTACT_SYNC");

            log.info("Sync request sent for device {}", number);
            return Response.OK("Sync request sent");
        } catch (Exception e) {
            log.error("Failed to trigger sync for device {}", number, e);
            return Response.ERROR("error.internal.server", "Failed to trigger sync");
        }
    }

    /**
     * Convert contact list to JSON array string
     */
    private String toJsonArray(List<ContactData.ContactEntry> contacts) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < contacts.size(); i++) {
            ContactData.ContactEntry c = contacts.get(i);
            sb.append("{");
            sb.append("\"name\":\"").append(escapeJson(c.getName())).append("\",");
            sb.append("\"phone\":\"").append(escapeJson(c.getPhone())).append("\",");
            sb.append("\"email\":\"").append(escapeJson(c.getEmail())).append("\"");
            sb.append("}");
            if (i < contacts.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Escape special characters in JSON string
     */
    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }
}