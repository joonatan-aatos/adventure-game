package logic

import scala.collection.mutable.ArrayBuffer

class World(val game: GameLogicInterface) {
  val stage: Stage = new Stage()
  val sprites: ArrayBuffer[Sprite] = ArrayBuffer[Sprite]()
  private val spritesToBeRemoved = ArrayBuffer[Sprite]()
  private var playerOption: Option[Player] = None

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
