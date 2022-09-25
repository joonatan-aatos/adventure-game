package logic

import java.awt.event.{KeyEvent, KeyListener}
import scala.collection.mutable.ArrayBuffer

class Player(val x: Float, val y: Float) extends Sprite(x, y), KeyListener {
  // TODO: Make keysPressed array thread-safe
  private val keysPressed: ArrayBuffer[Int] = ArrayBuffer[Int]()

  private val playerSpeed = 0.06f
  private val playerSize = 0.3f
  var input: (Float, Float) = (0f, 0f)
  var facingDirection: Directions.Value = Directions.Down

  // The player is attacking if attackingTimer != 0
  @volatile var attackingTimer: Int = 0
  private val attackLength = 21

  override def tick(world: World): Unit = {
    if attackingTimer != 0 then {
      attackingTimer -= 1
      return
    }
    input = captureNormalizedInput()
    if input._1 != 0 then
      facingDirection = if input._1 > 0 then Directions.Right else Directions.Left
    else if input._2 != 0 then
      facingDirection = if input._2 > 0 then Directions.Down else Directions.Up

    val dx = input._1 * playerSpeed
    val dy = input._2 * playerSpeed
    if canBeInPosition(world.stage, xPos + dx, yPos) then
      xPos += input._1 * playerSpeed
    if canBeInPosition(world.stage, xPos, yPos + dy) then
      yPos += input._2 * playerSpeed
  }

  private def captureNormalizedInput(): (Float, Float) = {
    var dx = 0
    var dy = 0
    if keysPressed.contains(KeyEvent.VK_D) then dx += 1
    if keysPressed.contains(KeyEvent.VK_A) then dx -= 1
    if keysPressed.contains(KeyEvent.VK_W) then dy -= 1
    if keysPressed.contains(KeyEvent.VK_S) then dy += 1
    if dx == 0 && dy == 0 then return (0f, 0f)
    val vectorLength = math.sqrt(math.abs(dx) + math.abs(dy)).toFloat
    (dx / vectorLength, dy / vectorLength)
  }

  private def canBeInPosition(stage: Stage, x: Float, y: Float): Boolean = {
    val offset = playerSize / 2f
    stage.canBeInPosition(x + offset, y + offset) &&
    stage.canBeInPosition(x + offset, y - offset) &&
    stage.canBeInPosition(x - offset, y + offset) &&
    stage.canBeInPosition(x - offset, y - offset)
  }

  private def startAttack(direction: Directions.Value): Unit = {
    if attackingTimer != 0 then return
    attackingTimer = attackLength
    facingDirection = direction
  }

  override def keyPressed(keyEvent: KeyEvent): Unit = {
    val code = keyEvent.getKeyCode
    if !keysPressed.contains(code) then
      keysPressed.append(code)

    code match {
      case KeyEvent.VK_SPACE => startAttack(facingDirection)
      case KeyEvent.VK_UP => startAttack(Directions.Up)
      case KeyEvent.VK_DOWN => startAttack(Directions.Down)
      case KeyEvent.VK_LEFT => startAttack(Directions.Left)
      case KeyEvent.VK_RIGHT => startAttack(Directions.Right)
      case _ =>
    }
  }

  override def keyReleased(keyEvent: KeyEvent): Unit = {
    keysPressed -= keyEvent.getKeyCode
  }

  override def keyTyped(keyEvent: KeyEvent): Unit = {}
}
