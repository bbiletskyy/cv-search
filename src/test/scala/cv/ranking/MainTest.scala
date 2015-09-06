package cv.ranking

import org.scalatest.FlatSpec
import org.scalatest.Matchers

import Main.Doc
import Main.sortDocsByQuery

class MainTest extends FlatSpec with Matchers {

  "Application" should "sort documents from corpus according to their relevance to the query" in {
    val doc1: Doc = Doc("d:/data/corpus/1.doc", Main.words("Mary hates singing."))
    val doc2: Doc = Doc("d:/data/corpus/2.doc", Main.words("Mary likes working."))
    val doc3: Doc = Doc("d:/data/corpus/3.doc", Main.words("Bill hates singing."))
    val corpus = Set(doc1, doc2, doc3)
    val query = Main.words("mary likes")

    val result = sortDocsByQuery(corpus, query)
    result.size should be(3)
    result.head.doc should be(doc2)
    result.tail.head.doc should be(doc1)
    result.last.doc should be(doc3)
  }

  "Application" should "lowercase words" in {
    val words = Main.words("MarY LIkeS")
    words.size should be(2)
    words.head should be("mary")
    words.last should be("likes")
  }

  "Application" should "should substitute special words tables" in {
    val original = "I like c++ more than java script or c#"
    val substitutions = Map("c++" -> "c_plus_plus", "c#" -> "c_sharp")
    val result = Main.replace(original, substitutions)

    substitutions.foreach(s => if (original.contains(s._1)) result.contains(s._2) should be(true))

    result.contains("c_plus_plus") should be(true)
    result.contains("c++") should be(false)

  }

  "Application" should "remove special characters" in {
    val words = Main.words("&^%%^Mary,*(*LiKes!")
    words.size should be(2)
    words.head should be("mary")
    words.last should be("likes")
  }
}

