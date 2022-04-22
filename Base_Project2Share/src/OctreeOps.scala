import Types._
import javafx.scene.Node

case class OctreeOps(octree: Octree[Placement])

object OctreeOps {

  def generateOctree(root: Placement, list: List[Node], maxDepth: Int): Octree[Placement] = {
    println(s"at root: ${root}")
    val modelsWithinRoot = Model.modelsWithinPlacement(list, root)
    if(maxDepth == 0 || modelsWithinRoot.equals(List())) return OcEmpty
    val appropriateModelsForRoot = Model.listAppropriateModelsForPlacement(list, root)
    appropriateModelsForRoot match {
      case x :: y => {
        println(root)
        Model.printModels(x::y)
        OcLeaf(root, x::y)
      }
      case List() => OcNode[Placement](
        root,
        generateOctree(Model.childrenNodePlacements(root)(0), list, maxDepth - 1),
        generateOctree(Model.childrenNodePlacements(root)(1), list, maxDepth - 1),
        generateOctree(Model.childrenNodePlacements(root)(2), list, maxDepth - 1),
        generateOctree(Model.childrenNodePlacements(root)(3), list, maxDepth - 1),
        generateOctree(Model.childrenNodePlacements(root)(4), list, maxDepth - 1),
        generateOctree(Model.childrenNodePlacements(root)(5), list, maxDepth - 1),
        generateOctree(Model.childrenNodePlacements(root)(6), list, maxDepth - 1),
        generateOctree(Model.childrenNodePlacements(root)(7), list, maxDepth - 1)
      )
    }
  }

  def main(args: Array[String]): Unit = {
    val models = FileReader.createShapesFromFile("Base_Project2Share/src/conf.txt")
    generateOctree(((0.0, 0.0, 0.0), 32), models, 6)
  }
}