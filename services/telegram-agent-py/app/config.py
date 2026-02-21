from pydantic_settings import BaseSettings, SettingsConfigDict
from typing import Optional


class Settings(BaseSettings):
    model_config = SettingsConfigDict(env_file=".env", extra="ignore")

    calendar_core_base_url: str = "http://localhost:8080"
    default_timezone: str = "America/Sao_Paulo"
    telegram_bot_token: Optional[str] = None
    telegram_webhook_secret: Optional[str] = None


settings = Settings()
