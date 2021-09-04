package com.akhandelwal.convert

import org.apache.avro.Schema

/**
  * Creates an Avro Schema for an arbitrary type T.
  *
  * This type is called [[AvroSchema]] and not just [[Schema]] to facilitate easy
  * importing when mixing with [[org.apache.avro.Schema]].
  */
object AvroSchema {

  /**
    * Generates an [[org.apache.avro.Schema]] for a type T using default configuration.
    *
    * Requires an instance of [[SchemaFor]] which is usually
    * generated by the custome-converter macros.
    */
  def apply[T](using schemaFor: SchemaFor[T]): Schema = schemaFor.schema
}