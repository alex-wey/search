package search.sol

object Query {
  //  val indexPath: String = "/Users/alexwey/Desktop/index.txt"
  //  val titlePath: String = "/Users/alexwey/Desktop/titles.txt"
  def main(args: Array[String]): Unit = {
    if (args.length != 3) {
      println("Requires titles file and index file as parameters.")
    } else {
      val titlePath: String = args(0)
      val indexPath: String = args(1)
      val pageRankPath: String = args(2)
      val repl: Repl = new Repl(true)
      repl.initQuery(indexPath, titlePath, pageRankPath)
      repl.run()
    }
  }
}