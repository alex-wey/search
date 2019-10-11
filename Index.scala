package search.sol

object Index {
  def main(args: Array[String]): Unit = {
    if (args.length == 4) {
      val xmlPath: String = args(0)
      val titlePath: String = args(1)
      val indexPath: String = args(2)
      val pageRankPath: String = args(3)
      val preProcess = new Preprocess(true)
      val indexer = new Indexer()
      preProcess.parse(xmlPath)
      preProcess.writeTitles(titlePath)
      preProcess.writePageRank(pageRankPath)
      indexer.fill(preProcess)
      indexer.writeIndexer(indexPath)
    } else {
      println("Requires xml file, titles , index, and page rank files as parameters.")
    }
  }
}
      
