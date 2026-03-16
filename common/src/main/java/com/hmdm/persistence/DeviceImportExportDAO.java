/*
 * Headwind MDM: Open Source Android MDM Software
 */
package com.hmdm.persistence;

import com.google.inject.Inject;
import com.hmdm.persistence.domain.DeviceExportJob;
import com.hmdm.persistence.domain.DeviceImportJob;
import com.hmdm.persistence.mapper.DeviceImportExportMapper;
import com.hmdm.security.SecurityContext;
import org.mybatis.guice.transactional.Transactional;

import java.util.List;

public class DeviceImportExportDAO {
    private final DeviceImportExportMapper mapper;

    @Inject
    public DeviceImportExportDAO(DeviceImportExportMapper mapper) {
        this.mapper = mapper;
    }

    // Import Jobs
    @Transactional
    public DeviceImportJob createImportJob(DeviceImportJob job) {
        job.setCustomerId(SecurityContext.get().getCurrentUser().get().getCustomerId());
        job.setUserId(SecurityContext.get().getCurrentUser().get().getId());
        mapper.insertImportJob(job);
        return job;
    }

    public DeviceImportJob getImportJob(Integer id) {
        return mapper.getImportJobById(id, SecurityContext.get().getCurrentUser().get().getCustomerId());
    }

    public List<DeviceImportJob> getImportJobs() {
        return mapper.getImportJobsByCustomer(SecurityContext.get().getCurrentUser().get().getCustomerId());
    }

    @Transactional
    public int updateImportJob(DeviceImportJob job) {
        return mapper.updateImportJob(job);
    }

    // Export Jobs
    @Transactional
    public DeviceExportJob createExportJob(DeviceExportJob job) {
        job.setCustomerId(SecurityContext.get().getCurrentUser().get().getCustomerId());
        job.setUserId(SecurityContext.get().getCurrentUser().get().getId());
        mapper.insertExportJob(job);
        return job;
    }

    public DeviceExportJob getExportJob(Integer id) {
        return mapper.getExportJobById(id, SecurityContext.get().getCurrentUser().get().getCustomerId());
    }

    public List<DeviceExportJob> getExportJobs() {
        return mapper.getExportJobsByCustomer(SecurityContext.get().getCurrentUser().get().getCustomerId());
    }

    @Transactional
    public int updateExportJob(DeviceExportJob job) {
        return mapper.updateExportJob(job);
    }
}
