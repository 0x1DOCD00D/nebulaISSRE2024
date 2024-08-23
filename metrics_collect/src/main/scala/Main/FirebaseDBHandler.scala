package Main

import Metrics.MetricsCollector.osBean
import Schema.{CPUMetrics, MemoryMetrics}
import com.google.firebase.database.{DatabaseReference, FirebaseDatabase}
import com.google.firebase.{FirebaseApp, FirebaseOptions}

import java.io.ByteArrayInputStream
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.io.FileInputStream
import java.time.Instant
import scala.annotation.tailrec
import scala.io.Source

object FirebaseDBHandler {
  val serviceAccount: String =
    """
      
      |""".stripMargin

  val stream = new ByteArrayInputStream(serviceAccount.getBytes(StandardCharsets.UTF_8))
  val options: FirebaseOptions = new FirebaseOptions.Builder()
    .setDatabaseUrl("https://nebula-cf706-default-rtdb.firebaseio.com")
    .setServiceAccount(stream)
    .build
  val app: FirebaseApp = FirebaseApp.initializeApp(options)
  val database: FirebaseDatabase = FirebaseDatabase.getInstance(app)

  def buildDBRef(reference: String, metricsType: String): DatabaseReference = {
    val ref = database.getReference(reference)
    val child = ref.child(metricsType)
//    val timestamp_ref = child.child(Instant.now.toEpochMilli.toString)
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

  def main(args: Array[String]): Unit = {
    val server = args(0)
    val experimentNumber = args(1)
    storeMetrics(server, experimentNumber)
  }

  @tailrec
  def storeMetrics(server: String, experiment: String): Unit =
    println("Storing metrics...")
    val cpuRef = buildDBRef(server, experiment).child("cpu")
    storeCpuMetrics(cpuRef)
    val memoryRef = buildDBRef(server, experiment).child("memory")
    storeMemoryMetrics(memoryRef)
    println("Metrics stored...")
    Thread.sleep(3000)
    storeMetrics(server, experiment)

}
