package Nebula

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.{Config, ConfigFactory}
import NebulaScala2.Scala2Main.generatedActorSystems
import NebulaScala3.Parser.{JSONParser, YAMLParser}

import scala.language.postfixOps

class ActorSystemFactory

object ActorSystemFactory {
  // Initializes actor systems based on provided configuration
  def initActorSystem(name: String, configString: String, cluster: Boolean, clusterJsonPath: String): Seq[ActorSystem] = {
    if (configString.isEmpty) {
      // Create and return a single actor system
      createAndStoreActorSystem(name)
    } else {
      val config = ConfigFactory.parseString(configString).resolve()
      if (cluster) {
        // Initialize and return a cluster of actor systems
        initializeCluster()
      } else {
        // Create and return a single actor system with provided config
        createAndStoreActorSystem(name, config)
      }
    }
  }

  // Creates and stores an ActorSystem with the given name and optional configuration
  private def createAndStoreActorSystem(name: String, config: Config = ConfigFactory.empty()): Seq[ActorSystem] = {
    val actorSystem = ActorSystem(name, config)
    generatedActorSystems += name -> actorSystem
    Seq(actorSystem) // Return as Seq[ActorSystem]
  }

  // Initializes a cluster of ActorSystems
  private def initializeCluster(): Seq[ActorSystem] = {
    FirebaseInitializer.initialize()

    val system1 = ActorSystem("Nebula", ConfigFactory.load())
    val system2 = ActorSystem("Nebula", ConfigFactory.load("application-2.conf"))

    // Start Metrics Actor on the first system
    val metricsActor = system1.actorOf(Props(new MetricsActor("demo", "exp1")), "metricsActor")
    metricsActor ! "start"

//    val metricsActor2 = system2.actorOf(Props(new MetricsActor("server2", "exp1")), "metricsActo2")
//    metricsActor2 ! "start"

    Seq(system1, system2) // Return as Seq[ActorSystem]
  }
}
