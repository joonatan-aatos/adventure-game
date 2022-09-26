package logic

import java.awt.event.{KeyEvent, KeyListener}
import scala.collection.immutable.HashSet
import scala.collection.mutable.ArrayBuffer

class Player(x: Float, y: Float) extends Sprite(x, y), KeyListener {
  private var keysPressed = HashSet[Int]()
  private val movementSpeed = 0.05f
  private val playerSize = 0.3f
  private val attackSize = 1.2f
  var input: (Float, Float) = (0f, 0f)
  var facingDirection: Direction = Direction.Down
  var health = 3

  // The player is attacking if attackingTimer != 0
  @volatile var attackingTimer: Int = 0
  private val attackLength = 21
  var takingDamageTimer: Int = 0
  private val takingDamageLength = 24

  override def tick(world: World): Unit = {
    if takingDamageTimer != 0 then {
      takingDamageTimer -= 1
      return
    }
    if attackingTimer != 0 then {
      attackingTimer -= 1
      if attackingTimer == 10 then {
        attack(world)
      }
      return
    }
    input = captureNormalizedInput()
    if input._1 != 0 then
      facingDirection = if input._1 > 0 then Direction.Right else Direction.Left
    else if input._2 != 0 then
      facingDirection = if input._2 > 0 then Direction.Down else Direction.Up

    val dx = input._1 * movementSpeed
    val dy = input._2 * movementSpeed
    if canBeInPosition(world.stage, xPos + dx, yPos) then
      xPos += input._1 * movementSpeed
    if canBeInPosition(world.stage, xPos, yPos + dy) then
      yPos += input._2 * movementSpeed
  }

  def takeHit(): Unit = {
    health = math.max(0, health - 1)
    takingDamageTimer = takingDamageLength
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

  private def startAttack(direction: Direction): Unit = {
    if attackingTimer != 0 || takingDamageTimer != 0 then return
    attackingTimer = attackLength
    facingDirection = direction
  }

  private def attack(world: World): Unit = {
    for (sprite <- world.sprites) {
      // TODO: Implement Enemy superclass that implements method getHit
      sprite match {
        case bat: Bat =>
          facingDirection match
            case Direction.Up =>
              if xPos - attackSize < sprite.xPos && sprite.xPos < xPos + attackSize && yPos - attackSize < sprite.yPos && sprite.yPos < yPos then
                bat.takeHit()
            case Direction.Down =>
              if xPos - attackSize < sprite.xPos && sprite.xPos < xPos + attackSize && yPos < sprite.yPos && sprite.yPos < yPos + attackSize then
                bat.takeHit()
            case Direction.Left =>
              if xPos - attackSize < sprite.xPos && sprite.xPos < xPos && yPos - attackSize < sprite.yPos && sprite.yPos < yPos + attackSize then
                bat.takeHit()
            case Direction.Right =>
              if xPos < sprite.xPos && sprite.xPos < xPos + attackSize && yPos - attackSize < sprite.yPos && sprite.yPos < yPos + attackSize then
                bat.takeHit()
        case _ =>
      }
    }
  }

  override def keyPressed(keyEvent: KeyEvent): Unit = {
    val code = keyEvent.getKeyCode
    if !keysPressed.contains(code) then
      keysPressed += code

    code match {
      case KeyEvent.VK_SPACE => startAttack(facingDirection)
      case KeyEvent.VK_UP => startAttack(Direction.Up)
      case KeyEvent.VK_DOWN => startAttack(Direction.Down)
      case KeyEvent.VK_LEFT => startAttack(Direction.Left)
      case KeyEvent.VK_RIGHT => startAttack(Direction.Right)
      case _ =>
    }
  }

  override def keyReleased(keyEvent: KeyEvent): Unit = {
    keysPressed -= keyEvent.getKeyCode
  }

  override def keyTyped(keyEvent: KeyEvent): Unit = {}

  override def toString: String = s"Player ($xPos, $yPos)"
}
