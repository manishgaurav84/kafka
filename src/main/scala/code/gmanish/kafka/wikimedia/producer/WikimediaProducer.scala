package code.gmanish.kafka.wikimedia.producer

import com.launchdarkly.eventsource.{EventHandler, EventSource}
import com.sun.xml.internal.bind.v2.TODO
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig}
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.log4j.{BasicConfigurator, Level, Logger}

import java.net.URI
import java.util.Properties
import java.util.concurrent.TimeUnit

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

    val producer  = new KafkaProducer[String, String](props)

    val topic = ConfigManager.getKafkaTopics().toString

    val eventHandler = new WikimediaChangeHandler(producer, topic)
    val url = ConfigManager.getWikiMediaURl()
    val builder = new EventSource.Builder(eventHandler, URI.create(url))

    val eventSource = builder.build()

    eventSource.start()

    // produce for 10 minutes and block the program
    TimeUnit.MINUTES.sleep(10)
  }
}
