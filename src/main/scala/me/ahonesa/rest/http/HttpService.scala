package me.ahonesa.rest.http

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import me.ahonesa.rest.routes.customers.CustomersRoute
import me.ahonesa.rest.services.CustomersService
import me.ahonesa.rest.swagger.SwaggerDocService
import me.ahonesa.rest.utils.CorsSupport

import scala.concurrent.ExecutionContext

class HttpService(usersService: CustomersService)(implicit executionContext: ExecutionContext, system: ActorSystem)
  extends CorsSupport {

  val customersRoute = new CustomersRoute(usersService)

  val routes =
      SwaggerDocService(system).routes ~
      corsHandler {
        customersRoute.route
      }

}
