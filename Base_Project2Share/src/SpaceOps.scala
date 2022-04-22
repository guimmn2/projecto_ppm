import Types._
import javafx.scene.Node
import javafx.scene.paint.{Color, PhongMaterial}
import javafx.scene.shape.{Box, DrawMode, Shape3D}

object SpaceOps {

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

  def filterModelsWithin(list: List[Node], box: Box): List[Node] = list.filter(m => isWithin(m, box))

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
    val x = placement._1._1; val y = placement._1._2; val z = placement._1._3;
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


  def printModels(list: List[Node]): Unit = list.foreach(m => println(m))
}