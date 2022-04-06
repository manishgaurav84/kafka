package code.gmanish.kafka.wikimedia.config

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

  def getElasticSearchConnString(): String = {
    config.getString("Elastic.connString")
  }
  def getElasticSearchPort(): String = {
    config.getString("Elastic.port")
  }
  def getElasticSearchHost(): String = {
    config.getString("Elastic.host")
  }
  def getElasticSearchScheme(): String = {
    config.getString("Elastic.scheme")
  }

}
