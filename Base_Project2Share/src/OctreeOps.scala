import Types._
import javafx.scene.Node
import javafx.scene.paint.Color

import scala.annotation.tailrec

object OctreeOps {

  def generateOctree(root: Placement, list: List[Node], maxDepth: Int): Octree[Placement] = {
    if (maxDepth == 0 || !ModelOps.areModelsWithin(list, ModelOps.createBox(root))) OcEmpty
    else {
      val appropriateModels = ModelOps.filterAppropriateModelsForPlacement(list, root)
      appropriateModels match {
        case x :: y => {
          OcLeaf(root, x :: y ++ ModelOps.getRestOfModelsThatFit(x::y, list, ModelOps.createBox(root)))
        }
        case List() => OcNode[Placement](
          root,
          generateOctree(ModelOps.subPlacements(root)(0), list, maxDepth - 1),
          generateOctree(ModelOps.subPlacements(root)(1), list, maxDepth - 1),
          generateOctree(ModelOps.subPlacements(root)(2), list, maxDepth - 1),
          generateOctree(ModelOps.subPlacements(root)(3), list, maxDepth - 1),
          generateOctree(ModelOps.subPlacements(root)(4), list, maxDepth - 1),
          generateOctree(ModelOps.subPlacements(root)(5), list, maxDepth - 1),
          generateOctree(ModelOps.subPlacements(root)(6), list, maxDepth - 1),
          generateOctree(ModelOps.subPlacements(root)(7), list, maxDepth - 1)
        )
      }
    }
  }

  //calculates max depth
  def generateOctree(root: Placement, list: List[Node]): Octree[Placement] = generateOctree(root, list, root._2.toInt)
  //default 32
  def generateDefaultOctree(list: List[Node]): Octree[Placement] = generateOctree(((0.0,0.0,0.0), 32), list, 6)

  def scaleOctree(octree: Octree[Placement], factor: Double): Octree[Placement] = {
    val models = getModelListFromOctree(octree, List())
    val scaledModels = ModelOps.scale3dModels(models, factor)
    val root = octree.asInstanceOf[OcNode[Placement]].coords
    val scaledSize = root._2 * factor
    println(s"${(ModelOps.log2(scaledSize) + 1.0).toInt}")
    generateOctree((root._1, scaledSize), scaledModels, (ModelOps.log2(scaledSize) + 1.0).toInt)
  }

  private def getModelListFromOctree(octree: Octree[Placement], list: List[Node]): List[Node] = {
    octree match {
      case OcLeaf(section: Section) => section._2
      case OcEmpty => Nil
      case OcNode(_, up_00, up_01, up_10, up_11, down_00, down_01, down_10, down_11) =>
        getModelListFromOctree(up_00, list) ++
          getModelListFromOctree(up_01, list) ++
          getModelListFromOctree(up_10, list) ++
          getModelListFromOctree(up_11, list) ++
          getModelListFromOctree(down_00, list) ++
          getModelListFromOctree(down_01, list) ++
          getModelListFromOctree(down_10, list) ++
          getModelListFromOctree(down_11, list)
    }
  }

  def mapColourEffect(func: Color => Color)(octree: Octree[Placement]): Octree[Placement] = {
    octree match {
      case OcLeaf(section: Section) => {
        //creates new list of models from existing models in leaf and applies colour effect
        val alteredModels = (section._2 foldRight List[Node]()) ((cur, next) => ModelOps.applyColourEffect(func)(ModelOps.createModelFromNode(cur)) :: next)
        println("mapColourEffect")
        ModelOps.printModels(alteredModels)
        OcLeaf(section._1, alteredModels)
      }
      case OcEmpty => OcEmpty
      case OcNode(root, a, b, c, d, e, f, g, h) => OcNode[Placement](
        root,
        mapColourEffect(func)(a),
        mapColourEffect(func)(b),
        mapColourEffect(func)(c),
        mapColourEffect(func)(d),
        mapColourEffect(func)(e),
        mapColourEffect(func)(f),
        mapColourEffect(func)(g),
        mapColourEffect(func)(h),
      )
    }
  }

  //curried greenRemove for octree
  val greenRemove = mapColourEffect(ModelOps.greenRemove)(_)
  val sepia = mapColourEffect(ModelOps.sepia)(_)

  def main(args: Array[String]): Unit = {
    val octree = generateDefaultOctree(FileReader.createShapesFromFile("./conf2.txt"))
    val models = getModelListFromOctree(octree, List())
    println("models from func: ")
    ModelOps.printModels(models)
    println("scaled models: ")
    ModelOps.printModels(ModelOps.scale3dModels(models, 2.0))
  }
}