package search.sol

import scala.collection.mutable.HashMap
import java.io.BufferedReader
import java.io.FileReader
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader

class Querier(val pageRankBool: Boolean) {

  // HashMap[Key: word, Value: a list of ISPairs]
  val queryMap: HashMap[String, List[ISPair]] = new HashMap()
  // HashMap[Key: id, Value: title]
  val idTitleMap: HashMap[String, String] = new HashMap()
  // HashMap[Key: title, Value: page rank]
  val pageRankMap: HashMap[String, Double] = new HashMap()

  def fillQMap(fileName: String): Unit = {
    var reader: BufferedReader = null
    try {
      reader = new BufferedReader(new FileReader(fileName))
      var l: String = reader.readLine()
      var word: String = new String()
      var id: String = new String()
      var score: Double = -1
      while (l != null) {
        word = l
        l = reader.readLine()
        while (isOnlyString(l)) {
          word += l
          l = reader.readLine()
        }
        id = l
        l = reader.readLine()
        score = l.toDouble
        println("this is the score pre" + score)
        if (pageRankBool) {
          score *= pageRankMap(idTitleMap(id))
          println("post" + score)
        }
        l = reader.readLine()
        queryMap += (word -> List(new ISPair(id, score)))
        while (isInt(l)) {
          id = l
          l = reader.readLine()
          score = l.toDouble
          l = reader.readLine()
          val lst = queryMap(word)
          queryMap(word) = insertVal(lst, new ISPair(id, score),
            binarySearch(lst, lst.size, new ISPair(id, score)))
        }
      }
      // any more resource initialization
      // stuff with read/write/flush, but no close!
    } catch {
      case e: IOException => println("error message5")
      // other exception cases here
    } finally {
      try {
        if (reader != null) reader.close
        // close all other resources too!
      } catch {
        case e: IOException => println("error message6")
      }
    }
  }

  def fillIdTitle(fileName: String): Unit = {
    var reader: BufferedReader = null
    try {
      reader = new BufferedReader(new FileReader(fileName))
      var l: String = reader.readLine()
      while (l != null) {
        val id = l
        l = reader.readLine()
        val title = l
        idTitleMap += (id -> title)
        l = reader.readLine()
      }
      // any more resource initialization
      // stuff with read/write/flush, but no close!
    } catch {
      case e: IOException => println("error message3")
      // other exception cases here
    } finally {
      try {
        if (reader != null) reader.close
        // close all other resources too!
      } catch {
        case e: IOException => println("error message4")
      }
    }
  }

  private def isInt(str: String): Boolean = {
    try {
      val inti = str.toInt
      true
    } catch {
      case _: Throwable => false
    }
  }

  private def isDouble(str: String): Boolean = {
    try {
      val strd = str.toDouble
      true
    } catch {
      case _: Throwable => false
    }
  }

  private def isOnlyString(str: String): Boolean = {
    !isInt(str) && !isDouble(str)
  }

  // note that i = binarySearch(lst,  value)
  def insertVal(lst: List[ISPair], value: ISPair, i: Int): List[ISPair] = lst match {
    case Nil      => if (i == 0) value :: Nil else Nil
    case hd :: tl => if (i == 0) value :: hd :: tl else hd :: insertVal(tl, value, i - 1)
  }

  def binarySearch(lst: List[ISPair], n: Int, value: ISPair): Int = {
    var l: Int = 0
    var r: Int = n - 1
    while (l <= r) {
      val m = (l + r) / 2
      if (lst(m).score > value.score) {
        l = m + 1
      } else if (lst(m).score < value.score) {
        r = m - 1
      } else {
        return m
      }
    }
    l
  }

  /**
   * Method that fills the pageRankMap (denoted at top).
   *
   * @param fileName - the pageRank.txt file that is to be parsed
   */
  def fillPRMap(fileName: String): Unit = {
    var reader: BufferedReader = null
    try {
      reader = new BufferedReader(new FileReader(fileName))
      var l: String = reader.readLine()
      var title: String = new String()
      var pageRank: Double = -1
      while (l != null) {
        title = l
        l = reader.readLine()
        while (isOnlyString(l)) {
          title += l
          l = reader.readLine()
        }
        pageRank = l.toDouble
        l = reader.readLine()
        pageRankMap += (title -> pageRank)
      }
      // any more resource initialization
      // stuff with read/write/flush, but no close!
    } catch {
      case e: IOException => println("error message1")
      // other exception cases here
    } finally {
      try {
        if (reader != null) reader.close
        // close all other resources too!
      } catch {
        case e: IOException => println("error message2")
      }
    }
  }

  def runQuery(indexPath: String, titlePath: String, pageRankPath: String): Unit = {
    val t0 = System.nanoTime()
    //    val indexPath: String = "/home/awey/Desktop/index.txt"
    //    val titlePath: String = "/home/awey/Desktop/titles.txt"
    //    val pageRankPath: String = "/Users/alexwey/Desktop/pageRanks.txt"
    //    val preProcess = new Preprocess()
    //    val indexer = new Indexer()
    //    preProcess.parse("/home/awey/course/cs0180/workspace/scalaproject/src/search/src/MedWiki.xml")
    //    preProcess.writeTitles(titlePath)
    //    indexer.fill(preProcess)
    //    indexer.writeIndexer(indexPath)
    val t1 = System.nanoTime()
    fillIdTitle(titlePath)
    if (pageRankBool) {
      fillPRMap(pageRankPath)
    }
    fillQMap(indexPath)
    val t2 = System.nanoTime()
    println("Elapsed time: " + ((t2 - t1) / 1000000000.0) + " seconds")
  }
}

object QuerierMain {
  def main(args: Array[String]): Unit = {
    //        val tst = new Querier()
    //        val is1 = new ISPair("123", 6)
    //        val is2 = new ISPair("456", 6)
    //        val is3 = new ISPair("789", 9)
    //        val is4 = new ISPair("999", 1)
    //        val lst = List(is3, is2, is1, is4)
    //        println(lst)
    //        println(tst.binarySearch(lst, lst.size, new ISPair("456", 5.9)))
    //        println(tst.binarySearch(lst, lst.size, new ISPair("456", 0)))
    //        println(tst.insertVal(lst, new ISPair("456", 10.0), tst.binarySearch(lst, lst.size, new ISPair("456", 10.0))))
    //    val t0 = System.nanoTime()
    val indexPath: String = "/Users/alexwey/Desktop/index.txt"
    val titlePath: String = "/Users/alexwey/Desktop/titles.txt"
    val pageRankPath: String = "/Users/alexwey/Desktop/pageRanks.txt"
    val preProcess = new Preprocess(true)
    val indexer = new Indexer()
    preProcess.parse("/Users/alexwey/Desktop/CS18/JAVA Setup/workspace/scalaproject/src/search/src/tester1.xml")
    preProcess.writeTitles(titlePath)
    indexer.fill(preProcess)
    indexer.writeIndexer(indexPath)
    val t1 = System.nanoTime()
    val querier = new Querier(true)
//    querier.fillIdTitle(titlePath)
//    querier.fillPRMap(pageRankPath)
//    querier.fillQMap(indexPath)
    querier.runQuery(indexPath, titlePath, pageRankPath)
    println(querier.pageRankMap)
    val t2 = System.nanoTime()
    println("Elapsed time: " + ((t2 - t1) / 1000000000.0) + " seconds")
    //    println(querier.queryMap("#histori").take(10))
  }
}