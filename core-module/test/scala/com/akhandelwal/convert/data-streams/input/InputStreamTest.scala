package com.akhandelwal.convert.data-streams.input

import java.io.ByteArrayOutputStream
import com.akhandelwal.convert._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import scala.util.Try

trait InputStreamTest extends AnyFunSuite with Matchers {

  def readData[T: SchemaFor : Decoder](out: ByteArrayOutputStream): T = readData(out.toByteArray)
  def readData[T: SchemaFor : Decoder](bytes: Array[Byte]): T = {
    InputStream.data.from(bytes).build(implicitly[SchemaFor[T]].schema).iterator.next()
  }

  def tryReadData[T: SchemaFor : Decoder](bytes: Array[Byte]): Iterator[Try[T]] = {
    InputStream.data.from(bytes).build(implicitly[SchemaFor[T]].schema).tryIterator
  }

  def writeData[T: Encoder : SchemaFor](t: T): ByteArrayOutputStream = {
    val out = new ByteArrayOutputStream
    val avro = OutputStream.data[T].to(out).build()
    avro.write(t)
    avro.close()
    out
  }

  def readBinary[T: SchemaFor : Decoder](out: ByteArrayOutputStream): T = readBinary(out.toByteArray)
  def readBinary[T: SchemaFor : Decoder](bytes: Array[Byte]): T = {
    InputStream.binary.from(bytes).build(implicitly[SchemaFor[T]].schema).iterator.next()
  }

  def writeBinary[T: SchemaFor : Encoder](t: T): ByteArrayOutputStream = {
    val out = new ByteArrayOutputStream
    val avro = OutputStream.binary[T].to(out).build()
    avro.write(t)
    avro.close()
    out
  }

  def readJson[T: SchemaFor : Decoder](out: ByteArrayOutputStream): T = readJson(out.toByteArray)
  def readJson[T](using schemaFor: SchemaFor[T], decoder: Decoder[T])(bytes: Array[Byte]): T = {
    InputStream.json[T].from(bytes).build(schemaFor.schema).iterator.next()
  }

  def writeJson[T: Encoder : SchemaFor](t: T): ByteArrayOutputStream = {
    val out = new ByteArrayOutputStream
    val avro = OutputStream.json[T].to(out).build()
    avro.write(t)
    avro.close()
    out
  }

  def writeRead[T: Encoder : Decoder : SchemaFor](t: T): Unit = {
    {
      val out = writeData(t)
      readData(out) shouldBe t
    }
    {
      val out = writeBinary(t)
      readBinary(out) shouldBe t
    }
    {
      val out = writeJson(t)
      readJson(out) shouldBe t
    }
  }

  def writeRead[T: Encoder : Decoder : SchemaFor](t: T, expected: T): Unit = {
    {
      val out = writeData(t)
      readData(out) shouldBe expected
    }
    {
      val out = writeBinary(t)
      readBinary(out) shouldBe expected
    }
    {
      val out = writeJson(t)
      readJson(out) shouldBe expected
    }
  }
}
