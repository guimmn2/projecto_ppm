import Types._
import javafx.scene.Node
import javafx.scene.paint.Color

object OctreeOps {

  def generateOcTree(root: Placement, list: List[Node], maxDepth: Int): Octree[Placement] = {
    println(s"root: ${root}")
    if (maxDepth == 0 || !ModelOps.areModelsWithin(list, ModelOps.createBox(root))) OcEmpty
    else {
      val appropriateModels = ModelOps.filterAppropriateModelsForPlacement(list, root)
      appropriateModels match {
        case x :: y => {
          ModelOps.printModels(x :: y ++ ModelOps.getRestOfModelsThatFit(x::y, list, ModelOps.createBox(root)));
          OcLeaf(root, x :: y ++ ModelOps.getRestOfModelsThatFit(x::y, list, ModelOps.createBox(root)))
        }
        case List() => OcNode[Placement](
          root,
          generateOcTree(ModelOps.subPlacements(root)(0), list, maxDepth - 1),
          generateOcTree(ModelOps.subPlacements(root)(1), list, maxDepth - 1),
          generateOcTree(ModelOps.subPlacements(root)(2), list, maxDepth - 1),
          generateOcTree(ModelOps.subPlacements(root)(3), list, maxDepth - 1),
          generateOcTree(ModelOps.subPlacements(root)(4), list, maxDepth - 1),
          generateOcTree(ModelOps.subPlacements(root)(5), list, maxDepth - 1),
          generateOcTree(ModelOps.subPlacements(root)(6), list, maxDepth - 1),
          generateOcTree(ModelOps.subPlacements(root)(7), list, maxDepth - 1)
        )
      }
    }
  }

  def generateDefaultOctree(list: List[Node]): Octree[Placement] = generateOcTree(((0.0,0.0,0.0), 32), list, 6)

  def scaleOctree(fact: Double, oct: Octree[Placement]): Octree[Placement] = {
    def scale3DModels(fact: Double, lst: List[Node]): List[Node] = {
      lst match {
        case List() => List()
        case x :: xs =>
          val newModel = ModelOps.createModelFromNode(x)
          x.setScaleX(x.getScaleX * fact)
          x.setScaleY(x.getScaleY * fact)
          x.setScaleZ(x.getScaleZ * fact)
          newModel :: scale3DModels(fact, xs)
      }
    }
    oct match {
      case OcEmpty => OcEmpty
      case OcLeaf(section: Section) => OcLeaf((section._1._1, section._1._2 * fact), scale3DModels(fact, section._2))
      case OcNode(((x, y, z), size), oc1, oc2, oc3, oc4, oc5, oc6, oc7, oc8) =>
        OcNode(((x, y, z), size * fact), scaleOctree(fact, oc1), scaleOctree(fact, oc2),
          scaleOctree(fact, oc3), scaleOctree(fact, oc4), scaleOctree(fact, oc5), scaleOctree(fact, oc6),
          scaleOctree(fact, oc7), scaleOctree(fact, oc8))
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

  def main(args: Array[String]): Unit = {
    val models = FileReader.createShapesFromFile("./conf.txt")
    val root = ((0.0, 0.0, 0.0), 32.0)
    val maxDepth = 6
    val octree = generateOcTree(root, models, maxDepth)
  }
}