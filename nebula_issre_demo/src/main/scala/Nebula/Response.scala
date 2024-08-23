package Nebula

import spray.json._
import DefaultJsonProtocol._

// Define the format for your data structure
case class Response(solution: List[Boolean])

object MyJsonProtocol extends DefaultJsonProtocol {
  implicit val responseFormat: RootJsonFormat[Response] = jsonFormat1(Response)
}

import MyJsonProtocol._

// Example function to parse the response
def parseResponse(jsonString: String): Response = {
  jsonString.parseJson.convertTo[Response]
}

