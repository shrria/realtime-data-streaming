# Kafka-Flink-Postgres

A real-time data streaming pipeline that demonstrates end-to-end data engineering using Apache Kafka, Apache Flink, and PostgreSQL. This project showcases how to build a robust streaming pipeline for processing and storing real-time data.

## Screenshots

![example1](./staticfiles/example1.png)
![example2](./staticfiles/example2.png)

## Tech Stack

- **Apache Kafka (Confluent)** - Message broker for real-time data streaming
- **Apache Zookeeper (Confluent)** - Distributed coordination service for Kafka
- **Apache Flink** - Stream processing framework for real-time analytics
- **PostgreSQL** - Relational database for data storage

## Prerequisites

- Docker
- Java 11+
- Maven

## Project Structure

```
kafka-flink-postgres/
├── flink-processor/    # Flink streaming application
│   ├── src/           # Source code
│   └── pom.xml        # Maven dependencies
├── docker/            # Docker configuration
│   ├── docker-compose.yaml
│   └── .env.example
└── staticfiles/       # Documentation assets
```

## Setup Instructions

1. Clone the repository:

   ```bash
   git clone <repository-url>
   cd kafka-flink-postgres
   ```

2. Build Flink Processor:

   ```bash
   cd flink-processor
   mvn clean package
   ```

3. Start the services:
   ```bash
   cd docker
   docker-compose --env-file .env.example up -d
   ```

## Component Access Points

### Kafka Control Center

- URL: `http://localhost:9021`
- Features:
  - Monitor topics and messages
  - View cluster health
  - Manage topics and configurations

### Flink Web UI

- URL: `http://localhost:8081`
- Features:
  - Monitor Flink jobs
  - View task managers
  - Access job metrics

### PostgreSQL

- Host: `localhost`
- Port: `5432`
- Default credentials:
  - Username: `postgres`
  - Password: `postgres`

## Data Flow

1. **Data Ingestion**: Data is streamed into Kafka topics
2. **Data Processing**: Flink jobs process the streaming data
3. **Data Storage**: Processed data is stored in PostgreSQL

## Running the Flink Application

The Flink application is automatically deployed when the Docker containers start. You can monitor the job status through the Flink Web UI at `http://localhost:8081`.

## Troubleshooting

### Common Issues

1. **Kafka Connection Issues**

   - Ensure Zookeeper is running: `docker ps | grep zookeeper`
   - Check Kafka broker logs: `docker logs broker`

2. **Flink Issues**

   - Check TaskManager logs: `docker logs taskmanager`
   - Verify JobManager status: `docker logs jobmanager`

3. **PostgreSQL Issues**
   - Check database logs: `docker logs postgres`
   - Verify connection: `docker exec -it postgres psql -U postgres`

### Service Health Checks

- Kafka: `http://localhost:9021/health`
- Flink: `http://localhost:8081/overview`
- PostgreSQL: `docker exec -it postgres pg_isready`

## References

- [Building a Real-Time Data Streaming Pipeline using Apache Kafka, Flink and Postgres](https://www.youtube.com/watch?v=FoypLT2W91c)
