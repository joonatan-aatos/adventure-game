package logic

import scala.collection.mutable.ArrayBuffer

class World {
  val stage: Stage = new Stage()
  val sprites: ArrayBuffer[Sprite] = ArrayBuffer[Sprite]()
  private val spritesToBeRemoved = ArrayBuffer[Sprite]()

  val player = new Player(5, 5)
  sprites.append(player)

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
