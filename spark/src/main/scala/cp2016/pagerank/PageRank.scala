package cp2016.pagerank

import scala.xml._
import org.apache.spark._
import org.apache.hadoop.fs._

object PageRank {
  def main(args: Array[String]) {
    val inputPath = args(0)
    val outputDir = args(1)

    val config = new SparkConf().setAppName("PageRank")
    val ctx = new SparkContext(config)

    // clean output directory
    val hadoopConf = ctx.hadoopConfiguration
    var hdfs = FileSystem.get(hadoopConf)
    try {
      hdfs.delete(new Path(outputDir), true)
    } catch {
      case ex : Throwable => {
        println(ex.getMessage)
      }
    }

    val pages = ctx.textFile(inputPath, ctx.defaultParallelism)

    val linkPattern = """\[\[[^\]]+\]\]""".r
    val linkSplitPattern = "[#|]"
    val adjMatrix = pages.map { line =>
      val xml = XML.loadString(line)
      val title = (xml \ "title").text
      val text = (xml \ "text").text
//      linkPattern.findAllIn(text).toList.map { link =>
//        (title, link.substring(2, link.length() - 2).split(linkSplitPattern)(0))
//      }.filter { tup => !tup._2.isEmpty() }
      (title, text)
    }
    println(adjMatrix)
    adjMatrix.saveAsTextFile(outputDir)

    ctx.stop
  }
}
