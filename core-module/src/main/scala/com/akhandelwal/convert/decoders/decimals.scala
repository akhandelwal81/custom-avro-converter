package com.akhandelwal.convert

import java.nio.ByteBuffer

import org.apache.avro.LogicalTypes.Decimal
import org.apache.avro.generic.GenericFixed
import org.apache.avro.{Conversions, Schema, SchemaBuilder}

import scala.math.BigDecimal.RoundingMode
import scala.math.BigDecimal.RoundingMode.RoundingMode

trait DecimalDecoders:
  given Decoder[Decimal] = new Decoder[Decimal] :
    override def decode(schema: Schema): Any => Decimal = {
      schema.getType match {
        case Schema.Type.BYTES => DecimalBytesDecoder.decode(schema)
        case Schema.Type.STRING => DecimalStringDecoder.decode(schema)
        case Schema.Type.FIXED => DecimalFixedDecoder.decode(schema)
        case t =>
          throw new Avro4sConfigurationException(
            s"Unable to create Decoder with schema type $t, only bytes, fixed, and string supported")
      }
    }

object DecimalBytesDecoder extends Decoder[Decimal] {
  override def decode(schema: Schema): Any => Decimal = {
    require(schema.getType == Schema.Type.BYTES)

    val logical = schema.getLogicalType.asInstanceOf[Decimal]
    val converter = new Conversions.DecimalConversion()
    val rm = java.math.RoundingMode.HALF_UP

    { value =>
      value match {
        case bb: ByteBuffer => converter.fromBytes(bb, schema, logical)
        case bytes: Array[Byte] => converter.fromBytes(ByteBuffer.wrap(bytes), schema, logical)
        case _ => throw new Avro4sDecodingException(s"Unable to decode '$value' to Decimal via ByteBuffer", value)
      }
    }
  }
}


object DecimalStringDecoder extends Decoder[Decimal] {
  override def decode(schema: Schema): Any => Decimal = {
    val decode = Decoder[String].decode(schema)
    { value => Decimal(decode(value)) }
  }
}

object DecimalFixedDecoder extends Decoder[Decimal] {
  override def decode(schema: Schema): Any => Decimal = {
    require(schema.getType == Schema.Type.FIXED)

    val logical = schema.getLogicalType.asInstanceOf[Decimal]
    val converter = new Conversions.DecimalConversion()
    val rm = java.math.RoundingMode.HALF_UP

    { value =>
      value match {
        case f: GenericFixed => converter.fromFixed(f, schema, logical)
        case _ => throw new Avro4sDecodingException(s"Unable to decode $value to Decimal via GenericFixed", value)
      }
    }
  }

}
