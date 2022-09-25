package logic

abstract class Sprite(var xPos: Float, var yPos: Float) {
  var shouldBeDeleted: Boolean = false
  def tick(world: World): Unit
}
