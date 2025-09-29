SET @col_exists := (
  SELECT COUNT(*)
  FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'files'
    AND COLUMN_NAME = 'type'
);

SET @sql := IF(@col_exists > 0,
  'ALTER TABLE files DROP COLUMN type',
  'SELECT 1'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
