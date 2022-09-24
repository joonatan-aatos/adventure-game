import engine.Engine
import game.Game
import visualizer.Visualizer

@main
def main(): Unit = {
  val gameTPS = 60
  val game = new Game()
  val engine = new Engine(game)
  engine.setDesiredFPS(60)
  engine.setDesiredTPS(gameTPS)
  engine.start()
}