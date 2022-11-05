package logic

import java.awt.event.{KeyEvent, KeyListener}
import scala.collection.immutable.HashSet
import scala.collection.mutable.ArrayBuffer

/**
 * The player class represents the playable character in the game. It handles all player-related
 * logic.
 * @param x
 *   Initial x position
 * @param y
 *   Initial y position
 */
class Player(x: Float, y: Float) extends Sprite(x, y), KeyListener {
  private val movementSpeed = 0.05f
  private val playerSize = 0.3f
  private val attackSize = 1.2f
  private val interactDistance = 1.6f
  private val attackLength = 21
  private val takingDamageLength = 30
  private val dyingLength = 120
  var input: (Float, Float) = (0f, 0f)
  var facingDirection: Direction = Direction.Down
  var health = 3
  var swordInUse: Boolean = false
  var name: String = ""
  // The player is attacking if attackingTimer != 0
  @volatile var attackingTimer: Int = 0
  var takingDamageTimer: Int = 0
  var dyingTimer: Int = 0
  private var keysPressed = HashSet[Int]()
  // When this is set to true, input will be ignored until the next tick
  @volatile private var ignoreInput = false
  private var worldOpt: Option[World] = None

  override def tick(world: World): Unit = {
    worldOpt = Option(world)
    if ignoreInput then ignoreInput = false

    // Return if there was a timer to handle
    if handelTimers(world) then return

    // Handle input
    input = captureNormalizedInput()
    if input._1 != 0 then facingDirection = if input._1 > 0 then Direction.Right else Direction.Left
    else if input._2 != 0 then
      facingDirection = if input._2 > 0 then Direction.Down else Direction.Up

    // Handle movement
    val dx = input._1 * movementSpeed
    val dy = input._2 * movementSpeed
    if canBeInPosition(world.stage, xPos + dx, yPos) then xPos += input._1 * movementSpeed
    if canBeInPosition(world.stage, xPos, yPos + dy) then yPos += input._2 * movementSpeed
  }

  private def handelTimers(world: World): Boolean = {
    var shouldReturn = false
    if dyingTimer != 0 then {
      if dyingTimer == 1 then shouldBeDeleted = true
      dyingTimer -= 1
      shouldReturn = true
    }
    if takingDamageTimer != 0 then {
      takingDamageTimer -= 1
      shouldReturn = true
    }
    if attackingTimer != 0 then {
      attackingTimer -= 1
      if attackingTimer == 10 then {
        attack(world)
      }
      shouldReturn = true
    }
    shouldReturn
  }

  private def attack(world: World): Unit = {
    for (sprite <- world.sprites) {
      // TODO: Implement Enemy superclass that implements method getHit
      sprite match {
        case bat: Bat =>
          facingDirection match
            case Direction.Up =>
              if xPos - attackSize < sprite.xPos && sprite.xPos < xPos + attackSize && yPos - attackSize < sprite.yPos && sprite.yPos < yPos
              then bat.takeHit()
            case Direction.Down =>
              if xPos - attackSize < sprite.xPos && sprite.xPos < xPos + attackSize && yPos < sprite.yPos && sprite.yPos < yPos + attackSize
              then bat.takeHit()
            case Direction.Left =>
              if xPos - attackSize < sprite.xPos && sprite.xPos < xPos && yPos - attackSize < sprite.yPos && sprite.yPos < yPos + attackSize
              then bat.takeHit()
            case Direction.Right =>
              if xPos < sprite.xPos && sprite.xPos < xPos + attackSize && yPos - attackSize < sprite.yPos && sprite.yPos < yPos + attackSize
              then bat.takeHit()
        case _ =>
      }
    }
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

  def takeHit(): Unit = {
    if dyingTimer != 0 || shouldBeDeleted then return
    health = math.max(0, health - 1)
    if health == 0 then
      dyingTimer = dyingLength
      return;
    takingDamageTimer = takingDamageLength
  }

  override def keyPressed(keyEvent: KeyEvent): Unit = {
    if ignoreInput then return
    val code = keyEvent.getKeyCode
    if !keysPressed.contains(code) then keysPressed += code

    code match {
      case KeyEvent.VK_SPACE => startAttack(facingDirection)
      case KeyEvent.VK_UP    => startAttack(Direction.Up)
      case KeyEvent.VK_DOWN  => startAttack(Direction.Down)
      case KeyEvent.VK_LEFT  => startAttack(Direction.Left)
      case KeyEvent.VK_RIGHT => startAttack(Direction.Right)
      case KeyEvent.VK_E     => if worldOpt.isDefined then interact(worldOpt.get)
      case _                 =>
    }
  }

  private def startAttack(direction: Direction): Unit = {
    if attackingTimer != 0 || takingDamageTimer != 0 || !swordInUse then return
    attackingTimer = attackLength
    facingDirection = direction
  }

  private def interact(world: World): Unit = {
    for (sprite <- world.sprites) {
      sprite match {
        case npc: Npc =>
          val dx = npc.xPos - xPos
          val dy = npc.yPos - yPos
          val distance = math.sqrt(dx * dx + dy * dy)
          if distance < interactDistance then
            ignoreInput = true
            input = (0f, 0f)
            npc.showDialog(world)
            // TODO: Implement this logic elsewhere
            if npc.name == "Antti" then swordInUse = true
            else if npc.name == "Tommi" then world.winConditionMet = true
        case _ =>
      }
    }
  }

  override def keyReleased(keyEvent: KeyEvent): Unit = {
    keysPressed -= keyEvent.getKeyCode
  }

  override def keyTyped(keyEvent: KeyEvent): Unit = {}

  override def toString: String = s"Player ($xPos, $yPos)"
}
