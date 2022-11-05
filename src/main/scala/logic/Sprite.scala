package logic

/**
 * The Sprite class represents all Sprites. All Sprites should extend this class
 * @param xPos
 *   Initial x position
 * @param yPos
 *   Initial y position
 */
abstract class Sprite(var xPos: Float, var yPos: Float) {
  var shouldBeDeleted: Boolean = false

  /**
   * The tick function is called repeatedly by the World class at a constant interval defined by the
   * Game class
   * @param world
   *   The world that the sprite exists in
   */
  def tick(world: World): Unit
}
