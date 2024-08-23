package Generator

import Schema.MessageSchema

class MessageGenerator {}

object MessageGenerator:
  val messages: Seq[String] = 
    Seq(
      "StressCPU",
      "StressRAM"
    )
    
  def generateMessage(iterator: Int,
                      messageSeq: Seq[MessageSchema]): Seq[MessageSchema] =
    if(iterator >= messages.size) messageSeq
    else generateMessage(
      iterator + 1,
      messageSeq :+ MessageSchema(messages(iterator), Seq.empty)
    )