# kafka
All Kafka projects

Wikimedia recent change logs are sent to Kafka cluster and cosumed by the client to sink it into the Elastic Search

1. Docker setup for Kafka single and multi node cluster
2. Docker setup for ELasticSearch and Kibana
3. KafkProducer -> Subscribes to wikimedia eventsource and send the streaming data from wikimedia to kafka brkers
4. KafkaConsumer -> Writes the data to Elastic Search
