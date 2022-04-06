package code.gmanish.kafka.wikimedia.producer

import com.launchdarkly.eventsource.{EventHandler, EventSource}
import com.sun.deploy.net.HttpRequest
import com.sun.xml.internal.bind.v2.TODO
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.log4j.{BasicConfigurator, Level, Logger}
import code.gmanish.kafka.wikimedia.config.ConfigManager

import java.net.URI
import java.util.Properties
import java.util.concurrent.TimeUnit
import com.sun.deploy.net.HttpRequest
import sun.net.www.http.HttpClient

import java.net.URI

object WikimediaProducer {

//  Logger.getLogger("org").setLevel(Level.ALL)


  BasicConfigurator.configure()
  val LOG = Logger.getLogger(WikimediaProducer.getClass.toString)

  @throws(classOf[InterruptedException])
  def main(args: Array[String]): Unit = {
    LOG.info("Here is the Producer...")

    val bootstrapServer = ConfigManager.getKafkaBrokers()

    val props = new Properties
    props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer)
    props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
    props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)

    //confis for kafka <=2.8
    props.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true")
    props.setProperty(ProducerConfig.ACKS_CONFIG, "all")
    props.setProperty(ProducerConfig.RETRIES_CONFIG, (Int.MaxValue).toString)

    //thoughput and compression
//    props.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy")
//    props.setProperty(ProducerConfig.LINGER_MS_CONFIG, "20")
//    props.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, (32*1024).toString)


    val producer  = new KafkaProducer[String, String](props)
    val topic = ConfigManager.getKafkaTopics().mkString
    val eventHandler = new WikimediaChangeHandler(producer, topic)
    val url = ConfigManager.getWikiMediaURl()
    LOG.info(URI.create(url))
    val builder = new EventSource.Builder(eventHandler, URI.create(url))
    val eventSource = builder.build
    eventSource.start()
    TimeUnit.SECONDS.sleep(10)
    eventSource.close()

  }
}
