sealed trait Args

case class NoOps(fileNr: Int) extends Args
case class Scale(fileNr: Int, scale: Double) extends Args
case class Colour(fileNr: Int, colourOp: Int) extends Args
case class ScaleAndColour(fileNr: Int, scale: Int, colourOp: Int) extends Args

object Args {
}