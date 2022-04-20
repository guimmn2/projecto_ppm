import scala.collection.mutable
import scala.io.Source
import Types._
import javafx.scene.Node
import javafx.scene.paint.{Color, PhongMaterial}
import javafx.scene.shape.{Box, Cylinder}

object FileReader {

  /** Impure (I/O) - tried to maintain impurity only in this object
   *
   * @param file
   * @return list of Strings with data necessary to create objects
   */
  def createShapesFromFile(file: String): List[Node] = {
    val bufferedSource = Source.fromFile(file)
    val shapeStack = new mutable.Stack[Node]

    for (line <- bufferedSource.getLines) {
      processLine(line) match {
        case (typeOf, color, transition, scale) =>
          if(typeOf.equals("Cylinder")) {
            val cylinder = new Cylinder(0.5f, 1, 10)
            cylinder.setTranslateX(transition._1)
            cylinder.setTranslateY(transition._2)
            cylinder.setTranslateZ(transition._3)
            val material = new PhongMaterial()
            material.setDiffuseColor(Color.rgb(color._1, color._2, color._3))
            cylinder.setMaterial(material)
            shapeStack.push(cylinder)
          } else {
            val box = new Box(1, 1, 1)
            box.setTranslateX(transition._1)
            box.setTranslateY(transition._2)
            box.setTranslateZ(transition._3)
            val material = new PhongMaterial()
            material.setDiffuseColor(Color.rgb(color._1, color._2, color._3))
            box.setMaterial(material)
            shapeStack.push(box)
          }
      }
    }
    bufferedSource.close
    shapeStack.toList
  }

  private def processLine(line: String): (String, Rgb, Transition, Scale) = {
    val data = line.split("[ (,)]")
    val typeOf = data(0)
    val rgb: Rgb = (data(2).toInt, data(3).toInt, data(4).toInt)
    val transition: Transition = (data(6).toInt, data(7).toInt, data(8).toInt)
    val scale: Scale = (data(9).toFloat, data(10).toFloat, data(11).toFloat)
    return (typeOf, rgb, transition, scale)
  }

  def main(args: Array[String]): Unit = {
    FileReader.createShapesFromFile("Base_Project2Share/src/conf.txt")
  }
}
