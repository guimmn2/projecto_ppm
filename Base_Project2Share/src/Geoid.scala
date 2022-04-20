import Types._

sealed trait Geoid

case class Cylinder(
                     radius: Float,
                     height: Int,
                     slices: Int,
                     color: Color,
                     transition: Transition,
                     scale: Scale
                   ) extends Geoid

case class Box(
                side1: Int,
                side2: Int,
                side3: Int,
                color: Color,
                transition: Transition,
                scale: Scale
              ) extends Geoid