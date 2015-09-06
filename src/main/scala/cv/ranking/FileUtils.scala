package cv.ranking

import java.io.File
import java.nio.file.Files._
import java.nio.file.Files.copy
import java.nio.file.Path
import java.nio.file.Paths.get
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import org.apache.tika.Tika
import java.util.stream.Collectors

/**
 * Utility routine of working with filesystem. Namely: managing directories, copying files,extracting tesxt from different file formats.
 */
object FileUtils {
  val data = get(".").resolve("data")
  val corpus = data.resolve("corpus").toAbsolutePath()
  val query = data.resolve("query").toAbsolutePath()
  val results = data.resolve("results").toAbsolutePath()
  val tika = new Tika

  def listFiles(path: Path): Seq[Path] = {
    if (isDirectory(path))
      path.toRealPath().toFile().listFiles().map(f => get(f.getAbsolutePath))
    else
      Seq()
  }

  def checkCorpusDir() = {
    if (!exists(corpus)) createDirectories(corpus)
    if (listFiles(corpus).isEmpty) {
      println(s"Corpus folder ${corpus.toRealPath()} is empty. Please put the documents to search from to this folder.")
      System.exit(0)
    }
  }

  def checkQueryDir() = {
    if (!exists(query)) createDirectories(query)
    if (listFiles(query).isEmpty) {
      println(s"Query folder ${query.toRealPath()} is empty. Please put the documents to search for to this folder.")
      System.exit(0)
    }
  }

  def checkResultsDir() = {
    if (exists(results)) listFiles(results).foreach { file => delete(file) }
    else createDirectories(results)
  }

  def copyFileToResults(path: String, score: Double) = {
    val file = get(path)
    val scoredName = get(s"$score-${file.getFileName}")
    val dest = results.resolve(scoredName)
    copy(file, dest, REPLACE_EXISTING)
  }

  def stringsFromCoprus = stringsFromFiles(corpus)
  def stringsFromQuery = stringsFromFiles(query)
  def stringFromFile(f: Path): String = tika.parseToString(f.toFile())
  def stringsFromFiles(dir: Path): Seq[(String, String)] = listFiles(dir).map(f => (f, stringFromFile(f))).map((e) => (e._1.toAbsolutePath().toString, e._2))
}