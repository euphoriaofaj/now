UPDATE translator_profiles SET is_available = 0 WHERE is_available IS NULL;

ALTER TABLE translator_profiles
  CHANGE COLUMN is_available is_verified TINYINT(1) NOT NULL DEFAULT 0;