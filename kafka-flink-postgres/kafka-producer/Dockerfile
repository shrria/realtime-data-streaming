FROM python:3.9-slim

RUN mkdir /code
WORKDIR /code

COPY requirements.txt .
RUN set -ex; \
    pip install --no-cache-dir -r requirements.txt

COPY producer.py wait-for-it.sh /code/

CMD ./wait-for-it.sh -s -t 30 $ZOOKEEPER_SERVER -- ./wait-for-it.sh -s -t 30 $KAFKA_SERVER -- python -u producer.py