import Generator.{ActorGenerator, FileWriter, GraphGenerator, MessageGenerator, OrchestratorGenerator}
import Schema.OrchestratorSchema
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.Logger
import play.api.libs.json.Json


object Main:
  val logger = Logger(this.getClass)
  val config = ConfigFactory.load().getConfig("exportPath")

  @main def runIt: Unit =
    // Generate Nebula Messages DSL
    val generatedMessages = MessageGenerator.generateMessage(
      0,
      Seq.empty
    )

    // Convert the generated messages to JSON
    import Generator.Formatters.messageFormat
    val messagesJson = Json.toJson(generatedMessages)
    logger.info(s"Generated messages...")
    println(messagesJson)

    // Write the generated messages to a file
    FileWriter.writeStringToFile(
      config.getString("jsonMessagesPath"),
      Json.prettyPrint(messagesJson)
    )
    logger.info(s"Wrote messages to file...")

    logger.info(s"Generating random Graph...")
    // Generate a random graph based on the configuration params
    val generatedGraph = GraphGenerator.generateRandomGraph()
    val graphHashMap = GraphGenerator.generateGraphHashMap(generatedGraph)

    // Generate Nebula Orchestrator DSL from the graph
    val generatedOrchestrator = OrchestratorGenerator.generateOrchestrator(
      graphHashMap,
      0,
      Seq.empty
    )

    // Convert the generated orchestrator to JSON
    import Generator.Formatters.orchestratorFormat
    val orchestratorJson = Json.toJson(generatedOrchestrator)
    logger.info(s"Generated orchestrator...")
    println(orchestratorJson)

    // Write the generated orchestrator to a file
    FileWriter.writeStringToFile(
      config.getString("jsonOrchestratorPath"),
      Json.prettyPrint(orchestratorJson)
    )

    // Generate Nebula Actors DSL from the graph
    val generatedActors = ActorGenerator.generateActor(
      graphHashMap,
      0,
      Seq.empty
    )

    // Convert the generated actors to JSON
    import Generator.Formatters.actorFormat
    val actorsJson = Json.toJson(generatedActors)
    logger.info(s"Generated actors...")
    println(actorsJson)

    // Write the generated actors to a file
    FileWriter.writeStringToFile(
      config.getString("jsonActorsPath"),
      Json.prettyPrint(actorsJson)
    )





