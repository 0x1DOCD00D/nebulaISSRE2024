package StressTesting

class ProcessorEater {}

object ProcessorEater:
  def processorEaterCode(milliseconds: Int) : String =
    s"""
      |  val sleepTime: Long = $milliseconds * 1000000L // convert to nanoseconds
      |  val startTime: Long = System.nanoTime
      |  while ((System.nanoTime - startTime) < sleepTime) {}
      |  val result = protoMessage
      |""".stripMargin

