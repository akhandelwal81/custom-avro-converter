package com.akhandelwal.convert.schema

import com.akhandelwal.convert.{AvroSchema, BigDecimals, ScalePrecision, SchemaFor}
import org.apache.avro.Schema
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

case class DecimalSeqOption(biggies: Seq[Option[Decimal]])
case class DecimalSeq(biggies: Seq[Decimal])
case class DecimalDefault(decimal: Decimal = 964.55)

class DecimalSchemaTest extends AnyWordSpec with Matchers {

  "SchemaEncoder" should {
    "encode decimal" in {
      case class Test(decimal: Decimal)
      val schema = AvroSchema[Test]
      val expected = new org.apache.avro.Schema.Parser().parse(getClass.getResourceAsStream("/decimal.json"))
      schema shouldBe expected
    }
    "accept decimal as logical type on bytes with custom scale and precision" in {
      implicit val sp = ScalePrecision(8, 20)
      case class Test(decimal: Decimal)
      val schema = AvroSchema[Test]
      val expected = new org.apache.avro.Schema.Parser().parse(getClass.getResourceAsStream("/decimal-scale-and-precision.json"))
      schema shouldBe expected
    }

    "suport Option[Decimal] as a union" in {
      case class DecimalOption(decimal: Option[Decimal])
      val schema = AvroSchema[DecimalOption]
      val expected = new org.apache.avro.Schema.Parser().parse(getClass.getResourceAsStream("/decimal_option.json"))
      schema shouldBe expected
    }
    "Seq[Decimal] be represented as an array of logical types" in {
      val schema = AvroSchema[DecimalSeq]
      val expected = new org.apache.avro.Schema.Parser().parse(getClass.getResourceAsStream("/decimal_seq.json"))
      schema shouldBe expected
    }
    "Seq[Option[Decimal]] be represented as an array of unions of nulls/decimals" in {
      val schema = AvroSchema[DecimalSeqOption]
      val expected = new org.apache.avro.Schema.Parser().parse(getClass.getResourceAsStream("/decimal_seq_option.json"))
      schema shouldBe expected
    }
    "allow decimals to be encoded as STRING when custom typeclasses are provided" in {

      given SchemaFor[Decimal] = Decimals.AsString

      case class DecimalAsStringTest(decimal: Decimal)
      val schema = AvroSchema[DecimalAsStringTest]
      val expected = new org.apache.avro.Schema.Parser().parse(this.getClass.getResourceAsStream("/decimal_as_string.json"))
      schema shouldBe expected
    }
    "allow decimals to be encoded as FIXED when custom typeclasses are provided" in {

      given SchemaFor[Decimal] = SchemaFor[Decimal](Schema.createFixed("decimal", null, null, 55))

      case class DecimalAsFixedTest(decimal: Decimal)
      val schema = AvroSchema[DecimalAsFixedTest]
      val expected = new org.apache.avro.Schema.Parser().parse(this.getClass.getResourceAsStream("/decimal_as_fixed.json"))
      schema shouldBe expected
    }
  }
}
