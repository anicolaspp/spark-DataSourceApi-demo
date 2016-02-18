package com.nico.datasource

import akka.event.Logging.LogLevel
import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by anicolaspp on 1/8/16.
  */
object app {


  def add(a: Int) (f: Int => Int): Int = f(a)

  class MyList[A] extends Traversable[A] {
    override def foreach[U](f: (A) => U): Unit = {

    }
  }


  def main(args: Array[String]) {


    val f = add(5) {x => x + 5}

    add(5)(a => 1 + 2)



    val config = new SparkConf().setAppName("testing provider")

    val sc = new SparkContext(config)
    sc.setLogLevel("WARN")
    val sqlContext = new SQLContext(sc)

    println("Here")

    val df = sqlContext.read.format("com.nico.datasource.dat").load("/Users/anicolaspp/data/")   //.load("/Users/nperez/person.dat")

    df.printSchema()
    df.show()

    val join = df.join(df, "name")

    join.show()

//    df.write.format("com.nico.datasource.dat").save("/Users/anicolaspp/data/output")

  }
}
