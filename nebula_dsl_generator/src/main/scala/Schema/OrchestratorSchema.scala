package Schema

import play.api.libs.json.Json

case class OrchestratorSchema(
    name: String,
    initMessages: Seq[String],
    numOfMessages: Seq[Int],
    timeInterval: Seq[Int],
    slackTimeInterval: Seq[Int],
    numOfInstances: Int
)
