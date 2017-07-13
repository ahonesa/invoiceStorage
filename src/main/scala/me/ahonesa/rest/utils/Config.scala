package me.ahonesa.rest.utils

import com.typesafe.config.ConfigFactory
import scala.collection.JavaConverters._

trait Config {

  private val config = ConfigFactory.load()
  private val httpConfig = config.getConfig("rest")

  val httpHost = httpConfig.getString("interface")
  val httpPort = httpConfig.getInt("port")

  val hosts = config.getStringList("cassandra.host").asScala.toSeq
  val keyspaceName = config.getString("cassandra.keyspace")
  val username = config.getString("cassandra.username")
  val password = config.getString("cassandra.password")

}
