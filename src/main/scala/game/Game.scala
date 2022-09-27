package game

import engine.{EngineInterface, GameInterface}
import logic.{Direction, GameLogicInterface, World}
import visualizer.Visualizer

import java.awt.event.{KeyEvent, KeyListener}
import scala.collection.mutable

class Game extends GameInterface, GameLogicInterface, KeyListener {
  enum State {
    case Default
    case ShowingDialog
  }
  val visualizer = new Visualizer()
  var engineInterface: Option[EngineInterface] = None
  val world = new World(this)
  var state: State = State.Default
  val dialogQueue: mutable.Queue[String] = mutable.Queue[String]()
  var currentDialog: Option[String] = None

  override def init(engine: EngineInterface): Unit = {
    this.engineInterface = Option(engine)
    visualizer.addEventListener(world.player)
    visualizer.addEventListener(this)
    visualizer.frame.setVisible(true)
    visualizer.canvas.requestFocus()
  }

  override def render(): Unit = {
    visualizer.render(world, currentDialog)
  }

  override def update(): Unit = {
    state match
      case State.Default =>
        world.tick()
      case State.ShowingDialog =>
  }

  override def showDialog(dialog: Vector[String]): Unit = {
    if dialog.isEmpty then return
    state = State.ShowingDialog
    dialogQueue ++= dialog
  }

  override def keyPressed(keyEvent: KeyEvent): Unit = {
    val code = keyEvent.getKeyCode
    code match {
      case KeyEvent.VK_E =>
        if state == State.ShowingDialog then
          if dialogQueue.nonEmpty then
            currentDialog = Option(dialogQueue.dequeue())
          else
            currentDialog = None
            state = State.Default
      case _ =>
    }
  }

  override def keyReleased(keyEvent: KeyEvent): Unit = {}

  override def keyTyped(keyEvent: KeyEvent): Unit = {}

}
