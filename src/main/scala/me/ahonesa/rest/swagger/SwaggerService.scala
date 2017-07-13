package me.ahonesa.rest.swagger

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.github.swagger.akka.model.Info
import com.github.swagger.akka.{HasActorSystem, SwaggerHttpService}
import io.swagger.models.auth.BasicAuthDefinition
import me.ahonesa.rest.routes.customers.CustomersRoute

case class SwaggerService(system: ActorSystem) extends SwaggerHttpService with HasActorSystem {
  import scala.reflect.runtime.{universe => ru}

  override implicit val actorSystem: ActorSystem = system
  override implicit val materializer: ActorMaterializer = ActorMaterializer()
  override val apiTypes = Seq(ru.typeOf[CustomersRoute])
  override val host = "localhost:12345"
  override val basePath = "/"
  override val apiDocsPath = "api-docs"
  override val info = Info(version = "0.1")
  override val securitySchemeDefinitions = Map("basicAuth" -> new BasicAuthDefinition())
}
