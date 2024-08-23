package Nebula

import akka.actor.Actor
import com.martensigwart.fakeload.{FakeLoad, FakeLoadExecutor, FakeLoadExecutors, FakeLoads}

import java.util.concurrent.TimeUnit

class ProcessorEater extends Actor{
  override def receive: Receive = {
    case load: Int =>
      println("Eating CPU ...")
      val fakeload = FakeLoads.create.lasting(3600, TimeUnit.SECONDS).withCpu(load)
      // Execution
      val executor = FakeLoadExecutors.newDefaultExecutor
      executor.execute(fakeload)
    case _ => println("Unknown message")
  }
}
