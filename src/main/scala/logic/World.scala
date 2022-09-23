package logic

import scala.collection.mutable.ArrayBuffer

class World {
  private val sprites: ArrayBuffer[Sprite] = ArrayBuffer[Sprite]()
  private val spritesToBeRemoved = ArrayBuffer[Sprite]()

  sprites.append(new Player(4, 4))

  def tick(): Unit = {
    for (sprite <- sprites) {
      sprite.tick()
    }
    for (sprite <- spritesToBeRemoved) {
      sprites -= sprite
    }
    spritesToBeRemoved.clear()
  }

  def getSprites: Vector[Sprite] = sprites.clone().toVector
}
