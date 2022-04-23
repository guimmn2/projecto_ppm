import javafx.scene.Node
import Types._

object OctreeOps {

  def scaleOctree(fact:Double, oct:Octree[Placement]):Octree[Placement] = {
    def scale3DModels(fact:Double, lst:List[Node]):List[Node]= {
      lst match {
        case List() => List()
        case x::xs =>
          x.setScaleX(fact)
          x.setScaleY(fact)
          x.setScaleZ(fact)
          x::scale3DModels(fact,xs)
      }
    }
    oct match {
      case OcEmpty => OcEmpty
      case OcLeaf(section: Section) => OcLeaf(section._1._1,section._1._2*fact,scale3DModels(fact,section._2))
      case OcNode(((x,y,z),size),oc1,oc2,oc3,oc4,oc5,oc6,oc7,oc8) =>
        OcNode(((x,y,z),size*fact),scaleOctree(fact,oc1),scaleOctree(fact,oc2),
          scaleOctree(fact,oc3),scaleOctree(fact,oc4),scaleOctree(fact,oc5),scaleOctree(fact,oc6),
          scaleOctree(fact,oc7),scaleOctree(fact,oc8))
    }
  }

}
