package logic

class NPC(x: Float, y: Float) extends Sprite(x, y) {
  var facingDirection: Direction = Direction.Down
  override def tick(world: World): Unit = {
    val player = world.player
    val dx = player.xPos - xPos
    val dy = player.yPos - yPos
    if math.abs(dx) > math.abs(dy) then
      facingDirection = if dx > 0 then Direction.Right else Direction.Left
    else
      facingDirection = if dy > 0 then Direction.Down else Direction.Up
  }
}
