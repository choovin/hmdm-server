-- White-Label / Rebranding Settings
-- Feature 10: White-Label Software

-- Add rebranding columns to customers table
ALTER TABLE customers ADD COLUMN IF NOT EXISTS custom_logo VARCHAR(255);
ALTER TABLE customers ADD COLUMN IF NOT EXISTS custom_favicon VARCHAR(255);
ALTER TABLE customers ADD COLUMN IF NOT EXISTS custom_css TEXT;
ALTER TABLE customers ADD COLUMN IF NOT EXISTS primary_color VARCHAR(7);
ALTER TABLE customers ADD COLUMN IF NOT EXISTS secondary_color VARCHAR(7);
ALTER TABLE customers ADD COLUMN IF NOT EXISTS accent_color VARCHAR(7);
ALTER TABLE customers ADD COLUMN IF NOT EXISTS custom_email_header TEXT;
ALTER TABLE customers ADD COLUMN IF NOT EXISTS custom_email_footer TEXT;
ALTER TABLE customers ADD COLUMN IF NOT EXISTS custom_email_subject_prefix VARCHAR(100);
ALTER TABLE customers ADD COLUMN IF NOT EXISTS custom_login_page_html TEXT;
ALTER TABLE customers ADD COLUMN IF NOT EXISTS hide_powered_by BOOLEAN DEFAULT FALSE;
ALTER TABLE customers ADD COLUMN IF NOT EXISTS custom_copyright_text VARCHAR(255);
ALTER TABLE customers ADD COLUMN IF NOT EXISTS enable_custom_domain BOOLEAN DEFAULT FALSE;
ALTER TABLE customers ADD COLUMN IF NOT EXISTS custom_domain VARCHAR(255);

-- Create table for white-label APK builds
CREATE TABLE IF NOT EXISTS white_label_builds (
    id SERIAL PRIMARY KEY,
    customer_id INTEGER NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    build_name VARCHAR(100) NOT NULL,
    app_name VARCHAR(100) NOT NULL,
    package_name VARCHAR(100) NOT NULL,
    version_code INTEGER NOT NULL DEFAULT 1,
    version_name VARCHAR(20) NOT NULL DEFAULT '1.0',
    icon_path VARCHAR(255),
    splash_screen_path VARCHAR(255),
    custom_strings TEXT,
    custom_colors TEXT,
    server_url VARCHAR(255) NOT NULL,
    build_status VARCHAR(20) DEFAULT 'pending',
    build_log TEXT,
    download_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    built_at TIMESTAMP,
    UNIQUE(customer_id, package_name)
);

CREATE INDEX IF NOT EXISTS idx_white_label_builds_customer ON white_label_builds(customer_id);
CREATE INDEX IF NOT EXISTS idx_white_label_builds_status ON white_label_builds(build_status);

-- Create table for email template customizations
CREATE TABLE IF NOT EXISTS email_templates (
    id SERIAL PRIMARY KEY,
    customer_id INTEGER NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    template_type VARCHAR(50) NOT NULL,
    subject VARCHAR(255),
    body_html TEXT,
    body_text TEXT,
    is_custom BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(customer_id, template_type)
);

CREATE INDEX IF NOT EXISTS idx_email_templates_customer ON email_templates(customer_id);

-- Insert default email template types
INSERT INTO email_templates (customer_id, template_type, subject, is_custom) VALUES
(0, 'password_reset', 'Password Recovery', FALSE),
(0, 'signup_confirmation', 'Account Registration', FALSE),
(0, 'device_enrollment', 'Device Enrollment', FALSE),
(0, 'alert_notification', 'Alert Notification', FALSE)
ON CONFLICT DO NOTHING;

-- Add white-label permissions
INSERT INTO user_role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM user_roles r, permissions p
WHERE r.name IN ('Super Admin', 'Admin')
AND p.name IN (
    'plugin_whitelabel_access',
    'plugin_whitelabel_build_apk',
    'plugin_whitelabel_email_templates',
    'plugin_whitelabel_custom_domain'
)
ON CONFLICT DO NOTHING;
