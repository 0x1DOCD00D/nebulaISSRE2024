package Generator

import java.nio.file.{Paths, Files}
import java.nio.charset.StandardCharsets

class FileWriter {}

object FileWriter:
  def writeStringToFile(path: String, fileContent: String): Unit =
    Files.write(Paths.get(path), fileContent.getBytes(StandardCharsets.UTF_8))
