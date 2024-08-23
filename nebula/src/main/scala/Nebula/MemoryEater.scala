package Nebula

import akka.actor.Actor

class MemoryEater extends Actor {
  def receive = {
    case _ =>
      val byte: Seq[Array[Byte]] = Seq()
      var iterator: Int = 0
      while (iterator <= 10000) {
        println("frees memory: ")
        val b: Array[Byte] = new Array[Byte](1048576)
        byte :+ (b)
        val rt: Runtime = Runtime.getRuntime
        println("free memory: " + rt.freeMemory)
        iterator = iterator + 1
        Thread.sleep(100)
      }

  }
}