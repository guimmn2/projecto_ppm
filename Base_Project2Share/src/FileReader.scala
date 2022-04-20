import scala.collection.mutable
import scala.io.Source

//classe que alberga métodos de IO
case class FileReader(file: String) {
  def readFromFile(): Unit = FileReader.readFromFile(this.file)
}

object FileReader {

  /** Impure (I/O) - tried to maintain impurity only in this function
   *
   * @param file
   * @return list of Strings with data necessary to create objects
   */
  def readFromFile(file: String): List[String] = {
    val bufferedSource = Source.fromFile(file)
    val dataList = new mutable.Stack[String]

    for (line <- bufferedSource.getLines){
      println(line.toUpperCase);
      dataList.push(line.toUpperCase())
    }

    bufferedSource.close
    return dataList.toArray.toList
  }

  def main(args: Array[String]): Unit = {
    FileReader.readFromFile("Base_Project2Share/src/conf.txt")
  }
}
