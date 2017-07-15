package me.ahonesa

import com.outworkers.phantom.dsl.Primitive
import me.ahonesa.core.models.{CustomerDetails, InvoicePayment, InvoiceSummary}
import me.ahonesa.rest.utils.CommonJsonFormats
import spray.json._

package object storage extends CommonJsonFormats {
  implicit val customerDetailsPrimitive: Primitive[CustomerDetails] = {
    Primitive.json[CustomerDetails](to => to.toJson.compactPrint)(jsonString => jsonString.parseJson.convertTo[CustomerDetails])
  }
  implicit val invoiceSummaryPrimitive: Primitive[InvoiceSummary] = {
    Primitive.json[InvoiceSummary](to => to.toJson.compactPrint)(jsonString => jsonString.parseJson.convertTo[InvoiceSummary])
  }
  implicit val invoicePaymentPrimitive: Primitive[InvoicePayment] = {
    Primitive.json[InvoicePayment](to => to.toJson.compactPrint)(jsonString => jsonString.parseJson.convertTo[InvoicePayment])
  }
}
