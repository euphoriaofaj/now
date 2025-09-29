UPDATE users SET balance = 0.00 WHERE balance IS NULL;
ALTER TABLE users
  MODIFY COLUMN balance DECIMAL(19,2) NOT NULL DEFAULT 0.00;

UPDATE calls SET sum_decimal = 0.00 WHERE sum_decimal IS NULL;
UPDATE calls SET commission = 0.00 WHERE commission IS NULL;

ALTER TABLE calls
  MODIFY COLUMN sum_decimal DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  MODIFY COLUMN commission  DECIMAL(10,2) NOT NULL DEFAULT 0.00;

UPDATE deposits SET coin_decimal = 0.00 WHERE coin_decimal IS NULL;
UPDATE deposits SET won_decimal  = 0.00 WHERE won_decimal  IS NULL;

ALTER TABLE deposits
  MODIFY COLUMN coin_decimal DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  MODIFY COLUMN won_decimal  DECIMAL(10,2) NOT NULL DEFAULT 0.00;
