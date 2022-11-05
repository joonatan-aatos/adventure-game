package logic

/**
 * The Bat class represents a generic Bat enemy. It contains all logic regarding the behaviour of
 * the Bat
 * @param x
 *   Initial x position
 * @param y
 *   Initial y position TODO: Implement common Enemy superclass
 */
class Bat(x: Float, y: Float) extends Sprite(x, y) {
  private val targetDistanceThreshold = 5f
  private val attackDistanceThreshold = 0.5f
  private val movementSpeed = 0.03f
  private val attackSpeed = 90
  var attackingTimer = 0
  var facingDirection: Direction = Direction.Left
  var target: Option[Player] = None

  override def tick(world: World): Unit = {
    if attackingTimer != 0 then attackingTimer -= 1

    val player = world.player
    val dx = player.xPos - xPos
    val dy = player.yPos - yPos
    val distance = math.sqrt(dx * dx + dy * dy).toFloat

    if target.isEmpty then
      if distance < targetDistanceThreshold then target = Option(player)
      return;

    if distance > targetDistanceThreshold then
      target = None
      return;

    if distance < attackDistanceThreshold then
      attack(player)
      return;

    if dx < 0 then facingDirection = Direction.Left
    else if dx > 0 then facingDirection = Direction.Right

    val dxNormalized = dx / distance
    val dyNormalized = dy / distance

    xPos += dxNormalized * movementSpeed
    yPos += dyNormalized * movementSpeed
  }

  private def attack(player: Player): Unit = {
    if attackingTimer == 0 then
      player.takeHit()
      attackingTimer = attackSpeed
  }

  def takeHit(): Unit = {
    // TODO: Implement dying animation
    shouldBeDeleted = true
  }

  override def toString: String = s"Bat ($xPos, $yPos)"
}
