import TextIO.{getConfigFiles, printFiles}
import Types.Placement

import java.io.File
import scala.io.StdIn.{readDouble, readInt, readLine}

object CLIUtils {

  def getAndHandlefileSelection(): Int = {
    var i = 1
    println("choose file:")
    getConfigFiles.foreach(f => {
      println(s"${i} - ${f.getName.replaceFirst("^ \\.", "")}")
      i = i + 1
    })
    println("0 - quit")
    print("-> ")
    val selected = readInt()
    if(selected < 0 || selected > getConfigFiles.length) {
      println("invalid file number")
      getAndHandlefileSelection()
    }
    else selected
  }

  val getConfigFiles = getListOfFiles(new File("."), List("txt"))

  private def getListOfFiles(dir: File, extensions: List[String]): List[File] = {
    dir.listFiles.filter(_.isFile).toList.filter { file =>
      extensions.exists(file.getName.endsWith(_))
    }
  }

  def getAndHandleOpSelection(): (Double, Int) = {
    val answer = validateAndReturnInput("do you wish to transform this octree?")
    if(answer == 1) {
      print("choose operation:\n1 - scale\n2 - colour")
      print("-> ")
      val selected = readInt()
      if(selected == 0) return (-1,-1)
      if(selected < 0 || selected > 2) {
        println("invalid option")
        getAndHandleOpSelection()
      }
      println(s"selected: ${selected}")
      //chose scale first
      if(selected == 1) {
        val scale = getAndHandleScale()
        val selected = validateAndReturnInput("do you wish to apply a colourOperation?")
        if(selected == 1) {
          val colourOp = getAndHandleColourOp()
          (scale, colourOp)
        } else {
          (scale, -1)
        }
        //chose colour op first
      } else {
        val colourOp = getAndHandleColourOp()
        val selected = validateAndReturnInput("do you wish to scale ?")
        if(selected == 1) {
          val scale = getAndHandleScale()
          (scale, colourOp)
        } else {
          (-1, colourOp)
        }
      }
    } else {
      return(-1, -1)
    }
  }

  private def validateAndReturnInput(prompt: String): Int = {
    println(prompt)
    println("1 - yes\n2 - no")
    val selected = readInt()
    if(selected < 1 || selected > 2) {
      println("invalid option")
      validateAndReturnInput(prompt)
    }
    else selected
  }

  def getAndHandleScale(): Double = {
    println("enter scale factor, must not be negative")
    print("-> ")
    val scale = readDouble()
    if(scale < 0) {
      println("invalid scale")
      getAndHandleScale()
    }
    else scale
  }

  def getAndHandleColourOp(): Int = {
    print("choose colour op:\n1 - greenRemove\n2 - sepia\n")
    print("-> ")
    val colourOp = readInt()
    if(colourOp < 1 || colourOp > 2) {
      println("invalid colour operation option")
      getAndHandleColourOp()
    }
    else colourOp
  }

  def generateOctree(fileNr: Int): Octree[Placement] = {
    fileNr match {
      case x => val models = FileReader.createShapesFromFile(getConfigFiles(x - 1).getCanonicalPath)
      OctreeOps.generateDefaultOctree(models)
    }
  }
  private def generateOctree(fileNr: Int, scale: Double): Octree[Placement] = OctreeOps.scaleOctree(generateOctree(fileNr), scale)
  private def generateOctree(fileNr: Int, colourOp: Int): Octree[Placement] = {
    colourOp match {
      case 1 => OctreeOps.greenRemove(generateOctree(fileNr))
      case 2 => OctreeOps.sepia(generateOctree(fileNr))
    }
  }
  private def generateOctree(fileNr: Int, scale: Double, colourOp: Int): Octree[Placement] = OctreeOps.scaleOctree(generateOctree(fileNr, colourOp), scale)

  def generateOctreeFromUserInput() {
    val fileNr = getAndHandlefileSelection()
    val opArgs = getAndHandleOpSelection()
    val octree = opArgs match {
      case (scale, colourOp) =>
        if (scale == -1 && colourOp == -1) generateOctree(fileNr)
        if (scale == -1 && colourOp != -1) generateOctree(fileNr, colourOp)
        if (scale != -1 && colourOp == 1) generateOctree(fileNr, scale)
        if (scale != -1 && colourOp != -1) generateOctree(fileNr, scale, colourOp)
    }
    println("launching app")
  }

  def main(args: Array[String]) = {
    generateOctreeFromUserInput()
  }
}