import time
import random
import schedule
import json

from faker import Faker



def get_data():
    faker = Faker()

    data = {
        "city": faker.city(),
        "temperature": random.uniform(0.0, 100.0),
    }

    return data

def stream_data():
    from kafka import KafkaProducer
    import os

    kafka_broker = os.environ.get("KAFKA_SERVER")
    kafka_topic = os.environ.get("KAFKA_TOPIC")
    max_block_ms = 5000

    producer = KafkaProducer(
        bootstrap_servers=[kafka_broker],
        max_block_ms=max_block_ms,
    )

    while True:
        if (int(time.time()%60) % 5) != 0:  # 5 seconds
            continue

        try:
            data = get_data()
            print(f"Sending data to Kafka: {data}")
            producer.send(kafka_topic, json.dumps(data).encode("utf-8"))
        except Exception as e:
            print(f"Error while streaming data: {e}")
            continue

        time.sleep(1)


if __name__ == "__main__":
    print("Starting Kafka producer")

    schedule.every(5).seconds.do(stream_data)

    while True:
        schedule.run_pending()
        time.sleep(1)
