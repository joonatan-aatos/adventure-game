package visualizer

import logic.Directions

import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import scala.collection.mutable.ArrayBuffer

object ResourceHelper {
  val SPRITE_SIZE = 16
  val TILE_SIZE = 16
  val PLAYER_SPRITESHEET: BufferedImage = ImageIO.read(getClass.getResourceAsStream("/img/Char_Sprites/char_spritesheet.png"))
  val OVERWORLD_TILESET: BufferedImage = ImageIO.read(getClass.getResourceAsStream("/img/Overworld_Tileset.png"))

  /****** OVERWORLD TILESET ******/

  def readTileSet(tileSet: BufferedImage, columns: Int, rows: Int): Vector[Vector[BufferedImage]] = {
    val tiles = ArrayBuffer[Vector[BufferedImage]]()
    for (x <- 0 until columns) {
      val column = ArrayBuffer[BufferedImage]()
      for (y <- 0 until rows) {
        column.append(tileSet.getSubimage(
          x*TILE_SIZE,
          y*TILE_SIZE,
          TILE_SIZE,
          TILE_SIZE
        ))
      }
      tiles.append(column.toVector)
    }
    tiles.toVector
  }

  val overworldTiles: Vector[Vector[BufferedImage]] = readTileSet(OVERWORLD_TILESET, 17, 13)

  /****** SPRITE SHEET ******/

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

  val PLAYER_ATTACKING_DOWN: Animation = new Animation(readSpriteSheat(PLAYER_SPRITESHEET, 0, 192, 6, SPRITE_SIZE * 3), 3)
  // TODO: Find a better way to read this animation from the sprite sheet
  private val playerAttackingUpFrames = readSpriteSheat(PLAYER_SPRITESHEET, 0, 240, 6, SPRITE_SIZE * 3).map(frame => {
    val newFrame = new BufferedImage(frame.getWidth, frame.getHeight, frame.getType)
    newFrame.getGraphics.drawImage(frame.getSubimage(0, 0, SPRITE_SIZE*3, SPRITE_SIZE*2), 0, 0, null)
    newFrame
  })
  val PLAYER_ATTACKING_UP: Animation = new Animation(playerAttackingUpFrames, 3)
  val PLAYER_ATTACKING_LEFT: Animation = new Animation(readSpriteSheat(PLAYER_SPRITESHEET, 0, 272, 6, SPRITE_SIZE * 3), 3)
  val PLAYER_ATTACKING_RIGHT: Animation = new Animation(readSpriteSheat(PLAYER_SPRITESHEET, 0, 320, 6, SPRITE_SIZE * 3), 3)

  val playerIdleMap: Map[Directions.Value, Animation] = Map[Directions.Value, Animation](
    Directions.Up -> ResourceHelper.PLAYER_IDLE_UP,
    Directions.Down -> ResourceHelper.PLAYER_IDLE_DOWN,
    Directions.Left -> ResourceHelper.PLAYER_IDLE_LEFT,
    Directions.Right -> ResourceHelper.PLAYER_IDLE_RIGHT
  )

  val playerRunningMap: Map[Directions.Value, Animation] = Map[Directions.Value, Animation](
    Directions.Up -> ResourceHelper.PLAYER_RUNNING_UP,
    Directions.Down -> ResourceHelper.PLAYER_RUNNING_DOWN,
    Directions.Left -> ResourceHelper.PLAYER_RUNNING_LEFT,
    Directions.Right -> ResourceHelper.PLAYER_RUNNING_RIGHT
  )

  val playerAttackingMap: Map[Directions.Value, Animation] = Map[Directions.Value, Animation](
    Directions.Up -> ResourceHelper.PLAYER_ATTACKING_UP,
    Directions.Down -> ResourceHelper.PLAYER_ATTACKING_DOWN,
    Directions.Left -> ResourceHelper.PLAYER_ATTACKING_LEFT,
    Directions.Right -> ResourceHelper.PLAYER_ATTACKING_RIGHT
  )
}
