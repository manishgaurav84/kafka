package code.gmanish.kafka.wikimedia.consumer

import code.gmanish.kafka.wikimedia.config.ConfigManager
import code.gmanish.kafka.wikimedia.producer.WikimediaProducer
import code.gmanish.kafka.wikimedia.consumer.ElasticSearchRestClient
import org.apache.http.client.methods.HttpPost
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecords, KafkaConsumer}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.log4j.{BasicConfigurator, Logger}
import org.elasticsearch.action.bulk.BulkRequest
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.indices.{CreateIndexRequest, GetIndexRequest}
import org.elasticsearch.common.xcontent.XContentType

import java.net.URI
import java.time.Duration
import java.util.{Collections, Properties}
import com.google.gson
import com.google.gson.JsonParser

object WikimediaConsumer {

  BasicConfigurator.configure()
  val LOG = Logger.getLogger(WikimediaConsumer.getClass.toString)


  def extractID(json: String) = {
    //gson library
    JsonParser.parseString(json)
      .getAsJsonObject
      .get("meta")
      .getAsJsonObject
      .get("id")
      .getAsString
  }

  def main(args: Array[String]): Unit = {

    val esClient = ElasticSearchRestClient.restHighLevelClient

    val wikimediaConsumer = createKafkaConsumer


    try{
      val isIndexExists = esClient.indices().exists(new GetIndexRequest("wikimedia"), RequestOptions.DEFAULT)
      LOG.info(s"Wikimedia index is present: $isIndexExists")
      if(!isIndexExists){
        val indexRequest = new CreateIndexRequest("wikimedia")
        esClient.indices().create(indexRequest, RequestOptions.DEFAULT)
        LOG.info("Wikimedia index created !!")
      }else{
        LOG.info("Wikimedia index already exists!!!")
      }

      val topic = ConfigManager.getKafkaTopics().mkString
      wikimediaConsumer.subscribe(Collections.singleton(topic))

//      LOG.info(s"Consumer is subscribed to the topic $topic")
      while(true){
//        LOG.info(s"Consumer is subscribed to the topic $topic")
        try{
          val records = wikimediaConsumer.poll(Duration.ofMillis(3000))

          val recordCount = records.count()
          LOG.info(s"Recieved $recordCount record(s)")

          val esBulkRequest = new BulkRequest

          records.forEach((record) => {

            //Idempotence strategy
            //Strategy 1: define the id using Kafka Record coordinates
//          val recordId = record.topic() + "_" + record.partition() + "_" + record.offset()
            //Strategy 2: Extract the Id from the JSON message itself
          val recordId = extractID(record.value)
          val indexRequest = new IndexRequest("wikimedia")
            .source(record.value(), XContentType.JSON).id(recordId)

          val esResponse = esClient.index(indexRequest, RequestOptions.DEFAULT)
          LOG.info(s"Response is ${esResponse.getId}. Inserted ${record.key()} into wikimedia index!!!")
        })
        }catch {
          case e:Exception => e.printStackTrace()
        }
      }
    }catch {
      case e:Exception => LOG.error("Exception occured in Elastic!!!")
    }finally {
      esClient.close()
      wikimediaConsumer.close()
    }


  }

  def createKafkaConsumer():KafkaConsumer[String, String] ={
    val props = new Properties
    props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, ConfigManager.getKafkaBrokers())
    props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
    props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
    props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "wikimedia-consumers")
    props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")
    props.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
    props.setProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "2000")

    new KafkaConsumer[String, String](props)
  }
}

