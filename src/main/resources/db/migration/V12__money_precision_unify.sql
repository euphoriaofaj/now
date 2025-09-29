ALTER TABLE deposits
  MODIFY won_decimal DECIMAL(19,2) NOT NULL,
  MODIFY coin_decimal DECIMAL(19,2) NOT NULL;

ALTER TABLE withdrawals
  MODIFY sum_decimal DECIMAL(19,2) NOT NULL;