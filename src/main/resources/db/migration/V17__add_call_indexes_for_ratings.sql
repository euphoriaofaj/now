CREATE INDEX idx_calls_caller_recipient_join_end_status
  ON calls (caller_id, recipient_id, translator_has_joined, is_end_call, call_status);

CREATE INDEX idx_calls_caller_recipient_join_end_created
  ON calls (caller_id, recipient_id, translator_has_joined, is_end_call, created_at DESC);