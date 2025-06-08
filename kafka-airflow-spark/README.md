# Kafka-Airflow-Spark

A real-time data streaming pipeline that demonstrates end-to-end data engineering using Apache Kafka, Apache Airflow, Apache Spark, and Cassandra. This project simulates a data pipeline that streams user data from a public API, processes it through Kafka, and stores it in Cassandra.

## Tech Stack

- **Apache Kafka (Confluent)** - Message broker for real-time data streaming
- **Apache Zookeeper (Confluent)** - Distributed coordination service for Kafka
- **Apache Airflow** - Workflow orchestration and scheduling
- **PostgreSQL** - Database for Airflow metadata
- **Apache Spark (Bitnami)** - Distributed computing system for data processing
- **Cassandra** - NoSQL database for storing streamed data

## Prerequisites

- Docker
- Python 3.10+

## Project Structure

```
kafka-airflow-spark/
├── airflow/
│   ├── dags/           # Airflow DAG definitions
│   ├── requirements.txt # Python dependencies
│   └── script/         # Airflow setup scripts
├── docker/
│   ├── docker-compose.yaml # Container orchestration
│   └── .env.example    # Environment variables template
└── spark_stream.py     # Spark streaming application
```

## Setup Instructions

1. Clone the repository:

   ```bash
   git clone <repository-url>
   cd kafka-airflow-spark
   ```

2. Start the services:

   ```bash
   docker-compose --env-file .env.example up -d
   ```

## Component Access Points

### Kafka Control Center

- URL: `http://localhost:9021`
- Features:
  - Monitor topics and messages
  - View cluster health
  - Manage topics and configurations

### Airflow Web Interface

- URL: `http://localhost:8080`
- Default credentials:
  - Username: `admin`
  - Password: `admin`
- Features:
  - Monitor DAGs
  - View task logs
  - Trigger manual runs

### Spark Master

- URL: `http://localhost:9090`
- Features:
  - Monitor Spark applications
  - View worker status
  - Access application logs

## Data Flow

1. **Data Generation**: Airflow DAG (`kafka_stream.py`) fetches random user data from an API
2. **Data Streaming**: Processed data is streamed to Kafka topic `users_created`
3. **Data Processing**: Spark application (`spark_stream.py`) consumes data from Kafka
4. **Data Storage**: Processed data is stored in Cassandra database

## Running the Spark Application

To submit the Spark streaming job:

```bash
spark-submit --master spark://localhost:7077 spark_stream.py
```

## Troubleshooting

### Common Issues

1. **Kafka Connection Issues**

   - Ensure Zookeeper is running: `docker ps | grep zookeeper`
   - Check Kafka broker logs: `docker logs broker`

2. **Airflow Issues**

   - Check webserver logs: `docker logs webserver`
   - Verify database connection: `docker logs postgres`

3. **Spark Issues**
   - Monitor worker status at Spark Master UI
   - Check worker logs: `docker logs spark-worker`

### Service Health Checks

- Kafka: `http://localhost:9021/health`
- Airflow: `http://localhost:8080/health`
- Spark: `http://localhost:9090`

## References

- [Realtime Data Streaming | End To End Data Engineering Project](https://www.youtube.com/watch?v=GqAcTrqKcrY)
