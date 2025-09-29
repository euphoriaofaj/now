ALTER TABLE translator_profiles
  ADD COLUMN rating_avg   DECIMAL(3,2) NOT NULL DEFAULT 0.00,
  ADD COLUMN rating_count INT          NOT NULL DEFAULT 0;