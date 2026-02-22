from fastapi import FastAPI
from pydantic import BaseModel

app = FastAPI()

class AgentRequest(BaseModel):
    user_id: str
    timezone: str
    now: str
    message: str

@app.post("/chat")
def chat(req: AgentRequest):
    msg = req.message.lower().strip()

