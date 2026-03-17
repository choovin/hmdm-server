-- Feature 9: Device Contacts Management
-- Database migration for device contacts

-- Table for storing device contacts
CREATE TABLE device_contacts (
    id SERIAL PRIMARY KEY,
    device_id INTEGER NOT NULL REFERENCES devices(id) ON DELETE CASCADE,
    customer_id INTEGER NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    contact_id VARCHAR(255), -- ID from the device
    display_name VARCHAR(500) NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    phone_number VARCHAR(255),
    phone_number_normalized VARCHAR(255), -- Normalized for searching
    email VARCHAR(255),
    company VARCHAR(255),
    job_title VARCHAR(255),
    department VARCHAR(255),
    notes TEXT,
    photo_uri TEXT,
    is_managed BOOLEAN DEFAULT FALSE, -- Whether this contact is managed by MDM
    is_synced BOOLEAN DEFAULT TRUE, -- Whether this contact is synced to device
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_synced_at TIMESTAMP,
    UNIQUE(device_id, contact_id)
);

-- Indexes for device contacts
CREATE INDEX idx_device_contacts_device_id ON device_contacts(device_id);
CREATE INDEX idx_device_contacts_customer_id ON device_contacts(customer_id);
CREATE INDEX idx_device_contacts_name ON device_contacts(display_name);
CREATE INDEX idx_device_contacts_phone ON device_contacts(phone_number_normalized);
CREATE INDEX idx_device_contacts_managed ON device_contacts(is_managed) WHERE is_managed = TRUE;

-- Table for contact phone numbers (multiple per contact)
CREATE TABLE device_contact_phones (
    id SERIAL PRIMARY KEY,
    contact_id INTEGER NOT NULL REFERENCES device_contacts(id) ON DELETE CASCADE,
    phone_type VARCHAR(50), -- HOME, WORK, MOBILE, OTHER
    phone_number VARCHAR(255) NOT NULL,
    phone_number_normalized VARCHAR(255),
    is_primary BOOLEAN DEFAULT FALSE
);

CREATE INDEX idx_contact_phones_contact_id ON device_contact_phones(contact_id);

-- Table for contact emails (multiple per contact)
CREATE TABLE device_contact_emails (
    id SERIAL PRIMARY KEY,
    contact_id INTEGER NOT NULL REFERENCES device_contacts(id) ON DELETE CASCADE,
    email_type VARCHAR(50), -- HOME, WORK, OTHER
    email VARCHAR(255) NOT NULL,
    is_primary BOOLEAN DEFAULT FALSE
);

CREATE INDEX idx_contact_emails_contact_id ON device_contact_emails(contact_id);

-- Table for managed contact templates
CREATE TABLE contact_templates (
    id SERIAL PRIMARY KEY,
    customer_id INTEGER NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    template_name VARCHAR(255) NOT NULL,
    display_name VARCHAR(500),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    phone_number VARCHAR(255),
    email VARCHAR(255),
    company VARCHAR(255),
    job_title VARCHAR(255),
    department VARCHAR(255),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_contact_templates_customer_id ON contact_templates(customer_id);

-- Table for contact sync operations
CREATE TABLE contact_sync_operations (
    id SERIAL PRIMARY KEY,
    customer_id INTEGER NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    device_id INTEGER NOT NULL REFERENCES devices(id) ON DELETE CASCADE,
    operation_type VARCHAR(50) NOT NULL, -- ADD, UPDATE, DELETE
    contact_id INTEGER REFERENCES device_contacts(id),
    status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, SYNCED, FAILED
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP
);

CREATE INDEX idx_contact_sync_ops_device_id ON contact_sync_operations(device_id);
CREATE INDEX idx_contact_sync_ops_status ON contact_sync_operations(status);

