package com.akhandelwal.convert.examples

import com.akhandelwal.convert.AvroSchema
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

/**
  * Tests created from README examples
  *
  */
class Samples extends AnyWordSpec with Matchers {

  import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

  import com.akhandelwal.convert.{OutputStream, InputStream}

  case class Composer(name: String, birthplace: String, compositions: Seq[String])

  val frank = Composer("frank kirby", "germany", Seq("king of good times", "ancient treaure"))

  "AvroStream binary serialization" should {

    "round trip the objects " in {
      val baos = new ByteArrayOutputStream()
      val output = OutputStream.binary[Composer].to(baos).build()
      output.write(frank)
      output.close()

      val bytes = baos.toByteArray

      bytes shouldBe (Array[Byte](30, 101, 110, 110, 105, 111, 32, 109, 111, 114, 114, 105, 99, 111, 110, 101, 8, 114,
        111, 109, 101, 4, 28, 108, 101, 103, 101, 110, 100, 32, 111, 102, 32, 49, 57, 48, 48, 30, 101, 99, 115, 116,
        97, 115, 121, 32, 111, 102, 32, 103, 111, 108, 100, 0))

      val in = new ByteArrayInputStream(bytes)
      val input = InputStream.binary[Composer].from(in).build(AvroSchema[Composer])
      val result = input.iterator.toSeq
      result shouldBe Vector(frank)
    }
  }
}
