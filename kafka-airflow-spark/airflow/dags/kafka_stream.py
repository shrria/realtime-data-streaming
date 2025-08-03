from datetime import datetime
from airflow import DAG
from airflow.operators.python import PythonOperator

default_args = {"owner": "beginner", "start_date": datetime(2024, 2, 3, 10, 00)}


KAFKA_BROKER = "kafka-node-1:9092"
TOPIC_NAME = "users_created"

def get_data():
    import requests

    url = "https://randomuser.me/api/"
    resp = requests.get(url)
    data = resp.json()["results"][0]

    return data


def format_data(raw_data):
    import uuid

    data = {}
    data["id"] = str(uuid.uuid4())
    data["first_name"] = raw_data["name"]["first"]
    data["last_name"] = raw_data["name"]["last"]
    data["gender"] = raw_data["gender"]
    data["address"] = (
        f"{str(raw_data['location']['street']['number'])}, {str(raw_data['location']['street']['name'])}, "
        f"{str(raw_data['location']['city'])}, {str(raw_data['location']['state'])}, {str(raw_data['location']['country'])}"
    )
    data["postcode"] = raw_data["location"]["postcode"]
    data["email"] = raw_data["email"]
    data["username"] = raw_data["login"]["username"]
    data["dob"] = raw_data["dob"]["date"]
    data["registered_date"] = raw_data["registered"]["date"]
    data["phone"] = raw_data["phone"]
    data["picture"] = raw_data["picture"]["medium"]
    return data


def stream_data():
    import json
    from kafka import KafkaProducer
    import time
    import logging

    max_block_ms = 5000
    producer = KafkaProducer(
        bootstrap_servers=[KAFKA_BROKER], max_block_ms=max_block_ms
    )

    current_time = time.time()
    while True:
        if time.time() > current_time + 60:  # 1 minute
            break

        try:
            new_data = get_data()
            new_user_data = format_data(new_data)
            producer.send(TOPIC_NAME, json.dumps(new_user_data).encode("utf-8"))
        except Exception as e:
            logging.error(f"Error while streaming data: {e}")
            continue


with DAG(
    "user_automation",
    default_args=default_args,
    schedule="@daily",
    catchup=False,
) as dag:

    streaming_task = PythonOperator(
        task_id="stream_data_from_api",
        python_callable=stream_data,
    )
