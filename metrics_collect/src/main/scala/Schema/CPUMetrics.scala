package Schema

import scala.beans.BeanProperty

case class CPUMetrics(cpuLoad: Double, systemCpuLoad: Double){
  def toBean: CPUMetricsBean = {
    val cpuMetrics = new CPUMetricsBean()
    cpuMetrics.cpuLoad = cpuLoad
    cpuMetrics.systemCpuLoad = systemCpuLoad
    cpuMetrics
  }
}

class CPUMetricsBean() {
  @BeanProperty var cpuLoad: Double = _
  @BeanProperty var systemCpuLoad: Double = _
  def toCase : CPUMetrics = {
    CPUMetrics(cpuLoad, systemCpuLoad)
  }
}
