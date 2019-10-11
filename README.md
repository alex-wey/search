Instructions for use
-
To interact with the program, a user has to open terminal, cd to the scalaproject
folder, compile by running the command: "scalac -d bin s*/search/*/*.scala", cd
into the bin, and enter the command: "scala search.sol.Index". Before running that
command, the user must input four strings as main arguments:

1. A path to the xml corpus to parse.
2. A path to the titles.txt that should be written.
3. A path to the index.txt that should be written.
4. A path to the the pageRank.txt file that should be written.

This will create the necessary files in order to run the query through the search
engine at a faster runtime.

To enter a query into the search engine, a user has to enter the command: "scala
search.sol.Query. Before running that command, the user must input three to four
strings as main arguments:

1. (Optional) A string, either the "--smart" or "-smart" flag or the "-pagerank"
or "--pagerank" flag. (Note: these strings were decided to have either one
one "-" or two "--" because eclipse automatically combines "--" into a
singular dash. Therefore, it is easier to input a single "-" when running through
Eclipse, while it does not matter when running through the terminal)
2. A path to the already written titles.txt.
3. A path to the already written index.txt.
4. A path to the already written pagerank.txt.
(Note: if a user does not call "-smart" or "-pagerank" the program ignores the fourth
argument)

After that, a query will be instantiated, which should take trivial time. Then, the REPL will open and output the results based on either a standard search with or without utilizing the PageRank algorithm (decided by the user's arguments entry). It is expected that the Query will only take seconds to run.

Overview of design
-
The Search program is divided into several classes:

The first class is called Preprocess.scala, which contains methods that help parse
the corpus for the IDs, titles, and texts of each page (which are then stored in
a double HashMap). This class also contains essential methods that implement the
PageRank algorithm that ultimately store the weights of each page in a double HashMap.
In this class are two other methods that write a titles.txt file and a pageRank.txt
file. The titles.txt file simply is a list of all the titles and their respective
IDs in the corpus and the pageRank.txt file contains a list of each title and its
page rank.

The second class is called Indexer.scala, which contains methods that produce a
HashMap, containing the IDs, words, and corresponding scores (which are calculated
using the TF and IDF calculated by other methods in the class). Then, there is a
method that writes an index.txt file. Within this file is a list of: a word,
followed by its corresponding id, followed by its score. Note that every word is
predicated with a hashtag. This made it easier in code to distinguish between
words and id's.

The third class is called Querier.scala, which contains methods that read in the
multiple txt files in order to produce and call upon three HashMaps. The first one
contains words and ISPairs. An ISPair is an object created in a fourth class called
ISPair.scala, which represent a word's id and score (ISPair.id returns the id and
ISPair.score returns the score). The second one contains titles and their corresponding
id's. Lastly, the third one contains titles and their corresponding PageRanks.

The fifth class is called Repl.scala, which calls upon the instantiated Querier
object (that will create each of the three HashMaps denoted in the Querier.scala
class) and contains methods that prompt the user's REPL. Within the class is a method
that returns a list of 10 words with the highest scores from calling upon the HashMap
containing words and ISPairs (this list of words is sorted from maximum to minimum
score in order to allow an efficient runtime).

The sixth class is called Index.scala, which is essentially the "main" method for
Preprocess.scala and Indexer.scala. This method calls on the necessary methods in
either class in order to index a corpus. Index.scala creates the titles.txt,
index.txt, and pageRanks.txt files.

Finally, the seventh and last class is called Query.scala, which reads in the
titles.txt, index.txt, and pageRanks.txt files created by the Index.scala class
and instantiates the REPL.
