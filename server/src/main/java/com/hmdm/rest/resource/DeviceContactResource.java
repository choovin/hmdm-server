/*
 * Headwind MDM: Open Source Android MDM Software
 */
package com.hmdm.rest.resource;

import com.google.inject.Inject;
import com.hmdm.notification.PushService;
import com.hmdm.persistence.DeviceContactDAO;
import com.hmdm.persistence.DeviceDAO;
import com.hmdm.persistence.domain.Device;
import com.hmdm.persistence.domain.DeviceContact;
import com.hmdm.rest.json.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.List;

@Api(tags = {"Device Contacts"}, authorizations = {@Authorization("apiKey")})
@Path("/private/device-contacts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DeviceContactResource {

    private final DeviceContactDAO deviceContactDAO;
    private final DeviceDAO deviceDAO;
    private final PushService pushService;

    @Inject
    public DeviceContactResource(DeviceContactDAO deviceContactDAO, DeviceDAO deviceDAO, PushService pushService) {
        this.deviceContactDAO = deviceContactDAO;
        this.deviceDAO = deviceDAO;
        this.pushService = pushService;
    }

    @GET
    @Path("/device/{deviceId}")
    @ApiOperation(value = "Get contacts by device",
            notes = "Retrieves all contacts for a specific device")
    public Response getContactsByDevice(
            @ApiParam("Device ID") @PathParam("deviceId") Integer deviceId) {
        List<DeviceContact> contacts = deviceContactDAO.getContactsByDevice(deviceId);
        return Response.OK(contacts);
    }

    @GET
    @Path("/{id}")
    @ApiOperation(value = "Get contact by ID",
            notes = "Retrieves a specific contact by its ID")
    public Response getContactById(
            @ApiParam("Contact ID") @PathParam("id") Integer id) {
        DeviceContact contact = deviceContactDAO.getContactById(id);
        if (contact == null) {
            return Response.ERROR("contact.not.found");
        }
        return Response.OK(contact);
    }

    @POST
    @ApiOperation(value = "Create contact",
            notes = "Creates a new device contact")
    public Response createContact(
            @ApiParam("Contact data") DeviceContact contact) {
        DeviceContact created = deviceContactDAO.createContact(contact);
        return Response.OK(created);
    }

    @PUT
    @Path("/{id}")
    @ApiOperation(value = "Update contact",
            notes = "Updates an existing device contact")
    public Response updateContact(
            @ApiParam("Contact ID") @PathParam("id") Integer id,
            @ApiParam("Contact data") DeviceContact contact) {
        contact.setId(id);
        int updated = deviceContactDAO.updateContact(contact);
        if (updated == 0) {
            return Response.ERROR("contact.not.found");
        }
        return Response.OK();
    }

    @DELETE
    @Path("/{id}")
    @ApiOperation(value = "Delete contact",
            notes = "Deletes a device contact")
    public Response deleteContact(
            @ApiParam("Contact ID") @PathParam("id") Integer id) {
        int deleted = deviceContactDAO.deleteContact(id);
        if (deleted == 0) {
            return Response.ERROR("contact.not.found");
        }
        return Response.OK();
    }

    @GET
    @Path("/search")
    @ApiOperation(value = "Search contacts",
            notes = "Searches contacts by query string")
    public Response searchContacts(
            @ApiParam("Device ID (optional)") @QueryParam("deviceId") Integer deviceId,
            @ApiParam("Search query") @QueryParam("query") String query) {
        List<DeviceContact> contacts = deviceContactDAO.searchContacts(deviceId, query);
        return Response.OK(contacts);
    }

    @POST
    @Path("/sync/{deviceId}")
    @ApiOperation(value = "Sync contacts to device",
            notes = "Sends a push notification to sync contacts to a specific device")
    public Response syncContactsToDevice(
            @ApiParam("Device ID") @PathParam("deviceId") Integer deviceId) {
        Device device = deviceDAO.getDeviceById(deviceId);
        if (device == null) {
            return Response.ERROR("device.not.found");
        }
        pushService.syncContacts(deviceId);
        return Response.OK();
    }

    @POST
    @Path("/sync")
    @ApiOperation(value = "Sync contacts to multiple devices",
            notes = "Sends a push notification to sync contacts to multiple devices")
    public Response syncContactsToDevices(
            @ApiParam("Device IDs") List<Integer> deviceIds) {
        pushService.syncContactsBulk(deviceIds);
        return Response.OK();
    }
}
