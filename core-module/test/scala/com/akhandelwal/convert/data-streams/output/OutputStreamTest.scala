package com.akhandelwal.convert.data-streams.output

import java.io.ByteArrayOutputStream

import com.akhandelwal.convert._
import org.apache.avro.file.{DataFileReader, SeekableByteArrayInput}
import org.apache.avro.generic.{GenericDatumReader, GenericRecord}
import org.apache.avro.io.DecoderFactory
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

trait OutputStreamTest extends AnyFunSuite with Matchers {

  def readData[T: SchemaFor](out: ByteArrayOutputStream): GenericRecord = readData(out.toByteArray)
  def readData[T: SchemaFor](bytes: Array[Byte]): GenericRecord = {
    val datumReader = new GenericDatumReader[GenericRecord](AvroSchema[T])
    val dataFileReader = new DataFileReader[GenericRecord](new SeekableByteArrayInput(bytes), datumReader)
    dataFileReader.next
  }

  def writeData[T: Encoder : SchemaFor](t: T): ByteArrayOutputStream = {
    val out = new ByteArrayOutputStream
    val avro = OutputStream.data[T].to(out).build()
    avro.write(t)
    avro.close()
    out
  }

  def readBinary[T: SchemaFor](out: ByteArrayOutputStream): GenericRecord = readBinary(out.toByteArray)
  def readBinary[T: SchemaFor](bytes: Array[Byte]): GenericRecord = {
    val datumReader = new GenericDatumReader[GenericRecord](AvroSchema[T])
    val decoder = DecoderFactory.get().binaryDecoder(new SeekableByteArrayInput(bytes), null)
    datumReader.read(null, decoder)
  }

  def writeBinary[T: Encoder : SchemaFor](t: T): ByteArrayOutputStream = {
    val out = new ByteArrayOutputStream
    val avro = OutputStream.binary[T].to(out).build()
    avro.write(t)
    avro.close()
    out
  }

  def readJson[T: SchemaFor](out: ByteArrayOutputStream): GenericRecord = readJson(out.toByteArray)
  def readJson[T: SchemaFor](bytes: Array[Byte]): GenericRecord = {
    val schema = AvroSchema[T]
    val datumReader = new GenericDatumReader[GenericRecord](schema)
    val decoder = DecoderFactory.get().jsonDecoder(schema, new SeekableByteArrayInput(bytes))
    datumReader.read(null, decoder)
  }

  def writeJson[T: Encoder : SchemaFor](t: T): ByteArrayOutputStream = {
    val out = new ByteArrayOutputStream
    val avro = OutputStream.json[T].to(out).build()
    avro.write(t)
    avro.close()
    out
  }

  def writeRead[T: Encoder : SchemaFor](t: T)(fn: GenericRecord => Any): Unit = {
    {
      val out = writeData(t)
      val record = readData(out)
      fn(record)
    }
    {
      val out = writeBinary(t)
      val record = readBinary(out)
      fn(record)
    }
    {
      val out = writeJson(t)
      val record = readJson(out)
      fn(record)
    }
  }
}
