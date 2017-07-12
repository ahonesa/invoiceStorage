package me.ahonesa

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import me.ahonesa.rest.http.HttpService
import me.ahonesa.rest.services.CustomersService
import me.ahonesa.rest.utils.Config
import me.ahonesa.storage.StorageConnector

import scala.concurrent.ExecutionContext

object Main extends App with StorageConnector with Config {

  implicit val actorSystem = ActorSystem()
  implicit val executor: ExecutionContext = actorSystem.dispatcher
  implicit val log: LoggingAdapter = Logging(actorSystem, getClass)
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val usersService = new CustomersService

  val httpService = new HttpService(usersService)

  Http().bindAndHandle(httpService.routes, httpHost, httpPort)

}
