package StressTesting

class MemoryEater {}

object MemoryEater:
  def memoryEaterCode(limit: Int): String =
    s"""val byte: Seq[Array[Byte]] = Seq()
      | var iterator: Int = 0
      |  while (iterator <= $limit) {
      |    println("frees memory: ")
      |    val b: Array[Byte] = new Array[Byte](1048576)
      |    byte :+ (b)
      |    val rt: Runtime = Runtime.getRuntime
      |    println("free memory: " + rt.freeMemory)
      |    iterator = iterator + 1
      |}
      |val result = protoMessage
      |""".stripMargin

