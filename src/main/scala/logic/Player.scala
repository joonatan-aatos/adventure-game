package logic

class Player(val x: Float, val y: Float) extends Sprite(x, y) {
  override def tick(): Unit = {
    xPos += 0.05f
  }
}
