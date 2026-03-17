/*
 * Headwind MDM: Open Source Android MDM Software
 */
package com.hmdm.rest.resource;

import com.google.inject.Inject;
import com.hmdm.persistence.WhiteLabelDAO;
import com.hmdm.persistence.domain.EmailTemplate;
import com.hmdm.persistence.domain.RebrandingSettings;
import com.hmdm.persistence.domain.WhiteLabelBuild;
import com.hmdm.rest.json.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Api(tags = {"White Label"}, authorizations = {@Authorization("apiKey")})
@Path("/private/white-label")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WhiteLabelResource {

    private final WhiteLabelDAO whiteLabelDAO;

    @Inject
    public WhiteLabelResource(WhiteLabelDAO whiteLabelDAO) {
        this.whiteLabelDAO = whiteLabelDAO;
    }

    // Rebranding Settings
    @GET
    @Path("/rebranding")
    @ApiOperation(value = "Get rebranding settings",
            notes = "Retrieves the rebranding settings for the current customer")
    public Response getRebrandingSettings() {
        RebrandingSettings settings = whiteLabelDAO.getRebrandingSettings();
        return Response.OK(settings);
    }

    @PUT
    @Path("/rebranding")
    @ApiOperation(value = "Update rebranding settings",
            notes = "Updates the rebranding settings for the current customer")
    public Response updateRebrandingSettings(
            @ApiParam("Rebranding settings") RebrandingSettings settings) {
        int updated = whiteLabelDAO.saveRebrandingSettings(settings);
        return Response.OK(updated);
    }

    // White Label Builds
    @GET
    @Path("/builds")
    @ApiOperation(value = "Get all builds",
            notes = "Retrieves all white-label APK builds")
    public Response getAllBuilds() {
        List<WhiteLabelBuild> builds = whiteLabelDAO.getAllBuilds();
        return Response.OK(builds);
    }

    @GET
    @Path("/builds/{id}")
    @ApiOperation(value = "Get build by ID",
            notes = "Retrieves a specific white-label build")
    public Response getBuildById(
            @ApiParam("Build ID") @PathParam("id") Integer id) {
        WhiteLabelBuild build = whiteLabelDAO.getBuildById(id);
        if (build == null) {
            return Response.ERROR("build.not.found");
        }
        return Response.OK(build);
    }

    @POST
    @Path("/builds")
    @ApiOperation(value = "Create build",
            notes = "Creates a new white-label APK build configuration")
    public Response createBuild(
            @ApiParam("Build configuration") WhiteLabelBuild build) {
        WhiteLabelBuild created = whiteLabelDAO.createBuild(build);
        return Response.OK(created);
    }

    @PUT
    @Path("/builds/{id}")
    @ApiOperation(value = "Update build",
            notes = "Updates a white-label APK build configuration")
    public Response updateBuild(
            @ApiParam("Build ID") @PathParam("id") Integer id,
            @ApiParam("Build configuration") WhiteLabelBuild build) {
        build.setId(id);
        int updated = whiteLabelDAO.updateBuild(build);
        if (updated == 0) {
            return Response.ERROR("build.not.found");
        }
        return Response.OK();
    }

    @DELETE
    @Path("/builds/{id}")
    @ApiOperation(value = "Delete build",
            notes = "Deletes a white-label APK build")
    public Response deleteBuild(
            @ApiParam("Build ID") @PathParam("id") Integer id) {
        int deleted = whiteLabelDAO.deleteBuild(id);
        if (deleted == 0) {
            return Response.ERROR("build.not.found");
        }
        return Response.OK();
    }

    @POST
    @Path("/builds/{id}/trigger")
    @ApiOperation(value = "Trigger build",
            notes = "Triggers the build process for a white-label APK")
    public Response triggerBuild(
            @ApiParam("Build ID") @PathParam("id") Integer id) {
        WhiteLabelBuild build = whiteLabelDAO.getBuildById(id);
        if (build == null) {
            return Response.ERROR("build.not.found");
        }
        // TODO: Trigger actual build process asynchronously
        build.setBuildStatus(WhiteLabelBuild.STATUS_BUILDING);
        whiteLabelDAO.updateBuildStatus(build);
        return Response.OK(build);
    }

    // Email Templates
    @GET
    @Path("/email-templates")
    @ApiOperation(value = "Get all email templates",
            notes = "Retrieves all email templates")
    public Response getAllEmailTemplates() {
        List<EmailTemplate> templates = whiteLabelDAO.getAllEmailTemplates();
        return Response.OK(templates);
    }

    @GET
    @Path("/email-templates/{type}")
    @ApiOperation(value = "Get email template",
            notes = "Retrieves a specific email template by type")
    public Response getEmailTemplate(
            @ApiParam("Template type") @PathParam("type") String type) {
        EmailTemplate template = whiteLabelDAO.getEmailTemplate(type);
        return Response.OK(template);
    }

    @PUT
    @Path("/email-templates")
    @ApiOperation(value = "Save email template",
            notes = "Saves an email template")
    public Response saveEmailTemplate(
            @ApiParam("Email template") EmailTemplate template) {
        EmailTemplate saved = whiteLabelDAO.saveEmailTemplate(template);
        return Response.OK(saved);
    }

    @DELETE
    @Path("/email-templates/{id}")
    @ApiOperation(value = "Delete email template",
            notes = "Deletes a custom email template")
    public Response deleteEmailTemplate(
            @ApiParam("Template ID") @PathParam("id") Integer id) {
        int deleted = whiteLabelDAO.deleteEmailTemplate(id);
        if (deleted == 0) {
            return Response.ERROR("template.not.found");
        }
        return Response.OK();
    }
}
