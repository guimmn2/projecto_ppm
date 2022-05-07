import Types._
import javafx.scene.Node
import javafx.scene.paint.{Color, PhongMaterial}
import javafx.scene.shape.{Box, Cylinder, DrawMode, Shape3D}

object ModelOps {

  val purpleMaterial = new PhongMaterial()
  purpleMaterial.setDiffuseColor(Color.rgb(150, 0, 150))

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

  def getRestOfModelsThatFit(referenceModels: List[Node], allModels: List[Node], box: Box): List[Node] = {
    allModels.filter(model => !referenceModels.contains(model) && isWithin(model, box))
  }

  def filterModelsWithin(models: List[Node], box: Box) = models.filter(m => isWithin(m, box))

  def areModelsWithin(list: List[Node], box: Box): Boolean = !filterModelsWithin(list, box).equals(List())

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

  def isModelInAppropriatePlacement(model: Node, placement: Placement): Boolean = {
    if (isWithin(model, createBox(placement))) (subSections(placement) foldRight List[Boolean]()) (intersects(model, _) :: _).filter(b => b == true).length >= 1
    else false
  }

  def filterAppropriateModelsForPlacement(list: List[Node], placement: Placement): List[Node] = list.filter(m => isModelInAppropriatePlacement(m, placement) == true)

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

  //function that returns a coloured node according to f
  def applyColourEffect(f: Color => Color)(model: Node): Node = {
    val newModel = createModelFromNode(model).asInstanceOf[Shape3D]
    val color: Color = newModel.getMaterial.asInstanceOf[PhongMaterial].getDiffuseColor
    val material = new PhongMaterial()
    material.setDiffuseColor(f(color))
    newModel.setMaterial(material)
    newModel
  }

  private def getColor(node: Node): Color = node.asInstanceOf[Shape3D].getMaterial.asInstanceOf[PhongMaterial].getDiffuseColor

  //color funcs
  val curryGreenRemove = applyColourEffect(greenRemove)(_)
  def greenRemove(c: Color): Color = Color.rgb((c.getRed * 255).toInt, 0, (c.getBlue * 255).toInt)

  //we did this because we were having trouble with pointers. Wasn't creating a new value equal to the Nodes we wanted ...
  def createModelFromNode(node: Node): Node = {
    if(node.isInstanceOf[Cylinder]) {
      val data = node.asInstanceOf[Cylinder]
      val coords = (data.getTranslateX, data.getTranslateY, data.getTranslateZ)
      val scale = (data.getScaleX, data.getScaleY, data.getScaleZ)
      val dimensions = (data.getRadius, data.getHeight, data.getDivisions)
      val material = data.getMaterial
      val cylinder = new Cylinder(dimensions._1, dimensions._2, dimensions._3)
      cylinder.setTranslateX(coords._1)
      cylinder.setTranslateY(coords._1)
      cylinder.setTranslateZ(coords._1)
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
      box.setTranslateY(coords._1)
      box.setTranslateZ(coords._1)
      box.setScaleX(scale._1)
      box.setScaleY(scale._2)
      box.setScaleZ(scale._3)
      box.setMaterial(material)
      box
    }
  }

  def printModels(list: List[Node]): Unit = list.foreach(m => println(s"class: ${m.getClass}" +
    s" color: { red: ${getColor(m).getRed * 255}, green: ${getColor(m).getGreen * 255},  blue: ${getColor(m).getBlue * 255} }"))

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

  def toDisplayAll(oct:Octree[Placement]):List[Node] = {
    toDisplayModels(oct,List()) ++ generateBoundingBoxes(oct,List())
  }

}