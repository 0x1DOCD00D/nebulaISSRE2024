package Nebula


import scala.io.Source
import java.net.{HttpURLConnection, URL}
import java.io.{BufferedWriter, OutputStreamWriter}

object JSONParser {
  def getGAResult(jsonString: String): GAResult = {
    val parsedJson = scala.util.parsing.json.JSON.parseFull(jsonString)
    parsedJson match {
      case Some(data: Map[String, Any]) =>
        val body = data("body").asInstanceOf[String]
        val bodyJson = scala.util.parsing.json.JSON.parseFull(body).get.asInstanceOf[Map[String, List[Boolean]]]
        val solution = bodyJson("solution")  // Directly get the list of booleans
        GAResult(solution)
      case _ =>
        throw new Exception("Failed to parse JSON response.")
    }
  }
}

case class GAResult(result: Seq[Boolean])

object GeneticAlgorithmRunner {
  def applyGA(): Seq[Boolean] = {
    val apiUrl = "http://localhost:9000/2015-03-31/functions/function/invocations"
    val jsonInputString = """{
      "resource": "/run_ga",
      "path": "/run_ga",
      "httpMethod": "GET",
      "requestContext": {},
      "multiValueQueryStringParameters": null
    }"""

    val url = new URL(apiUrl)
    val connection = url.openConnection().asInstanceOf[HttpURLConnection]
    connection.setRequestMethod("POST")
    connection.setRequestProperty("Content-Type", "application/json")
    connection.setDoOutput(true)

    val writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream, "UTF-8"))
    writer.write(jsonInputString)
    writer.flush()
    writer.close()

    val responseCode = connection.getResponseCode
    if (responseCode == HttpURLConnection.HTTP_OK) {
      val res = Source.fromInputStream(connection.getInputStream).mkString
      connection.disconnect()
      JSONParser.getGAResult(res).result
    } else {
      connection.disconnect()
      throw new Exception(s"Request failed with response code $responseCode")
    }
  }
}


