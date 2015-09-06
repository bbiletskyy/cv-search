CV-search application searches the corpus of cv's (actually any pdf, doc or txt document) using a set of documents as a query. 

Promblem statement. Assume we have a bunch of CV's in different formats which we want to search.

There are several ways we can compose search queries. One can search the text in cv's for keywords, like "java", "teamlead", "scrum". This approach is not  very handy, since there are not so many combinations of keywords you can come up with, not enough to describe a much reacher set of sv's.

Another approach is to parse cv's and to extract important fields to database, then to query for. This approach is very complex due to parsing and data extraction part, it also  requires the knowledge  of a query language, such as SQL.

What if we could query our cv's-base by specifying a bunch of example cv's, to look for similar ones. This application sorts cv's in *.pdf, doc, txt formats from the "corpus" folder in order of their relevance to the cv's in the "query" folder. In other words you query by examples instead of keywords.

Uses Tika parser and Sparks MLlib TF-IDF statistics implementation in order to sort a set of documents according to their similarity to the query qocuments. 

How to run:

1. Put documents to the corpus folder
2. Put examples to the query folder
3. Execute in command line: <code>sbt run</code> 

See more:

1. [Spark MLlib](http://spark.apache.org/mllib/)
2. [Vector Space Model](https://en.wikipedia.org/wiki/Vector_space_model)
3. [TF-IDF](https://en.wikipedia.org/wiki/Tf%E2%80%93idf)
