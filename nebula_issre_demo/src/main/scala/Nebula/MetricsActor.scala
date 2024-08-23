package Nebula

import akka.actor.ActorSystem
import akka.actor.Actor
import akka.actor.Props
import com.google.firebase.database.{DatabaseReference, FirebaseDatabase}
import Nebula.MetricsCollector.osBean
import Schema.{CPUMetrics, MemoryMetrics}

import java.time.Instant
import scala.annotation.tailrec
import scala.concurrent.duration.*

class MetricsActor(server: String, experimentName: String) extends Actor {
  import context.dispatcher

  val database: FirebaseDatabase = FirebaseDatabase.getInstance()

  def receive = {
    case "start" =>
      context.system.scheduler.scheduleWithFixedDelay(15.seconds, 0.minutes, self, "collectMetrics")

    case "collectMetrics" =>
      storeMetrics(server, experimentName)
  }

  def storeMetrics(server: String, experiment: String): Unit = {
    println ("Storing metrics...")
    val cpuRef = buildDBRef(server, experiment).child("cpu")
    storeCpuMetrics (cpuRef)
    val memoryRef = buildDBRef(server, experiment).child("memory")
    storeMemoryMetrics (memoryRef)
    println ("Metrics stored...")
    Thread.sleep (3000)
    storeMetrics (server, experiment)

  }

  def buildDBRef(reference: String, metricsType: String): DatabaseReference = {
    val ref = database.getReference(reference)
    val child = ref.child(metricsType)
    val timestamp_ref = child.child(Instant.now.toEpochMilli.toString)
    child
  }

  def storeCpuMetrics(ref: DatabaseReference): Unit = {
    // What % CPU load this current JVM is taking, from 0.0-1.0
    val cpuLoad = osBean.getProcessCpuLoad
    // What % load the overall system is at, from 0.0-1.0
    val systemCpuLoad = osBean.getSystemCpuLoad
    val value = CPUMetrics(cpuLoad, systemCpuLoad).toBean
    ref.setValue(value)
  }

  def storeMemoryMetrics(ref: DatabaseReference): Unit = {
    val runtime = Runtime.getRuntime
    val usedMemory = runtime.totalMemory - runtime.freeMemory
    val freeMemory = runtime.freeMemory
    val totalMemory = runtime.totalMemory
    val value = MemoryMetrics(usedMemory, freeMemory, totalMemory).toBean
    ref.setValue(value)
  }

  // Helper method to validate metrics
  private def isValidMetric(value: Double): Boolean = {
    !value.isNaN && !value.isInfinity
  }
}

