CREATE TABLE conversation_state (
                                    id BIGSERIAL PRIMARY KEY,
                                    channel_user_id BIGINT NOT NULL,
                                    active_intent VARCHAR(100),
                                    state_json JSONB,
                                    expires_at TIMESTAMP,
                                    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

                                    CONSTRAINT fk_conversation_state_channel_user
                                        FOREIGN KEY (channel_user_id)
                                            REFERENCES channel_user(id)
                                            ON DELETE CASCADE
);