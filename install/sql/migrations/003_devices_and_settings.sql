-- Devices and Settings Schema
-- Creates tables: devices, settings, deviceGroups

-- Devices table
CREATE TABLE IF NOT EXISTS devices (
    id SERIAL PRIMARY KEY,
    number VARCHAR(100) NOT NULL,
    description TEXT,
    lastUpdate TIMESTAMP,
    configurationId INTEGER REFERENCES configurations(id),
    oldConfigurationId INTEGER,
    info TEXT,
    imei VARCHAR(100),
    phone VARCHAR(50),
    customerId INTEGER REFERENCES customers(id) ON DELETE CASCADE,
    imeiUpdateTs TIMESTAMP,
    publicIp VARCHAR(50),
    custom1 VARCHAR(255),
    custom2 VARCHAR(255),
    custom3 VARCHAR(255),
    oldNumber VARCHAR(100),
    fastSearch VARCHAR(500),
    mdmMode VARCHAR(50) DEFAULT 'device_owner',
    kioskMode BOOLEAN DEFAULT FALSE,
    androidVersion VARCHAR(50),
    enrollTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    serial VARCHAR(100)
);

-- Device groups mapping
CREATE TABLE IF NOT EXISTS devicegroups (
    deviceId INTEGER NOT NULL REFERENCES devices(id) ON DELETE CASCADE,
    groupId INTEGER NOT NULL REFERENCES groups(id) ON DELETE CASCADE,
    PRIMARY KEY (deviceId, groupId)
);

-- Settings table
CREATE TABLE IF NOT EXISTS settings (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    value TEXT,
    lastUpdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Device settings table
CREATE TABLE IF NOT EXISTS devicesettings (
    id SERIAL PRIMARY KEY,
    deviceId INTEGER NOT NULL REFERENCES devices(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    value TEXT,
    lastUpdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(deviceId, name)
);

-- Pending configurations for devices
CREATE TABLE IF NOT EXISTS pendingconfigurations (
    id SERIAL PRIMARY KEY,
    deviceId INTEGER NOT NULL REFERENCES devices(id) ON DELETE CASCADE,
    configurationId INTEGER NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Device log table (base schema, extended in migration 003)
CREATE TABLE IF NOT EXISTS devicelogs (
    id SERIAL PRIMARY KEY,
    deviceId INTEGER NOT NULL REFERENCES devices(id) ON DELETE CASCADE,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    logLevel VARCHAR(20) DEFAULT 'INFO',
    message TEXT
);

-- Push messages table
CREATE TABLE IF NOT EXISTS pushmessages (
    id SERIAL PRIMARY KEY,
    deviceId INTEGER NOT NULL REFERENCES devices(id) ON DELETE CASCADE,
    messageType VARCHAR(50) NOT NULL,
    payload TEXT,
    createTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    sendTime TIMESTAMP,
    status VARCHAR(50) DEFAULT 'pending'
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_devices_number ON devices(number);
CREATE INDEX IF NOT EXISTS idx_devices_customer ON devices(customerId);
CREATE INDEX IF NOT EXISTS idx_devices_configuration ON devices(configurationId);
CREATE INDEX IF NOT EXISTS idx_devices_imei ON devices(imei);
CREATE INDEX IF NOT EXISTS idx_devices_fastsearch ON devices(fastSearch);
CREATE INDEX IF NOT EXISTS idx_devicesettings_device ON devicesettings(deviceId);
CREATE INDEX IF NOT EXISTS idx_devicelogs_device ON devicelogs(deviceId);
CREATE INDEX IF NOT EXISTS idx_devicelogs_timestamp ON devicelogs(timestamp);
CREATE INDEX IF NOT EXISTS idx_pushmessages_device ON pushmessages(deviceId);
CREATE INDEX IF NOT EXISTS idx_pushmessages_status ON pushmessages(status);
CREATE INDEX IF NOT EXISTS idx_pendingconfigs_device ON pendingconfigurations(deviceId);
