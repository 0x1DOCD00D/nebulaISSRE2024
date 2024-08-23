package Metrics

import com.sun.management.OperatingSystemMXBean

import java.lang.management.ManagementFactory

class MetricsCollector

case class CPUMetrics(cpuLoad: Double, systemCpuLoad: Double)
case class MemoryMetrics(usedMemory: Long, freeMemory: Long, totalMemory: Long)

object MetricsCollector:
  val osBean: OperatingSystemMXBean = ManagementFactory.getPlatformMXBean(classOf[OperatingSystemMXBean])


  def collectCPUUsage(): CPUMetrics =
    // What % CPU load this current JVM is taking, from 0.0-1.0
    val cpuLoad = osBean.getProcessCpuLoad
    // What % load the overall system is at, from 0.0-1.0
    val systemCpuLoad = osBean.getSystemCpuLoad

    CPUMetrics(cpuLoad, systemCpuLoad)

  def collectRAMUsage(): MemoryMetrics =
    val runtime = Runtime.getRuntime
    val usedMemory = runtime.totalMemory - runtime.freeMemory
    val freeMemory = runtime.freeMemory
    val totalMemory = runtime.totalMemory

    MemoryMetrics(usedMemory, freeMemory, totalMemory)





