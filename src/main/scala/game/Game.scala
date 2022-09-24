package game

import engine.{EngineInterface, GameInterface}
import logic.World
import visualizer.Visualizer

class Game extends GameInterface {
  val visualizer = new Visualizer()
  var engineInterface: Option[EngineInterface] = None
  val world = new World()

  override def init(engine: EngineInterface): Unit = {
    this.engineInterface = Option(engine)
    visualizer.addEventListener(world.player)
    visualizer.frame.setVisible(true)
    visualizer.canvas.requestFocus()
  }

  override def render(): Unit = {
    visualizer.render(world)
  }

  override def update(): Unit = {
    world.tick()
  }
}
