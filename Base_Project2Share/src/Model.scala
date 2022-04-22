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

  def isWithinBox(model: Node, box: Box): Boolean = box.getBoundsInParent.contains(model.asInstanceOf[Shape3D].getBoundsInParent)

  def intersectsBox(model: Node, box: Box): Boolean = model.asInstanceOf[Shape3D].intersects(box.getBoundsInParent)

  def modelsWithinBox(list: List[Node], box: Box): List[Node] = list.filter(m => isWithinBox(m, box))

  def modelsWithinPlacement(models: List[Node], placement: Placement): List[Node] = modelsWithinBox(models, boxFromPlacement(placement))

  def oneIntersectsPlacement(models: List[Node], placement: Placement): Boolean = (models foldRight true) ((cur, next) => !intersectsBox(cur, boxFromPlacement(placement)) && next)

  def isWithinPlacement(model: Node, placement: Placement): Boolean = isWithinBox(model, boxFromPlacement(placement))

  def intersectsPlacement(model: Node, placement: Placement): Boolean = intersectsBox(model, boxFromPlacement(placement))

  def isAppropriatePlacement(model: Node, placement: Placement): Boolean = {
    //calculates next subdivision of placement and verifies if it will intersect it
    //if it does it means it's already in the appropriate node
    val size = placement._2 / 2
    val coords = (placement._1._1, placement._1._2, placement._1._3)
    isWithinPlacement(model, placement) && intersectsPlacement(model, (coords, size))
  }

}