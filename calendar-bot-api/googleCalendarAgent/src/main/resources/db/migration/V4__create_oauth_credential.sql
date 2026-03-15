CREATE TABLE oauth_credential (
                                  id BIGSERIAL PRIMARY KEY,
                                  channel_user_id BIGINT NOT NULL,
                                  provider VARCHAR(50) NOT NULL,
                                  account_email VARCHAR(255),
                                  refresh_token TEXT,
                                  access_token TEXT,
                                  token_expiry TIMESTAMP,
                                  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                  updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

                                  CONSTRAINT fk_oauth_credential_channel_user
                                      FOREIGN KEY (channel_user_id)
                                          REFERENCES channel_user(id)
                                          ON DELETE CASCADE
);