import InitSubScene.subScene
import OctreeOps.getModelListFromOctree
import Types.Placement
import javafx.application.Application
import javafx.event.ActionEvent
import javafx.fxml.{FXML, FXMLLoader}
import javafx.scene.control.{Button, RadioButton, Slider, Tab, TabPane, ToggleGroup}
import javafx.scene.{Parent, Scene, SubScene}
import javafx.scene.layout.{AnchorPane, HBox, VBox}
class Controller {

  @FXML
  private var subScene1:SubScene = _

  @FXML
  private var subScene2:SubScene = _

  @FXML
  private var ap1:AnchorPane = _

  @FXML
  private var tabPane1:TabPane = _
  //Creating the first tab

  @FXML
  private var tabHome:Tab = _

  @FXML
  private var tabScale:Tab = _

  var originalOct:Octree[Placement] = _
  var changedOct:Octree[Placement] = _

  @FXML
  private var tabColour:Tab = _

  @FXML
  private var s1:Slider = _


  @FXML
  private var buttonGR:Button = _

  @FXML
  private var buttonSepia:Button = _

  @FXML
  def onSliderChanged(): Unit = {
    InitSubScene.worldRoot.getChildren.clear()
    InitSubScene.worldRoot.getChildren.add(InitSubScene.lineX)
    InitSubScene.worldRoot.getChildren.add(InitSubScene.lineY)
    InitSubScene.worldRoot.getChildren.add(InitSubScene.lineZ)
    InitSubScene.worldRoot.getChildren.add(InitSubScene.camVolume)
    val octree = originalOct
    val scaledOctree = OctreeOps.scaleOctreeV2(octree,s1.getValue)
    ModelOps.toDisplayAll(scaledOctree).foreach(m => InitSubScene.worldRoot.getChildren.add(m))
    changedOct = scaledOctree
  }

  @FXML
  def greenRemoveSelected():Unit = {
    var newOctree:Octree[Placement] = null
    if(changedOct == null){
      newOctree = OctreeOps.greenRemove(originalOct)
      ModelOps.toDisplayAll(newOctree).foreach(m => InitSubScene.worldRoot.getChildren.add(m))
    }else {
      newOctree = OctreeOps.greenRemove(changedOct)
      ModelOps.toDisplayAll(newOctree).foreach(m => InitSubScene.worldRoot.getChildren.add(m))
    }

  }

  @FXML
  def sepiaSelected():Unit = {
    var newOctree:Octree[Placement] = null
    if(changedOct == null){
      newOctree = OctreeOps.sepia(originalOct)
      ModelOps.toDisplayAll(newOctree).foreach(m => InitSubScene.worldRoot.getChildren.add(m))
    }else {
      newOctree = OctreeOps.sepia(changedOct)
      ModelOps.toDisplayAll(newOctree).foreach(m => InitSubScene.worldRoot.getChildren.add(m))
    }
  }


  //method automatically invoked after the @FXML fields have been injected
  @FXML
  def initialize(): Unit = {
    InitSubScene.subScene.widthProperty.bind(subScene1.widthProperty)
    InitSubScene.subScene.heightProperty.bind(subScene1.heightProperty)
    subScene1.setRoot(InitSubScene.root)
    subScene1.widthProperty.bind(ap1.widthProperty.subtract(400))
    subScene1.heightProperty.bind(ap1.heightProperty)
  }
}

