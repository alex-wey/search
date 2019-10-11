Will Kantaros
Alex Wey

--------------------------------------------------------------------------------------
Part 1: Answers to the following questions
--------------------------------------------------------------------------------------
1. With the scoring mechanism used in the PageRank algorithm in mind, what could
an organization do to ensure their own pages get promoted to the top of the search
results?:

An organization could create many "fake sites" that only reference their pages.
While these "fake sites" wouldn't have any real legitimacy, if the organization
were to create enough of them, their pages would be promoted much closer to the
top of the search results.

2. Say an inaccurate news article has gone viral and individuals have been linking
to it heavily in their online feeds (social media, blogs, pages, etc.). How could
this affect the results of someone who is attempting to research the story through
a search engine that uses PageRank?

If this inaccurate news article took advantage of PageRank, then it would have
greater authority and show up higher in the results. This is because if people have
been heavily linking to it, according to the first key principle in PageRank, the
page's authority would be increased substantially. In effect, someone who is attempting
to research the story through a search engine that uses PageRank, chances are that
if the article has enough authority, it would show up in response to that person's
query, regardless of if it actually has relevance.

3. Both scenarios above are examples of likely undesirable outcomes of the PageRank
algorithm. Contrast the two scenarios now with respect to human behavior. To what
extent is intent or malice required to get the adverse affects in each of the scenarios?
Unlike the first two questions, here we are asking you to think about the social,
not technical, aspects of PageRank.

Most people are good people in the world who would not abuse the weights of page
ranks to increase the authority of their pages in search engines. However, this
certainly is not the case for everyone - some people would abuse this. For instance,
if they were trying to promote a website or even were trying to delegitimize real
news in replace of fake news - such as in the 2016 presidential election - then
people may maliciously abuse the PageRank algorithm to benefit their cause.

--------------------------------------------------------------------------------------
Part 2: Instructions for use, describing how a user would interact with your program.
--------------------------------------------------------------------------------------
To interact with the program, a user has to open terminal, cd to the scalaproject
folder, compile by running the command: "scalac -d bin s*/search/*/*.scala", cd
into the bin, and enter the command: "scala search.sol.Index". Before running that
command, the user must input four strings as main arguments:

1. A path to the xml corpus to parse.
2. A path to the titles.txt that should be written.
3. A path to the index.txt that should be written.
4. A path to the the pageRank.txt file that should be written.

This will create the necessary files in order to run the query through the search
engine at a faster runtime. (Note: MedWiki.xml takes roughly 60 seconds to index)

To enter a query into the search engine, a user has to enter the command: "scala
search.sol.Query. Before running that command, the user must input three to four
strings as main arguments:

1. (Optional) A string, either the "--smart" or "-smart" flag or the "-pagerank"
or "--pagerank" flag. (Note: we decided to allow these strings to have either one
one "-" or two "--" because eclipse automatically combines "--" into a
singular dash. Therefore, it is easier to input a single "-" when running through
Eclipse, while it does not matter when running through the terminal)
2. A path to the already written titles.txt.
3. A path to the already written index.txt.
4. A path to the already written pagerank.txt.
(Note: if a user does not call "-smart" or "-pagerank" the program ignores the fourth
argument)

After that, a query will be instantiated, which will take trivial time (MedWiki
takes around 2 seconds). Then, the REPL will open and output the results based on
either a standard search with or without utilizing the PageRank algorithm (decided
by the user's arguments entry). We expect that the Query will only take seconds
to run.

--------------------------------------------------------------------------------------
Part 3: A brief overview of your design, including how the pieces of your program fit.
--------------------------------------------------------------------------------------
Our Search program is divided into several classes:

The first class is called Preprocess.scala, which contains methods that help parse
the corpus for the id's, titles, and texts of each page (which are then stored in
a double HashMap). This class also contains essential methods that implement the
PageRank algorithm that ultimately store the weights of each page in a double HashMap.
In this class are two other methods that write a titles.txt file and a pageRank.txt
file. The titles.txt file simply is a list of all the titles and their respective
id's in the corpus and the pageRank.txt file contains a list of each title and its
page rank.

The second class is called Indexer.scala, which contains methods that produce a
HashMap, containing the id's, words, and corresponding scores (which are calculated
using the TF and IDF calculated by other methods in the class). Then, there is a
method that writes an index.txt file. Within this file is a list of: a word,
followed by its corresponding id, followed by its score. Note that every word is
predicated with a hashtag. This made it easier in our code to distinguish between
words and ids

The third class is called Querier.scala, which contains methods that read in the
multiple txt files in order to produce and call upon three HashMaps. The first one
contains words and ISPairs. An ISPair is an object we created in a fourth class called
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
index.txt, and pageRanks.txt files. See part #2 for more information on how the
Index.scala runs.

Finally, the seventh and last class is called Query.scala, which reads in the
titles.txt, index.txt, and pageRanks.txt files created by the Index.scala class
and instantiates the REPL. See part #2 for more information on how the
Query.scala runs.

--------------------------------------------------------------------------------------
Part 4: A description of features you failed to implement, as well as any extra
features you implemented.
--------------------------------------------------------------------------------------

