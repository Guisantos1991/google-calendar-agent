CREATE TABLE message_log (
                             id BIGSERIAL PRIMARY KEY,
                             channel_user_id BIGINT NOT NULL,
                             direction VARCHAR(10) NOT NULL,
                             text TEXT,
                             raw_payload_json JSONB,
                             detected_intent VARCHAR(100),
                             created_at TIMESTAMP NOT NULL DEFAULT NOW(),

                             CONSTRAINT fk_message_log_channel_user
                                 FOREIGN KEY (channel_user_id)
                                     REFERENCES channel_user(id)
                                     ON DELETE CASCADE
);