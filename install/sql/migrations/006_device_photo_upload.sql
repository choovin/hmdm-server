-- Feature 4: Photo Upload from Devices
-- Database migration for device photo management

-- Table for storing device photos
CREATE TABLE device_photos (
    id SERIAL PRIMARY KEY,
    device_id INTEGER NOT NULL REFERENCES devices(id) ON DELETE CASCADE,
    customer_id INTEGER NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT DEFAULT 0,
    mime_type VARCHAR(50),
    width INTEGER,
    height INTEGER,
    description TEXT,
    tags TEXT,
    uploaded_by_device BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    taken_at TIMESTAMP,
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8)
);

-- Indexes for efficient queries
CREATE INDEX idx_device_photos_device_id ON device_photos(device_id);
CREATE INDEX idx_device_photos_customer_id ON device_photos(customer_id);
CREATE INDEX idx_device_photos_created_at ON device_photos(created_at);

-- Table for photo upload requests (from admin to device)
CREATE TABLE photo_upload_requests (
    id SERIAL PRIMARY KEY,
    device_id INTEGER NOT NULL REFERENCES devices(id) ON DELETE CASCADE,
    customer_id INTEGER NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, COMPLETED, FAILED, CANCELLED
    requested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    photo_count INTEGER DEFAULT 0,
    description TEXT,
    error_message TEXT
);

CREATE INDEX idx_photo_upload_requests_device_id ON photo_upload_requests(device_id);
CREATE INDEX idx_photo_upload_requests_status ON photo_upload_requests(status);
