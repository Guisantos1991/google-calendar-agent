CREATE TABLE channel_user (
                              id BIGSERIAL PRIMARY KEY,
                              channel VARCHAR(30) NOT NULL,
                              external_user_id VARCHAR(100) NOT NULL,
                              external_chat_id VARCHAR(100) NOT NULL,
                              display_name VARCHAR(255),
                              created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                              updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX ux_channel_user_channel_external_user
    ON channel_user(channel, external_user_id);
