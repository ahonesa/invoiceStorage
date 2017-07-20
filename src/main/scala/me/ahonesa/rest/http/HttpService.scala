package me.ahonesa.rest.http

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import me.ahonesa.rest.routes.customers.CustomersRoute
import me.ahonesa.rest.routes.health.HealthRoute
import me.ahonesa.rest.routes.invoices.InvoicesRoute
import me.ahonesa.rest.routes.payments.PaymentsRoute
import me.ahonesa.rest.services.{CustomerService, InvoiceService}
import me.ahonesa.rest.swagger.SwaggerService
import me.ahonesa.rest.utils.CorsSupport

import scala.concurrent.ExecutionContext

class HttpService(usersService: CustomerService, invoicesService: InvoiceService)(implicit executionContext: ExecutionContext, system: ActorSystem)
  extends CorsSupport {

  val customersRoute = CustomersRoute(usersService)
  val invoicesRoute = InvoicesRoute(invoicesService)
  val paymentsRoute = PaymentsRoute(invoicesService)
  val healthRoute = HealthRoute()

  val routes =
      corsHandler {
        SwaggerService(system).routes ~
        customersRoute.route ~
        invoicesRoute.route ~
        paymentsRoute.route ~
        healthRoute.route
      }

}
