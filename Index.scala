package search.sol

import java.io.IOException

/**
  * Object that represents an Index and contains the main method that runs the
  * indexer of the search engine.
  */
object Index {

  /**
    * Main method
    *
    * @param arg - index arguments
    */
  def main(args: Array[String]): Unit = {
    if (args.length == 4) {
      val t0 = System.nanoTime()
      val xmlPath: String = args(0)
      val titlePath: String = args(1)
      val indexPath: String = args(2)
      val pageRankPath: String = args(3)
      val preProcess = new Preprocess(true)
      val indexer = new Indexer()
      try {
        preProcess.parse(xmlPath)
        preProcess.writeTitles(titlePath)
        preProcess.writePageRank(pageRankPath)
        indexer.fillPMap(preProcess)
        indexer.writeIndexer(indexPath)
        val t1 = System.nanoTime()
        println("Elapsed time: " + ((t1 - t0) / 1000000000.0) + " seconds")
        println("Files indexed successfully!")
      } catch {
        case e: IOException => println("Faulty file path." + 
            "Please ensure all arguments point to desired file location")
      }
    } else {
      println("Invalid args: Index requires xml file, titles," + 
          " index, and PageRank files as parameters.")
    }
  }
}