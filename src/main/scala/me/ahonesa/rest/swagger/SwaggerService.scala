package me.ahonesa.rest.swagger

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.github.swagger.akka.model.Info
import com.github.swagger.akka.{HasActorSystem, SwaggerHttpService}
import me.ahonesa.rest.routes.customers.CustomersRoute
import me.ahonesa.rest.routes.health.HealthRoute
import me.ahonesa.rest.routes.invoices.InvoicesRoute
import me.ahonesa.rest.routes.payments.PaymentsRoute

case class SwaggerService(system: ActorSystem) extends SwaggerHttpService with HasActorSystem {
  import scala.reflect.runtime.{universe => ru}

  override implicit val actorSystem: ActorSystem = system
  override implicit val materializer: ActorMaterializer = ActorMaterializer()
  override val apiTypes = Seq(ru.typeOf[CustomersRoute], ru.typeOf[InvoicesRoute], ru.typeOf[PaymentsRoute], ru.typeOf[HealthRoute])
  override val host = "localhost:12345"
  override val basePath = "/"
  override val apiDocsPath = "api-docs"
  override val info = Info(version = "0.1")
}
