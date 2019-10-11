package search.sol

import java.io.IOException

/**
  * Object that represents a Query and contains the main method that runs the
  * querier of the search engine.
  */
object Query {

  /**
    * Method that runs the query with or without PageRank
    *
    * @param pRank - true if query is to be ran with PageRank, false otherwise
    * @param indexPath - the file path of index.txt
    * @param titlePath - the file path of titles.txt
    * @param pageRankPath - the file path of pageRank.txt
    */
  private def runQuery(pRank: Boolean, indexPath: String, titlePath: String, pageRankPath: String): Unit = {
    val repl: Repl = new Repl(pRank)
    repl.initQuery(indexPath, titlePath, pageRankPath)
    repl.run()
  }

  /**
    * Main method
    *
    * @param arg - query arguments
    */
  def main(args: Array[String]): Unit = {
    if (args.length == 4) {
      val titlePath: String = args(1)
      val indexPath: String = args(2)
      val pageRankPath: String = args(3)
      if (args(0) == "-smart" || args(0) == "--smart") {
        println("--smart is not implemented, running with pagerank...")
        runQuery(true, indexPath, titlePath, pageRankPath)
      } else if (args(0) == "-pagerank" || args(0) == "--pagerank") {
        println("Running with pagerank...")
        runQuery(true, indexPath, titlePath, pageRankPath)
      } else {
        println("error in your arguments, please rerun")
      }
    } else if (args.length == 3) {
      println("Running standard search...")
      val titlePath: String = args(0)
      val indexPath: String = args(1)
      val pageRankPath: String = args(2)
      runQuery(false, indexPath, titlePath, pageRankPath)
    } else {
      println("Invalid args: Query requires titles, index, and PageRank file as parameters.")
    }
  }
}
