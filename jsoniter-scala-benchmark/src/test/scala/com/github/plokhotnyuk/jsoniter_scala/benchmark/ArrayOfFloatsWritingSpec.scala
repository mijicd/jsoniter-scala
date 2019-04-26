package com.github.plokhotnyuk.jsoniter_scala.benchmark

import java.nio.charset.StandardCharsets.UTF_8

class ArrayOfFloatsWritingSpec extends BenchmarkSpecBase {
  private val benchmark: ArrayOfFloatsWriting = new ArrayOfFloatsWriting {
    private val values: Array[String] = Array(
      "7.038531e-26",
      "1.199999988079071",
      "3.4028235677973366e38",
      "7.006492321624086e-46"
    )

    setup()
    jsonBytes = (1 to size).map(i => values(i % values.length)).mkString("[", ",", "]").getBytes(UTF_8)
    obj = (1 to size).map(i => values(i % values.length).toFloat).toArray
    jsonString = obj.mkString("[", ",", "]")
    preallocatedBuf = new Array[Byte](jsonBytes.length + 100/*to avoid possible out of bounds error*/)
  }
  
  "ArrayOfFloatsWriting" should {
    "write properly" in {
      sameOrBetter(toString(benchmark.avSystemGenCodec()), benchmark.jsonString)
      sameOrBetter(toString(benchmark.circe()), benchmark.jsonString)
      sameOrBetter(toString(benchmark.dslJsonScala()), benchmark.jsonString)
      sameOrBetter(toString(benchmark.jacksonScala()), benchmark.jsonString)
      //FIXME: PreciseFloatSupport.enable() doesn't work sometime and Jsoniter Java serializes values rounded to 6 digits
      //sameOrBetter(toString(benchmark.jsoniterJava()), benchmark.jsonString)
      sameOrBetter(toString(benchmark.jsoniterScala()), benchmark.jsonString)
      sameOrBetter(toString(benchmark.preallocatedBuf, 0, benchmark.jsoniterScalaPrealloc()), benchmark.jsonString)
      //FIXME: Play-JSON serializes double values instead of float
      //sameOrBetter(toString(benchmark.playJson()), benchmark.jsonString)
      //FIXME: Spray-JSON serializes double values instead of float
      //sameOrBetter(toString(benchmark.sprayJson()), benchmark.jsonString)
      sameOrBetter(toString(benchmark.uPickle()), benchmark.jsonString)
    }
  }

  private[this] def sameOrBetter(actual: String, expected: String): Unit =
    actual.substring(1, actual.length - 1).split(',')
      .zip(expected.substring(1, expected.length - 1).split(',')).foreach { case (a, e) =>
      require(a.toFloat == e.toFloat && a.length <= e.length,
        s"expected the same or better: $e, but got: $a")
    }
}