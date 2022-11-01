import com.mifmif.common.regex.Generex
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.PrivateMethodTester

import language.deprecated.symbolLiterals
import org.scalatest.matchers.should.Matchers

import java.util
import java.text.SimpleDateFormat
import java.time.{Instant, ZoneId}
import java.util.{Calendar, Date, TimeZone}
import scala.util.matching.Regex

// Tests for logic inside the lambda functions

class lambdaTimeIntervalPresentTests extends AnyFlatSpec with Matchers with PrivateMethodTester {
  behavior of "lambda function that checks time interval logic"
  private val lowestLogTime = "07:00:00.000"
  private val latestLogTime = "12:00:00.000"
  val pattern: Regex = "([a-c][e-g][0-3]|[A-Z][5-9][f-w]){5,15}".r
  val log1 = "20:50:35.672 [scala-execution-context-global-76] WARN  HelperUtils.Parameters$ - 3XJ~|hjhbB>IN:be2I7pQ5tae3bf0M7fZ6vV5qtmSB+'L@VN,O:I"
  val log2 = "17:59:15.524 [scala-execution-context-global-77] DEBUG HelperUtils.Parameters$ - Hxf6U=wULb,=#i1O9qce3H8rae0be2TLM^`#O&{?C^i-,X"
  val format = new SimpleDateFormat("HH:mm:ss.SSS")
  format.setTimeZone(TimeZone.getTimeZone("GMT"))
  it should "given a a time it should exist partially within the known interval" in {
    val lowestLogTimeDate = format.parse(lowestLogTime)
    val latestLogTimeDate = format.parse(latestLogTime)

    val test1LowTimeDate = format.parse("06:00:00.000")

    val test2LowTimeDate = format.parse("13:00:00.000")

    val result1 =(test1LowTimeDate.compareTo(latestLogTimeDate))
    val result2 =(test2LowTimeDate.compareTo(lowestLogTimeDate))

    result1 should be < 0

    result2 should be > 0
  }

  it should "given a a time that completely exists within the known interval" in {
    val lowestLogTimeDate = format.parse(lowestLogTime)
    val latestLogTimeDate = format.parse(latestLogTime)

    val test1LowTimeDate = format.parse("08:00:00.000")

    val test2LowTimeDate = format.parse("10:00:00.000")

    val result1 = (test1LowTimeDate.compareTo(latestLogTimeDate))
    val result2 = (test2LowTimeDate.compareTo(lowestLogTimeDate))

    result1 should be < 0

    result2 should be > 0
  }

  it should "given a a time that completely does not exists within the known interval" in {
    val lowestLogTimeDate = format.parse(lowestLogTime)
    val latestLogTimeDate = format.parse(latestLogTime)

    val test1LowTimeDate = format.parse("17:00:00.000")

    val test2LowTimeDate = format.parse("18:00:00.000")

    val result1 = (test1LowTimeDate.compareTo(latestLogTimeDate))
    val result2 = (test2LowTimeDate.compareTo(lowestLogTimeDate))

    result1 should be > 0

    result2 should be > 0
  }

  it should("splitting log entries must have expected length in timestamps") in {
    val exampleDateString = log1.substring(0,11)
    exampleDateString.length should be (11)

  }

  it should("splitting for the generated string at the end of a log entry should not cause an exception") in {
    val exampleDateString = log1.substring(0, 11)
    val strIndex = log1.indexOf("- ")
    val regex = log1.substring(strIndex, log1.length)
  }

  it should("splitting for the generated string on a log that has the regex pattern in the generated string should be found") in {
    val exampleDateString = log2.substring(0, 11)
    val strIndex = log2.indexOf("- ")
    val regex = log2.substring(strIndex, log2.length)
    val regexFound = pattern.findFirstMatchIn(regex)
    regexFound should not be (None)
  }







}