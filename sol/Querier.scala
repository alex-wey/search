package search.sol

import scala.collection.mutable.HashMap
import java.io.BufferedReader
import java.io.FileReader
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader

/**
 * Class that represents a Querier.
 *
 * @param pageRankBool - true if the Search program is taking into account page
 * 											ranks
 */
class Querier(val pageRankBool: Boolean) {

  // HashMap[Key: word, Value: a list of ISPairs]
  val queryMap: HashMap[String, List[ISPair]] = new HashMap()
  // HashMap[Key: id, Value: title]
  val idTitleMap: HashMap[String, String] = new HashMap()
  // HashMap[Key: title, Value: PageRank]
  val pageRankMap: HashMap[String, Double] = new HashMap()

  /**
   * Method that fills the queryMap (denoted at top).
   *
   * @param fileName - the index.txt file that is to be parsed
   */

  def fillQMap(fileName: String): Unit = {
    var count = 0
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
        do {
          id = l
          l = reader.readLine()
          if (pageRankBool) {
            score = l.toDouble * pageRankMap(idTitleMap(id))
          } else {
            score = l.toDouble
          }
          l = reader.readLine()
          queryMap.get(word) match {
            case None => queryMap += (word -> List(new ISPair(id, score)))
            case Some(i) => {
              val lst = queryMap(word)
              queryMap(word) = insertVal(lst, new ISPair(id, score),
                binarySearch(lst, lst.size, new ISPair(id, score)))
            }
          }
        } while (isInt(l))
      }
    } catch {
      case e: IOException => {
        println("Faulty index file path." +
            " Please ensure all file paths are inputted correctly.")
        System.exit(0)
      }
    } finally {
      try {
        if (reader != null) reader.close
      } catch {
        case e: IOException => println("error message6")
      }
    }
  }

  /**
   * Method that fills the idTitleMap (denoted at the top).
   *
   * @param fileName - the titles.txt file that is to be parsed
   */
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
    } catch {
      case e: IOException => {
        println("Faulty title file path." +
            " Please ensure all file paths are inputted correctly.")
        System.exit(0)
      }
    } finally {
      try {
        if (reader != null) reader.close
      } catch {
        case e: IOException => println("error message4")
      }
    }
  }

  /**
   * Method that checks if an object can be of type Int.
   *
   * @param str - a string
   * @return true if the string can be of type Int, false otherwise
   */
  private def isInt(str: String): Boolean = {
    try {
      val inti = str.toInt
      true
    } catch {
      case _: Throwable => false
    }
  }

  /**
   * Method that checks if an object can be of type Double.
   *
   * @param str - a string
   * @return true if the string can be of type Double, false otherwise
   *
   */
  private def isDouble(str: String): Boolean = {
    try {
      val strd = str.toDouble
      true
    } catch {
      case _: Throwable => false
    }
  }

  /**
   * Method that checks if an object can only be String.
   *
   * @param str - a string
   * @return true if the string can only be String, false otherwise
   *
   */
  private def isOnlyString(str: String): Boolean = {
    !isInt(str) && !isDouble(str)
  }

  /**
   * Method that inserts a value into a certain index.
   *
   * @param lst - the list of objects of type ISPair
   * @param value - the ISPair
   * @param i - the certain index
   * @return a list of objects of type ISPair sorted from maximum to minimum
   */
  def insertVal(lst: List[ISPair], value: ISPair, i: Int): List[ISPair] = lst match {
    case Nil      => if (i == 0) value :: Nil else Nil
    case hd :: tl => if (i == 0) value :: hd :: tl else hd :: insertVal(tl, value, i - 1)
  }

  /**
   * Method that calculates an integer that represents an index for an object
   * of type ISPair to be sorted into a list from maximum to minimum.
   *
   * @param lst - the list of objects of type ISPair
   * @param n - the length of the list of objects of type ISPair
   * @param value - the ISPair
   * @return the index for a value
   */
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
    } catch {
      case e: IOException => {
        println("Faulty pageRank path. Please ensure all filepaths are inputted correctly.")
        System.exit(0)
      }
    } finally {
      try {
        if (reader != null) reader.close
      } catch {
        case e: IOException => println("error message2")
      }
    }
  }

  /**
    * Method that runs the query.
    *
    * @param indexPath - file path to index.txt
    * @param titlePath - file path to titles.txt
    * @param pageRankPath - file path to pageRank.txt
    */
  def runQuerier(indexPath: String, titlePath: String, pageRankPath: String): Unit = {
    fillIdTitle(titlePath)
    if (pageRankBool) {
      fillPRMap(pageRankPath)
    }
    fillQMap(indexPath)
  }
}
