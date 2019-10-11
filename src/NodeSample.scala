package search.src

import scala.xml.Node

object NodeSample {

  val otherNode = <OtherPage>Here is other stuff</OtherPage>

  val mainNode =
    <xml>
      <page>
        <id>0</id>
        <body>
          This is the body text!
        </body>
      </page>
      <page>
        <id>1</id>
      </page>
      { otherNode }
    </xml>

  def main(args: Array[String]) {
    // These are both the string "This is the body text!"
    val page = (mainNode \ "page").text
    println(page)

    val page0Body = ((mainNode \ "page") \ "body").text
    val page0AltBody = (mainNode \\ "body").text
    println(page0Body)
    println(page0AltBody)
  }

}
