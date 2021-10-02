package com.akhandelwal.convert.encoders
import org.apache.avro.Schema
import scala.jdk.CollectionConverters._
import scala.reflect.ClassTag

import com.akhandelwal.convert.Encoder

trait CollectionEncoders:

  private def iterableEncoder[T, C[P] <: Iterable[P]](encoder: Encoder[T]): Encoder[C[T]] = new Encoder[C[T]] {
    override def encode(schema: Schema): C[T] => Any = {
      require(schema.getType == Schema.Type.ARRAY)
      val elementEncoder = encoder.encode(schema.getElementType)
      { t => t.map(elementEncoder.apply).toList.asJava }
    }
  }

  given[T](using encoder: Encoder[T], tag: ClassTag[T]): Encoder[Array[T]] = new Encoder[Array[T]] {
    override def encode(schema: Schema): Array[T] => Any = {
      require(schema.getType == Schema.Type.ARRAY)
      val elementEncoder = encoder.encode(schema.getElementType)
      { t => t.map(elementEncoder.apply).toList.asJava }
    }
  }

  given[T](using encoder: Encoder[T]): Encoder[List[T]] = iterableEncoder(encoder)
  given[T](using encoder: Encoder[T]): Encoder[Seq[T]] = iterableEncoder(encoder)
  given[T](using encoder: Encoder[T]): Encoder[Set[T]] = iterableEncoder(encoder)
  given[T](using encoder: Encoder[T]): Encoder[Vector[T]] = iterableEncoder(encoder)

  given mapEncoder[T](using encoder: Encoder[T]): Encoder[Map[String, T]] = new MapEncoder[T](encoder)

class MapEncoder[T](encoder: Encoder[T]) extends Encoder[Map[String, T]] :
  override def encode(schema: Schema): Map[String, T] => Any = {
    val encodeT = encoder.encode(schema.getValueType)
    { value =>
      val map = new java.util.HashMap[String, Any]
      value.foreach { case (k, v) => map.put(k, encodeT.apply(v)) }
      map
    }
  }
