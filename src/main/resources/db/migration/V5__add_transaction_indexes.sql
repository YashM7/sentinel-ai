CREATE INDEX idx_transactions_user_created_at
ON transactions(user_id, created_at);

CREATE INDEX idx_transactions_status
ON transactions(status);

CREATE INDEX idx_transactions_transaction_id
ON transactions(transaction_id);