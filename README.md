# M4ps
Navigation system utilizing a Java back-end with
these components (Autocorrect, KD-Tree/Nearest Neighbor
Search, and Djikstra's Pathfinder). Front-end work was
done mainly utilizing Javascript and canvas for path-
drawing and HTML/CSS for the barebones display.

DESIGN DETAILS
For map, we use the database loading from Bacon to create a connection to the passed in database, create a corpus for autocorrect out of street names, and get the nodes for the KDTree for nearest.

For ways, we use a sequel query to find all ways within a bounding box.

For nearest, we use the KDTree to search for the nearest neighbor to a given coordinate.

For route, the main classes are Dijkstra, which finds the shortest path between two vertices (in this case,
Nodes) and returns the Ways. There are Edge and Vertex interfaces for Node and Way.

For the suggest command, our main classes include Trie (made up of TrieNodes), CorpusParser, AcCommands, and SuggestionGenerator. It also has comparators: BigramProbComparator for sorting strings by their bigram probability, UnigramProbComparator for sorting by unigram probability, and RankingComparator which combines the first two by first sorting by bigrams and using unigrams when there's a tie. If there's a tie in unigrams, suggestions are sorted alphabetically. Suggest uses default options - it splits on whitespace,
has led of one, and prefix is on.

The CorpusParser takes in a .txt file and adds each street name as a word, so suggest will only return valid street names. Each node of the trie represents one character of a word - its are all of the characters that could follow that character and feasibly create a valid street name. We use the trie for prefix matching and efficiently generating whitespace and Levenshtein edit distance suggestions in SuggestionGenerator. 

MapsCommands also includes an inner class for traffic.

OPTIMIZATIONS
We used tile caching for our front end in order to make drawing the map more efficient and used threads for the traffic server.

RUNNING PROGRAM
Make sure to move maps.sqlite3 into the data/maps folder before doing the following.

To build and run junit tests, run mvn package.
To run the program without the GUI, run ./run in the maps-nsteinb1-snathan directory.

To run the program with the GUI and no traffic, run the following: ./run --gui --port 8080.
You can replace 8080 with any other port number. Then navigate to http://localhost:8080/maps in your
browser (we used Chrome to test).

To run the program with the GUI and traffic, run the traffic server from the top directory using:
python cs032_traffic_server_nitros 8080 data/maps/maps.sqlite3
You can replace 8080 with a different port, but it must be the same port as the one you'll use
to run the GUI. You can also replace data/maps/maps.sqlite3 with another database.

For less traffic, you can run the above with cs032_traffic_server instead of cs032_traffic_server_nitros.

For systems tests, run: python3 cs32-test tests/* --timeout 180 from the top directory.


