package search.sol

import scala.xml.Node
import scala.xml.NodeSeq
import scala.collection.mutable.HashMap
import scala.collection.mutable.HashSet
import scala.util.matching.Regex
import search.src.StopWords
import search.src.PorterStemmer
import java.io.BufferedWriter
import java.io.FileWriter
import java.io.IOException

/**
 * Class that represents a Preprocess.
 *
 * @param pageRankBool - true if the preprocessing step calls on page rank,
 *      							   false otherwise
 */
class Preprocess(val pageRankBool: Boolean) { //val tf: Boolean

  // The number of pages in the corpus
  private var n: Int = 0
  // the epsilon value for scoring
  private val e: Double = 0.15
  // HashMap[Key: id, value: HashMap[Key: title, Value: text]]
  val parseMap: HashMap[String, HashMap[String, List[String]]] = new HashMap()
  // HashMap[Key: title, value: list of titles that document has links to]
  val fromLinkMap: HashMap[String, HashSet[String]] = new HashMap()
  // HashMap[Key: title, value: list of titles that have links to document]
  val toLinkMap: HashMap[String, HashSet[String]] = new HashMap()

  // HashMap[Key: jLink, value: HashMap[Key: kLink that refers to jLink, Value: weight]]
  //  val weightsMap: HashMap[String, HashMap[String, Double]] = new HashMap()

  //  // HashMap[Key: title, value: page rank of document]
  //  val pageRanksMap: HashMap[String, Double] = new HashMap()

  val weights: HashMap[String, HashMap[String, Double]] = new HashMap()

  /**
   * Method that parses the corpus.
   *
   * @param corpus - the corpus of Wikipedia pages
   */
  def parse(corpus: String): Unit = {
    val mainNode: Node = xml.XML.loadFile(corpus)
    val pageSeq: NodeSeq = mainNode \ "page"
    for (page <- pageSeq) {
      n += 1
      val id: String = (page \ "id").text.trim
      val title: String = (page \ "title").text.trim
      val text: List[String] = (tokenize((page \ "text").text.toLowerCase()
        + " " + title.toLowerCase())).map(x => PorterStemmer.stem(x))
      val ttMap: HashMap[String, List[String]] = new HashMap()
      ttMap += (title -> text)
      parseMap += (id -> ttMap)
      if (pageRankBool) {
        val linkList: HashSet[String] = parseLinks((page \ "text").text.toLowerCase(), title.toLowerCase())
        fromLinkMap += (title.toLowerCase() -> linkList)
        makeToLinkMap()
      }
    }
    if (pageRankBool) {
      initWeights()
    }

  }

  //  def tokenize(text: String): List[String] = {
  //    val regex = new Regex("""\[\[[^\[]+?\]\]|[^\W_]+'[^\W_]+|[^\W_]+""")
  //    val regex2 = new Regex("""[^\W_]+'[^\W_]+|[^\W_]+""")
  //    val matchesIterator = regex.findAllMatchIn(text)
  //    matchesIterator.toList.map(aMatch => aMatch.matched).filter(x => !StopWords.isStopWord(x))
  //  }

  /**
   * Method that tokenizes and removes all stop words in the text.
   *
   * @param text - the text in the page
   * @return a list of tokenized terms without stop words
   */
  private def tokenize(text: String): List[String] = {
    val regexAll = new Regex("""\[\[[^\[]+?\]\]|[^\W_]+'[^\W_]+|[^\W_]+""")
    val regexWord = new Regex("""[^\W_]+'[^\W_]+|[^\W_]+""")
    val regexPipe = ("""\[\[[^\[]+?\|[^\[]+?\]\]""")
    val matchesIterator = regexAll.findAllMatchIn(text)
    val preTokenized = matchesIterator.toList.map(aMatch => aMatch.matched).filter(x => !StopWords.isStopWord(x))
    tokenizeHelp(preTokenized, regexWord)
  }

  /**
   * Helper method that tokenizes the links in the text.
   *
   * @param textList - the list of tokenized terms without stop words
   * @param regexWord - the regular expression for all words
   * @return a list of tokenized links without stop words
   */
  private def tokenizeHelp(textList: List[String], regexWord: Regex): List[String] = textList match {
    case Nil => Nil
    case hd :: tl => {
      if (hd.matches("""\[\[[^\[]+?\|[^\[]+?\]\]""")) {
        val lstSplit = hd.split("\\|")(1)
        val matchesIterator = regexWord.findAllMatchIn(lstSplit)
        matchesIterator.toList.map(aMatch => aMatch.matched).filter(x => !StopWords.isStopWord(x)) ++ tl
      } else {
        val matchesIterator = regexWord.findAllMatchIn(hd)
        matchesIterator.toList.map(aMatch => aMatch.matched).filter(x => !StopWords.isStopWord(x)) ++ tl
      }
    }
  }

  /**
   * Method that writes the titles.txt.
   *
   * @param fileName - the name of the txt file to be written
   */
  def writeTitles(fileName: String): Unit = {
    var writer: BufferedWriter = null
    try {
      writer = new BufferedWriter(new FileWriter(fileName))
      for ((id, value) <- parseMap) {
        for ((title, count) <- value) {
          writer.write(id)
          writer.write("\n")
          writer.write(title)
          writer.write("\n")
        }
      }
    } catch {
      case e: IOException => println("File not found for titles.")
    } finally {
      try {
        if (writer != null) writer.close
      } catch {
        case e: IOException => println("error with writeTitles")
      }
    }
  }

