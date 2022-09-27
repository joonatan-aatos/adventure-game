package logic

/**
 * The Npc class represents all npcs in the game
 * @param x Initial x position
 * @param y Initial y position
 * @param name Name of the npc
 * @param dialog What the npc says when it is talked to
 *               TODO: Implement different dialog depending on game state
 */
class Npc(x: Float, y: Float, val name: String, val dialog: Vector[String]) extends Sprite(x, y) {
  private val dialogWithName = dialog.map(s => s"$name: $s")
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

  /**
   * Attempts to show the dialog of this Npc
   * @param world The world that this Npc exists in
   */
  def showDialog(world: World): Unit = {
    val formattedDialog = dialogWithName.map(s => s.replaceAll("\\{name\\}", world.player.name.capitalize))
    world.game.showDialog(formattedDialog)
  }
}
