package search.sol

import scala.collection.mutable.HashMap
import java.io.BufferedWriter
import java.io.FileWriter
import java.io.IOException

/**
  * Class that represents an Indexer.
  */
class Indexer {

  // The number of pages in the corpus
  private var n: Int = 0
  // HashMap[Key: id, Value: HashMap[Key: word, Value: score]]
  private val indexer: HashMap[String, HashMap[String, Int]] = new HashMap()
  // HashMap[Key: id, Value: maximum score]
  private val idMax: HashMap[String, Double] = new HashMap()

  /**
    * Method that fills the parseMap (denoted at top of Preprocess class).
    *
    * @param parseMap - a Preprocess object
    */
  def fillPMap(parseMap: Preprocess): Unit = {
    for ((id, value) <- parseMap.parseMap) {
      idMax.get(id) match {
        case Some(i) => Unit
        case None    => idMax += (id -> 0)
      }
      for ((title, text) <- value) {
        for (word <- text) {
          indexer.get(word) match {
            case Some(i) => i.get(id) match {
              case Some(j) => {
                indexer(word)(id) += 1
                idMax(id) = math.max(idMax(id), indexer(word)(id))
              }
              case None => {
                indexer(word) += (id -> 1)
                idMax(id) = math.max(idMax(id), indexer(word)(id))
              }
            }
            case None => {
              indexer += (word -> HashMap(id -> 1))
              idMax(id) = math.max(idMax(id), indexer(word)(id))
            }
          }
        }
      }
      n += 1
    }
  }

  /**
    * Method that calculates the Term Frequency (TF).
    *
    * @param word - the given word
    * @param id - the id that contains the given word
    * @return the TF
    */
  private def tf(word: String, id: String): Double = {
    val countWord: Double = indexer(word)(id).toDouble
    val countPopWord: Double = idMax(id).toDouble
    countWord / countPopWord
  }

  /**
    * Method that calculates the Inverse Document Frequency (IDF).
    *
    * @param word - the given word
    * @return the IDF
    */
  private def idf(word: String): Double = {
    math.log(n.toDouble / indexer(word).size.toDouble)
  }

  /**
    * Method that calculates the score of a word.
    *
    * @param word - the given word
    * @param id - the id that contains the given word
    * @return the score of the given word
    */
  private def score(word: String, id: String): Double = {
    tf(word, id) * idf(word)
  }

  /**
    * Method that writes the index.txt file.
    *
    * @param fileName - the name of the txt file to be written
    */
  def writeIndexer(fileName: String): Unit = {
    var writer: BufferedWriter = null
    try {
      writer = new BufferedWriter(new FileWriter(fileName))
      for ((word, value) <- indexer) {
        writer.write("#")
        writer.write(word)
        writer.write("\n")
        for ((id, count) <- value) {
          writer.write(id)
          writer.write("\n")
          writer.write(score(word, id).toString())
          writer.write("\n")
        }
      }
    } catch {
      case e: IOException => println("File not found for index path")
    } finally {
      try {
        if (writer != null) writer.close
      } catch {
        case e: IOException => println("Error with writeIndexer")
      }
    }
  }
}
