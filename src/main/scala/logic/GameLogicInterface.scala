package logic

/**
 * An interface for the World to communicate with Game
 */
trait GameLogicInterface {
  def showDialog(dialog: Vector[String]): Unit
}
