Instructions
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
