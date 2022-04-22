import Types._
import javafx.scene.Node

case class OctreeOps(octree: Octree[Placement])

object OctreeOps {

  /*
  def generateOctree(root: Placement, list: List[Node]): Octree[Placement] = {
    /*
    in list of objects filter the ones that are appropriate for this node,
    that is the ones that fit in this and don't intersect any of the next
     */
  }
   */

  def main(args: Array[String]): Unit = {
    val models = FileReader.createShapesFromFile("Base_Project2Share/src/conf.txt")
    //generateOctree(((0.0, 0.0, 0.0), 32), models)
  }
}