-- Feature 7: LDAP Integration
-- Database migration for LDAP configuration

-- Table for storing LDAP server configurations
CREATE TABLE ldap_configurations (
    id SERIAL PRIMARY KEY,
    customer_id INTEGER NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    enabled BOOLEAN DEFAULT FALSE,
    server_host VARCHAR(255) NOT NULL,
    server_port INTEGER DEFAULT 389,
    use_ssl BOOLEAN DEFAULT FALSE,
    use_tls BOOLEAN DEFAULT FALSE,
    bind_dn VARCHAR(500), -- DN for binding to LDAP server
    bind_password TEXT, -- Encrypted password for binding
    base_dn VARCHAR(500) NOT NULL, -- Base DN for searching users
    user_search_filter VARCHAR(500) DEFAULT '(uid={0})', -- Filter for searching users
    user_name_attribute VARCHAR(100) DEFAULT 'uid', -- Attribute containing username
    user_email_attribute VARCHAR(100) DEFAULT 'mail', -- Attribute containing email
    user_first_name_attribute VARCHAR(100) DEFAULT 'givenName',
    user_last_name_attribute VARCHAR(100) DEFAULT 'sn',
    group_search_filter VARCHAR(500), -- Filter for searching groups
    group_name_attribute VARCHAR(100) DEFAULT 'cn',
    admin_group_dn VARCHAR(500), -- Group DN for admin users
    auto_create_users BOOLEAN DEFAULT TRUE,
    auto_assign_configuration_id INTEGER REFERENCES configurations(id),
    connection_timeout INTEGER DEFAULT 5000, -- milliseconds
    read_timeout INTEGER DEFAULT 10000, -- milliseconds
    last_sync_at TIMESTAMP,
    sync_enabled BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for efficient queries
CREATE INDEX idx_ldap_config_customer_id ON ldap_configurations(customer_id);
CREATE UNIQUE INDEX idx_ldap_config_customer_unique ON ldap_configurations(customer_id) WHERE enabled = TRUE;

-- Table for LDAP group mappings
CREATE TABLE ldap_group_mappings (
    id SERIAL PRIMARY KEY,
    ldap_config_id INTEGER NOT NULL REFERENCES ldap_configurations(id) ON DELETE CASCADE,
    ldap_group_dn VARCHAR(500) NOT NULL,
    local_role VARCHAR(50) NOT NULL, -- ADMIN, OPERATOR, VIEWER, etc.
    local_configuration_id INTEGER REFERENCES configurations(id),
    priority INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_ldap_group_mappings_config_id ON ldap_group_mappings(ldap_config_id);

-- Table for LDAP synchronization logs
CREATE TABLE ldap_sync_logs (
    id SERIAL PRIMARY KEY,
    customer_id INTEGER NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    sync_type VARCHAR(50) NOT NULL, -- FULL, INCREMENTAL, TEST
    status VARCHAR(20) NOT NULL, -- SUCCESS, FAILED, PARTIAL
    users_synced INTEGER DEFAULT 0,
    users_created INTEGER DEFAULT 0,
    users_updated INTEGER DEFAULT 0,
    users_failed INTEGER DEFAULT 0,
    error_message TEXT,
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP
);

CREATE INDEX idx_ldap_sync_logs_customer_id ON ldap_sync_logs(customer_id);
CREATE INDEX idx_ldap_sync_logs_started_at ON ldap_sync_logs(started_at);

-- Add column to users table to track LDAP users
ALTER TABLE users ADD COLUMN IF NOT EXISTS ldap_user BOOLEAN DEFAULT FALSE;
ALTER TABLE users ADD COLUMN IF NOT EXISTS ldap_dn VARCHAR(500);
ALTER TABLE users ADD COLUMN IF NOT EXISTS ldap_synced_at TIMESTAMP;

CREATE INDEX idx_users_ldap_user ON users(ldap_user) WHERE ldap_user = TRUE;
