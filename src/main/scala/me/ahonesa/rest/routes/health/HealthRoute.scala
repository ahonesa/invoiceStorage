package me.ahonesa.rest.routes.health

import javax.ws.rs.Path

import akka.http.scaladsl.server.Directives._
import io.swagger.annotations._
import me.ahonesa.rest.utils.CommonJsonFormats
import spray.json._
import spray.json.DefaultJsonProtocol._

import scala.concurrent.ExecutionContext

@Path("/health")
@Api(value = "/health", produces = "application/json")
case class HealthRoute(implicit executionContext: ExecutionContext) extends CommonJsonFormats {

  val healthPath = "health"

  val route = pathPrefix(healthPath) {
    getHealth
  }

  @ApiOperation(httpMethod = "GET", value = "Return Hello World")
  def getHealth =
      get {
        complete { 200 -> "Hello, world!".toJson }
      }

}
