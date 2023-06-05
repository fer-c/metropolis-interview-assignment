package com.ferc.metropolis

import org.apache.spark.sql.Dataset

trait TransactionDataSetProvider {
  def readTransactions(): Dataset[Transaction]
}
