import Types._
import javafx.scene.Node
import javafx.scene.shape.Cylinder

case class OctreeOps(octree: Octree[Placement])

object OctreeOps {

  def generateOcTree(root: Placement, list: List[Node], maxDepth: Int): Octree[Placement] = {
    println(s"root: ${root}")
    if(maxDepth == 0 || !SpaceOps.areModelsWithin(list, SpaceOps.createBox(root))) OcEmpty
    else {
      val appropriateModels = SpaceOps.filterAppropriateModelsForPlacement(list, root)
      appropriateModels match {
        case x::y => {
          SpaceOps.printModels(x::y);
          OcLeaf(root, x::y)
        }
        case List() => OcNode[Placement](
          root,
          generateOcTree(SpaceOps.subPlacements(root)(0), list, maxDepth - 1),
          generateOcTree(SpaceOps.subPlacements(root)(1), list, maxDepth - 1),
          generateOcTree(SpaceOps.subPlacements(root)(2), list, maxDepth - 1),
          generateOcTree(SpaceOps.subPlacements(root)(3), list, maxDepth - 1),
          generateOcTree(SpaceOps.subPlacements(root)(4), list, maxDepth - 1),
          generateOcTree(SpaceOps.subPlacements(root)(5), list, maxDepth - 1),
          generateOcTree(SpaceOps.subPlacements(root)(6), list, maxDepth - 1),
          generateOcTree(SpaceOps.subPlacements(root)(7), list, maxDepth - 1)
        )
      }
    }
  }

  def main(args: Array[String]): Unit = {

    val cylinder1 = new Cylinder(0.5, 1, 10)
    cylinder1.setTranslateX(6)
    cylinder1.setTranslateY(2)
    cylinder1.setTranslateZ(2)
    cylinder1.setScaleX(2)
    cylinder1.setScaleY(2)
    cylinder1.setScaleZ(2)

    generateOcTree(((0.0, 0.0, 0.0), 32), List(cylinder1), 6)
  }
}