package Generator

import Generator.GraphGenerator.Vertex
import Probability.ProbabilityCalculator
import Schema.{MessageSchema, OrchestratorSchema}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.Logger

import scala.util.Random

class OrchestratorGenerator {}

object OrchestratorGenerator:
  val config = ConfigFactory.load().getConfig("orchestratorGenerator")
  val logger = Logger(this.getClass)
  val DEFAULT_ACTOR_INSTANCES = config.getInt("defaultActorInstances")
  val networkUtilizationMean = config.getInt("networkUtilizationMean")

  // Message to send
  private def generateInitMessages(messages: Seq[String]): Seq[String] =
    val messageIndex = Random.between(0, messages.size)
    val message = messages(messageIndex)
    Seq(message)

  // Number of messages to send to each actor
  private def generateMessagesParam(totalMessages: Int, iterator:Int, numOfMessages:Seq[Int]): Seq[Int] =
    if(iterator >= totalMessages) numOfMessages
    else generateMessagesParam(totalMessages, iterator + 1,
      numOfMessages :+ ProbabilityCalculator.computePoissonDistribution(
        networkUtilizationMean
      ))

  private def generateTimeInterval(numOfMessages: Int, iterator:Int, timeIntevalSeq: Seq[Int]): Seq[Int] =
    if(iterator >= numOfMessages) timeIntevalSeq
    else generateTimeInterval(
      numOfMessages,
      iterator + 1,
      timeIntevalSeq :+ Random.between(0, config.getInt("timeInterval") + 1)
    )

  private def generateSlackTimeInterval(numOfMessages: Int) = Seq.fill(numOfMessages)(1)

  def generateOrchestrator(graph : Map[Int, Seq[Int]],
                           iterator: Int,
                           orchestratorSchema: Seq[OrchestratorSchema],
                          ): Seq[OrchestratorSchema] =
    if (iterator >= graph.size) orchestratorSchema
    else
      val generatedMessages: Seq[String] = generateInitMessages(MessageGenerator.messages)
      generateOrchestrator(
        graph,
        iterator + 1,
        orchestratorSchema :+
          OrchestratorSchema(
            s"actor_${iterator}",
            generatedMessages,
            generateMessagesParam(generatedMessages.size, 0, Seq.empty),
            generateTimeInterval(generatedMessages.size, 0, Seq.empty),
            generateSlackTimeInterval(generatedMessages.size),
            DEFAULT_ACTOR_INSTANCES
          )
        )








