-- Files and Plugins Schema
-- Creates tables: hfiles, plugins, plugin_device_log_settings_rules

-- Files table (named hfiles to avoid SQL reserved word conflict)
CREATE TABLE IF NOT EXISTS hfiles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    filePath VARCHAR(500),
    size BIGINT DEFAULT 0,
    version VARCHAR(50),
    url TEXT,
    customerId INTEGER REFERENCES customers(id) ON DELETE CASCADE,
    uploadTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Plugins table
CREATE TABLE IF NOT EXISTS plugins (
    id SERIAL PRIMARY KEY,
    identifier VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    version VARCHAR(50),
    url TEXT,
    required BOOLEAN DEFAULT FALSE,
    enabled BOOLEAN DEFAULT TRUE,
    system BOOLEAN DEFAULT FALSE
);

-- Plugin device log settings rules table
CREATE TABLE IF NOT EXISTS plugin_devicelog_settings_rules (
    id SERIAL PRIMARY KEY,
    pluginId INTEGER NOT NULL REFERENCES plugins(id) ON DELETE CASCADE,
    severity VARCHAR(20) NOT NULL,
    filter TEXT,
    createTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Device log settings (for configuring device log collection)
CREATE TABLE IF NOT EXISTS devicelogsettings (
    id SERIAL PRIMARY KEY,
    deviceId INTEGER REFERENCES devices(id) ON DELETE CASCADE,
    configurationId INTEGER REFERENCES configurations(id) ON DELETE CASCADE,
    enabled BOOLEAN DEFAULT TRUE,
    logLevel VARCHAR(20) DEFAULT 'INFO',
    bufferSize INTEGER DEFAULT 1000,
    sendInterval INTEGER DEFAULT 300,
    lastUpdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT device_or_configuration CHECK (
        (deviceId IS NOT NULL AND configurationId IS NULL) OR
        (deviceId IS NULL AND configurationId IS NOT NULL)
    )
);

-- Application settings table (for device-specific app settings)
CREATE TABLE IF NOT EXISTS applicationsettings (
    id SERIAL PRIMARY KEY,
    deviceId INTEGER NOT NULL REFERENCES devices(id) ON DELETE CASCADE,
    applicationId INTEGER NOT NULL REFERENCES applications(id) ON DELETE CASCADE,
    settingsJson TEXT,
    lastUpdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(deviceId, applicationId)
);

-- Password reset tokens table
CREATE TABLE IF NOT EXISTS passwordresettokens (
    id SERIAL PRIMARY KEY,
    userId INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiryTime TIMESTAMP NOT NULL,
    used BOOLEAN DEFAULT FALSE,
    createTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Signup tokens table
CREATE TABLE IF NOT EXISTS signuptokens (
    id SERIAL PRIMARY KEY,
    email VARCHAR(100) NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    data TEXT,
    expiryTime TIMESTAMP NOT NULL,
    used BOOLEAN DEFAULT FALSE,
    createTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_hfiles_customer ON hfiles(customerId);
CREATE INDEX IF NOT EXISTS idx_hfiles_name ON hfiles(name);
CREATE INDEX IF NOT EXISTS idx_plugins_identifier ON plugins(identifier);
CREATE INDEX IF NOT EXISTS idx_plugin_devicelog_plugin ON plugin_devicelog_settings_rules(pluginId);
CREATE INDEX IF NOT EXISTS idx_devicelogsettings_device ON devicelogsettings(deviceId);
CREATE INDEX IF NOT EXISTS idx_devicelogsettings_configuration ON devicelogsettings(configurationId);
CREATE INDEX IF NOT EXISTS idx_applicationsettings_device ON applicationsettings(deviceId);
CREATE INDEX IF NOT EXISTS idx_applicationsettings_application ON applicationsettings(applicationId);
CREATE INDEX IF NOT EXISTS idx_passwordresettokens_token ON passwordresettokens(token);
CREATE INDEX IF NOT EXISTS idx_passwordresettokens_user ON passwordresettokens(userId);
CREATE INDEX IF NOT EXISTS idx_signuptokens_token ON signuptokens(token);
