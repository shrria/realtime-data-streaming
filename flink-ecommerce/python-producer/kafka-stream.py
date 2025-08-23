import logging
import random
from datetime import datetime
import json
import time

from faker import Faker
from confluent_kafka import SerializingProducer

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

KAFKA_SERVER = "localhost:9092"
TOPIC_NAME = "financial-transactions"

faker = Faker()


def random_price():
    return round(random.uniform(10, 1000), 2)


PRODUCTS = [
    {
        "Id": "product_a",
        "Name": "laptop",
        "Category": "electronics",
        "Brand": "brand_a",
        "Price": random_price(),
    },
    {
        "Id": "product_b",
        "Name": "mobile",
        "Category": "electronics",
        "Brand": "brand_b",
        "Price": random_price(),
    },
    {
        "Id": "product_c",
        "Name": "tablet",
        "Category": "electronics",
        "Brand": "brand_c",
        "Price": random_price(),
    },
    {
        "Id": "product_d",
        "Name": "watch",
        "Category": "fashion",
        "Brand": "brand_d",
        "Price": random_price(),
    },
    {
        "Id": "product_e",
        "Name": "shirt",
        "Category": "fashion",
        "Brand": "brand_e",
        "Price": random_price(),
    },
    {
        "Id": "product_f",
        "Name": "shoes",
        "Category": "fashion",
        "Brand": "brand_f",
        "Price": random_price(),
    },
    {
        "Id": "product_g",
        "Name": "book",
        "Category": "stationary",
        "Brand": "brand_g",
        "Price": random_price(),
    },
    {
        "Id": "product_h",
        "Name": "pen",
        "Category": "stationary",
        "Brand": "brand_h",
        "Price": random_price(),
    },
    {
        "Id": "product_i",
        "Name": "apple",
        "Category": "groceries",
        "Brand": "brand_i",
        "Price": random_price(),
    },
]


def generate_sales_transactions():
    user = faker.simple_profile()

    product = random.choice(PRODUCTS)
    quantity = random.randint(1, 10)

    return {
        "transactionId": faker.uuid4(),
        "productId": product["Id"],
        "productName": product["Name"],
        "productCategory": product["Category"],
        "productPrice": product["Price"],
        "productQuantity": quantity,
        "productBrand": product["Brand"],
        "currency": random.choice(["USD", "EUR", "GBP"]),
        "customerId": user["username"],
        "transactionDate": datetime.now().strftime("%Y-%m-%dT%H:%M:%S.%f%z"),
        "paymentMethod": random.choice(
            ["credit_card", "debit_card", "paypal", "bank_transfer"]
        ),
        "totalAmount": product["Price"] * quantity,
    }


def delivery_report(err, msg):
    if err is not None:
        logger.error(f"Message delivery failed: {err}")
    else:
        logger.info(
            f"Message delivered to {msg.topic()} [{msg.partition()}] at offset {msg.offset()}"
        )


if __name__ == "__main__":
    producer = SerializingProducer({"bootstrap.servers": KAFKA_SERVER})

    current_time = datetime.now()

    while (datetime.now() - current_time).total_seconds() < 120:
        try:
            transaction = generate_sales_transactions()

            producer.produce(
                TOPIC_NAME,
                key=transaction["transactionId"],
                value=json.dumps(transaction),
                on_delivery=delivery_report,
            )

            producer.poll(0)

            # Wait for 0.5 to 2 seconds before producing next message
            time.sleep(random.uniform(0.5, 2))
        except BufferError as e:
            logger.error("Buffer full! Waiting...")
            time.sleep(1)
        except Exception as e:
            logger.error(f"Error producing transaction: {e}")
            time.sleep(1)
