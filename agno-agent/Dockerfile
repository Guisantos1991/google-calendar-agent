FROM python:3.12-slim

WORKDIR /app

# Instala dependências
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copia código fonte
COPY app.py .
COPY src/ ./src/

EXPOSE 8000
CMD ["fastapi", "run", "app.py", "--host", "0.0.0.0", "--port", "8000"]