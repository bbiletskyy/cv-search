package cv.ranking

import java.io.File
import org.apache.spark.mllib.linalg._
import org.apache.spark.SparkConf
import org.apache.tika.Tika
import org.apache.spark.SparkContext
import org.apache.spark.mllib.feature.IDF
import org.apache.spark.mllib.feature.HashingTF

/**
 * Problem statement. Assume we have a bunch of CV's in different formats which we want to search.
 *
 * There are several ways we can compose search queries. One can search the text in cv's for keywords,
 *  like "java", "teamlead", "scrum". This approach is not  very handy, since there are not so many
 *  combinations of keywords you can come up with, not enough to describe a much reacher set of sv's.
 *
 *
 *  Another approach is to parse cv's and to extract important fields to database, then to query for.
 *  This approach is very complex due to parsing and data extraction part, it also  requires the knowledge
 *  of a query language, such as SQL.
 *
 *  What if we could query our cv's-base by specifying a bunch of example cv's, to look for similar ones.
 *  This application sorts cv's in *.pdf, doc, txt formats from the "corpus" folder in order of their relevance
 *  to the cv's in the "query" folder. In other words you query by examples instead of keywords.
 *
 */
object Main {
  lazy val sc = new SparkContext(sparkConf("localhost", 7777))
  lazy val hashingTF = new HashingTF

  case class Doc(path: String, words: Seq[String])
  case class DocScore(doc: Doc, score: Double)

  /** 
   *  Copies cv's from the "corpus" folder to the "results" folder and sorts them according to cv's in the "query" folder.
   */
  def main(args: Array[String]): Unit = {
    //checking data directories
    FileUtils.checkCorpusDir()
    FileUtils.checkQueryDir()
    FileUtils.checkResultsDir()

    //loading documents from the corpus folder, we store the path and the words of the document.
    val corpus = FileUtils.stringsFromCoprus.map(e => Doc(e._1, words(e._2))).toSet
    //loading all the words from the documents in the query folder into one a single sequence.
    val query = FileUtils.stringsFromQuery.flatMap(e => words(e._2))
    println(s"Corpus contains ${corpus.size} documents")
    println(s"Query: ${query.mkString(", ")}")
    //sorting documents according to their relevance to the query
    val result = sortDocsByQuery(corpus, query)
    //saving scored documents to the results folder
    result.foreach { d => FileUtils.copyFileToResults(d.doc.path, d.score) }
    println(s"${result.size} documents processed.")
  }

  /**Extracts sequence of words from a string. The string will be normalised.*/
  def words(s: String): Seq[String] = normalize(s).split(" ").map(w => w.trim).filter(!_.isEmpty())

  /** Performs some example substitutions ands removes special characters, except for the underscore "_" character.*/
  def normalize(s: String) = replace(s, substitutions).replaceAll("""([\p{Punct}&&[^_]]|\b\p{IsLetter}{1,2}\b)\s*""", " ")

  /**Returns the map of substitutions */
  def substitutions = Map(
    "c#" -> "c_sharp",
    "c++" -> "c_plus_plus",
    "java script" -> "java_script")

  /**
   * Performs a sequence of replacements in the string according to the substitutions map defiled as
   * <code>Map("c#"->"c_sharp", "java script"->"java_script")</code>
   *  Such substitutions can be useful in case 1) of short keywords containing special characters; 2) keywords consisting of multiple words.
   *
   *  1) Text mining algorithms usually don't consider words shorter than 3 characters as meaningless. After special characters removal
   *  important keywords might be lost (for example: "c#" -> "c" -> ""). In Such cases prior to special characters removal we perform
   *  a substitution "c#"->"c_sharp" (the underscore character is the only special character we allow for this reasons).
   *
   *  2) Text mining algorithms usually don't consider the order of words in the text (a.k.a. Bag Of Words model). This means that the
   *
   *  meaning of some important complex keyword might be lost (for example: "java script" -> "script", "java"). In such cases
   *  in order to preserve the order of words we perform a substitution ("java script" -> "java_script").
   */
  def replace(s: String, substitutions: Map[String, String]) = substitutions.keys.foldLeft(s.toLowerCase())((buff, key) => buff.replace(key, substitutions(key)))

  /**
   * Sorts docs according to their relevance to the query.
   * 
   * We use he TF-IDF statistics to project an arbitrary text to an n-dimensional metric space. 
   * The distance between the points of n-dimensional space can be treated as the inverse similarity 
   * measure of the documents.
   * 
   * We use the distance between the points in n-dimensional space to sort the documents according 
   * to their similarity to the query. 
   */
  def sortDocsByQuery(docs: Set[Doc], query: Seq[String]): Set[DocScore] = {
    //enable parallel processing for docs
    val documentsRDD = sc.parallelize(docs.toSeq)
    //obtain TF statistics for corpus documents
    val corpusTfs = hashingTF.transform(documentsRDD.map(_.words))
    //cache values, since we goign to re-use them multiple times
    corpusTfs.cache()
    //create a model of information relevance of words basing on corpus
    val corpusIdfModel = new IDF().fit(corpusTfs)
    //use IDF model to transform corpus TF to TF-IDF
    val corpusTfIdfs = corpusIdfModel.transform(corpusTfs)
    //obtain TF from query
    val queryTfs = hashingTF.transform(sc.parallelize(Seq(query)))
    //obtain TF-IDF fromquery
    val queryTfIdfs = corpusIdfModel.transform(queryTfs)
    //Create a sequence of pairs of document with its corresponding TF-IDF vector.
    //This procedure projects a text to a n-dimensional metric space, where inverse distance between TF-IDF vectors corresponds to similarity between original documents.  
    val documentTfIdfPairs = documentsRDD.zip(corpusTfIdfs)
    //query's TF-IDF
    val queryTfIdf = queryTfIdfs.collect().head
    //define score function that 
    def score(x: Vector) = Vectors.sqdist(x, queryTfIdf)
    //sort documents according to the distance of each document to the query document,inother words to the documents' relevance to the query
    val documentScorePairs = documentTfIdfPairs.map(e => DocScore(e._1, score(e._2))).sortBy(e => e.score, ascending = true)
    //collecting results  and converting them to a set
    documentScorePairs.collect().toSet
  }

  /** Crate Spark configuration */
  def sparkConf(driverHost: String, driverPort: Int) = new SparkConf(false)
    .setMaster("local[*]")
    .setAppName("Spark Streaming with Scala and Akka")
    .set("spark.logConf", "true")
    .set("spark.driver.port", driverPort.toString)
    .set("spark.driver.host", driverHost)
    .set("spark.akka.logLifecycleEvents", "true")
}