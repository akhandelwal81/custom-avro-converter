package com.akhandelwal.convert.schema
import org.apache.avro.{LogicalTypes, Schema, SchemaBuilder}

case class ScalePrecision(scale: Int, precision: Int)

object ScalePrecision {
  given default: ScalePrecision = ScalePrecision(2, 8)
}

trait DecimalSchemas:
  given(using scapre: ScalePrecision): SchemaFor[Decimal] = new DecimalSchemaFor(scapre)

class DecimalSchemaFor(scapre: ScalePrecision) extends SchemaFor[Decimal] :
  override def schema: Schema = LogicalTypes.decimal(scapre.precision, sp.scale).addToSchema(SchemaBuilder.builder.bytesType)

object Decimals {
  val AsString: SchemaFor[Decimal] = SchemaFor[Decimal](SchemaBuilder.builder.stringType)
}
