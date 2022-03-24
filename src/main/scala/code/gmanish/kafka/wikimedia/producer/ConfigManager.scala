package code.gmanish.kafka.wikimedia.producer

import com.typesafe.config.ConfigFactory

object ConfigManager {
  private val config = ConfigFactory.load("application.conf")
  config.resolve()

  println(config)

  def getKafkaTopics() = {
    config.getString("KafkaConfig.Topics").split(",").toSet
  }

  def getKafkaBrokers() = {
    config.getString("KafkaConfig.Brokers")
  }

  def getWikiMediaURl() = {
    config.getString("WikiMedia.Url")
  }

}
