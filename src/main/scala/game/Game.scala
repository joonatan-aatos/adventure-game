package game

import engine.{EngineInterface, GameInterface}
import logic.{Direction, GameLogicInterface, World}
import visualizer.Visualizer

import java.awt.event.{KeyEvent, KeyListener, WindowEvent}
import javax.swing.JOptionPane
import scala.collection.mutable

/**
 * The Game class is responsible for orchestrating the whole game.
 * In a sense, it is a glue between the World and the Visualizer.
 * The Game class handles much of the program's higher level state.
 */
class Game extends GameInterface, GameLogicInterface, KeyListener {
  enum State {
    case Default
    case ShowingDialog
  }
  val visualizer = new Visualizer()
  var engineInterface: Option[EngineInterface] = None
  var world = new World(this)
  var state: State = State.Default
  val dialogQueue: mutable.Queue[String] = mutable.Queue[String]()
  var currentDialog: Option[String] = None

  override def init(engine: EngineInterface): Unit = {
    this.engineInterface = Option(engine)
    // TODO: Ask for the players name in some other way
    var playerName = ""
    while playerName.isEmpty do
      val result = JOptionPane.showInputDialog("Mikä on sinun nimi?")
      if result != null then playerName = result
    world.player.name = playerName
    JOptionPane.showMessageDialog(
      visualizer.frame,
      "Pelaaja liikkuu näppäimillä W, A, S ja D.\nMuille pelin hahmoille voi puhua painamalla E-näppäintä.\nAputekstin saa auki H-näppäimellä"
    )
    visualizer.addEventListener(this)
    visualizer.addEventListener(world.player)
    visualizer.frame.setVisible(true)
    visualizer.canvas.requestFocus()
  }

  override def render(): Unit = {
    visualizer.render(world, currentDialog)
  }

  override def update(): Unit = {
    state match
      case State.Default =>
        if world.winConditionMet then
          // TODO: Implement these prompts some other way
          if JOptionPane.showOptionDialog(
            visualizer.frame,
            "Haluatko vielä jatkaa pelaamista?",
            "Voitit pelin",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            Array("Kyllä", "Ei"),
            "Kyllä"
          ) == 0 then
            world.winConditionMet = false
          else
            engineInterface.get.stop()
            visualizer.frame.dispatchEvent(new WindowEvent(visualizer.frame, WindowEvent.WINDOW_CLOSING));
        if world.player.shouldBeDeleted then
          JOptionPane.showMessageDialog(visualizer.frame, "Hups, taisit kuolla...")
          JOptionPane.showMessageDialog(visualizer.frame, "Ei haittaa! Tässä pelissä saat loputtomasti uusia yrityksiä :)")
          visualizer.removeEventListener(world.player)
          world = new World(this)
          visualizer.addEventListener(world.player)

        world.tick()
      case State.ShowingDialog =>
  }

  override def showDialog(dialog: Vector[String]): Unit = {
    if dialog.isEmpty then return
    state = State.ShowingDialog
    dialogQueue ++= dialog
    currentDialog = Option(dialogQueue.dequeue())
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
      case KeyEvent.VK_P =>
        engineInterface.get.printFps(!engineInterface.get.getPrintFps)
      case KeyEvent.VK_H =>
        val helpMessage = """Pelaaja liikkuu näppäimillä W, A, S ja D.
Muille pelin hahmoille voi puhua painamalla E-näppäintä.
Miekalla voi hyökätä käyttämällä välilyöntiä tai nuolinäppäimiä.
P-näppäimellä voi tulostaa tietoa pelin käyttämistä resursseista.
H-näppäimellä voi avata tämän tekstin.
"""
        JOptionPane.showMessageDialog(visualizer.frame, helpMessage)
      case _ =>
    }
  }

  override def keyReleased(keyEvent: KeyEvent): Unit = {}

  override def keyTyped(keyEvent: KeyEvent): Unit = {}

}
