ALTER TABLE transactions
  ADD CONSTRAINT uq_txn_corr UNIQUE (correlation_id);