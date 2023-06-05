package com.ferc.metropolis

import org.apache.spark.sql.types

import java.time.LocalDateTime

case class Transaction(transaction_id: Long,
                       site_id: Long,
                       user_id: Long,
                       vehicle_id: Long,
                       payment_status: String,
                       entry_time: LocalDateTime,
                       exit_time: Option[LocalDateTime],
                       price: Option[Double])

case class WeekTotalTransaction(site_id: Long,
                                week_year: Int,
                                total: Double)

case class WeekTotalTransactionDiff(site_id: Long,
                                    week_year: Int,
                                    week_range: String,
                                    sign: String,
                                    total: Double)

case class SiteTotalTransaction(site_id: Long,
                                total: Double)

object Transaction {
  val transaction_id: String = "transaction_id"
  val site_id: String = "site_id"
  val user_id: String = "user_id"
  val vehicle_id: String = "vehicle_id"
  val payment_status: String = "payment_status"
  val entry_time: String = "entry_time"
  val exit_time: String = "exit_time"
  val price: String = "price"
  val week_year: String = "week_year"
  val total: String = "total"
  val sign: String = "sign"

  val schema = new types.StructType()
    .add(types.StructField(name = transaction_id, dataType = types.LongType, nullable = false))
    .add(types.StructField(name = site_id, dataType = types.LongType, nullable = false))
    .add(types.StructField(name = user_id, dataType = types.LongType, nullable = false))
    .add(types.StructField(name = vehicle_id, dataType = types.LongType, nullable = false))
    .add(types.StructField(name = payment_status, dataType = types.StringType, nullable = false))
    .add(types.StructField(name = entry_time, dataType = types.TimestampType, nullable = false))
    .add(types.StructField(name = exit_time, dataType = types.TimestampType, nullable = true))
    .add(types.StructField(name = price, dataType = types.DoubleType, nullable = true));

}