The only feature we did not implement was the ‘--smart’ flag feature.

--------------------------------------------------------------------------------------
Part 5: A description of any known bugs in your program.
--------------------------------------------------------------------------------------

We are unaware of any known bugs in our program. That said, we did not have a chance
to run BigWiki.xml on our program. While the runtime of our program is a minute
under the cap for MedWiki, which suggests that we have fairly efficient runtime, we do
instantiate a fair amount of HashMaps throughout our program. This may cause issues
in regards to memory overflow.

Certain methods tokenize links and titles such that all stop words are removed,
which is good. However, if there were a link called "a", it would be removed
because "a" is a stop word. That said, this is not the case for our PageRank algorithm.

Note: we are intentionally making our HashMap fields public in order to easily
access their contents (for testing purposes) and to ensure accurate functionality.

Note: We are under the assumption that the user will input the appropriate .xml or .txt
file when running the Index.scala code. We are also under the assumption that the user
will input the appropriate index.txt, titles.txt, and pageRank.txt files when running
Query.scala (this would make sense, as we create these files in Index.scala) We do
handle exceptions in both Index.scala and Query.scala, such as when there are not
enough arguments or invalid filespaths. However, we think it is reasonable enough to
assume that the appropriate .xml and .txt files will be called when the valid paths are
inputted. 

--------------------------------------------------------------------------------------
Part 6: A description of how you tested your program.
--------------------------------------------------------------------------------------

Systematic testing:

---------------------------------- Index.scala ---------------------------------------
We tested the Index on SearchTester.txt

This is a small file, with the same values as that of the example on page 14 in the
pdf, so the page rank values should theoretically be A: 0.4326 B: 0.2340 C: 0.3333
When we open our own pageRanks.txt file, our page rank values are A: 0.4326, B: 0.2340,
C: 0.3333. Therefore, on a fundamental level our pageRank.txt file appears
to be working well.

Next, we need to see if our titles.txt file and index.txt file have been written properly.
For titles.txt, A should be followed by a 1, B by a 2, and C by a 3 (the title then id
value of each page). This is the same as our titles.txt file, so this is also correct for
this example. Finally, we need to see if our index is working properly. Our score is
important to the "text of each document"
The text for A should be "a b c d".
However, because we stem each word and call stopper, 'a' will be removed from the text.
For page B the text should be "b d"
For page C the text should be "a c d", which will then be stemmed to "c d"

Now, we can call the grading formula given to us in the pdf.
Tf for letters b, c, and d in page A is 1 / 1.
Tf for letters b and d in page B is also 1 / 1
Tf for letter c in page C is 1 / 2, but note that TF for d in page C is 2 / 2

IDF for letter b is log (3 / 2) = 0.40546
IDF for letter c is log (3 / 2) = 0.40546
IDF for letter d is log (3 / 3) = 0

Our final scores now should be:
  Letter "a" should not show up since it is a stop word
  Letter "b" in page A is = 1 * 0.1760 = 0.40546
             in page B is = 1 * 0.1760 = 0.40546
  Letter "c" in page A is = 1 * 0.1760 = 0.40546
             in page C is = 1/2 * 0.1760 = 0.20273
  Letter "d" in page A, B, and C is equal to 0, since d appears in every page

This matches exactly with our index.txt file, so this is also correct

Now, for base case purposes, we're going to test on a corpus with one file (SingleFile.txt)

This file has no words, as well.
The text for OneFile should just be "singlefil", since we add the title to the text of
the file for search purposes and then stem it to match with other things
Here, our IDF is log (1 / 1) = 0, so our score for singlefil in SingleFile would be 0
As mentioned on a piazza post, it doesn't matter what the pagerank score is, since it
is uniform across all files (only one file)
The titles txt file should be 1 followed by a new line followed by "SingleFile",
which it is! Although this is a rudimentary case, complete functionality
for titles.txt isn't much more than this.

We also tested functionality on EmptyFile.txt for base case purposes
This did exactly as intended, outputting a blank titles, index, and pagerank file
When attempting to search for any word, no pages were returned

