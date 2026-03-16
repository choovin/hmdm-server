-- Feature 8: Export and Import Devices
-- Database migration for device import/export tracking

-- Table for tracking device import jobs
CREATE TABLE device_import_jobs (
    id SERIAL PRIMARY KEY,
    customer_id INTEGER NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    user_id INTEGER REFERENCES users(id),
    import_type VARCHAR(50) NOT NULL, -- CSV, EXCEL, JSON
    file_name VARCHAR(500),
    file_size BIGINT,
    status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, PROCESSING, COMPLETED, FAILED
    total_records INTEGER DEFAULT 0,
    processed_records INTEGER DEFAULT 0,
    success_count INTEGER DEFAULT 0,
    failed_count INTEGER DEFAULT 0,
    skipped_count INTEGER DEFAULT 0,
    error_log TEXT,
    import_options JSONB, -- Options like skipFirstRow, encoding, delimiter, etc.
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    started_at TIMESTAMP,
    completed_at TIMESTAMP
);

-- Indexes for import jobs
CREATE INDEX idx_device_import_jobs_customer_id ON device_import_jobs(customer_id);
CREATE INDEX idx_device_import_jobs_status ON device_import_jobs(status);
CREATE INDEX idx_device_import_jobs_created_at ON device_import_jobs(created_at);

-- Table for tracking device export jobs
CREATE TABLE device_export_jobs (
    id SERIAL PRIMARY KEY,
    customer_id INTEGER NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    user_id INTEGER REFERENCES users(id),
    export_type VARCHAR(50) NOT NULL, -- CSV, EXCEL, PDF, JSON
    file_name VARCHAR(500),
    file_path VARCHAR(1000),
    file_size BIGINT,
    status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, PROCESSING, COMPLETED, FAILED, EXPIRED
    filter_criteria JSONB, -- Filters applied (group, config, status, etc.)
    total_records INTEGER DEFAULT 0,
    export_options JSONB, -- Options like includeFields, format, etc.
    expires_at TIMESTAMP, -- When the export file should be deleted
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    started_at TIMESTAMP,
    completed_at TIMESTAMP
);

-- Indexes for export jobs
CREATE INDEX idx_device_export_jobs_customer_id ON device_export_jobs(customer_id);
CREATE INDEX idx_device_export_jobs_status ON device_export_jobs(status);
CREATE INDEX idx_device_export_jobs_expires_at ON device_export_jobs(expires_at);

-- Table for storing import error details
CREATE TABLE device_import_errors (
    id SERIAL PRIMARY KEY,
    job_id INTEGER NOT NULL REFERENCES device_import_jobs(id) ON DELETE CASCADE,
    row_number INTEGER,
    device_number VARCHAR(255),
    error_type VARCHAR(50), -- DUPLICATE, INVALID, MISSING_FIELD, NOT_FOUND
    error_message TEXT,
    raw_data TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for import errors
CREATE INDEX idx_device_import_errors_job_id ON device_import_errors(job_id);

-- Table for device import templates
CREATE TABLE device_import_templates (
    id SERIAL PRIMARY KEY,
    customer_id INTEGER NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    template_name VARCHAR(255) NOT NULL,
    template_type VARCHAR(50) NOT NULL, -- CSV, EXCEL
    field_mapping JSONB NOT NULL, -- Maps file columns to device fields
    delimiter VARCHAR(10) DEFAULT ',',
    encoding VARCHAR(50) DEFAULT 'UTF-8',
    has_header_row BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_device_import_templates_customer_id ON device_import_templates(customer_id);

