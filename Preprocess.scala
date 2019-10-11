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
  * @param pageRankBool - true if the preprocessing step calls on PageRank,
  *      							   false otherwise
  */
class Preprocess(val pageRankBool: Boolean) {

  // The number of pages in the corpus
  private var n: Int = 0
  // The epsilon value for scoring
  private val e: Double = 0.15
  // HashMap[Key: id, Value: HashMap[Key: title, Value: text]]
  val parseMap: HashMap[String, HashMap[String, List[String]]] = new HashMap()
  // HashSet[titles]
  private val titleSet: HashSet[String] = new HashSet
  // HashMap[Key: title, Value: list of titles that document has links to]
  private val fromLinkMap: HashMap[String, HashSet[String]] = new HashMap()
  // HashMap[Key: title, Value: list of titles that have links to document]
  private val toLinkMap: HashMap[String, HashSet[String]] = new HashMap()
  // HashMap[Key: jLink, Value: HashMap[Key: kLink r to jLink, Value: weight]]
  private val weights: HashMap[String, HashMap[String, Double]] = new HashMap()

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
      val title: String = (page \ "title").text.trim.toLowerCase()
      titleSet += title
      val text1: List[String] = (tokenize((page \ "text").text.toLowerCase()
        + " " + title.toLowerCase()))
      val text: List[String] = text1.map(x => PorterStemmer.stem(x))
      val ttMap: HashMap[String, List[String]] = new HashMap()
      ttMap += (title -> text)
      parseMap += (id -> ttMap)
    }
    if (pageRankBool) {
      for (page <- pageSeq) {
        val title: String = (page \ "title").text.trim.toLowerCase()
        val linkList: HashSet[String] = parseLinks((page \ "text").text.toLowerCase(), title.toLowerCase())
        fromLinkMap += (title.toLowerCase() -> linkList)
      }
      makeToLinkMap()
      initWeights()
      makePageRanks()
    }
  }

  /**
    * Method that tokenizes and removes all stop words in the text.
    *
    * @param text - the text in the page
    * @return a list of tokenized terms without stop words
    */
  def tokenize(text: String): List[String] = {
    val regexAll = new Regex("""\[\[[^\[]+?\]\]|[^\W_]+'[^\W_]+|[^\W_]+""")
    val regexWord = new Regex("""[^\W_]+'[^\W_]+|[^\W_]+""")
    val regexPipe = ("""\[\[[^\[]+?\|[^\[]+?\]\]""")
    val matchesIterator = regexAll.findAllMatchIn(text)
    val preTokenized = matchesIterator.toList.map(aMatch => aMatch.matched).filter(x => !StopWords.isStopWord(x))
    var terms: List[String] = Nil
    for (term <- preTokenized) {
      if (term.matches("""\[\[[^\[]+?\]\]""")) {
        var termsplit = term.split("""\||\[|\]|:""")
        if (term.contains("|")) {
          termsplit = termsplit(1).split(" ")
          for (innerWord <- termsplit) {
            if (innerWord.trim().length() > 0 && !StopWords.isStopWord(innerWord))
              terms = innerWord :: terms
          }
        } else {
          for (wordlst <- termsplit) {
            if (wordlst.trim().length() > 0) {
              for (wrd <- wordlst.split(" ")) {
                if (!StopWords.isStopWord(wrd))
                  terms = terms :+ wrd
              }
            }
          }
        }
      } else {
        terms = terms :+ term.trim
      }
    }
    terms
  }

  /**
    * Method that writes the titles.txt file.
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
    * Method that parses the titles from each link in the text.
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
      if (title.toLowerCase() != pageTitle && titleSet.contains(title.toLowerCase()))
        set.add(title.toLowerCase())
    }
    if (set.isEmpty) {
      for (t <- titleSet) {
        if (t.toLowerCase() != pageTitle)
          set.add(t.toLowerCase())
      }
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

  /**
    * Method that creates a weightsMap (denoted at top).
    */
  private def initWeights(): Unit = {
    for ((j, linksToId) <- toLinkMap) {
      val k: HashMap[String, Double] = new HashMap()
      for (link <- linksToId) {
        val nk: Int = fromLinkMap(link).size
        k += (link -> ((e / n) + ((1 - e) * (1.0 / nk))))
      }
      if (!k.isEmpty)
        weights += (j -> k)
    }
  }

  /**
    * Method that calculates the PageRank.
    *
    * @return a HashMap[Key: title, Value: PageRank of document]
    */
  private def makePageRanks(): HashMap[String, Double] = {
    val basicWeight: Double = e / n
    initWeights()
    val distance: Double = 0.001
    // HashMap[Key: title, Value: PageRank of document]
    val r: HashMap[String, Double] = new HashMap()
    // HashMap[Key: title, Value: PageRank of document]
    val pageRank: HashMap[String, Double] = new HashMap()
    for ((title, links) <- fromLinkMap) {
      r += (title -> 0.0)
      pageRank += (title -> (1.0 / n))
    }
    while (euclideanDistance(r, pageRank) > distance) {
      for ((title, rank) <- r) {
        r(title) = pageRank(title)
      }
      for ((title1, links) <- pageRank) {
        pageRank(title1) = 0.0
        for ((title2, links) <- pageRank) {
          val wjk: Double = weights.get(title1) match {
            case None => basicWeight
            case Some(thing) => thing.get(title2) match {
              case None    => basicWeight
              case Some(w) => w
            }
          }
          pageRank(title1) = pageRank(title1) + (wjk * r(title2))
        }
      }
    }
    pageRank
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
    for ((title, ranking) <- r) {
      sum += math.pow((rp(title) - r(title)), 2)
    }
    math.sqrt(sum)
  }

  /**
   * Method that writes the PageRank.txt file.
   *
   * @param fileName - the name of the txt file to be written
   */
  def writePageRank(fileName: String): Unit = {
    var writer: BufferedWriter = null
    try {
      writer = new BufferedWriter(new FileWriter(fileName))
      for ((title, pageRank) <- makePageRanks()) {
        writer.write(title)
        writer.write("\n")
        writer.write(pageRank.toString)
        writer.write("\n")
      }
    } catch {
      case e: IOException => println("File not found for PageRank.")
    } finally {
      try {
        if (writer != null) writer.close
      } catch {
        case e: IOException => println("error with writePageRank")
      }
    }
  }
}

//object Preprocess1 {
//  def main(args: Array[String]): Unit = {
//    //    val test = new Preprocess(true)
//    //    val tk = "[[hammer]] dog [[dog:bam fuck]] monkey balls [[here|we go]] horsecock"
//    //    val termsplit = test.tokenize(tk)
//    ////    for (t <- termsplit)
//    ////      println(t)
//    val t0 = System.nanoTime()
//    val test = new Preprocess(true)
//    test.parse("/Users/alexwey/Desktop/CS18/JAVA_Setup/workspace/scalaproject/src/search/src/LinkTester.xml")
//    val t1 = System.nanoTime()
//    println("Elapsed time: " + ((t1 - t0) / 1000000000.0) + " seconds")
//    println(test.parseMap)
//    test.writePageRank("/Users/alexwey/Desktop/pageRanks.txt")
//  }
//}