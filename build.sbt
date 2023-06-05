ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.11"

lazy val root = (project in file("."))
  .settings(
    name := "metropolis-interview-assignment"
  )

javaOptions ++= Seq(
  "--add-opens", "java.base/sun.nio.ch=ALL-UNNAMED"
)

// https://mvnrepository.com/artifact/org.apache.spark/spark-core
libraryDependencies += "org.apache.spark" %% "spark-core" % "3.4.0"
// https://mvnrepository.com/artifact/org.apache.spark/spark-sql
libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.4.0"
// https://mvnrepository.com/artifact/org.apache.hadoop/hadoop-client-api
libraryDependencies += "org.apache.hadoop" % "hadoop-client" % "3.3.4"
libraryDependencies += "org.apache.hadoop" % "hadoop-client-api" % "3.3.4"

libraryDependencies += "org.apache.logging.log4j" % "log4j-api-scala_2.13" % "12.0"
libraryDependencies += "org.apache.logging.log4j" % "log4j-core" % "2.19.0" % Runtime

// https://mvnrepository.com/artifact/com.lihaoyi/upickle
libraryDependencies += "com.lihaoyi" %% "upickle" % "3.1.0"
