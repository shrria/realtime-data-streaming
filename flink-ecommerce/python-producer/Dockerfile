FROM python:3.12-slim-bullseye
COPY --from=ghcr.io/astral-sh/uv:0.6.6 /uv /uvx /bin/
ENV PYTHONUNBUFFERED=1

WORKDIR /app

COPY . .
RUN uv sync --frozen

ENTRYPOINT ["uv", "run", "python", "kafka-stream.py"]