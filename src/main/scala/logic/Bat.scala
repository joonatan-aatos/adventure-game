package logic

class Bat(x: Float, y: Float) extends Sprite(x, y) {
  private val targetDistanceThreshold = 5f
  private val attackDistanceThreshold = 0.5f
  private val movementSpeed = 0.03f
  var facingDirection: Direction = Direction.Left
  var target: Option[Player] = None

  override def tick(world: World): Unit = {
    val player = world.player
    val dx = player.xPos - xPos
    val dy = player.yPos - yPos
    val distance = math.sqrt(dx*dx + dy*dy).toFloat

    if target.isEmpty then
      if distance < targetDistanceThreshold then
        target = Option(player)
      return;

    if distance > targetDistanceThreshold then
      target = None
      return;

    if distance < attackDistanceThreshold then
      return;
    if dx < 0 then facingDirection = Direction.Left
    else if dx > 0 then facingDirection = Direction.Right

    val dxNormalized = dx / distance
    val dyNormalized = dy / distance

    xPos += dxNormalized * movementSpeed
    yPos += dyNormalized * movementSpeed
  }

  def getHit(): Unit = {
    // TODO: Implement dying animation
    shouldBeDeleted = true
  }

  override def toString: String = s"Bat ($xPos, $yPos)"
}