  /**
   * Method that parses the titles for each link in the text.
   *
   * @param text - the text in the page
   * @param title - the title of the page
   * @return a set of titles parsed from each link
   */
  private def parseLinks(text: String, pageTitle: String): HashSet[String] = {
    val regexLink = new Regex("""\[\[[^\[]+?\]\]""")
    val matchesIterator = regexLink.findAllMatchIn(text)
    val lst = matchesIterator.toList.map(aMatch => getTitle(aMatch.matched))
    val set = new HashSet[String]
    for (title <- lst) {
//      if (title.toLowerCase() != pageTitle)
        set.add(title.toLowerCase())
    }
    set
  }

  /**
   * Method that gets the title in a link.
   *
   * @param text - the text in the page
   * @return the title in a link
   */
  private def getTitle(text: String): String = {
    val regexTitle = new Regex("""(?<=\[\[).+?(?=[\]\|])""")
    val mIterator = regexTitle.findAllMatchIn(text)
    val lst = mIterator.toList.map(aMatch => aMatch.matched)
    lst(0)
  }

  /**
   * Method that creates a toLinkMap (denoted at top).
   */
  private def makeToLinkMap(): Unit = {
    for ((title, links) <- fromLinkMap) {
      val set: HashSet[String] = makeHelper(title)
      toLinkMap += (title -> set)
    }
  }

  /**
   * Helper method for makeToLinkMap.
   *
   * @param title - title within the link
   * @return a set of titles that have links to a certain page
   */
  private def makeHelper(title: String): HashSet[String] = {
    val set: HashSet[String] = new HashSet()
    for ((t, slinks) <- fromLinkMap) {
      if (slinks.contains(title)) set.add(t)
    }
    set
  }

  //    private def initWeights(): Unit = {
  //      val e: Double = 0.15
  //      for ((j, slink) <- toLinkMap) {
  //        val nk: Int = slink.size
  //        if (nk > 0) {
  //          val kweight = ()
  //          WeightsMap += (j -> (e / n + (1 - e) * (1.0 / nk)))
  //        } else {
  //          WeightsMap += (j -> (e / n))
  //        }
  //      }
  //    }
  
  //  private def initWeights(): Unit = {
  //    val e: Double = 0.15
  //    for ((j, jlinks) <- toLinkMap) {
  //      val kVals: HashMap[String, Double] = new HashMap()
  //      for ((k, klinks) <- fromLinkMap) {
  //        //        if (k == j) {
  //        //          Unit
  //        //        } else {
  //        val nk: Int = jlinks.size
  //        if (klinks.contains(j)) {
  //          kVals += (k -> ((e / n) + ((1 - e) * (1.0 / nk))))
  //        } else
  //          kVals += (k -> (e / n))
  //        //        }
  //      }
  //      weightsMap += (j -> kVals)
  //    }
  //  }

  /**
   * Method that creates a weightsMap (denoted at top).
   */
  private def initWeights(): Unit = {
    for ((j, linksToId) <- toLinkMap) {
      val k: HashMap[String, Double] = new HashMap()
      for (link <- linksToId) {
        val nk: Int = toLinkMap(j).size
        k += (link -> ((e / n) + ((1 - e) * (1.0 / nk))))
      }
      if (!k.isEmpty)
        weights += (j -> k)
    }
  }

  /**
   * Method that creates a pageRanksMap (denoted at top).
   */
  def makePageRanks(): HashMap[String, Double] = {
    val basicWeight: Double = e / n
    initWeights()
    val distance: Double = 0.25
    // title -> ranking
    var r: HashMap[String, Double] = new HashMap()
    // title -> ranking
    var rp: HashMap[String, Double] = new HashMap()
    for ((title, links) <- fromLinkMap) {
      r += (title -> 0.0)
      rp += (title -> (1.0 / n))
    }
//    val rp: Array[Double] = Array.fill[Double](n)(1.0 / n)
    while (euclideanDistance(r, rp) > distance) {
      r = rp
      for ((title1, links) <- rp){
        rp(title1) = 0.0
        for ((title2, links) <- rp){
          val wjk: Double = weights.get(title1) match {
            case None => basicWeight
            case Some(thing) => thing.get(title2) match {
              case None => basicWeight
              case Some(w) => w
            }
          }
          println(title1 + " " + title2 + " " + wjk)
          rp(title1) = rp(title1) + (wjk * r(title2))
        }
      }
    }
    rp
  }

  /**
   * Method that calculates the Euclidean distance.
   *
   * @param r - the initialized array of n zeros
   * @param rp - the initialized array of size n containing arbitrary ranks
   *             (1/n)
   * @return the Euclidean distance
   */
  private def euclideanDistance(r: HashMap[String, Double], rp: HashMap[String, Double]): Double = {
    var sum: Double = 0
    for ((title,  ranking) <- r) {
      sum += math.pow((rp(title) - r(title)), 2)
    }
    math.sqrt(sum)
  }
}

object Preprocess1 {
  def main(args: Array[String]): Unit = {
    val t0 = System.nanoTime()
    val test = new Preprocess(true)
    test.parse("/Users/alexwey/Desktop/CS18/JAVA Setup/workspace/scalaproject/src/search/src/AnatopismWiki.xml")
    //    val value = "#"
    //    val finder = value
    //    for ((title, value) <- test.parseMap) {
    //      for ((id, lst) <- value) {
    //        if (title.contains(finder))
    //          println(title)
    //        else if (id.contains(finder))
    //          println(id)
    //        else {
    //          for (word <- lst) {
    //            if (word.contains(finder))
    //              println(word)
    //          }
    //        }
    //      }
    //    }
    val t1 = System.nanoTime()
    println("Elapsed time: " + ((t1 - t0) / 1000000000.0) + " seconds")
    println(test.fromLinkMap("macro-historical").size)
    //    println(test.toLinkMap)
    println(test.weights)
    println(test.makePageRanks())
  }
}