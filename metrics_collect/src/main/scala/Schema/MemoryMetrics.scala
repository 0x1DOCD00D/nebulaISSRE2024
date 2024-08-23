package Schema

import scala.beans.BeanProperty

case class MemoryMetrics(usedMemory: Long, freeMemory: Long, totalMemory: Long){
  def toBean: MemoryMetricsBean = {
    val memoryMetrics = new MemoryMetricsBean()
    memoryMetrics.usedMemory = usedMemory
    memoryMetrics.freeMemory = freeMemory
    memoryMetrics.totalMemory = totalMemory
    memoryMetrics
  }
}

class MemoryMetricsBean() {
  @BeanProperty var usedMemory: Long = _
  @BeanProperty var freeMemory: Long = _
  @BeanProperty var totalMemory: Long = _
  def toCase : MemoryMetrics = {
    MemoryMetrics(usedMemory, freeMemory, totalMemory)
  }
}
