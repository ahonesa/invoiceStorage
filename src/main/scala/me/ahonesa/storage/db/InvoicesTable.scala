package me.ahonesa.storage.db

import com.outworkers.phantom.dsl._
import com.outworkers.phantom.keys.PrimaryKey
import me.ahonesa.core.models.{Invoice, InvoicePayment, InvoiceSummary}
import me.ahonesa.rest.utils.CommonJsonFormats
import spray.json._
import me.ahonesa.storage._

abstract class InvoicesTable extends Table[InvoicesTable, Invoice] {

  override def tableName: String = "invoices"

  object invoiceId extends StringColumn with PartitionKey

  object customerId extends StringColumn with Index

  object invoiceDate extends DateColumn

  object invoiceSummary extends JsonColumn[InvoiceSummary]

  object invoiceStatus extends StringColumn

  object invoicePayments extends JsonSetColumn[InvoicePayment]

}



