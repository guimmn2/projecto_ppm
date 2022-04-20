sealed trait Object

case class Cylinder(radius: Float, height: Int, slices: Int) extends Object
case class Box(side1: Int, side2: Int, side3: Int) extends Object
