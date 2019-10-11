package search.sol

/**
  * Class that represents an ISPair.
  * 
  * param id - the ID of the page
  * param score - the score (TF * IDF) of each page
  */
class ISPair(val id: String, val score: Double) {
  override def toString: String = "[" + id + "," + score.toString + "]"

  override def equals(that: Any): Boolean = that match {
    case that: ISPair => this.id == that.id
    case _            => false
  }
}