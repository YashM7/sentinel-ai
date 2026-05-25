CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    entity_type VARCHAR(255) NOT NULL,
    entity_id VARCHAR(255) NOT NULL,
    action VARCHAR(255) NOT NULL,
    details VARCHAR(1000),
    created_at TIMESTAMP NOT NULL
);