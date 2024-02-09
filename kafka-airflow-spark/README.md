# kafka-airflow-spark

## Tech Stack

- Apache Kafka (Confluent)
- Apache Zookeeper (Confluent)
- Apache Airflow
- PostgreSQL
- Apache Spark (Bitnami)
- Cassandra

## Setup

- `cd docker`
- `docker-compose --env-file .env.example up -d`

### Kafka

(Auto create topics)
Access the control center at `http://localhost:9021`

### Airflow

(Auto create scheduler and webserver)
Access the webserver at `http://localhost:8080`

### Spark

(Auto create spark master and worker)
Access the master at `http://localhost:9090`

Run the following command to start the submit the job to the spark cluster:

```
spark-submit --master spark://localhost:7077 spark_stream.py
```

## References

- [Realtime Data Streaming | End To End Data Engineering Project](https://www.youtube.com/watch?v=GqAcTrqKcrY)
