ALTER TABLE password_resets
  ADD COLUMN expires_at DATETIME NULL AFTER reset_code,
  ADD COLUMN used       BIT(1)   NULL AFTER expires_at;

UPDATE password_resets
SET
    token = COALESCE(token, REPLACE(UUID(), '-', '')),
    phone = COALESCE(phone, 'UNKNOWN'),
    reset_code = COALESCE(reset_code, FLOOR(RAND()*9000) + 1000),
    expires_at = NOW() + INTERVAL 15 MINUTE,
    used = b'0';

ALTER TABLE password_resets
  MODIFY COLUMN token      VARCHAR(64)  NOT NULL,
  MODIFY COLUMN phone      VARCHAR(100) NOT NULL,
  MODIFY COLUMN reset_code INT          NOT NULL,
  MODIFY COLUMN expires_at DATETIME     NOT NULL,
  MODIFY COLUMN used       BIT(1)       NOT NULL;

CREATE INDEX idx_password_resets_phone_created_at ON password_resets (phone, created_at DESC);
CREATE INDEX idx_password_resets_verify ON password_resets (phone, reset_code, used, expires_at);
