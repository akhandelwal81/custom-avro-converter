package com.akhandelwal.convert.schemas

import com.akhandelwal.convert.{DefaultFieldMapper, FieldMapper, SchemaFor}
import org.apache.avro.{Schema, SchemaBuilder}

trait CollectionSchemas:

  private def buildIterableSchemaFor[M[X] <: Iterable[X], T](using schemaFor: SchemaFor[T]): SchemaFor[M[T]] =
    schemaFor.map(SchemaBuilder.array.items(_))

  given[T](using schemaFor: SchemaFor[T]): SchemaFor[Array[T]] = new SchemaFor[Array[T]] :
    override def schema: Schema = Schema.createArray(schemaFor.schema)

  given[T](using schemaFor: SchemaFor[T]): SchemaFor[Seq[T]] = buildIterableSchemaFor[Seq, T]

  given[T](using SchemaFor[T]): SchemaFor[Set[T]] = buildIterableSchemaFor[Set, T]

  given[T](using SchemaFor[T]): SchemaFor[Vector[T]] = buildIterableSchemaFor[Vector, T]

  given[T](using schemaFor: SchemaFor[T]): SchemaFor[List[T]] = buildIterableSchemaFor[List, T]

  given[V](using schemaFor: SchemaFor[V]): SchemaFor[Map[String, V]] =
    schemaFor.map(SchemaBuilder.map().values(_))
