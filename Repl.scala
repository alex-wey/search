package search.sol

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.IOException
import scala.util.matching.Regex
import search.src.PorterStemmer
import search.src.StopWords

/**
  * Class that represents a Repl.
  *
  * @param pageRankBool - true if the Search program is taking into account page
  * 											 ranks
  */
class Repl(val pageRankBool: Boolean) {

  // Extracts user's query from Repl
  private val r: BufferedReader = new BufferedReader(new InputStreamReader(System.in))
  // Instantiates a Querier object
  private val q: Querier = new Querier(pageRankBool)

  /**
    * Method that initializes the Querier field.
    *
    * @param indexPath - path to index.txt file
    * @param titlePath - path to titles.txt file
    * @param pageRankPath - path to pageRank.txt file
    */
  def initQuery(indexPath: String, titlePath: String, pageRankPath: String): Unit = {
    q.runQuerier(indexPath, titlePath, pageRankPath)
  }

  /**
    * Method that gets the next line in a user's query.
    */
  private def getNextInput(): String = {
    try {
      val line: String = r.readLine()
      if (line == ":quit")
        System.exit(0)
      return line
    } catch {
      case e: IOException => {
        println("Input error. Please try again!")
        getNextInput()
      }
    }
  }

  /**
    * Method that outputs prompts in the Repl for the user.
    */
  def run(): Unit = {
    println("Search: Type \':quit\' to escape!")
    val input: String = getNextInput()
    val t1 = System.nanoTime()
    printResults(results(input))
    val t2 = System.nanoTime()
    println("Time taken to find results: " + (t2 - t1) / 1000000000.0)
    run()
  }

  /**
    * Method that finds a list of objects of type ISPair in order from maximum
    * to minimum final scores (at most the top 10).
    *
    * @param input - the user's query
    * @return a list of objects of type ISPair from maximum to minimum final scores
    */
  private def results(input: String): List[ISPair] = {
    val tokenized: List[String] = tokenize(input.toLowerCase()).map(x => PorterStemmer.stem(x))
    var finalScored: List[ISPair] = Nil
    for (str <- tokenized) q.queryMap.get("#" + str) match {
      case None => Unit
      case Some(i) => for (isp <- i.take(10)) {
        val currentSize: Int = finalScored.size
        val cleaned: List[ISPair] = finalScored.filter(x => x != isp)
        if (finalScored.contains(isp)) {
          finalScored = q.insertVal(
            cleaned,
            new ISPair(isp.id, isp.score + getScore("#" + str, isp)),
            q.binarySearch(cleaned, cleaned.size,
              new ISPair(isp.id, isp.score + getScore("#" + str, isp))))
        } else {
          finalScored = q.insertVal(finalScored, isp, q.binarySearch(finalScored, finalScored.size, isp))
        }
      }
    }
    finalScored.take(10)
  }

  /**
    * Method that finds the score in an object of type ISPair
    *
    * @param word - the given word
    * @param isp - the object of type ISPair
    * @return the score of the ISPair
    */
  private def getScore(word: String, isp: ISPair): Double = q.queryMap.get(word) match {
    case None    => 0
    case Some(i) => if (i.contains(isp)) isp.score else 0
  }

  /**
    * Method that prints the results from a user's query
    *
    * @param input - the list of objects of type ISPair
    */
  private def printResults(input: List[ISPair]): Unit = {
    if (input == Nil)
      println("Word not found. Try searching for something else!")
    else {
      var counter: Int = 1
      for (isp <- input) {
        print(counter.toString + " ")
        println(q.idTitleMap(isp.id))
        counter += 1
      }
    }
  }

  /**
    * Method that tokenizes and removes all stop words in the user's query.
    *
    * @param text - the text in the page
    * @return a list of tokenized terms without stop words
    */
  private def tokenize(text: String): List[String] = {
    val regex = new Regex("""\[\[[^\[]+?\]\]|[^\W_]+'[^\W_]+|[^\W_]+""")
    val matchesIterator = regex.findAllMatchIn(text)
    matchesIterator.toList.map(aMatch => aMatch.matched).filter(x => !StopWords.isStopWord(x))
  }
}

//object replMain {
//  def main(args: Array[String]): Unit = {
//    val t0 = System.nanoTime()
//    val indexPath: String = "/Users/alexwey/Desktop/index.txt"
//    val titlePath: String = "/Users/alexwey/Desktop/titles.txt"
//    val pageRankPath: String = "/Users/alexwey/Desktop/pageRanks.txt"
//    val preProcess = new Preprocess(true)
//    val indexer = new Indexer()
//    preProcess.parse("/Users/alexwey/Desktop/CS18/JAVA_Setup/workspace/scalaproject/src/search/src/PageRankWiki.xml")
//    preProcess.writeTitles(titlePath)
//    preProcess.writePageRank(pageRankPath)
//    indexer.fillPMap(preProcess)
//    indexer.writeIndexer(indexPath)
//    val t1 = System.nanoTime()
//    println((t1 - t0) / 1000000000.0)
//    val repl: Repl = new Repl(true)
//    repl.initQuery(indexPath, titlePath, pageRankPath)
//    repl.run()
//  }
//}