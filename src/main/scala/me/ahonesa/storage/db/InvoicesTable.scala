package me.ahonesa.storage.db

import com.outworkers.phantom.dsl.{PartitionKey, Table}
import com.outworkers.phantom.keys.PrimaryKey
import me.ahonesa.core.models.{Invoice, InvoicePayment, InvoiceSummary}


abstract class InvoicesTable extends Table[InvoicesTable, Invoice] {

  override def tableName: String = "invoices"

  object customer_id extends StringColumn with PartitionKey

  object invoice_id extends StringColumn with PrimaryKey

  object invoice_date extends DateColumn

  object invoice_summary extends JsonColumn[InvoiceSummary]

  object invoice_payments extends JsonSetColumn[InvoicePayment]

}
