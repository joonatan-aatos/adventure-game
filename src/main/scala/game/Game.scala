package game

import engine.{EngineInterface, GameInterface}
import visualizer.Visualizer

class Game extends GameInterface {
  val visualizer = new Visualizer()
  var engineInterface: Option[EngineInterface] = None

  override def init(engine: EngineInterface): Unit = {
    this.engineInterface = Option(engine)
    visualizer.frame.setVisible(true)
  }

  override def render(): Unit = {}

  override def update(): Unit = {}
}
