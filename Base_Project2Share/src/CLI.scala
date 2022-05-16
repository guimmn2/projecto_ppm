import Types.Placement
import javafx.application.Application
import javafx.geometry.{Insets, Pos}
import javafx.scene.layout.StackPane
import javafx.scene.{Group, PerspectiveCamera, Scene, SceneAntialiasing, SubScene}
import javafx.scene.paint.{Color, PhongMaterial}
import javafx.scene.shape.{Box, Cylinder, DrawMode, Line}
import javafx.scene.transform.Rotate
import javafx.stage.Stage

class CLI extends Application {
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

    val whiteMaterial = new PhongMaterial()
    whiteMaterial.setDiffuseColor(Color.rgb(255, 255, 255))

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

    // 3D objects (group of nodes - javafx.scene.Node) that will be provide to the subScene
    val worldRoot:Group = new Group(camVolume, lineX, lineY, lineZ)
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

    //initialize cli and generate octree from inputs
    val octree = CLIUtils.generateOctreeFromUserInput()
    //send octree components to the world
    ModelOps.toDisplayAll(octree.asInstanceOf[Octree[Placement]]).foreach(x => worldRoot.getChildren.add(x))

    //Spaghet
    var oldIntersectedBoxes: List[Box] = Nil
    //Mouse left click interaction
    scene.setOnMouseClicked((event) => {
      if(oldIntersectedBoxes != Nil) oldIntersectedBoxes.foreach(b => worldRoot.getChildren.remove(b))
      camVolume.setTranslateX(camVolume.getTranslateX + 2)
      val intersectedBoxes = ModelOps.getPartitionCoordsInCameraExceptRoot(octree, camVolume)
      intersectedBoxes.foreach(b => {
        b.setMaterial(whiteMaterial)
        worldRoot.getChildren.add(b)
      })
      oldIntersectedBoxes = intersectedBoxes
      worldRoot.getChildren.removeAll()
    })

    stage.show()
  }

  override def init(): Unit = {
    println("init")
  }

  override def stop(): Unit = {
    println("stopped")
  }
}

object CLIApp {

  def main(args: Array[String]): Unit = {
    Application.launch(classOf[CLI], args: _*)
  }
}
