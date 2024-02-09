# kafka-flink-postgres

## Screenshots

![example1](./staticfiles/example1.png)
![example2](./staticfiles/example2.png)

## Tech Stack

- Apache Kafka (Confluent)
- Apache Zookeeper (Confluent)
- Apache Flink
- PostgreSQL

## Setup

1. Prepare jars for Flink

- `cd flink-processor`
- `mvn clean package`

2. Start Docker

- `cd docker`
- `docker-compose up -d`

## References

- [Building a Real-Time Data Streaming Pipeline using Apache Kafka, Flink and Postgres](https://www.youtube.com/watch?v=FoypLT2W91c)
