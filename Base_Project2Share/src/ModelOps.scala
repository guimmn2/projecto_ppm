import Types._
import javafx.scene.Node
import javafx.scene.paint.{Color, PhongMaterial}
import javafx.scene.shape.{Box, Cylinder, DrawMode, Shape3D}
import javafx.scene.transform.Rotate

object ModelOps {

  val purpleMaterial = new PhongMaterial()
  purpleMaterial.setDiffuseColor(Color.rgb(150, 0, 150))

  /**
   * creates a box given a placement
   * @param placement
   * @return Box
   */
  def createBox(placement: Placement): Box = {
    val size = placement._2
    val x = placement._1._1;
    val y = placement._1._2;
    val z = placement._1._3
    val box = new Box(size, size, size)
    box.setTranslateX(x + size / 2)
    box.setTranslateY(y + size / 2)
    box.setTranslateZ(z + size / 2)
    box.setMaterial(purpleMaterial)
    box.setDrawMode(DrawMode.LINE)
    box
  }

  def isWithin(model: Node, box: Box): Boolean = box.getBoundsInParent.contains(model.asInstanceOf[Shape3D].getBoundsInParent)

  def intersects(model: Node, box: Box): Boolean = !isWithin(model, box) && model.asInstanceOf[Shape3D].getBoundsInParent.intersects(box.getBoundsInParent)

  /**
   * disjoints list params and returns those that are within given box
   * @param referenceModels - already within
   * @param allModels - checking if are within
   * @param box - space to consider
   * @return list of models that fit inside generated partition, that are not yet inside
   */
  def getRestOfModelsThatFit(referenceModels: List[Node], allModels: List[Node], box: Box): List[Node] = {
    allModels.filter(model => !referenceModels.contains(model) && isWithin(model, box))
  }

  /**
   * @param models
   * @param box
   * @return models that are within box
   */
  def filterModelsWithin(models: List[Node], box: Box) = models.filter(m => isWithin(m, box))

  /**
   * @param list
   * @param box
   * @return true if exist models that are within
   */
  def areModelsWithin(list: List[Node], box: Box): Boolean = !filterModelsWithin(list, box).equals(List())

  /**
   * generates boxes that represent the octree sections
   * @param placement
   * @return
   */
  def subSections(placement: Placement): List[Box] = {
    val sub = subPlacements(placement)
    List[Box](
      createBox(sub(0)),
      createBox(sub(1)),
      createBox(sub(2)),
      createBox(sub(3)),
      createBox(sub(4)),
      createBox(sub(5)),
      createBox(sub(6)),
      createBox(sub(7)),
    )
  }

  /**
   * auxiliary function for subSections
   * @param placement
   * @return
   */
  def subPlacements(placement: Placement): List[Placement] = {
    val subSize = placement._2 / 2
    val x = placement._1._1;
    val y = placement._1._2;
    val z = placement._1._3;
    List(
      ((x, y, z), subSize),
      ((x + subSize, y, z), subSize),
      ((x, y + subSize, z), subSize),
      ((x, y, z + subSize), subSize),
      ((x + subSize, y + subSize, z), subSize),
      ((x, y + subSize, z + subSize), subSize),
      ((x + subSize, y, z + subSize), subSize),
      ((x + subSize, y + subSize, z + subSize), subSize),
    )
  }

  /**
   * verifies if model is in the perfect section of octree, that is if wouldn't fit
   * in the next subdivision or if it would intersect one or more
   * @param model
   * @param placement
   * @return true if appropriate, false if no
   */
  def isModelInAppropriatePlacement(model: Node, placement: Placement): Boolean = {
    if (isWithin(model, createBox(placement))) (subSections(placement) foldRight List[Boolean]()) (intersects(model, _) :: _).filter(b => b == true).length >= 1
    else false
  }

  /**
   * applies function above to return the list of appropriate models given a placement
   * @param list
   * @param placement
   * @return list of models that are perfect for the placement
   */
  def filterAppropriateModelsForPlacement(list: List[Node], placement: Placement): List[Node] = list.filter(m => isModelInAppropriatePlacement(m, placement) == true)

