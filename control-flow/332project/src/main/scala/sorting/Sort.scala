package sorting

import java.io._
import scala.jdk.CollectionConverters._
import scala.collection.mutable.ListBuffer
import scala.io.Source

class Sort {

  val _sampleSizeKB = 100
  val _sampleSize = _sampleSizeKB / 10

  def sortFile(path: String) = {
    val lines = Source.fromFile(path).getLines().map(_.splitAt(10)).toList
    val sortedLines = lines.sortBy(_._1)
    new PrintWriter(path) {
      for (line <- sortedLines) {
        write(line._1 + line._2 + "\r\n")
      }
      close
    }
  }

  def samplingFile(path: String, sampleSizeKB: Int = _sampleSizeKB) = {
    val linesList = Source.fromFile(path).getLines().map(_.splitAt(10)).toList
    val keyArray = linesList.map(_._1).toArray
    val sampleSize = _sampleSize
    val sampleArray = new Array[String](sampleSize)
    for (i <- 0 until sampleSize) {
      sampleArray(i) = keyArray(scala.util.Random.nextInt(keyArray.length))
    }
    val sampleList = sampleArray.toList
    sampleList

  }

}
