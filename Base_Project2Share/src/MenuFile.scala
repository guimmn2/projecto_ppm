import InitSubScene.subScene
import OctreeOps.getModelListFromOctree
import Types.Placement
import javafx.application.Application
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.fxml.{FXML, FXMLLoader}
import javafx.scene.control.{Button, Label, ListView, Slider, Tab, TabPane, TextField}
import javafx.scene._
import javafx.scene.layout.{AnchorPane, HBox, VBox}
import javafx.stage._
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.shape.Box
import javafx.stage.Stage
import javafx.scene.control.ButtonBase
import javafx.event.EventHandler
import java.io.File

class MenuFile {

  @FXML
  private var button1: Button = _

  var currentFile: File = _
  @FXML
  var currentOctree: Octree[Placement] = _
  var octreeBoxes: List[Box] = _

  //  @FXML
  //  def handleButtonPress(event: ActionEvent): Octree[Placement] = {
  //    button1.setDisable(true)
  //    val secondStage: Stage = new Stage()
  //    secondStage.setMaximized(true)
  //    secondStage.initModality(Modality.APPLICATION_MODAL)
  //    secondStage.initOwner(button1.getScene().getWindow)
  //    val fxmlLoader = new FXMLLoader(getClass.getResource("Controller.fxml"))
  //    val mainViewRoot: Parent = fxmlLoader.load()
  //    val scene = new Scene(mainViewRoot)
  //    secondStage.setScene(scene)
  //    secondStage.setTitle("PPM Project 21/22 - Octree Methods")
  //    val models = FileReader.createShapesFromFile(currentFile.toString)
  //    currentOctree = OctreeOps.generateDefaultOctree(models)
  //    octreeBoxes = ModelOps.generateBoundingBoxes(currentOctree, List())
  //    ModelOps.toDisplayAll(currentOctree).foreach(m => InitSubScene.worldRoot.getChildren.add(m))
  //    secondStage.show()
  //    currentOctree
  //  }


  @FXML
  var listView1: ListView[File] = _

  @FXML
  private var textField1: TextField = _

  @FXML
  private var ap1: AnchorPane = _

  @FXML
  private var label1: Label = _

  def OnButton1Clicked(): Unit = {
    button1.setDisable(true)
    val models = FileReader.createShapesFromFile(currentFile.toString)
    currentOctree = OctreeOps.generateDefaultOctree(models)
    octreeBoxes = ModelOps.generateBoundingBoxes(currentOctree, List())
    ModelOps.toDisplayAll(currentOctree).foreach(m => InitSubScene.worldRoot.getChildren.add(m))
    val secondStage: Stage = new Stage()
    secondStage.setMaximized(true)
    secondStage.initModality(Modality.APPLICATION_MODAL)
    secondStage.initOwner(button1.getScene().getWindow)
    val fxmlLoader = new FXMLLoader(getClass.getResource("Controller.fxml"))
    val mainViewRoot: Parent = fxmlLoader.load()
    val scene = new Scene(mainViewRoot)
    val myController:Controller = fxmlLoader.getController
    myController.originalOct = currentOctree
    secondStage.setTitle("PPM Project 21/22 - Octree Methods")
    secondStage.setScene(scene)
    secondStage.show()
      }

    @FXML
    def initialize(): Unit = {
      listView1.getSelectionModel().selectedItemProperty().addListener(new ChangeListener[File] {
        override def changed(observableValue: ObservableValue[_ <: File], t: File, t1: File): Unit = {
          currentFile = listView1.getSelectionModel().getSelectedItem()
        }
      })


    }


  }
