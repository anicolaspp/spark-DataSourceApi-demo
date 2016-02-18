package com.nico.datasource.dat

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, DataFrame, SaveMode, SQLContext}
import org.apache.spark.sql.sources.{CreatableRelationProvider, BaseRelation, RelationProvider, SchemaRelationProvider}
import org.apache.spark.sql.types._


/**
  * Created by anicolaspp on 1/8/16.
  */
class DefaultSource extends RelationProvider with SchemaRelationProvider with CreatableRelationProvider {

  override def createRelation(sqlContext: SQLContext, parameters: Map[String, String]): BaseRelation = {
    createRelation(sqlContext, parameters, null)
  }

  override def createRelation(sqlContext: SQLContext, parameters: Map[String, String], schema: StructType): BaseRelation = {
    parameters.getOrElse("path", sys.error("'path' must be specified for CSV data."))

    return LegacyRelation(parameters.get("path").get, schema)(sqlContext)
  }

  def saveAsCsvFile(data: DataFrame, path: String) = {
    val dataCustomIterator = data.rdd.map(row => {
      val values = row.toSeq.map(value => value.toString)

      values.mkString(",")
    })

    dataCustomIterator.saveAsTextFile(path)
  }

  override def createRelation(sqlContext: SQLContext, mode: SaveMode, parameters: Map[String, String], data: DataFrame): BaseRelation = {
    saveAsCsvFile(data, parameters.get("path").get)

//
//    val canSaveData = mode match {
//      case SaveMode.Append => sys.error("Append mode is not supported")
//      case SaveMode.Overwrite =>
//    }

    createRelation(sqlContext, parameters, data.schema)
  }
}
