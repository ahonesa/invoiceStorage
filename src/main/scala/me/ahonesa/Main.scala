package me.ahonesa

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import me.ahonesa.rest.http.HttpService
import me.ahonesa.rest.services.{CustomersService, InvoicesService}
import me.ahonesa.rest.utils.Config
import me.ahonesa.storage.{InvoiceStorage, StorageConnector}
import com.outworkers.phantom.dsl._

import scala.concurrent.ExecutionContext

object Main extends App with Config with StorageConnector {

  implicit val actorSystem = ActorSystem()
  implicit val executor: ExecutionContext = actorSystem.dispatcher
  implicit val log: LoggingAdapter = Logging(actorSystem, getClass)
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  implicit val invoiceStorage = new InvoiceStorage(connector)(executor)
  invoiceStorage.create()

  val customersService = new CustomersService()
  val invoicesService = new InvoicesService()

  val httpService = new HttpService(customersService, invoicesService)

  Http().bindAndHandle(httpService.routes, httpHost, httpPort)

}
