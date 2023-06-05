package com.ferc.metropolis

import org.apache.spark.internal.Logging
import org.apache.spark.sql.{Dataset, Encoders, SparkSession}

class CSVTransactionDataSetProvider(val filename: String)(implicit val session: SparkSession) extends TransactionDataSetProvider with Logging {

  override def readTransactions(): Dataset[Transaction] = {


    val lines = session.read
      .format("csv")
      .schema(Transaction.schema)
      .option("header", "true") //first line in file has headers
      .option("mode", "DROPMALFORMED")
      .csv(filename)
      .as(Encoders.product[Transaction])

    log.info(s"Number of lines: ${lines.count()}")
    lines
  }
}
