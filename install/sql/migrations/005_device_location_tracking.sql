-- Migration for Device Location Tracking feature
-- Adds tables for storing device location data and geofencing

-- Create device_locations table
CREATE TABLE IF NOT EXISTS device_locations (
    id SERIAL PRIMARY KEY,
    device_id INTEGER NOT NULL REFERENCES devices(id) ON DELETE CASCADE,
    latitude NUMERIC(10, 8) NOT NULL,
    longitude NUMERIC(11, 8) NOT NULL,
    accuracy NUMERIC(10, 2),
    altitude NUMERIC(10, 2),
    speed NUMERIC(10, 2),
    battery_level INTEGER,
    address VARCHAR(500),
    provider VARCHAR(50), -- gps, network, passive
    timestamp BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index for faster queries by device
CREATE INDEX IF NOT EXISTS idx_device_locations_device_id ON device_locations(device_id);
CREATE INDEX IF NOT EXISTS idx_device_locations_timestamp ON device_locations(timestamp);
CREATE INDEX IF NOT EXISTS idx_device_locations_device_timestamp ON device_locations(device_id, timestamp DESC);

-- Create geofences table
CREATE TABLE IF NOT EXISTS geofences (
    id SERIAL PRIMARY KEY,
    customer_id INTEGER NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    latitude NUMERIC(10, 8) NOT NULL,
    longitude NUMERIC(11, 8) NOT NULL,
    radius INTEGER NOT NULL, -- in meters
    enter_notification BOOLEAN DEFAULT true,
    exit_notification BOOLEAN DEFAULT true,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create geofence_devices table (many-to-many)
CREATE TABLE IF NOT EXISTS geofence_devices (
    id SERIAL PRIMARY KEY,
    geofence_id INTEGER NOT NULL REFERENCES geofences(id) ON DELETE CASCADE,
    device_id INTEGER NOT NULL REFERENCES devices(id) ON DELETE CASCADE,
    status VARCHAR(20) DEFAULT 'unknown', -- inside, outside, unknown
    last_triggered_at TIMESTAMP,
    UNIQUE(geofence_id, device_id)
);

CREATE INDEX IF NOT EXISTS idx_geofences_customer_id ON geofences(customer_id);
CREATE INDEX IF NOT EXISTS idx_geofence_devices_geofence_id ON geofence_devices(geofence_id);
CREATE INDEX IF NOT EXISTS idx_geofence_devices_device_id ON geofence_devices(device_id);

-- Add location tracking settings to configurations
ALTER TABLE configurations
    ADD COLUMN IF NOT EXISTS location_tracking_enabled BOOLEAN DEFAULT false,
    ADD COLUMN IF NOT EXISTS location_tracking_interval INTEGER DEFAULT 300, -- in seconds
    ADD COLUMN IF NOT EXISTS location_tracking_mode VARCHAR(20) DEFAULT 'balanced'; -- high_accuracy, balanced, low_power

-- Add location enabled to devices table (for quick filtering)
ALTER TABLE devices
    ADD COLUMN IF NOT EXISTS location_enabled BOOLEAN DEFAULT false,
    ADD COLUMN IF NOT EXISTS last_location_at BIGINT;

-- Create view for latest device locations
CREATE OR REPLACE VIEW device_latest_locations AS
SELECT DISTINCT ON (device_id)
    device_id,
    latitude,
    longitude,
    accuracy,
    battery_level,
    timestamp,
    created_at
FROM device_locations
ORDER BY device_id, timestamp DESC;
