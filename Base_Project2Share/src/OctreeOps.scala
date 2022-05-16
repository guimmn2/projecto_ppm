import Types._
import javafx.scene.Node
import javafx.scene.paint.Color

import scala.annotation.tailrec

object OctreeOps {

  /**
   * creates an octree. if the objects do not fit within the octree, they are discarded.
   * @param root
   * @param list
   * @param maxDepth
   * @return octree
   */
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

  /**
   * applies function above, but calculates appropriate max depth
   * @param root
   * @param list
   * @return octree
   */
  def generateOctree(root: Placement, list: List[Node]): Octree[Placement] = generateOctree(root, list, root._2.toInt)

  /**
   * generates an octree with the default size of 32
   * @param list
   * @return octree
   */
  def generateDefaultOctree(list: List[Node]): Octree[Placement] = generateOctree(((0.0,0.0,0.0), 32), list, 6)

  /**
   * gathers all the models of an octree, scaling them to the given factor
   * gets the octree root size and multiplies it by the given factor;
   * generates new octree based on this data.
   * @param octree
   * @param factor
   * @return octree
   */
  def scaleOctree(octree: Octree[Placement], factor: Double): Octree[Placement] = {
    val models = getModelListFromOctree(octree, List())
    val scaledModels = ModelOps.scale3dModels(models, factor)
    val root = octree.asInstanceOf[OcNode[Placement]].coords
    val scaledSize = root._2 * factor
    generateOctree((root._1, scaledSize), scaledModels, (ModelOps.log2(scaledSize) + 1.0).toInt)
  }

  /**
   * traverses the octree collecting all models in a list
   * @param octree
   * @param list
   * @return list of models
   */
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

  /**
   * traverses the octree creating new models of a leaf and applying the given colour effect function
   * @param func
   * @param octree
   * @return
   */
  def mapColourEffect(func: Color => Color)(octree: Octree[Placement]): Octree[Placement] = {
    octree match {
      case OcLeaf(section: Section) => {
        val alteredModels = (section._2 foldRight List[Node]()) ((cur, next) => ModelOps.applyColourEffect(func)(ModelOps.createModelFromNode(cur)) :: next)
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