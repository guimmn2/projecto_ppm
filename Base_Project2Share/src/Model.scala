import Types.Placement
import javafx.scene.Node
import javafx.scene.paint.{Color, PhongMaterial}
import javafx.scene.shape.{Box, DrawMode, Shape3D}

object Model {

  val redMaterial = new PhongMaterial()
  redMaterial.setDiffuseColor(Color.rgb(150, 0, 150))

  def boxFromPlacement(placement: Placement): Box = {
    val coords = (placement._1)
    val size = placement._2
    val b = new Box(size, size, size)
    b.setTranslateX(coords._1 + size / 2)
    b.setTranslateY(coords._2 + size / 2)
    b.setTranslateZ(coords._3 + size / 2)
    b.setMaterial(redMaterial)
    b.setDrawMode(DrawMode.LINE)
    b
  }

  def isWithin(model1: Node, model2: Node): Boolean = model2.getBoundsInParent.contains(model1.asInstanceOf[Shape3D].getBoundsInParent)

  def intersects(model1: Node, model2: Node): Boolean = {
    //if one is inside the other or vice-versa don't consider as intersecting
    if (isWithin(model1, model2) || isWithin(model2, model1)) false
    else if(model1.asInstanceOf[Shape3D].getBoundsInParent.intersects(model2.getBoundsInParent)) true
    else false
  }

  def modelsWithinBox(list: List[Node], box: Box): List[Node] = list.filter(m => isWithin(m, box))

  def modelsWithinPlacement(models: List[Node], placement: Placement): List[Node] = modelsWithinBox(models, boxFromPlacement(placement))

  def oneIntersectsPlacement(models: List[Node], placement: Placement): Boolean = (models foldRight true) ((cur, next) => !intersects(cur, boxFromPlacement(placement)) && next)

  def isWithinPlacement(model: Node, placement: Placement): Boolean = isWithin(model, boxFromPlacement(placement))

  def intersectsPlacement(model: Node, placement: Placement): Boolean = intersects(model, boxFromPlacement(placement))

  def isAppropriatePlacement(model: Node, placement: Placement): Boolean = {
    //it's appropriate if it fits in this placement and intersects at least 2 of the subsections
    if(isWithinPlacement(model, placement)) {
      val childrenPlacements = childrenNodePlacements(placement)
      val intersections = (childrenPlacements foldRight List[Boolean]()) (intersectsPlacement(model,_) :: _)
      intersections.filter(i => i == true).length >= 2
    } else false
  }

  def listAppropriateModelsForPlacement(list: List[Node], placement: Placement): List[Node] = list.filter(model => isAppropriatePlacement(model, placement))

  def printModels(list: List[Node]): Unit = list.map(m => println(s"${m.getClass}"))

  def childrenNodePlacements(placement: Placement): List[Placement] = {
    val subSize = placement._2 / 2
    List(
      ((0.0, 0.0, 0.0), subSize),
      ((0.0, 0.0, subSize), subSize),
      ((0.0, subSize, 0.0), subSize),
      ((0.0, subSize, subSize), subSize),
      ((subSize, 0.0, 0.0), subSize),
      ((subSize, 0.0, subSize), subSize),
      ((subSize, subSize, 0.0), subSize),
      ((subSize, subSize, subSize), subSize),
    )
  }

}