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

public class DataStreamJob {

	static final String TOPIC_NAME = System.getenv("KAFKA_TOPIC");
	static final String BOOTSTRAP_SERVERS = System.getenv("KAFKA_SERVER");
	static final String JOB_NAME = System.getenv("JOB_NAME");

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

		transactionStream.print();

		// Execute program, beginning computation.
		env.execute(JOB_NAME);
	}
}
