package Generator

import Generator.GraphGenerator.Vertex
import Schema.{ActorSchema, CaseSchema, MethodSchema}
import StressTesting.{MemoryEater, ProcessorEater}

class ActorGenerator {}

object ActorGenerator:

  private def generateExecutionCode(message: String) =
    message match {
      case "StressRAM" => MemoryEater.memoryEaterCode(100)
      case "StressCPU" => ProcessorEater.processorEaterCode(100)
      case _ => """println("Hello World")"""
    }

  private def generateTransitions(vertex: Seq[Int]): Seq[String] =
    vertex.map(x => s"actor_$x").toSeq

  private def generatePatternMatching(messages: Seq[String], iterator: Int,
                                      patternMatchingList: Seq[CaseSchema],
                                      vertex: Seq[Int]
                                     ): Seq[CaseSchema] =
    if(iterator >= messages.size) patternMatchingList
    else generatePatternMatching(
        messages,
        iterator + 1,
        patternMatchingList :+
          CaseSchema(
            messages(iterator),
            generateExecutionCode(messages(iterator)),
            None,
            generateTransitions(vertex)
          ),
      vertex
    )


  private def generateActorMethods(vertex: Seq[Int]): Seq[MethodSchema] =
    Seq(
      MethodSchema(
        "receive",
        "PartialFunction[Any, Unit]",
        Seq.empty,
        generatePatternMatching(MessageGenerator.messages, 0, Seq.empty, vertex),
        Seq.empty
      )
    )

  def generateActor(graph: Map[Int, Seq[Int]],
                    iterator: Int,
                    actorList: Seq[ActorSchema]): Seq[ActorSchema] =
    if(iterator >= graph.size) actorList
    else generateActor(
      graph,
      iterator + 1,
      actorList :+ ActorSchema(
        s"actor_${iterator}",
        Seq.empty,
        Seq.empty,
        generateActorMethods(graph.getOrElse(iterator, return Seq.empty))
      )
    )



