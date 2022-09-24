package visualizer

import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import scala.collection.mutable.ArrayBuffer

object ResourceHelper {
  val SPRITE_SIZE = 16
  val PLAYER_SPRITESHEET: BufferedImage = ImageIO.read(getClass.getResourceAsStream("/img/Char_Sprites/char_spritesheet.png"))

  private def readSpriteSheat(sheet: BufferedImage, xPos: Int, yPos: Int, count: Int, spriteSize: Int): Vector[BufferedImage] = {
    val images = ArrayBuffer[BufferedImage]()
    for (i <- 0 until count) {
      images.append(sheet.getSubimage(
        xPos + i * spriteSize,
        yPos,
        spriteSize,
        spriteSize
      ))
    }
    images.toVector
  }

  val PLAYER_IDLE_DOWN: Animation = new Animation(readSpriteSheat(PLAYER_SPRITESHEET, 128, 16, 6, SPRITE_SIZE), 10)
  val PLAYER_IDLE_LEFT: Animation = new Animation(readSpriteSheat(PLAYER_SPRITESHEET, 128, 32, 6, SPRITE_SIZE), 10)
  val PLAYER_IDLE_RIGHT: Animation = new Animation(readSpriteSheat(PLAYER_SPRITESHEET, 128, 48, 6, SPRITE_SIZE), 10)
  val PLAYER_IDLE_UP: Animation = new Animation(readSpriteSheat(PLAYER_SPRITESHEET, 128, 64, 6, SPRITE_SIZE), 10)

  val PLAYER_RUNNING_DOWN: Animation = new Animation(readSpriteSheat(PLAYER_SPRITESHEET, 16, 16, 6, SPRITE_SIZE), 6)
  val PLAYER_RUNNING_LEFT: Animation = new Animation(readSpriteSheat(PLAYER_SPRITESHEET, 16, 32, 6, SPRITE_SIZE), 6)
  val PLAYER_RUNNING_RIGHT: Animation = new Animation(readSpriteSheat(PLAYER_SPRITESHEET, 16, 48, 6, SPRITE_SIZE), 6)
  val PLAYER_RUNNING_UP: Animation = new Animation(readSpriteSheat(PLAYER_SPRITESHEET, 16, 64, 6, SPRITE_SIZE), 6)
}
