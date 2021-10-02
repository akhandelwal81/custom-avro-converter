package com.akhandelwal.convert.data-streams.input

import com.akhandelwal.convert._

import scala.util.Failure

class BasicInputStreamTest extends InputStreamTest {

  case class BooleanTest(z: Boolean)
  case class StringTest(z: String)
  case class FloatTest(z: Float)
  case class DoubleTest(z: Double)
  case class IntTest(z: Int)
  case class LongTest(z: Long)

  test("Read and Write Booleans") {
    writeRead(BooleanTest(true))
  }

  test("Read and Skip corrupted data") {
    val items = tryReadData[StringTest](writeData(FloatTest(3.4F)).toByteArray).toSeq
    items.size shouldBe 1
    items.head shouldBe a[Failure[_]]
  }

  test("Read and Write String") {
    writeRead(StringTest("Hello world"))
  }

  test("Read and Write Long Types") {
    writeRead(LongTest(65653L))
  }

  test("Read and Write Int Types") {
    writeRead(IntTest(44))
  }

  test("Read and Write Double Types") {
    writeRead(DoubleTest(3.235))
  }

  test("Read and Write Float Types") {
    writeRead(FloatTest(3.4F))
  }
}
