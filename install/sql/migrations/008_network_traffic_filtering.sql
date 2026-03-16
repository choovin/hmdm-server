-- Feature 6: Network Traffic Filtering
-- Database migration for network traffic filtering rules

-- Table for storing network traffic filtering rules
CREATE TABLE network_traffic_rules (
    id SERIAL PRIMARY KEY,
    customer_id INTEGER NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    configuration_id INTEGER REFERENCES configurations(id) ON DELETE CASCADE,
    rule_name VARCHAR(255) NOT NULL,
    rule_type VARCHAR(50) NOT NULL, -- ALLOW, BLOCK, WHITELIST, BLACKLIST
    traffic_type VARCHAR(50) NOT NULL, -- URL, DOMAIN, IP, PORT, APP
    pattern TEXT NOT NULL, -- The actual pattern to match (URL, domain, IP, etc.)
    description TEXT,
    priority INTEGER DEFAULT 0, -- Rule priority for ordering
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for efficient queries
CREATE INDEX idx_network_rules_customer_id ON network_traffic_rules(customer_id);
CREATE INDEX idx_network_rules_config_id ON network_traffic_rules(configuration_id);
CREATE INDEX idx_network_rules_type ON network_traffic_rules(rule_type);
CREATE INDEX idx_network_rules_traffic_type ON network_traffic_rules(traffic_type);
CREATE INDEX idx_network_rules_enabled ON network_traffic_rules(enabled);

-- Table for network traffic logs (from devices)
CREATE TABLE network_traffic_logs (
    id SERIAL PRIMARY KEY,
    device_id INTEGER NOT NULL REFERENCES devices(id) ON DELETE CASCADE,
    customer_id INTEGER NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    rule_id INTEGER REFERENCES network_traffic_rules(id) ON DELETE SET NULL,
    url TEXT,
    domain VARCHAR(255),
    ip_address VARCHAR(45), -- IPv6 compatible
    port INTEGER,
    app_package VARCHAR(255),
    action VARCHAR(20) NOT NULL, -- ALLOWED, BLOCKED
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_usage_bytes BIGINT DEFAULT 0
);

-- Indexes for traffic logs
CREATE INDEX idx_traffic_logs_device_id ON network_traffic_logs(device_id);
CREATE INDEX idx_traffic_logs_customer_id ON network_traffic_logs(customer_id);
CREATE INDEX idx_traffic_logs_timestamp ON network_traffic_logs(timestamp);
CREATE INDEX idx_traffic_logs_domain ON network_traffic_logs(domain);
CREATE INDEX idx_traffic_logs_action ON network_traffic_logs(action);

-- Table for device-specific network settings
CREATE TABLE device_network_settings (
    id SERIAL PRIMARY KEY,
    device_id INTEGER NOT NULL REFERENCES devices(id) ON DELETE CASCADE,
    customer_id INTEGER NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    filtering_enabled BOOLEAN DEFAULT FALSE,
    logging_enabled BOOLEAN DEFAULT TRUE,
    strict_mode BOOLEAN DEFAULT FALSE, -- Block all by default
    proxy_host VARCHAR(255),
    proxy_port INTEGER,
    proxy_enabled BOOLEAN DEFAULT FALSE,
    dns_over_https_enabled BOOLEAN DEFAULT FALSE,
    dns_over_https_url VARCHAR(255),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(device_id)
);

-- Indexes for device network settings
CREATE INDEX idx_device_net_settings_device_id ON device_network_settings(device_id);
CREATE INDEX idx_device_net_settings_customer_id ON device_network_settings(customer_id);

