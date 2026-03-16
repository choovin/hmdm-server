/*
 *
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
 *
 */

package com.hmdm.persistence.mapper;

import com.hmdm.persistence.domain.DeviceExportJob;
import com.hmdm.persistence.domain.DeviceImportJob;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>Mapper interface for device import/export.</p>
 */
public interface DeviceImportExportMapper {

    // Import Jobs
    int insertImportJob(DeviceImportJob job);
    DeviceImportJob getImportJobById(@Param("id") Integer id, @Param("customerId") Integer customerId);
    List<DeviceImportJob> getImportJobsByCustomer(@Param("customerId") Integer customerId);
    int updateImportJob(DeviceImportJob job);
    int updateImportJobStatus(@Param("id") Integer id, @Param("status") String status, @Param("customerId") Integer customerId);
    int deleteImportJob(@Param("id") Integer id, @Param("customerId") Integer customerId);

    // Export Jobs
    int insertExportJob(DeviceExportJob job);
    DeviceExportJob getExportJobById(@Param("id") Integer id, @Param("customerId") Integer customerId);
    List<DeviceExportJob> getExportJobsByCustomer(@Param("customerId") Integer customerId);
    int updateExportJob(DeviceExportJob job);
    int updateExportJobStatus(@Param("id") Integer id, @Param("status") String status, @Param("customerId") Integer customerId);
    int deleteExportJob(@Param("id") Integer id, @Param("customerId") Integer customerId);
}