  /**
   * @param octree
   * @param cylinder
   * @return returns list of boxes that the camera intersects
   */
  def getPartitionCoordsInCamera(octree: Octree[Placement], cylinder: Cylinder): List[Box] = {
    octree match {
      case OcLeaf(section: Section) => if (intersects(cylinder, createBox(section._1))) List(createBox(section._1)) else Nil
      case OcEmpty => Nil
      case OcNode(coords, up_00, up_01, up_10, up_11, down_00, down_01, down_10, down_11) =>
        if (intersects(cylinder, createBox(coords))) {
          createBox(coords) ::
            getPartitionCoordsInCamera(up_00, cylinder) ++
              getPartitionCoordsInCamera(up_01, cylinder) ++
              getPartitionCoordsInCamera(up_10, cylinder) ++
              getPartitionCoordsInCamera(up_11, cylinder) ++
              getPartitionCoordsInCamera(down_00, cylinder) ++
              getPartitionCoordsInCamera(down_01, cylinder) ++
              getPartitionCoordsInCamera(down_10, cylinder) ++
              getPartitionCoordsInCamera(down_11, cylinder)
        } else {
          Nil
        }
    }
  }

  /**
   * @param octree
   * @param cylinder
   * @return the same as above but removes root, so it doesn't appear colored
   */
  def getPartitionCoordsInCameraExceptRoot(octree: Octree[Placement], cylinder: Cylinder): List[Box] = {
    getPartitionCoordsInCamera(octree, cylinder) match {
      case _ :: xy => xy
      case Nil => Nil
    }
  }

  /**
   * traverses the octree creating boxes for each node / leaf
   * @param octree
   * @param list
   * @return list of boxes that represent every node, child node and leaf
   */
  def generateBoundingBoxes(octree: Octree[Placement], list: List[Box]): List[Box] = {
    octree match {
      case OcEmpty => Nil
      case OcLeaf((plc: Placement, _)) => createBox(plc) :: list
      case OcNode(root, a, b, c, d, e, f, g, h) =>
        createBox(root) ::
          generateBoundingBoxes(a, list) ++
            generateBoundingBoxes(b, list) ++
            generateBoundingBoxes(c, list) ++
            generateBoundingBoxes(d, list) ++
            generateBoundingBoxes(e, list) ++
            generateBoundingBoxes(f, list) ++
            generateBoundingBoxes(g, list) ++
            generateBoundingBoxes(h, list)
    }
  }

  /**
   * applies a colour effect function to a given model
   * @param f
   * @param model
   * @return new model with the applied colour effect
   */
  def applyColourEffect(f: Color => Color)(model: Node): Node = {
    val newModel = createModelFromNode(model).asInstanceOf[Shape3D]
    val color: Color = newModel.getMaterial.asInstanceOf[PhongMaterial].getDiffuseColor
    val material = new PhongMaterial()
    material.setDiffuseColor(f(color))
    newModel.setMaterial(material)
    newModel
  }

  /**
   * @param node
   * @return color of a node
   */
  private def getColor(node: Node): Color = node.asInstanceOf[Shape3D].getMaterial.asInstanceOf[PhongMaterial].getDiffuseColor

  //curried colour funcs
  val curryGreenRemove = applyColourEffect(greenRemove)(_)
  val currySepia = applyColourEffect(sepia)(_)

  def greenRemove(c: Color): Color = Color.rgb((c.getRed * 255).toInt, 0, (c.getBlue * 255).toInt)

  def sepia(c: Color): Color = {
    Color.rgb(
      (c.getRed * 255 * 0.4 + c.getGreen * 255 * 0.77 + c.getBlue * 255 * 0.2).toInt,
      (c.getRed * 255 * 0.35 + c.getGreen * 255 * 0.69 + c.getBlue * 255 * 0.17).toInt,
      (c.getRed * 255 * 0.27 + c.getGreen * 255 * 0.53 + c.getBlue * 255 * 0.13).toInt
    )
  }

