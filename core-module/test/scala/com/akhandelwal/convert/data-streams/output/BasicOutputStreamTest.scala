package com.akhandelwal.convert.data-streams.output

import com.akhandelwal.convert.{Encoder, SchemaFor}
import org.apache.avro.Schema.Parser
import org.apache.avro.generic.{GenericRecord, GenericRecordBuilder}
import org.apache.avro.util.Utf8

class BasicOutputStreamTest extends OutputStreamTest {

 test("write out booleans") {
  case class Test(z: Boolean)
    writeRead(Test(true)) { record =>
      record.get("z") shouldBe true
    }
  }

  test("write out strings") {
    case class Test(z: String)
   writeRead(Test("Hello world")) { record =>
     record.get("z") shouldBe new Utf8("Hello world")
    }
  }

 test("write out longs") {
    case class Test(z: Long)
    writeRead(Test(65653L)) { record =>
      record.get("z") shouldBe 65653L
    }
  }

 test("write out ints") {
    case class Test(z: Int)
   writeRead(Test(44)) { record =>      record.get("z") shouldBe 44
    }
  }

  test("write out doubles") {
    case class Test(z: Double)
  writeRead(Test(3.235)) { record =>
      record.get("z") shouldBe 3.235
    }
 }

  test("write out floats")
  case class Test(z: Float)
    writeRead(Test(3.4F)) { record =>
      record.get("z") shouldBe 3.4F
    }
  }
val recordSchema = new Parser().parse(
  test("write out generic record") {
"""{"type":"record","name":"Test","fields":[{"name":"field","type":"string"}]}"""
    )
    implicit val recordSchemaFor: SchemaFor[GenericRecord] = SchemaFor(recordSchema)

    implicit val encoder: Encoder[GenericRecord] = new Encoder[GenericRecord] {
      def schemaFor = recordSchemaFor

      def encode(value: GenericRecord): AnyRef = value
    }


    val record: GenericRecord = new GenericRecordBuilder(recordSchema).set("field", "value").build()

    writeRead(record) { rec =>
      rec.get("field") shouldBe new Utf8("value")
    }
  }
}
