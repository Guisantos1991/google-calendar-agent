FROM python:3.12-slim

WORKDIR /app
RUN pip install --no-cache-dir "fastapi[standard]" pydantic

COPY app.py /app/app.py

EXPOSE 8000
CMD ["fastapi", "run", "app.py", "--host", "0.0.0.0", "--port", "8000"]