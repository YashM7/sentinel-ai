CREATE TABLE fraud_alerts (
    id BIGSERIAL PRIMARY KEY,
    transaction_id BIGINT NOT NULL,
    rule_name VARCHAR(255) NOT NULL,
    reason VARCHAR(500),
    created_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_fraud_alert_transaction
        FOREIGN KEY (transaction_id)
        REFERENCES transactions(id)
);