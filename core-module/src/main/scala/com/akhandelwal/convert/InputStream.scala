package com.akhandelwal.convert

import java.io.{ByteArrayInputStream, File, InputStream}
import java.nio.ByteBuffer
import java.nio.file.{Files, Path, Paths}
import org.apache.avro.Schema
import scala.util.Try

trait InputStream[T] extends AutoCloseable {

  /**
    * Closes this stream and any underlying resources.
    */
  def close(): Unit

  /**
    * Returns an iterator for the values of T in the stream.
    */
  def iterator: Iterator[T]

  /**
    * Returns an iterator for values of Try[T], so that any
    * decoding issues are wrapped.
    */
  def tryIterator: Iterator[Try[T]]
}

object InputStream {

  /**
    * Creates a new [[InputStreamBuilder]] that will read from binary
    * encoded files.
    */
  def binary[T: Decoder]: InputStreamBuilder[T] = new InputStreamBuilder[T](AvroFormat.Binary)

  /**
    * Creates a new [[InputStreamBuilder]] that will read from binary
    * encoded files with the schema present.
    */
  def data[T: Decoder]: InputStreamBuilder[T] = new InputStreamBuilder[T](AvroFormat.Data)

  /**
    * Creates a new [[InputStreamBuilder]] that will read from json
    * encoded files.
    */
  def json[T: Decoder]: InputStreamBuilder[T] = new InputStreamBuilder[T](AvroFormat.Json)
}


class InputStreamBuilder[T: Decoder](format: AvroFormat) {
  def from(path: Path): InputStreamBuilderWithSource[T] = from(Files.newInputStream(path))
  def from(path: String): InputStreamBuilderWithSource[T] = from(Paths.get(path))
  def from(file: File): InputStreamBuilderWithSource[T] = from(file.toPath)
  def from(in: InputStream): InputStreamBuilderWithSource[T] = new InputStreamBuilderWithSource(format, in)
  def from(bytes: Array[Byte]): InputStreamBuilderWithSource[T] = from(new ByteArrayInputStream(bytes))
  def from(buffer: ByteBuffer): InputStreamBuilderWithSource[T] = from(new ByteArrayInputStream(buffer.array))
}


class InputStreamBuilderWithSource[T: Decoder](format: AvroFormat, in: InputStream) {

  /**
    * Builds an [[InputStream]] with the specified writer schema.
    */
  def build(writerSchema: Schema) = format match {
    case AvroFormat.Data => new DataInputStream[T](in, Some(writerSchema))
    }

  /**
    * Builds an [[InputStream]] that uses the schema present in the file.
    * This method does not work for binary or json formats because those
    * formats do not store the schema.
    */
  def build = format match {
    case AvroFormat.Data => new DataInputStream[T](in, None)
    case _ => throw new ConvertConfigurationException("Must specify a schema for binary or json formats")
  }
}
