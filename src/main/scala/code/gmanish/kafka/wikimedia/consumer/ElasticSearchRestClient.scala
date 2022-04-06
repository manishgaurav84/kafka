package code.gmanish.kafka.wikimedia.consumer
import code.gmanish.kafka.wikimedia.config.ConfigManager
import code.gmanish.kafka.wikimedia.producer.WikimediaProducer
import org.apache.http.HttpHost
import org.apache.http.auth.{AuthScope, UsernamePasswordCredentials}
import org.apache.http.impl.client.{BasicCredentialsProvider, DefaultConnectionKeepAliveStrategy}
import org.apache.log4j.{BasicConfigurator, Logger}
import org.elasticsearch.client.{RestClient, RestClientBuilder, RestHighLevelClient}

import java.net.URI

object ElasticSearchRestClient {
  BasicConfigurator.configure()
  val LOG = Logger.getLogger(ElasticSearchRestClient.getClass.toString)
  val connString = ConfigManager.getElasticSearchConnString()
  val connUri = URI.create(connString)
  LOG.info(s"connUri is $connUri")
  val PORT = connUri.getPort // ConfigManager.getElasticSearchPort.toInt
  val HOST = connUri.getHost //ConfigManager.getElasticSearchHost
  val SCHEME = connUri.getScheme //ConfigManager.getElasticSearchScheme

  val userInfo = connUri.getUserInfo
  val httpHost = new HttpHost(HOST, PORT, SCHEME)

  def restHighLevelClient:RestHighLevelClient = {
    new RestHighLevelClient(
      RestClient.builder(httpHost))
  }
//  def getRestHighLevelCLient:RestHighLevelClient => {
//    val
//    if (userInfo == null){
//    restHighLevelClient = new RestHighLevelClient(
//    RestClient.builder(httpHost))
//  }
//    else{
//    val auth = userInfo.split(":")
//    val credentialsProvider = new BasicCredentialsProvider
//    credentialsProvider.setCredentials(AuthScope.ANY,
//    new UsernamePasswordCredentials(auth(0), auth(1)))
//
//    restHighLevelClient = new RestHighLevelClient(
//    RestClient.builder(new HttpHost(HOST, PORT, SCHEME))
//    .setHttpClientConfigCallback(
//    httpAsyncClientBuilder => httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
//    .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy)
//    )
//    )
//
//  }


}