Finally, we tested Index.scala functionality on PageRankWiki.xml
This should run exactly the same as our pageRanks.txt file.
Note that the page rank for every page other than page 100 should be identical - as
They all point solely to page 100.
When we run Index.scala, the pageRank.txt file has every title assigned to a value of
0.005447619, except for 100, who's value is 0.46068.
Note that (1 * 0.46068) + (99 * 0.005447619) is equal to 1, which satisfies the invariant
of the the PageRank algorithm.
For our index.txt file, every word from 1 - 99 is only referenced one, and that is
in the same page. This is because the body of each page only calls on 100, so the only
Time the word is called is when it's in the title.This is seen in index.txt
For the word 100, there are 100 different pages that call it (because every page links
to 100). This is seen in the index.txt file, as there are 100 <id - score> values
following the word 100. The score for each of these id's pointing to the 100 word is
zero, which solidifies our confidence in our code, as the score of a word that is in
every document is zero. Our index.txt file appears to be correct.
Finally, we need to check the accuracy of our titles.txt file. Ensuring accuracy for
easy: simply check if each subsequent line is identical to the line before (as titles
and id's are equivalent in this case). Looking through our titles.txt file, it is
200 lines long in length, and word is duplicated below it (functionality is correct).


We believe that each of these cases provide unique insight into the system testing of
our index.scala file. Given that the desired txt files are outputted for each, we are
confident that the internal methods are working properly and our code is accurate.

Note: smallwiki.xml and medwiki.xml also operate as expected, although for testing
purposes it may be tedious to explain in entirety that our values are correct, so we
chose to leave them out of our test cases.

---------------------------------- Query.scala ---------------------------------------

Base Cases:

We when we call Query.scala on emptyFile.txt (I.e. we write titles.txt, index.txt, and
pagerank.txt based on the corpus of emptyFile.txt in Index.scala), we should never
Output results based off of a search. This is intuitive, as an empty xml file could
never return links based off of searched values.
Note that when there are no words in the index.txt file (as is the case here), the
terminal outputs "Word not found, Try searching for something else!" And then prompts
The user "Search: Type ':quit' to escape"
This is our intent through the private methods getNextInput(), run(), and printResults()

When we call Query.scala on an Indexed singleFile.xml, the expected output is the same
for empty, except when we search for the title of the file. Searching for the title of
The file should output just the title, and nothing else. (i.e. searching for singlefile
should output: 1 singlefile)
Note that when searching for a word, the repl stems and calls .toLowerCase on each word,
Ensuring the most accurate results possible. This is seen when if we call "SiNgLEFileS",
which parses the word and can effectively handle the "different input" and understand
That the user is trying to find the singlefile document
Note that running this with pagerank turned on would be identical, as page rank for only
One document would have no effect on the outcome.

Calling Query.scala on Query.scala on an Indexed searchTester.xml (recall that
searchTester contains the links that create the pagerank example on page 14 of the
search.pdf), also produces the desired output. Since "a" is a stop word, attempting
To search for "a" or any variation of "a" should return nothing. Since "b" has an
Looking at our index.txt file, d's score is 0 for all files, so it can output them in
Any order. Notice that then we call "d" in the repl, it returns all three files, and
Also does the intended output for b and c.

Calling Query.scala on PageRankWiki1.xml also returns the desired outputs. When searching
For the word "Lawrenceville", it should output 77 first, and then any of the other
equivalent files after (this is derived by looking at the id - score pairs in index.txt,
Lawrencevill at 77 has a score of 1.897, while all the other scores are 0.9485). When
we search for Lawrenceville, it outputs the intended results. NOW, when we call
--pagerank or --smart, 77 should no longer be the first result outputted.
This is because --smart and --pagerank store a different score value into the internal
hashmap that is equal to the index.txt score * the pagerank.txt score. Because the
pagerank score for 100 is so much larger than the other files, it will significantly
Outweighs the other files in rankings. Note that when we call Query.scala with either
The -smart, --smart, -pagerank, or --pagerank tags, and then search for Lawrenceville,
it outputs the intended result, which is: 1 100, 2 77, 3 96 and so on and so on.

We believe that each of these cases provide unique insight into the system testing of
our Query.scala file. Given that the desired txt files are outputted for each, we are
confident that the internal methods are working properly and our code is accurate.

Note: smallwiki.xml and medwiki.xml also operate as expected, although for testing
purposes it may be tedious to explain in entirety that our values are correct, so we
chose to leave them out of our test cases.


----------------------- Error handling in arguments thrown ---------------------------

We have spent significant time discussing how our code should function if we were to
call Query.scala or Index.scala with the proper arguments. Now, how should Query.scala
and Index.scala look if we called them with invalid arguments? We will be discussing
our implementation of error handling here.

Error handling for Index.scala:
	If one inputs a number of arguments not equal to 4, rather than throwing an
Exception, we chose to handle this and print back out to the user "Invalid args: index
Requires xml file, titles, index, and PageRank files as parameters"
	If any of the filepaths do not point to a valid file, it will print back to
the user "Please ensure all arguments point to desired file location"


Error handling for Query.scala:
	If one inputs less than three arguments or more than 4, Repl will return to
the user "Invalid args: Query requires titles, index, and PageRank file as parameters"
	If one inputs 4 arguments and the first argument is not "-smart", "--smart",
"-pagerank", or "--pagerank", the repl will return to the user "error in your arguments,
please rerun"
	If one inputs an invalid title path, it will return to the user: "faulty title
path. Please ensure all file paths are inputted correctly"
	If one inputs an invalid index path, it will return to the user: Faulty
index file path. Please ensure all file paths are inputted correctly"
	If one inputs an invalid pagerank path, it will return to the user: "Faulty
pageRank path. Please ensure all file paths are inputted correctly"

-------------------------------- Unit Testing ----------------------------------------

Most of our methods are private, and all of our methods are tested in one way or
Another in system testing. However, we also test binarySearch and insertVal (from the
Querier.scala class) in the class SearchTest.scala


--------------------------------------------------------------------------------------
Part 7: A list of the people with whom you collaborated.
--------------------------------------------------------------------------------------

We collaborated with James Pizzorolo, Adam Pikelny, and various TAs.
