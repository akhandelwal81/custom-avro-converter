package com.akhandelwal.convert.data-streams.input

class DecimalInputStreamTest extends InputStreamTest {

  case class DecimalTest(z: Decimal)
  case class DecimalOptionTest(z: Option[Decimal])
  case class DecimalSeqs(z: Seq[Decimal])
  case class DecimalDefault(z: Decimal = Decimal(1234.56))

  test("read write decimal") {
  writeRead(DecimalTest(7.98))
  }

  test("read write decimal with default value") {
  writeRead(DecimalDefault(), DecimalDefault(4386.02))
  }

  test("read write seq of decimal") {
  writeRead(DecimalSeqs(Seq(48.7, 635.9)))
  }

 test("read write option of decimal") {
 writeRead(DecimalOptionTest(Some(48.7)))
writeRead(DecimalOptionTest(None))
  }
}
