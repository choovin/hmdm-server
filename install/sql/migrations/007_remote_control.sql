-- Feature 5: Remote Control
-- Database migration for remote control sessions

-- Table for storing remote control sessions
CREATE TABLE remote_control_sessions (
    id SERIAL PRIMARY KEY,
    device_id INTEGER NOT NULL REFERENCES devices(id) ON DELETE CASCADE,
    customer_id INTEGER NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    user_id INTEGER REFERENCES users(id),
    session_token VARCHAR(255) UNIQUE NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, CONNECTING, CONNECTED, DISCONNECTED, ERROR
    session_type VARCHAR(20) DEFAULT 'VIEW', -- VIEW, CONTROL
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    connected_at TIMESTAMP,
    ended_at TIMESTAMP,
    duration_seconds INTEGER DEFAULT 0,
    remote_address VARCHAR(255),
    error_message TEXT,
    webrtc_offer TEXT,
    webrtc_answer TEXT,
    ice_candidates TEXT
);

-- Indexes for efficient queries
CREATE INDEX idx_remote_sessions_device_id ON remote_control_sessions(device_id);
CREATE INDEX idx_remote_sessions_customer_id ON remote_control_sessions(customer_id);
CREATE INDEX idx_remote_sessions_status ON remote_control_sessions(status);
CREATE INDEX idx_remote_sessions_token ON remote_control_sessions(session_token);

-- Table for remote control audit log
CREATE TABLE remote_control_audit (
    id SERIAL PRIMARY KEY,
    session_id INTEGER REFERENCES remote_control_sessions(id) ON DELETE CASCADE,
    device_id INTEGER NOT NULL,
    customer_id INTEGER NOT NULL,
    user_id INTEGER REFERENCES users(id),
    action VARCHAR(50) NOT NULL, -- SESSION_STARTED, SESSION_ENDED, COMMAND_SENT, etc.
    action_details TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_remote_audit_session_id ON remote_control_audit(session_id);
CREATE INDEX idx_remote_audit_device_id ON remote_control_audit(device_id);
CREATE INDEX idx_remote_audit_created_at ON remote_control_audit(created_at);
