package me.ahonesa.storage

import com.outworkers.phantom.connectors.{CassandraConnection, ContactPoints}
import me.ahonesa.rest.utils.Config

trait StorageConnector extends Config {
  lazy val connector: CassandraConnection = ContactPoints(hosts)
    .withClusterBuilder(_.withCredentials(username, password))
    .keySpace(keyspace)
}
