-- Initial Core Schema
-- Creates base tables: customers, userroles, permissions, users, groups, userrolesettings

-- Customers table
CREATE TABLE IF NOT EXISTS customers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    description TEXT,
    filesDir VARCHAR(255),
    master BOOLEAN DEFAULT FALSE,
    prefix VARCHAR(50),
    deviceConfigurationId INTEGER,
    lastLoginTime TIMESTAMP,
    registrationTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expiryTime TIMESTAMP,
    deviceLimit INTEGER DEFAULT 0,
    sizeLimit INTEGER DEFAULT 0,
    accountType VARCHAR(50) DEFAULT 'default',
    customerStatus VARCHAR(50) DEFAULT 'active',
    firstName VARCHAR(100),
    lastName VARCHAR(100),
    language VARCHAR(10) DEFAULT 'en',
    inactiveState BOOLEAN DEFAULT FALSE,
    pauseState BOOLEAN DEFAULT FALSE,
    abandonState BOOLEAN DEFAULT FALSE,
    signupStatus VARCHAR(50),
    signupToken VARCHAR(255)
);

-- User roles table
CREATE TABLE IF NOT EXISTS userroles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    superAdmin BOOLEAN DEFAULT FALSE,
    customerId INTEGER REFERENCES customers(id) ON DELETE CASCADE
);

-- Permissions table
CREATE TABLE IF NOT EXISTS permissions (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

-- User role permissions mapping
CREATE TABLE IF NOT EXISTS userrolepermissions (
    userRoleId INTEGER NOT NULL REFERENCES userroles(id) ON DELETE CASCADE,
    permissionId INTEGER NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (userRoleId, permissionId)
);

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    login VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    name VARCHAR(100),
    password VARCHAR(255) NOT NULL,
    customerId INTEGER REFERENCES customers(id) ON DELETE CASCADE,
    userRoleId INTEGER REFERENCES userroles(id),
    allDevicesAvailable BOOLEAN DEFAULT FALSE,
    allConfigAvailable BOOLEAN DEFAULT FALSE,
    passwordReset BOOLEAN DEFAULT FALSE,
    authToken VARCHAR(255),
    passwordResetToken VARCHAR(255),
    authData TEXT,
    twoFactorSecret VARCHAR(255),
    twoFactorAccepted BOOLEAN DEFAULT FALSE,
    twoFactor BOOLEAN DEFAULT FALSE,
    idleLogout INTEGER DEFAULT 0,
    lastLoginFail TIMESTAMP,
    UNIQUE(login, customerId)
);

-- Groups table
CREATE TABLE IF NOT EXISTS groups (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    customerId INTEGER REFERENCES customers(id) ON DELETE CASCADE,
    UNIQUE(name, customerId)
);

-- User groups mapping
CREATE TABLE IF NOT EXISTS usergroups (
    userId INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    groupId INTEGER NOT NULL REFERENCES groups(id) ON DELETE CASCADE,
    PRIMARY KEY (userId, groupId)
);

-- User configurations mapping
CREATE TABLE IF NOT EXISTS userconfigurations (
    userId INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    configurationId INTEGER NOT NULL,
    PRIMARY KEY (userId, configurationId)
);

-- User role settings
CREATE TABLE IF NOT EXISTS userrolesettings (
    id SERIAL PRIMARY KEY,
    userRoleId INTEGER REFERENCES userroles(id) ON DELETE CASCADE,
    columnName VARCHAR(100) NOT NULL,
    columnTitle VARCHAR(100),
    columnWidth INTEGER DEFAULT 100,
    orderIndex INTEGER DEFAULT 0,
    UNIQUE(userRoleId, columnName)
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_customers_prefix ON customers(prefix);
CREATE INDEX IF NOT EXISTS idx_customers_signup_token ON customers(signupToken);
CREATE INDEX IF NOT EXISTS idx_userroles_customer ON userroles(customerId);
CREATE INDEX IF NOT EXISTS idx_users_login ON users(login);
CREATE INDEX IF NOT EXISTS idx_users_customer ON users(customerId);
CREATE INDEX IF NOT EXISTS idx_users_userrole ON users(userRoleId);
CREATE INDEX IF NOT EXISTS idx_users_password_reset_token ON users(passwordResetToken);
CREATE INDEX IF NOT EXISTS idx_groups_customer ON groups(customerId);
