import TextIO.getConfigFiles
import Types.Placement

import java.io.File
import scala.io.StdIn.{readInt, readLine}

object CLI {

  def getUserInput(): String = readLine.trim

  def printFiles() = {
    var i = 1
    getConfigFiles.foreach(f => {
      println(s" ${i} - ${f.getName.replaceFirst("^ \\.", "")}")
      i = i + 1
    })
  }

  val getConfigFiles = getListOfFiles(new File("."), List("txt"))

  private def getListOfFiles(dir: File, extensions: List[String]): List[File] = {
    dir.listFiles.filter(_.isFile).toList.filter { file =>
      extensions.exists(file.getName.endsWith(_))
    }
  }

  def selectFile(): Octree[Placement] = {
    val selected = readInt()
    selected match {
      case x => val models = FileReader.createShapesFromFile(getConfigFiles(x - 1).getCanonicalPath)
      OctreeOps.generateDefaultOctree(models)
    }
  }

  def main(args: Array[String]) = {
    printFiles
  }
}