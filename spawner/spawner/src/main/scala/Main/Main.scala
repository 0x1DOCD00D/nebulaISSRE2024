package Main

import akka.actor.{Actor, ActorSystem}
import com.typesafe.config.ConfigFactory

import java.io.{BufferedReader, InputStreamReader}
import java.net.*
import java.net.InetAddress
import scala.io.Source




class Main

object Main:
  @main def runIt(): Unit =
    println("Init the ActorSystem...")
    val ip: String = Source.fromFile("ip.txt").getLines.next()
    println("My IP Address is: " + ip)
    val config =
      s"""
        |akka {
        |  actor {
        |    provider = remote
        |  }
        |  remote {
        |   use-unsafe-remote-features-outside-cluster = true
        |    artery{
        |      canonical.hostname = $ip
        |      canonical.port = 5555
        |    }
        |  }
        |}
        |""".stripMargin

    val system = ActorSystem("Nebula", ConfigFactory.parseString(config))
    println("ActorSystem is running...")
    println(system.name)
    Thread.sleep(10000000)
