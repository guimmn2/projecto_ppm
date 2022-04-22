import javafx.application.Application
import javafx.geometry.Insets
import javafx.scene.paint.PhongMaterial
import javafx.scene.shape._
import javafx.scene.transform.{Rotate, Translate}
import javafx.scene.{Group, Node}
import javafx.stage.Stage
import javafx.geometry.Pos
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.{PerspectiveCamera, Scene, SceneAntialiasing, SubScene}

class Main extends Application {

  //Auxiliary types
  type Point = (Double, Double, Double)
  type Size = Double
  type Placement = (Point, Size) //1st point: origin, 2nd point: size

  //Shape3D is an abstract class that extends javafx.scene.Node
  //Box and Cylinder are subclasses of Shape3D
  type Section = (Placement, List[Node])  //example: ( ((0.0,0.0,0.0), 2.0), List(new Cylinder(0.5, 1, 10)))


  /*
    Additional information about JavaFX basic concepts (e.g. Stage, Scene) will be provided in week7
   */
  override def start(stage: Stage): Unit = {

    //Get and print program arguments (args: Array[String])
    val params = getParameters
    println("Program arguments:" + params.getRaw)

    //Materials to be applied to the 3D objects
    val redMaterial = new PhongMaterial()
    redMaterial.setDiffuseColor(Color.rgb(150,0,0))

    val greenMaterial = new PhongMaterial()
    greenMaterial.setDiffuseColor(Color.rgb(0,255,0))

    val blueMaterial = new PhongMaterial()
    blueMaterial.setDiffuseColor(Color.rgb(0,0,150))

    //3D objects
    val lineX = new Line(0, 0, 200, 0)
    lineX.setStroke(Color.GREEN)

    val lineY = new Line(0, 0, 0, 200)
    lineY.setStroke(Color.YELLOW)

    val lineZ = new Line(0, 0, 200, 0)
    lineZ.setStroke(Color.LIGHTSALMON)
    lineZ.getTransforms().add(new Rotate(-90, 0, 0, 0, Rotate.Y_AXIS))

    val camVolume = new Cylinder(10, 50, 10)
    camVolume.setTranslateX(1)
    camVolume.getTransforms().add(new Rotate(45, 0, 0, 0, Rotate.X_AXIS))
    camVolume.setMaterial(blueMaterial)
    camVolume.setDrawMode(DrawMode.LINE)

    val wiredBox = new Box(32, 32, 32)
    wiredBox.setTranslateX(16)
    wiredBox.setTranslateY(16)
    wiredBox.setTranslateZ(16)
    wiredBox.setMaterial(redMaterial)
    wiredBox.setDrawMode(DrawMode.LINE)

    val cylinder1 = new Cylinder(0.5, 1, 10)
    cylinder1.setTranslateX(2)
    cylinder1.setTranslateY(2)
    cylinder1.setTranslateZ(2)
    cylinder1.setScaleX(2)
    cylinder1.setScaleY(2)
    cylinder1.setScaleZ(2)
    cylinder1.setMaterial(greenMaterial)

    val box1 = new Box(1, 1, 1)  //
    box1.setTranslateX(5)
    box1.setTranslateY(5)
    box1.setTranslateZ(5)
    box1.setMaterial(greenMaterial)

    // 3D objects (group of nodes - javafx.scene.Node) that will be provide to the subScene
    val worldRoot:Group = new Group(wiredBox, camVolume, lineX, lineY, lineZ, cylinder1, box1)

    //loads objects into world
    FileReader.createShapesFromFile("Base_Project2Share/src/conf.txt").map(x => worldRoot.getChildren.add(x))

    // Camera
    val camera = new PerspectiveCamera(true)

    val cameraTransform = new CameraTransformer
    cameraTransform.setTranslate(0, 0, 0)
    cameraTransform.getChildren.add(camera)
    camera.setNearClip(0.1)
    camera.setFarClip(10000.0)

    camera.setTranslateZ(-500)
    camera.setFieldOfView(20)
    cameraTransform.ry.setAngle(-45.0)
    cameraTransform.rx.setAngle(-45.0)
    worldRoot.getChildren.add(cameraTransform)

    // SubScene - composed by the nodes present in the worldRoot
    val subScene = new SubScene(worldRoot, 800, 600, true, SceneAntialiasing.BALANCED)
    subScene.setFill(Color.DARKSLATEGRAY)
    subScene.setCamera(camera)

    // CameraView - an additional perspective of the environment
    val cameraView = new CameraView(subScene)
    cameraView.setFirstPersonNavigationEabled(true)
    cameraView.setFitWidth(350)
    cameraView.setFitHeight(225)
    cameraView.getRx.setAngle(-45)
    cameraView.getT.setZ(-100)
    cameraView.getT.setY(-500)
    cameraView.getCamera.setTranslateZ(-50)
    cameraView.startViewing

      // Position of the CameraView: Right-bottom corner
      StackPane.setAlignment(cameraView, Pos.BOTTOM_RIGHT)
      StackPane.setMargin(cameraView, new Insets(5))

    // Scene - defines what is rendered (in this case the subScene and the cameraView)
    val root = new StackPane(subScene, cameraView)
    subScene.widthProperty.bind(root.widthProperty)
    subScene.heightProperty.bind(root.heightProperty)

    val scene = new Scene(root, 810, 610, true, SceneAntialiasing.BALANCED)

    //Mouse left click interaction
    scene.setOnMouseClicked((event) => {
      camVolume.setTranslateX(camVolume.getTranslateX + 2)
      worldRoot.getChildren.removeAll()
    })

    //setup and start the Stage
    stage.setTitle("PPM Project 21/22")
    stage.setScene(scene)
    stage.show


    //oct1 - example of an Octree[Placement] that contains only one Node (i.e. cylinder1)
    //In case of difficulties to implement task T2 this octree can be used as input for tasks T3, T4 and T5

    val placement1: Placement = ((0, 0, 0), 8.0)
    val sec1: Section = (((0.0,0.0,0.0), 4.0), List(cylinder1.asInstanceOf[Node]))
    val sec2: Section = (((0.0,0.0,0.0), 8.0), List(box1.asInstanceOf[Node]))
    val ocLeaf1 = OcLeaf(sec1)
    val ocLeaf2 = OcLeaf(sec2)
    val oct1:Octree[Placement] = OcNode[Placement](placement1, ocLeaf1, ocLeaf2, OcEmpty, OcEmpty, OcEmpty, OcEmpty, OcEmpty, OcEmpty)
    val oct2:Octree[Placement] = oct1

    //example of bounding boxes (corresponding to the octree oct1) added manually to the world
    val b2 = new Box(8,8,8)
    //translate because it is added by defaut to the coords (0,0,0)
    b2.setTranslateX(8/2)
    b2.setTranslateY(8/2)
    b2.setTranslateZ(8/2)
    b2.setMaterial(redMaterial)
    b2.setDrawMode(DrawMode.LINE)

    val placement1Box = Model.boxFromPlacement(placement1)

    val b3 = new Box(4,4,4)
    //translate because it is added by defaut to the coords (0,0,0)
    b3.setTranslateX(4/2)
    b3.setTranslateY(4/2)
    b3.setTranslateZ(4/2)
    b3.setMaterial(greenMaterial)
    b3.setDrawMode(DrawMode.LINE)

    val b3Adj = new Box(4, 4, 4)
    b3Adj.setTranslateX(4 + 2)
    b3Adj.setTranslateY(2)
    b3Adj.setTranslateZ(2)
    b3Adj.setMaterial(blueMaterial)
    b3Adj.setDrawMode(DrawMode.LINE)

    //adding boxes b2 and b3 to the world
    //worldRoot.getChildren.add(b2)
    worldRoot.getChildren.add(b3)
    worldRoot.getChildren.add(b3Adj)
    worldRoot.getChildren.add(placement1Box)

    val bigBox = new Box(30, 30, 30)
    bigBox.setTranslateX(15)
    bigBox.setTranslateY(15)
    bigBox.setTranslateZ(15)
    bigBox.setMaterial(greenMaterial)
    bigBox.setDrawMode(DrawMode.LINE)

    //Tests and stuff
    println(s"cylinder1 intersecting sec1 box ? ${Model.intersectsPlacement(cylinder1, sec1._1)}")
    println(s"cylinder1 intersecting adjacent box? ${Model.intersects(cylinder1, b3Adj)}")
    println(s"adjacent box intersecting cylinder1? ${Model.intersects(b3Adj, cylinder1)}")
    println(s"adjacent box within cylinder1? ${Model.isWithin(b3Adj, cylinder1)}")
    println(s"cylinder1 intersecting first subsection? ${Model.intersectsPlacement(cylinder1, Model.childrenNodePlacements(sec1._1)(0))}")
    println(s"cylinder1 intersecting last subsection? ${Model.intersectsPlacement(cylinder1, Model.childrenNodePlacements(sec1._1)(7))}")
    println(s"cylinder1 inside first subsection? ${Model.isWithinPlacement(cylinder1, Model.childrenNodePlacements(sec1._1)(0))}")
    println(s"last subsection intersecting cylinder1? ${Model.intersects(Model.boxFromPlacement(Model.childrenNodePlacements(sec1._1)(7)), cylinder1)}")
    println(s"first subsection intersecting cylinder1? ${Model.intersects(Model.boxFromPlacement(Model.childrenNodePlacements(sec1._1)(0)), cylinder1)}")
    println(s"bigBox inside root? ${Model.isWithin(bigBox, Model.boxFromPlacement((0.0,0.0,0.0),32))}")
    println(s"bigBox intersecting last root subsection? ${Model.intersects(bigBox, Model.boxFromPlacement(Model.childrenNodePlacements((0.0, 0.0, 0.0), 32)(7)))}")

    worldRoot.getChildren.add(Model.boxFromPlacement(Model.childrenNodePlacements(sec1._1)(0)))
    worldRoot.getChildren.add(Model.boxFromPlacement(Model.childrenNodePlacements(sec1._1)(7)))
    worldRoot.getChildren.add(bigBox)

    //childrenPlacement working? YES
    Model.childrenNodePlacements(((0.0, 0.0, 0.0), 32)).map((p => {
      println((s"${p._1}"))
      worldRoot.getChildren.add(Model.boxFromPlacement(p))
    }))

  }

  override def init(): Unit = {
    println("init")
  }

  override def stop(): Unit = {
    println("stopped")
  }

}

object FxApp {

  def main(args: Array[String]): Unit = {
    Application.launch(classOf[Main], args: _*)
  }
}

