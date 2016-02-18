package com.nico.datasource.dat

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.sources.{PrunedScan, BaseRelation, TableScan}
import org.apache.spark.sql.types._
import org.apache.spark.sql.{Row, SQLContext}

/**
  * Created by anicolaspp on 1/8/16.
  */
class LegacyRelation(location: String, userSchema: StructType)(@transient val sqlContext: SQLContext)
  extends BaseRelation
      with TableScan with Serializable {

  override def schema: StructType = {
    if (this.userSchema != null) {
      return this.userSchema
    }
    else {
      return StructType(Seq(StructField("name", StringType, nullable = true), StructField("age", IntegerType, nullable = true)))
    }
  }

  private def castValue(value: String, toType: DataType) = toType match {
    case _: StringType      => value
    case _: IntegerType     => value.toInt
  }

  override def buildScan(): RDD[Row] = {
    val schemaFields = schema.fields
    val rdd = sqlContext.sparkContext.wholeTextFiles(location).map(x => x._2)

    val rows = rdd.map(file => {
      val lines = file.split("\n")

      val typedValues = lines.zipWithIndex.map{
        case (value, index) => {
          val dataType = schemaFields(index).dataType
          castValue(value, dataType)
        }
      }

      Row.fromSeq(typedValues)
    })

    rows
  }
}

object LegacyRelation {
  def apply(location: String, userSchema: StructType)(sqlContext: SQLContext): LegacyRelation =
    return new LegacyRelation(location, userSchema)(sqlContext)
}
