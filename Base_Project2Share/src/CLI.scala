import TextIO.{getConfigFiles, printFiles}
import Types.Placement

import java.io.File
import scala.io.StdIn.{readDouble, readInt, readLine}

case class State(fileNr: Int, scale: Int, colourOp: Int)

object CLI {

  def promptFileOptions() = {
    var i = 1
    println("choose file:")
    getConfigFiles.foreach(f => {
      println(s"${i} - ${f.getName.replaceFirst("^ \\.", "")}")
      i = i + 1
    })
    println("0 - quit")
    print("-> ")
  }

  def getAndHandlefileSelection(): Int = {
    promptFileOptions()
    val selected = readInt()
    if(selected < 0 || selected > getConfigFiles.length) {
      println("invalid file number")
      getAndHandlefileSelection()
    }
    else selected
  }

  def getAndHandleOpSelection(): (Double, Int) = {
    promptOps()
    val selected = readInt()
    if(selected < 0 || selected > 2) {
      println("invalid option")
      getAndHandleOpSelection()
    }
    println(s"selected: ${selected}")
    if(selected == 1) {
      val scale = getAndHandleScale()
      val selected = validateAndReturnInput("do you wish to apply a colourOperation?")
      if(selected == 1) {
        val colourOp = getAndHandleColourOp()
        (scale, colourOp)
      } else {
        (scale, 0)
      }
    } else {
      println("at else")
      val colourOp = getAndHandleColourOp()
      val selected = validateAndReturnInput("do you wish to scale ?")
      if(selected == 1) {
        val scale = getAndHandleScale()
        (scale, colourOp)
      } else {
        (1, colourOp)
      }
    }
  }

  def validateAndReturnInput(prompt: String): Int = {
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
    promptScale()
    val scale = readDouble()
    if(scale < 0) {
      println("invalid scale")
      getAndHandleScale()
    }
    else scale
  }

  def getAndHandleColourOp(): Int = {
    promptColourOps()
    val colourOp = readInt()
    if(colourOp < 1 || colourOp > 2) {
      println("invalid colour operation option")
      getAndHandleColourOp()
    }
    else colourOp
  }

  def promptOps() = {
    print("choose operation:\n1 - scale\n2 - colour\n")
    print("-> ")
  }

  def promptScale() = {
    println("enter scale factor, must not be negative")
    print("-> ")
  }

  def promptColourOps() = {
    print("choose colour op:\n1 - greenRemove\n2 - sepia\n")
    print("-> ")
  }

  val getConfigFiles = getListOfFiles(new File("."), List("txt"))

  private def getListOfFiles(dir: File, extensions: List[String]): List[File] = {
    dir.listFiles.filter(_.isFile).toList.filter { file =>
      extensions.exists(file.getName.endsWith(_))
    }
  }

  def generateOctreeFromFileNr(fileNr: Int): Octree[Placement] = {
    fileNr match {
      case x => val models = FileReader.createShapesFromFile(getConfigFiles(x - 1).getCanonicalPath)
      OctreeOps.generateDefaultOctree(models)
    }
  }

  def mainLoop() {
    //show options, collect inputs
    //handle inputs
    //pass state
    val fileNr = getAndHandlefileSelection()
    if(fileNr == 0) {
      println("quitting")
      return
    }
    val opArgs = getAndHandleOpSelection()
    println(s"fileNr:${fileNr}")
    println(s"opArgs:${opArgs}")
    println("launching app")
  }

  def main(args: Array[String]) = {
    mainLoop()
  }
}