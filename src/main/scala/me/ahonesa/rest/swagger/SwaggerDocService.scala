package me.ahonesa.rest.swagger

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.github.swagger.akka.model.Info
import com.github.swagger.akka.{HasActorSystem, SwaggerHttpService}
import me.ahonesa.rest.routes.customers.CustomersRoute

case class SwaggerDocService(system: ActorSystem) extends SwaggerHttpService with HasActorSystem {
  import scala.reflect.runtime.{universe => ru}

  override implicit val actorSystem: ActorSystem = system
  override implicit val materializer: ActorMaterializer = ActorMaterializer()
  override val apiTypes = Seq(ru.typeOf[CustomersRoute])
  override val host = "localhost:12345" //the url of your api, not swagger's json endpoint
  override val basePath = "/"    //the basePath for the API you are exposing
  override val apiDocsPath = "api-docs" //where you want the swagger-json endpoint exposed
  override val info = Info() //provides license and other description details
}
