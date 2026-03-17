/*
 * Headwind MDM: Open Source Android MDM Software
 */
package com.hmdm.rest.resource;

import com.google.inject.Inject;
import com.hmdm.persistence.DeviceImportExportDAO;
import com.hmdm.persistence.domain.DeviceExportJob;
import com.hmdm.persistence.domain.DeviceImportJob;
import com.hmdm.rest.json.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Api(value = "Device Import/Export", authorizations = {@Authorization(value = "Bearer")})
@Path("/private/devices/import-export")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DeviceImportExportResource {
    private final DeviceImportExportDAO dao;

    @Inject
    public DeviceImportExportResource(DeviceImportExportDAO dao) {
        this.dao = dao;
    }

    // Import Jobs
    @ApiOperation(value = "Get import jobs")
    @GET
    @Path("/import-jobs")
    public Response getImportJobs() {
        List<DeviceImportJob> jobs = dao.getImportJobs();
        return Response.OK(jobs);
    }

    @ApiOperation(value = "Create import job")
    @POST
    @Path("/import-jobs")
    public Response createImportJob(DeviceImportJob job) {
        if (job == null || job.getImportType() == null) {
            return Response.ERROR("Validation error", "Import type required");
        }
        DeviceImportJob created = dao.createImportJob(job);
        return Response.OK(created);
    }

    // Export Jobs
    @ApiOperation(value = "Get export jobs")
    @GET
    @Path("/export-jobs")
    public Response getExportJobs() {
        List<DeviceExportJob> jobs = dao.getExportJobs();
        return Response.OK(jobs);
    }

    @ApiOperation(value = "Create export job")
    @POST
    @Path("/export-jobs")
    public Response createExportJob(DeviceExportJob job) {
        if (job == null || job.getExportType() == null) {
            return Response.ERROR("Validation error", "Export type required");
        }
        DeviceExportJob created = dao.createExportJob(job);
        return Response.OK(created);
    }

    @ApiOperation(value = "Export devices to CSV")
    @POST
    @Path("/export/csv")
    public Response exportToCsv() {
        // Trigger async export job
        DeviceExportJob job = new DeviceExportJob();
        job.setExportType(DeviceExportJob.TYPE_CSV);
        job = dao.createExportJob(job);
        return Response.OK(job);
    }

    @ApiOperation(value = "Export devices to Excel")
    @POST
    @Path("/export/excel")
    public Response exportToExcel() {
        DeviceExportJob job = new DeviceExportJob();
        job.setExportType(DeviceExportJob.TYPE_EXCEL);
        job = dao.createExportJob(job);
        return Response.OK(job);
    }
}
