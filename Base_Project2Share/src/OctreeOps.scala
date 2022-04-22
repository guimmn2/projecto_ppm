import Types._
import javafx.scene.Node

case class OctreeOps(octree: Octree[Placement])

object OctreeOps {

  def generateOctree(root: Placement, list: List[Node]): Octree[Placement] = {
    /*
    verify if in this node there is one or more objects we may add to this node
    if so it becomes a leaf
    else we subdividide it and ask the same of every childNode
     */
    val modelsWithinPlacement = Model.modelsWithinPlacement(list, root)
    println(s"models within root at ${root._1} with size ${root._2}: " + modelsWithinPlacement)
    if(modelsWithinPlacement.equals(List())) return OcEmpty

    val appropriateModels = list.filter(model => Model.isAppropriatePlacement(model, root))
    println("appropriate models: " + appropriateModels)

    appropriateModels match {
      case models => OcLeaf(root, models)
      case List() =>
        val coords = childrenNodePlacements(root)
        OcNode(
          coords(0),
          generateOctree(coords(0), list),
          generateOctree(coords(1), list),
          generateOctree(coords(2), list),
          generateOctree(coords(3), list),
          generateOctree(coords(4), list),
          generateOctree(coords(5), list),
          generateOctree(coords(6), list),
          generateOctree(coords(7), list)
        )
    }
  }

  private def childrenNodePlacements(placement: Placement): List[Placement] = {
    val size = placement._2 / 2

    val x = placement._1._1 / 2
    val y = placement._1._2 / 2
    val z = placement._1._3 / 2

    List(
      ((0.0, 0.0, 0.0), size),
      ((0.0, 0.0, z), size),
      ((0.0, y, 0.0), size),
      ((0.0, y, z), size),
      ((x, 0.0, 0.0), size),
      ((x, 0.0, z), size),
      ((x, y, 0.0), size),
      ((x, y, z), size),
    )
  }

  def main(args: Array[String]): Unit = {
    val models = FileReader.createShapesFromFile("Base_Project2Share/src/conf.txt")
    generateOctree(((0.0, 0.0, 0.0), 32), models)
  }
}