package com.akhandelwal.convert

import org.apache.avro.Schema

import java.io.{File, OutputStream}
import java.nio.file.{Files, Path, Paths}
import org.apache.avro.file.CodecFactory
import org.apache.avro.io.EncoderFactory

/**
  * An [[OutputStream]] will write instances of T to an underlying
  * representation.
  *
  * There are three implementations of this stream
  *  - a Data stream,
  *
  * See the methods on the companion object to create instances of each
  * of these types of stream.
  */
trait OutputStream[T] extends AutoCloseable {
  def close(): Unit
  def flush(): Unit
  def fSync(): Unit
  def write(t: T): Unit
  def write(ts: Seq[T]): Unit = ts.foreach(write)
}

object OutputStream:

  /**
    * An [[OutputStream]] that does not write the schema.
    *
    * Use this when you want the smallest messages possible at the cost of not having the
    * schema available in the messages for downstream clients.
    */
  def binary[T](schema: Schema, encoder: Encoder[T]): OutputStreamBuilder[T] =
    given Encoder[T] = encoder
    new OutputStreamBuilder[T](schema, encoder, AvroFormat.Binary)

  def binary[T](using schemaFor: SchemaFor[T], encoder: Encoder[T]): OutputStreamBuilder[T] =
    new OutputStreamBuilder[T](schemaFor.schema, encoder, AvroFormat.Binary)

  /**
    * An [[OutputStream]] that writes the schema alongside data.
    *
    * This is the standard implementation for Avro.
    */
  def data[T](schema: Schema, encoder: Encoder[T]): OutputStreamBuilder[T] =
    new OutputStreamBuilder[T](schema, encoder, AvroFormat.Data)

  def data[T](using schemaFor: SchemaFor[T], encoder: Encoder[T]): OutputStreamBuilder[T] =
    new OutputStreamBuilder[T](schemaFor.schema, encoder, AvroFormat.Data)

class OutputStreamBuilder[T](schema: Schema, encoder: Encoder[T], format: AvroFormat) {
  def to(path: Path): OutputStreamBuilderWithSource[T] = to(Files.newOutputStream(path))
  def to(path: String): OutputStreamBuilderWithSource[T] = to(Paths.get(path))
  def to(file: File): OutputStreamBuilderWithSource[T] = to(file.toPath)
  def to(out: OutputStream): OutputStreamBuilderWithSource[T] =
    new OutputStreamBuilderWithSource(schema, encoder, format, out)
}

case class OutputStreamBuilderWithSource[T](schema: Schema,
                                                encoder: Encoder[T],
                                                format: AvroFormat,
                                                out: OutputStream,
                                                codec: CodecFactory = CodecFactory.nullCodec) {

  def withCodec(codec: CodecFactory) = copy(codec = codec)

  /**
    * Builds an [[OutputStream]]
    */
  def build(): OutputStream[T] = {
    given Encoder[T] = encoder
    format match {
      case AvroFormat.Data => new DataOutputStream[T](schema, out, codec)

    }
  }
}
