import java.io.File
import scala.io.StdIn.readLine
import scala.io.StdIn.readDouble
import scala.util.Random
import java.nio.file.{Files, Paths}


object TextIO {

  def showPromptFile(): Unit = { print("\nEscolha o ficheiro: ") }
  def printFiles = getConfigFiles.foreach(f => {
    println(s"${f.getName.replaceFirst("^ \\.", "")}")
  })
  def getUserInputFile(): String = readLine.trim

  val getConfigFiles = getListOfFiles(new File("."), List("txt"))

  private def getListOfFiles(dir: File, extensions: List[String]): List[File] = {
    dir.listFiles.filter(_.isFile).toList.filter { file =>
      extensions.exists(file.getName.endsWith(_))
    }
  }

  def showPromptMethods(): Unit = { print("\nEscolha um método só há ScaleOctree por enquanto: ")}
  def getUserInputMethod(): String = readLine.trim.toUpperCase()

  def showPromptScaleOctree(): Unit = { print("\nEscolha o número para scale: ")}
  def getUserInputScaleOctree(): Double = readDouble()

  def main(args: Array[String]): Unit = {
    TextIO.printFiles
  }
}
