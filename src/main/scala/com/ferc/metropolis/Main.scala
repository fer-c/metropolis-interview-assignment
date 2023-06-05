package com.ferc.metropolis

import org.apache.spark.SparkConf
import org.apache.spark.internal.Logging
import org.apache.spark.sql._

import java.io.{File, PrintWriter}
import java.time.temporal.WeekFields

object Main extends Logging {
  implicit val encoderWeekTotalTransaction: Encoder[WeekTotalTransaction] = Encoders.product[WeekTotalTransaction]
  implicit val encoderWeekTotalTransactionDiff: Encoder[WeekTotalTransactionDiff] = Encoders.product[WeekTotalTransactionDiff]
  val usage =
    """
  Usage: metropolis-interview-assignment filename


"""

  final def main(args: Array[String]): Unit = {
    if (args.length == 0) {
      println(usage)
      sys.exit(-1)
    }

    val filename = args(0)
    
    val conf = new SparkConf()
      .setMaster("local[*]") // Set the Spark master URL
      .setAppName("EmbeddedSparkNode") // Set an application name
    implicit val session: SparkSession = SparkSession.builder.config(conf).getOrCreate()
    val provider: TransactionDataSetProvider = new CSVTransactionDataSetProvider(filename)
    val data = provider.readTransactions()
      .filter(_.payment_status.equalsIgnoreCase("Payment Completed"))
    val resultA = exerciseA(data)
    printSiteResults("Result A", resultA)

    val resultB = exerciseB(data)
    printSiteResults("Result B", resultB)

    val resultC = exerciseC(data)
    printWeeklySiteResults("Result C", resultC)

    val resultD = exerciseD(data)
    printWeeklyDiffSiteResults("Result D", resultD)
    session.stop()
  }

  private def printSiteResults(title: String, result: Dataset[SiteTotalTransaction]): Unit = {
    val printable = result.filter(_.total != 0)
      .collect()
      .foldLeft(Map[String, Double]())((acc, r) => acc + (String.valueOf(r.site_id) -> r.total))
    println(s"$title: ${upickle.default.write(printable, indent = 4)}")
    val writer: PrintWriter = reportWriterFor(title)
    writer.println(s"${title}:\n${result.count()} items")
    result.collect().foreach(r => writer.println(s"\t${r.site_id}: ${r.total}"))
  }

  private def printWeeklySiteResults(title: String, result: Dataset[WeekTotalTransaction]): Unit = {
    val writer: PrintWriter = reportWriterFor(title)
    writer.println(s"${title}:\n${result.count()} items")
    result.collect().foreach(r => writer.println(s"\tSite ${r.site_id}, Week ${r.week_year}, $$${r.total}"))
    val printable = result.filter(_.total != 0)
      .collect()
      .foldLeft(Map[String, Map[String, Double]]())((acc, r) => {
        val siteId = String.valueOf(r.site_id)
        val values = acc.getOrElse(siteId, Map[String, Double]()) + (String.valueOf(r.week_year) -> r.total)
        acc.removed(siteId) + ( siteId -> values )
      })
    println(s"$title: ${upickle.default.write(printable, indent = 4)}")
  }

  private def printWeeklyDiffSiteResults(title: String, result: Dataset[WeekTotalTransactionDiff]): Unit = {
    val printable = result.filter(_.total != 0)
      .collect()
      .foldLeft(Map[String, Map[String, Double]]())((acc, r) => {
        val siteId = String.valueOf(r.site_id)
        val values = acc.getOrElse(siteId, Map[String, Double]()) + (r.week_range-> r.total)
        acc.removed(siteId) + ( siteId -> values )
      })
    println(s"$title:\n${upickle.default.write(printable, indent = 4)}")
    val writer: PrintWriter = reportWriterFor(title)
    writer.println(s"${title}: ${result.count()} items")
    result.collect().foreach(r => writer.println(s"\tSite ${r.site_id}, Week ${r.week_range}, ${r.sign}$$${Math.abs(r.total)}"))
  }

