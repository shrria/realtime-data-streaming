/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package flink.ecommerce;

import dto.Transaction;
import deserializer.TransactionDeserializationSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcStatementBuilder;

public class DataStreamJob {

	static final String TOPIC_NAME = System.getenv("KAFKA_TOPIC");
	static final String BOOTSTRAP_SERVERS = System.getenv("KAFKA_SERVER");
	static final String JOB_NAME = System.getenv("JOB_NAME");

	static final String POSTGRES_HOST = System.getenv("POSTGRES_HOST");
	static final String POSTGRES_PORT = System.getenv("POSTGRES_PORT");
	static final String POSTGRES_DB = System.getenv("POSTGRES_DB");
	static final String POSTGRES_USER = System.getenv("POSTGRES_USER");
	static final String POSTGRES_PASSWORD = System.getenv("POSTGRES_PASSWORD");

	public static void main(String[] args) throws Exception {
		// Sets up the execution environment, which is the main entry point
		// to building Flink applications.
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		KafkaSource<Transaction> source = KafkaSource.<Transaction>builder()
				.setBootstrapServers(BOOTSTRAP_SERVERS)
				.setProperty("partition.discovery.interval.ms", "1000")
				.setTopics(TOPIC_NAME)
				.setStartingOffsets(OffsetsInitializer.earliest())
				.setValueOnlyDeserializer(new TransactionDeserializationSchema())
				.build();

		DataStreamSource<Transaction> sourceStream = env.fromSource(source, WatermarkStrategy.noWatermarks(), "kafka");

		DataStream<Transaction> transactionStream = sourceStream.map(transaction -> {
			return transaction;
		});


		// Create transactions table
		String createTableQuery = "CREATE TABLE IF NOT EXISTS transactions (" +
			"transaction_id VARCHAR(255) PRIMARY KEY, " +
			"product_id VARCHAR(255), " +
			"product_name VARCHAR(255), " +
			"product_category VARCHAR(255), " +
			"product_price DOUBLE PRECISION, " +
			"product_quantity INTEGER, " +
			"product_brand VARCHAR(255), " +
			"currency VARCHAR(255), " +
			"customer_id VARCHAR(255), " +
			"transaction_date TIMESTAMP, " +
			"payment_method VARCHAR(255), " +
			"total_amount DOUBLE PRECISION)";

		transactionStream.addSink(JdbcSink.sink(createTableQuery,
			(JdbcStatementBuilder<Transaction>) (statement, transaction) -> {
			},
			getExecutionOptions(),
			getConnectionOptions()
		)).name("Create Transactions Table");

		// Insert transactions into transactions table
		String insertTransactionQuery = "INSERT INTO transactions (transaction_id, product_id, product_name, product_category, product_price, product_quantity, product_brand, currency, customer_id, transaction_date, payment_method, total_amount) " +
			"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
			"ON CONFLICT (transaction_id) DO UPDATE SET " +
			"product_id = EXCLUDED.product_id, " +
			"product_name = EXCLUDED.product_name, " +
			"product_category = EXCLUDED.product_category, " +
			"product_price = EXCLUDED.product_price, " +
			"product_quantity = EXCLUDED.product_quantity, " +
			"product_brand = EXCLUDED.product_brand, "+
			"currency = EXCLUDED.currency, " +
			"customer_id = EXCLUDED.customer_id, " +
			"transaction_date = EXCLUDED.transaction_date, " +
			"payment_method = EXCLUDED.payment_method, " +
			"total_amount = EXCLUDED.total_amount " +
			"WHERE transactions.transaction_id = EXCLUDED.transaction_id";

		transactionStream.addSink(JdbcSink.sink(
			insertTransactionQuery,
			(JdbcStatementBuilder<Transaction>) (statement, transaction) -> {
				statement.setString(1, transaction.getTransactionId());
				statement.setString(2, transaction.getProductId());
				statement.setString(3, transaction.getProductName());
				statement.setString(4, transaction.getProductCategory());
				statement.setDouble(5, transaction.getProductPrice());
				statement.setInt(6, transaction.getProductQuantity());
				statement.setString(7, transaction.getProductBrand());
				statement.setString(8, transaction.getCurrency());
				statement.setString(9, transaction.getCustomerId());
				statement.setTimestamp(10, transaction.getTransactionDate());
				statement.setString(11, transaction.getPaymentMethod());
				statement.setDouble(12, transaction.getTotalAmount());
			},
			getExecutionOptions(),
			getConnectionOptions()
		)).name("Insert Transactions");

		// Execute program, beginning computation.
		env.execute(JOB_NAME);
	}

	private static JdbcExecutionOptions getExecutionOptions() {
		return JdbcExecutionOptions.builder()
				.withBatchSize(1000)
				.withBatchIntervalMs(200)
				.withMaxRetries(5)
				.build();
	}

	private static JdbcConnectionOptions getConnectionOptions() {
		return new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
				.withUrl("jdbc:postgresql://" + POSTGRES_HOST + ":" + POSTGRES_PORT + "/" + POSTGRES_DB)
				.withDriverName("org.postgresql.Driver")
				.withUsername(POSTGRES_USER)
				.withPassword(POSTGRES_PASSWORD)
				.build();
	}
}
