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

  def subSections(box: Box): List[Box] = {
    val subSize = box.getWidth / 2
    List[Box](
      createBox(((0.0, 0.0, 0.0), subSize)),
      createBox(((0.0, 0.0, subSize), subSize)),
      createBox(((0.0, subSize, 0.0), subSize)),
      createBox(((0.0, subSize, subSize), subSize)),
      createBox(((subSize, 0.0, 0.0), subSize)),
      createBox(((subSize, 0.0, subSize), subSize)),
      createBox(((subSize, subSize, 0.0), subSize)),
      createBox(((subSize, subSize, subSize), subSize)),
    )
  }

  def isModelInAppropriateBox(model: Node, box: Box): Boolean = {
    if(isWithin(model, box)) (subSections(box) foldRight List[Boolean]()) (intersects(model, _) :: _).filter(b => b == true).length >= 1
    else false
  }

  def filterAppropriateModelsForBox(list: List[Node], box: Box): List[Node] = list.filter(m => isModelInAppropriateBox(m, box) == true)


  def printModels(list: List[Node]): Unit = list.foreach(m => println(m))
}