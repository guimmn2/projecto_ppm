import scala.collection.mutable
import scala.io.Source
import Types._

object FileReader {

  /** Impure (I/O) - tried to maintain impurity only in this object
   *
   * @param file
   * @return list of Strings with data necessary to create objects
   */
  def createGeoidsFromFile(file: String): List[Geoid] = {
    val bufferedSource = Source.fromFile(file)
    val geoidStack = new mutable.Stack[Geoid]

    for (line <- bufferedSource.getLines) {
      processLine(line) match {
        case (typeOf, color, transition, scale) =>
          if (typeOf.equals("Cylinder")) geoidStack.push(Cylinder(0.5f, 1, 10, color, transition, scale))
          else geoidStack.push(Box(1, 1, 1, color, transition, scale))
      }
    }
    bufferedSource.close
    println(geoidStack.toList(0))
    geoidStack.toList
  }

  private def processLine(line: String): (String, Color, Transition, Scale) = {
    val data = line.split("[ (,)]")
    val typeOf = data(0)
    val color: Color = (data(2).toInt, data(3).toInt, data(4).toInt)
    val transition: Transition = (data(6).toInt, data(7).toInt, data(8).toInt)
    val scale: Scale = (data(9).toFloat, data(10).toFloat, data(11).toFloat)
    return (typeOf, color, transition, scale)
  }

  def main(args: Array[String]): Unit = {
    FileReader.createGeoidsFromFile("Base_Project2Share/src/conf.txt")
  }
}
