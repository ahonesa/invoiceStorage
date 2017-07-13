package me.ahonesa.rest.routes.customers

import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.{Marshaller, ToResponseMarshaller}
import akka.http.scaladsl.model.HttpEntity.ChunkStreamPart
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, MediaTypes}
import akka.http.scaladsl.server.Directives._
import me.ahonesa.core.models.Response

trait CustomerMarshaller {

}
