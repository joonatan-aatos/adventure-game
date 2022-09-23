package engine

trait EngineInterface {
  def start(): Unit
  def stop(): Unit
  def setDesiredTPS(tps: Int): Unit
  def setDesiredFPS(fps: Int): Unit
  def printFps(print: Boolean): Unit
  def getFPS(): Int
  def getTPS(): Int
}
