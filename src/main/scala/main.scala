import engine.Engine
import game.Game
import visualizer.Visualizer

@main
def main(): Unit = {
  // Create an engine and assign a game to it
  val gameTPS = 60
  val game = new Game()
  val engine = new Engine(game)
  engine.setDesiredFPS(gameTPS)
  engine.setDesiredTPS(gameTPS)
  // The engine is run on a separate thread so execution may continue after this
  engine.start()
}
