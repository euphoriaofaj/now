CREATE TABLE transactions (
  id               BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id          BIGINT        NOT NULL,
  type             VARCHAR(50)   NOT NULL,
  amount           DECIMAL(19,2) NOT NULL,
  status           VARCHAR(30)   NOT NULL,
  before_balance   DECIMAL(19,2) NOT NULL,
  after_balance    DECIMAL(19,2) NOT NULL,
  correlation_id   VARCHAR(100)  NULL,
  description      VARCHAR(500)  NULL,

  created_at       DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at       DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

  CONSTRAINT fk_txn_user FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE RESTRICT ON UPDATE CASCADE,

  CONSTRAINT chk_txn_amount_nonneg CHECK (amount >= 0),
  CONSTRAINT chk_txn_before_nonneg CHECK (before_balance >= 0),
  CONSTRAINT chk_txn_after_nonneg  CHECK (after_balance  >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_txn_user ON transactions(user_id);
CREATE INDEX idx_txn_corr ON transactions(correlation_id);
