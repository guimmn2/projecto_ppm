import InitSubScene.subScene
import javafx.application.Application
import javafx.geometry.Insets
import javafx.scene.paint.PhongMaterial
import javafx.scene.shape._
import javafx.scene.transform.{Rotate, Translate}
import javafx.scene.{Group, Node, Parent, PerspectiveCamera, Scene, SceneAntialiasing, SubScene}
import javafx.stage.Stage
import javafx.geometry.Pos
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import TextIO._
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.fxml.FXMLLoader

import java.io.File

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

    //setup and start the Stage

    val fxmlLoader = new FXMLLoader(getClass.getResource("MenuFile.fxml"))
    val mainViewRoot: Parent = fxmlLoader.load()
    val scene = new Scene(mainViewRoot)
    val myController:MenuFile = fxmlLoader.getController()

    var myList = CLIUtils.getConfigFiles
    myList.foreach(file => myController.listView1.getItems().add(file))

    stage.setTitle("PPM Project 21/22 - Select File Menu")
    stage.setScene(scene)
    stage.centerOnScreen()

    stage.show

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

