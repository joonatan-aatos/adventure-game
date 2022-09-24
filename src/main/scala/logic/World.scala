package logic

import scala.collection.mutable.ArrayBuffer

class World {
  val sprites: ArrayBuffer[Sprite] = ArrayBuffer[Sprite]()
  private val spritesToBeRemoved = ArrayBuffer[Sprite]()

  val player = new Player(4, 4)
  sprites.append(player)

  def tick(): Unit = {
    for (sprite <- sprites) {
      sprite.tick()
      if sprite.shouldBeDeleted then spritesToBeRemoved.append(sprite)
    }
    for (sprite <- spritesToBeRemoved) {
      sprites -= sprite
    }
    spritesToBeRemoved.clear()
  }
}
