package code.gmanish.kafka.wikimedia.producer

import com.launchdarkly.eventsource.{EventHandler, MessageEvent}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.log4j.{BasicConfigurator, Level, Logger}

case class WikimediaChangeHandler(producer: KafkaProducer[String, String], topic:String)
  extends EventHandler{
  BasicConfigurator.configure()
  val LOG = Logger.getLogger(classOf[WikimediaChangeHandler].getName)

  override def onOpen(): Unit = {
    LOG.info("WikimediaChangeHandler:onOpen")
  }

  override def onClosed(): Unit = {
    LOG.info("OnClosed called.. Closing producer!!!")
    producer.close()
  }

  override def onMessage(s: String, messageEvent: MessageEvent): Unit = {
    //asyncrhonous
    LOG.info(s"Sending to $topic topic : ${messageEvent.getData}")
    producer.send(new ProducerRecord[String, String](topic, messageEvent.getData))
  }

  override def onComment(s: String): Unit = {LOG.info("onComment called..!!!")}

  override def onError(throwable: Throwable): Unit = {
    throwable.printStackTrace()
    LOG.error("Error occured while Stream Reading!!!")
  }
}
