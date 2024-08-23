package Generator

import com.typesafe.config.ConfigFactory

import scala.annotation.tailrec
import scala.collection.immutable.HashMap
import scala.math.{ceil, log, sqrt}
import com.typesafe.scalalogging.Logger

class GraphGenerator


object GraphGenerator:
  val config = ConfigFactory.load().getConfig("graphGenerator")
  val logger = Logger(this.getClass)
  val NUMBER_OF_VERTICES = config.getInt("NUMBER_OF_VERTICES")
  val PROBABILITY = config.getDouble("PROBABILITY")
  val MAX = config.getInt("MAX")
  val LOG_MAX = log(MAX);
  val MAXIMUM_EDGES = NUMBER_OF_VERTICES * (NUMBER_OF_VERTICES - 1) / 2;

  case class Vertex(x: Int, y: Int)

  def generateRandomGraph(): Seq[Vertex] =
    val denominator = if(PROBABILITY != 0 || PROBABILITY != 1) 1/log(1-PROBABILITY) else -1
    val square = 4 * NUMBER_OF_VERTICES * NUMBER_OF_VERTICES - 4 * NUMBER_OF_VERTICES - 7;	//used in decoding
    val generatedGraph: Seq[Vertex] = runZerAlgorithm(Seq.empty, 0, denominator, square, 1)
    printGraph(generatedGraph)
    generatedGraph


  def generateGraphHashMap(graph: Seq[Vertex]): Map[Int, Seq[Int]] =
    val max = graph.sortBy(_.x).lastOption
    val last = max.getOrElse(Vertex(0, 0)).x
    val graphMap =
      graph.groupBy(_.x).map(x => (x._1, x._2.map(_.y))) +
        (last + 1 -> Seq.empty)
    logger.info(s"Generated Map: $graphMap")
    graphMap

  private def printGraph(graph: Seq[Vertex]) =
      graph.groupBy(_.x).foreach(println(_))

  private def runZerAlgorithm(vertexList: Seq[Vertex],iterator: Int, denominator: Double, square: Double, seed: Int): Seq[Vertex] =
    val randomSeed = RandomLCG(seed)
    val skip = ceil((log(randomSeed) - LOG_MAX) * denominator - 1)
    val skipValue = iterator + skip
    if (iterator >= MAXIMUM_EDGES || (skipValue >= MAXIMUM_EDGES && NUMBER_OF_VERTICES >=5)) return vertexList
    if (skipValue < NUMBER_OF_VERTICES - 1)
      val sv: Int = 0
      val tv: Int = iterator + 1
      runZerAlgorithm(vertexList :+ Vertex(sv, tv),
        iterator + 1, denominator, square,
        randomSeed
        )
    else
      val sv: Int = ceil((2 * NUMBER_OF_VERTICES - 1 - sqrt(square - 8 * iterator)) / 2 - 1).toInt
      val tv: Int = sv + (iterator + 1 - (2 * NUMBER_OF_VERTICES - sv - 1) * sv / 2)
      runZerAlgorithm(vertexList :+ Vertex(sv, tv),
        iterator + 1, denominator, square,
        randomSeed
      )

  private def RandomLCG(seed: Int) =
    val a = 16807; //ie 7**5
    val m = 2147483647 //ie 2**31-1
    val temp = seed * a
    (temp)%m;


