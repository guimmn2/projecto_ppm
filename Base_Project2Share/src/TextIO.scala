import scala.io.StdIn.readLine
import scala.io.StdIn.readDouble
import scala.util.Random
import java.nio.file.{Paths, Files}


object TextIO {

  def showPromptFile(): Unit = { print("\nEscolha o ficheiro: ") }
  def getUserInputFile(): String = readLine.trim

  def showPromptMethods(): Unit = { print("\nEscolha um método só há ScaleOctree por enquanto: ")}
  def getUserInputMethod(): String = readLine.trim

  def showPromptScaleOctree(): Unit = { print("\nEscolha o número para scale: ")}
  def getUserInputScaleOctree(): Double = readDouble()
}
