CREATE TABLE fraud_cases (
    id BIGSERIAL PRIMARY KEY,

    case_number VARCHAR(100) NOT NULL UNIQUE,

    status VARCHAR(50) NOT NULL,

    transaction_id BIGINT NOT NULL UNIQUE,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_fraud_case_transaction
        FOREIGN KEY (transaction_id)
        REFERENCES transactions(id)
);