  //we did this because we were having trouble with pointers. Wasn't creating a new value equal to the Nodes we wanted ...

  /**
   * @param node
   * @return copy of node, be it cylinder or box
   */
  def createModelFromNode(node: Node): Node = {
    if (node.isInstanceOf[Cylinder]) {
      val data = node.asInstanceOf[Cylinder]
      val coords = (data.getTranslateX, data.getTranslateY, data.getTranslateZ)
      val scale = (data.getScaleX, data.getScaleY, data.getScaleZ)
      val dimensions = (data.getRadius, data.getHeight, data.getDivisions)
      val material = data.getMaterial
      val cylinder = new Cylinder(dimensions._1, dimensions._2, dimensions._3)
      cylinder.setTranslateX(coords._1)
      cylinder.setTranslateY(coords._2)
      cylinder.setTranslateZ(coords._3)
      cylinder.setScaleX(scale._1)
      cylinder.setScaleY(scale._2)
      cylinder.setScaleZ(scale._3)
      cylinder.setMaterial(material)
      cylinder
    } else {
      val data = node.asInstanceOf[Box]
      val coords = (data.getTranslateX, data.getTranslateY, data.getTranslateZ)
      val scale = (data.getScaleX, data.getScaleY, data.getScaleZ)
      val dimensions = (data.getHeight, data.getWidth, data.getDepth)
      val material = data.getMaterial
      val box = new Box(dimensions._1, dimensions._2, dimensions._3)
      box.setTranslateX(coords._1)
      box.setTranslateY(coords._2)
      box.setTranslateZ(coords._3)
      box.setScaleX(scale._1)
      box.setScaleY(scale._2)
      box.setScaleZ(scale._3)
      box.setMaterial(material)
      box
    }
  }

  def log2(x: Double) = Math.log10(x)/Math.log10(2.0)

  def printModels(list: List[Node]): Unit = list.foreach(m => println(s"class: ${m.getClass}" +
    s" color: { red: ${getColor(m).getRed * 255}, green: ${getColor(m).getGreen * 255},  blue: ${getColor(m).getBlue * 255} }"))

  /**
   * traverses octree collecting in a list every model
   * @param oct
   * @param lst
   * @return list of models of a given octree
   */
  def toDisplayModels(oct:Octree[Placement],lst:List[Node]):List[Node] = {
    oct match {
      case OcEmpty => List()
      case OcLeaf(section:Section) => section._2 ++ lst
      case OcNode(((x, y, z), size), oc1, oc2, oc3, oc4, oc5, oc6, oc7, oc8) =>
        toDisplayModels(oc1,lst) ++
          toDisplayModels(oc2,lst) ++
          toDisplayModels(oc3,lst) ++
          toDisplayModels(oc4,lst) ++
          toDisplayModels(oc5,lst) ++
          toDisplayModels(oc6,lst) ++
          toDisplayModels(oc7,lst) ++
          toDisplayModels(oc8,lst)
    }
  }

  /**
   * concatenates bounding boxes of octree with models of octree
   * @param oct
   * @return returns models and bounding boxes of octree
   */
  def toDisplayAll(oct:Octree[Placement]): List[Node] = {
    toDisplayModels(oct,List()) ++ generateBoundingBoxes(oct,List())
  }

  /**
   * @param list
   * @param factor
   * @return scaled models
   */
  def scale3dModels(list: List[Node], factor: Double) = {
    list.map(m => {
      val newModel = createModelFromNode(m)
      newModel.setScaleX(m.getScaleX * factor)
      newModel.setScaleY(m.getScaleY * factor)
      newModel.setScaleZ(m.getScaleZ * factor)
      newModel.setTranslateX(m.getTranslateX * factor)
      newModel.setTranslateY(m.getTranslateY * factor)
      newModel.setTranslateZ(m.getTranslateZ * factor)
      newModel
    })
  }

  def main(args: Array[String]) = {
  }
}