import logging
from kafka import KafkaConsumer

KAFKA_SERVER = "localhost:19092"
TOPIC = 'users_created'

consumer = KafkaConsumer(
    TOPIC,
    bootstrap_servers=[KAFKA_SERVER],
    group_id='users_created_group',
    auto_offset_reset='earliest'
)

if __name__ == "__main__":
    logging.info("Starting Kafka consumer")
    for message in consumer:
        print(message)