  private def reportWriterFor(title: String) = {
    new PrintWriter(new File(s"logs/report${title.replaceAll(" ", "")}.txt"))
  }

  private def exerciseA(data: Dataset[Transaction]): Dataset[SiteTotalTransaction] = {
    totalsBySiteDS(data)
  }

  private def exerciseB(data: Dataset[Transaction]): Dataset[SiteTotalTransaction] = {
    totalsBySiteDS(data.filter(isWeekendDay _))
  }

  private def exerciseC(data: Dataset[Transaction]): Dataset[WeekTotalTransaction] = {
    totalBySiteAndWeek(data)
  }

  private def exerciseD(data: Dataset[Transaction]): Dataset[WeekTotalTransactionDiff] = {
    val totalSiteWeek = totalBySiteAndWeek(data)

    // Join the result against itself to calculate the diff with previous week
    val result: Dataset[WeekTotalTransactionDiff] = totalSiteWeek.as("r1").join(totalSiteWeek.as("r2"))
      .where("r1.site_id = r2.site_id and (r1.week_year-1) = r2.week_year")
      .map(r => {
        val diff = r.getAs[Double](2) - r.getAs[Double](5)
        val week = r.getAs[Int](1)
        val weekRange = if(week <= 1) week.toString else s"${week-1}-${week}"
        WeekTotalTransactionDiff(
          site_id = r.getAs(0),
          week_year = week,
          week_range = weekRange,
          sign = if (diff > 0) "+" else if (diff < 0) "-" else "",
          total = diff
        )
      })
      .orderBy(Transaction.site_id, Transaction.week_year)
    result
  }

  /**
   * @return The sum of price grouping by site_id
   */
  private def totalsBySiteDS(data: Dataset[Transaction]): Dataset[SiteTotalTransaction] =
    data.groupBy(Transaction.site_id)
      .agg(functions.sum(Transaction.price).alias(Transaction.total))
      .orderBy(Transaction.site_id)
      .as(Encoders.product[SiteTotalTransaction])

  /**
   * @return If the record belongs to a weekend day or not
   */
  private def isWeekendDay(r: Transaction): Boolean =
    Array(java.time.DayOfWeek.SUNDAY, java.time.DayOfWeek.SUNDAY)
      .contains(r.entry_time.getDayOfWeek)

  /**
   * @return the sum of price grouping by site and week (for all weeks in the year)
   */
  private def totalBySiteAndWeek(data: Dataset[Transaction]) = {
    val dataByWeek: Dataset[WeekTotalTransaction] = mapTransactionToWeekData(data)
    val result: Dataset[WeekTotalTransaction] =
      dataByWeek.union(expandAllYearWeeks(dataByWeek))
        .groupBy(Transaction.site_id, Transaction.week_year)
        .agg(functions.sum(Transaction.total).alias(Transaction.total))
        .orderBy(Transaction.site_id, Transaction.week_year)
        .as[WeekTotalTransaction]
    result
  }

  /**
   * @return The dataset for all weeks in the year for every site in the dataset
   */
  private def expandAllYearWeeks(data: Dataset[WeekTotalTransaction]): Dataset[WeekTotalTransaction] =
    data.select(Transaction.site_id).
      flatMap(r => (1 to 52)
        .map(week => WeekTotalTransaction(
          site_id = r.getAs(Transaction.site_id),
          week_year = week,
          total = 0)))

  /**
   * @return the mapping from Transaction to WeekTotalTransaction
   */
  private def mapTransactionToWeekData(data: Dataset[Transaction]): Dataset[WeekTotalTransaction] =
    data.map(r =>
      WeekTotalTransaction(
        r.site_id,
        r.entry_time.get(WeekFields.ISO.weekOfWeekBasedYear()),
        r.price.get))

}
