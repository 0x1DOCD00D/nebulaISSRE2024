package Nebula

import Generator.ActorCodeGeneratorOrchestration
import HelperUtils.ObtainConfigReference

import scala.concurrent.*
import ExecutionContext.Implicits.global
import NebulaScala2.Compiler.{ActorCodeCompiler, MessageCodeCompiler, ToolboxGenerator}
import NebulaScala2.{Compiler, Scala2Main}
import NebulaScala3.Generator.{ConfigCodeGenerator, ProtoMessageGenerator}
import NebulaScala3.Parser.{JSONParser, YAMLParser}
import NebulaScala3.Scala3Main
import com.typesafe.scalalogging.Logger
import NebulaScala2.Scala2Main.generatedActorsProps
import NebulaScala3.Scala3Main.protoBufferList
import NebulaScala2.Scala2Main.generatedActorsRef
import NebulaScala2.Scala2Main.generatedActorSystems
import NebulaScala3.message.ProtoMessage
import akka.actor.{ActorRef, ActorSystem}
import com.typesafe.config.Config
import GUI.MainActivity.{clusteringJsonPath, systemRunning}

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.io.StdIn.readLine

class Main

object Main:
  // Init the config file to get static params
//  val config: Config = ObtainConfigReference("nebula") match {
//    case Some(value) => value
//    case None => throw new RuntimeException("Cannot obtain a reference to the config data.")
//  }
  //Logger init
  val logger: Logger = Logger("Main")

  def stopNebula(): Unit =
    generatedActorSystems.foreach {
      case (name, actorSystem) =>
        actorSystem.terminate()
        logger.info(s"ActorSystem $name has been terminated...")
    }

  //Main method of the framework
  def startNebula(actorJsonPath: String,
                  messagesJsonPath: String,
                  orchestratorPath: String,
                  clusterShardingConfigPath: String,
                  monitoringJsonPath: String,
                  allocation: Seq[Boolean]): Unit =
    logger.info(Scala2Main.scala2Message)
    logger.info(Scala3Main.scala3Message)

    //Init Kamon monitoring instrumentation
    //if (config.getBoolean("nebula.enableKamon")) Scala2Main.initKamon()

    //Get the current Toolbox from the Scala2 APIs
    val toolbox = ToolboxGenerator.generateToolbox()

    val actorsJson = JSONParser.getActorSchemaFromJson(actorJsonPath)
    val messagesJson = JSONParser.getMessagesSchemaFromJson(messagesJsonPath)
    val orchestratorJson = JSONParser.getOrchestratorFromJson(orchestratorPath)
    var clusterConfigCode = ""
    var monitoringConfigCode = ""
    if(clusteringJsonPath.nonEmpty) {
      val clusterShardingJson = JSONParser.getClusterShardingSchemaFromJson(clusterShardingConfigPath)
      clusterConfigCode = ConfigCodeGenerator.generateClusterConfigCode(clusterShardingJson)
    }
    if(monitoringJsonPath.nonEmpty) {
      val monitoringJson = JSONParser.getMonitoringFromJson(monitoringJsonPath)
      monitoringConfigCode = ConfigCodeGenerator.generateMonitoringConfig(monitoringJson)
    }


    Thread.sleep(3000)
    //Generate ActorCode as String
    val actorCode = ActorCodeGeneratorOrchestration.generateActorCode(actorsJson, 0, Seq.empty)

    logger.info("Actors code have been generated...")
    println(actorCode)
    Thread.sleep(3000)

    //Generate ActorProps
    val generatedActors = ActorCodeCompiler.compileActors(actorCode, toolbox, 0, Seq.empty)
    logger.info("Actors code have been compiled into Props objects...")

    //Store ActorProps into the global state of Nebula
    generatedActors.zipWithIndex.foreach {
      case (actor, index) =>
        generatedActorsProps += actorsJson(index).actorName.toLowerCase -> actor
    }

    logger.info("Actor Props have been generated...")
    println(generatedActorsProps)
    Thread.sleep(3000)

    //Generate messages within the standard ProtoBuffer
    val protoMessages = ProtoMessageGenerator.generateProtoMessages(
      messagesJson,
      0,
      Seq.empty
    )

    //Store generated ProtoBuffer into the global state of Nebula
    protoMessages.foreach(message => protoBufferList += message.name.toLowerCase -> message)

    logger.info("ProtoMessages have been generated...")
    println(protoBufferList)
    Thread.sleep(3000)

    var combinedConfig = ""
    if(monitoringConfigCode.nonEmpty && clusterConfigCode.nonEmpty){
      logger.info("Generating configuration for monitoring and clustering...")
      combinedConfig = clusterConfigCode.concat(monitoringConfigCode)
    } else if(monitoringConfigCode.isEmpty && clusterConfigCode.nonEmpty){
      logger.info("Generating configuration for clustering...")
      combinedConfig = clusterConfigCode
    } else if(monitoringConfigCode.nonEmpty && clusterConfigCode.isEmpty){
      logger.info("Generating configuration for monitoring...")
      combinedConfig = monitoringConfigCode
    }
    logger.info(combinedConfig)
    Thread.sleep(3000)

    val actorSystem: Seq[ActorSystem] = ActorSystemFactory.initActorSystem("system", combinedConfig, clusteringJsonPath.nonEmpty, clusteringJsonPath)

    //Start Nebula orchestration --> init actors from actorProps stored
    orchestratorJson.foreach { actor =>
      initActor(actor.numOfInstances, actor.name, 0)
    }

    def initActor(numOfInstances: Int, actorName: String, iterator: Int): Unit = {
      if(iterator < numOfInstances){
        val actorProps = generatedActorsProps.getOrElse(actorName.toLowerCase, return)
        if(allocation.nonEmpty){
          if(allocation(iterator)){
            val actorRef: ActorRef = ActorFactory.initActor(actorSystem.head, actorProps, actorName + "_" + iterator)
            generatedActorsRef = generatedActorsRef.updated(
              actorName.toLowerCase,
              generatedActorsRef.getOrElse(
                actorName.toLowerCase,
                Seq.empty[ActorRef]
              ) :+ actorRef)
          }  else {
            val actorRef: ActorRef = ActorFactory.initActor(actorSystem(1), actorProps, actorName + "_" + iterator)
            generatedActorsRef = generatedActorsRef.updated(
              actorName.toLowerCase,
              generatedActorsRef.getOrElse(
                actorName.toLowerCase,
                Seq.empty[ActorRef]
              ) :+ actorRef)
          }
        } else {
          if (iterator % 2 == 0) {
            val actorRef: ActorRef = ActorFactory.initActor(actorSystem(0), actorProps, actorName + "_" + iterator)
            generatedActorsRef = generatedActorsRef.updated(
              actorName.toLowerCase,
              generatedActorsRef.getOrElse(
                actorName.toLowerCase,
                Seq.empty[ActorRef]
              ) :+ actorRef)
          } else {
            val actorRef: ActorRef = ActorFactory.initActor(actorSystem(1), actorProps, actorName + "_" + iterator)
            generatedActorsRef = generatedActorsRef.updated(
              actorName.toLowerCase,
              generatedActorsRef.getOrElse(
                actorName.toLowerCase,
                Seq.empty[ActorRef]
              ) :+ actorRef)
          }
        }

        initActor(numOfInstances, actorName, iterator + 1)
      }
    }

    logger.info("End of ActorRef init ...")
    println(generatedActorsRef)
    Thread.sleep(3000)

    logger.info("Sending init messages...")
    //Send init messages --> send init messages to the Actor instantiated
    orchestratorJson.foreach { orchestration =>
      val actor = generatedActorsRef.getOrElse(orchestration.name.toLowerCase, return)
      if(orchestration.initMessages!=null) {
        orchestration.initMessages.zipWithIndex.foreach {
          case (message, index) =>
            val protoMessage = protoBufferList.getOrElse(message.toLowerCase, return)
            sendMessage(actor, protoMessage, orchestration.timeInterval(index), orchestration.slackTimeInterval(index), orchestration.numOfMessages(index), true)
        }
      }
    }

    def sendMessage(actorRefs: Seq[ActorRef], message: ProtoMessage, timeInterval: Int, slackTimeInterval:Int, numOfMessages: Int, initBoolean: Boolean): Future[Unit] = Future {
      if(numOfMessages > 0 && systemRunning) {
        logger.info("Sending a message...")
        actorRefs.foreach { actor =>
          if(slackTimeInterval > 0 && initBoolean) {
            Thread.sleep(slackTimeInterval.toLong*1000)
          }
          actor ! message
          if(timeInterval > 0 ) {
            println(generatedActorsRef)
            Thread.sleep(1000 * timeInterval.toLong)
            sendMessage(actorRefs, message, timeInterval, slackTimeInterval, numOfMessages - 1, false)
          }
        }

      }
    }



  def optimize(actorJsonPath: String,
               messagesJsonPath: String,
               orchestratorPath: String,
               clusterShardingConfigPath: String,
               monitoringJsonPath: String): Unit = {
    logger.info("Optimization process has started...")
    // Run the Genetic Algorithm to optimize the actor allocation
    val actorAllocation = GeneticAlgorithmRunner.applyGA()
    stopNebula()
    startNebula(actorJsonPath, messagesJsonPath, orchestratorPath, clusterShardingConfigPath, monitoringJsonPath, actorAllocation)
    logger.info("Optimization process has ended...")

  }





