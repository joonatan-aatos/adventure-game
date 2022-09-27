package logic

import scala.collection.mutable.ArrayBuffer

/**
 * The World class represents the game world.
 * It stores all sprites and all of the world state.
 * @param game An interface for communicating with the Game object
 */
class World(val game: GameLogicInterface) {
  val stage: Stage = new Stage()
  val sprites: ArrayBuffer[Sprite] = ArrayBuffer[Sprite]()
  private val spritesToBeRemoved = ArrayBuffer[Sprite]()
  private var playerOption: Option[Player] = None
  var winConditionMet = false

  // Spawn all enteties defined by the stage
  for (entity <- stage.entities) {
    entity._1 match
      case "Player" =>
        playerOption = Option(new Player(entity._2._1.toFloat, entity._2._2.toFloat))
        sprites.append(playerOption.get)
      case "Bat" =>
        sprites.append(new Bat(entity._2._1.toFloat, entity._2._2.toFloat))
      case "Npc" =>
        val additionalData = entity._3.get.asInstanceOf[(String, Vector[String])]
        sprites.append(new Npc(entity._2._1.toFloat, entity._2._2.toFloat, additionalData._1, additionalData._2))
  }
  val player: Player = playerOption.get

  /**
   * The tick function is called repeatedly by the game at a constant interval.
   */
  def tick(): Unit = {
    for (sprite <- sprites) {
      sprite.tick(this)
      if sprite.shouldBeDeleted then spritesToBeRemoved.append(sprite)
    }
    for (sprite <- spritesToBeRemoved) {
      sprites -= sprite
    }
    spritesToBeRemoved.clear()
  }
}
