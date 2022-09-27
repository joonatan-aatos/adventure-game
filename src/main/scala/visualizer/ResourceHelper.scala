package visualizer

import logic.Direction

import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import scala.collection.mutable.ArrayBuffer

object ResourceHelper {
  val SPRITE_SIZE = 16
  val TILE_SIZE = 16

  /****** OVERWORLD TILESET ******/

  val OVERWORLD_TILESET: BufferedImage = ImageIO.read(getClass.getResourceAsStream("/img/Overworld_Tileset.png"))

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

  val overworldTiles: Vector[Vector[BufferedImage]] = readTileSet(OVERWORLD_TILESET, 18, 13)

  /****** SPRITES ******/

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

  /****** PLAYER SPRITE SHEET ******/

  val PLAYER_SPRITESHEET: BufferedImage = ImageIO.read(getClass.getResourceAsStream("/img/Char_Sprites/char_spritesheet.png"))

  val PLAYER_IDLE_DOWN: Animation = new Animation(readSpriteSheat(PLAYER_SPRITESHEET, 128, 16, 6, SPRITE_SIZE), 10)
  val PLAYER_IDLE_LEFT: Animation = new Animation(readSpriteSheat(PLAYER_SPRITESHEET, 128, 32, 6, SPRITE_SIZE), 10)
  val PLAYER_IDLE_RIGHT: Animation = new Animation(readSpriteSheat(PLAYER_SPRITESHEET, 128, 48, 6, SPRITE_SIZE), 10)
  val PLAYER_IDLE_UP: Animation = new Animation(readSpriteSheat(PLAYER_SPRITESHEET, 128, 64, 6, SPRITE_SIZE), 10)

  val PLAYER_RUNNING_DOWN: Animation = new Animation(readSpriteSheat(PLAYER_SPRITESHEET, 16, 16, 6, SPRITE_SIZE), 6)
  val PLAYER_RUNNING_LEFT: Animation = new Animation(readSpriteSheat(PLAYER_SPRITESHEET, 16, 32, 6, SPRITE_SIZE), 6)
  val PLAYER_RUNNING_RIGHT: Animation = new Animation(readSpriteSheat(PLAYER_SPRITESHEET, 16, 48, 6, SPRITE_SIZE), 6)
  val PLAYER_RUNNING_UP: Animation = new Animation(readSpriteSheat(PLAYER_SPRITESHEET, 16, 64, 6, SPRITE_SIZE), 6)

  val PLAYER_TAKING_DAMAGE_DOWN: Animation = new Animation(readSpriteSheat(PLAYER_SPRITESHEET, 240, 16, 3, SPRITE_SIZE), 10)
  val PLAYER_TAKING_DAMAGE_LEFT: Animation = new Animation(readSpriteSheat(PLAYER_SPRITESHEET, 240, 32, 3, SPRITE_SIZE), 10)
  val PLAYER_TAKING_DAMAGE_RIGHT: Animation = new Animation(readSpriteSheat(PLAYER_SPRITESHEET, 240, 48, 3, SPRITE_SIZE), 10)
  val PLAYER_TAKING_DAMAGE_UP: Animation = new Animation(readSpriteSheat(PLAYER_SPRITESHEET, 240, 64, 3, SPRITE_SIZE), 10)

  val PLAYER_DYING: Animation = new Animation(readSpriteSheat(PLAYER_SPRITESHEET, 16, 176, 6, SPRITE_SIZE), 20)

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

  val playerIdleMap: Map[Direction, Animation] = Map[Direction, Animation](
    Direction.Up -> PLAYER_IDLE_UP,
    Direction.Down -> PLAYER_IDLE_DOWN,
    Direction.Left -> PLAYER_IDLE_LEFT,
    Direction.Right -> PLAYER_IDLE_RIGHT
  )

  val playerRunningMap: Map[Direction, Animation] = Map[Direction, Animation](
    Direction.Up -> PLAYER_RUNNING_UP,
    Direction.Down -> PLAYER_RUNNING_DOWN,
    Direction.Left -> PLAYER_RUNNING_LEFT,
    Direction.Right -> PLAYER_RUNNING_RIGHT
  )
  
  val playerTakingDamageMap: Map[Direction, Animation] = Map[Direction, Animation](
    Direction.Up -> PLAYER_TAKING_DAMAGE_UP,
    Direction.Down -> PLAYER_TAKING_DAMAGE_DOWN,
    Direction.Left -> PLAYER_TAKING_DAMAGE_LEFT,
    Direction.Right -> PLAYER_TAKING_DAMAGE_RIGHT
  )

  val playerAttackingMap: Map[Direction, Animation] = Map[Direction, Animation](
    Direction.Up -> PLAYER_ATTACKING_UP,
    Direction.Down -> PLAYER_ATTACKING_DOWN,
    Direction.Left -> PLAYER_ATTACKING_LEFT,
    Direction.Right -> PLAYER_ATTACKING_RIGHT
  )

  /****** NPC SPRITE SHEET ******/

  val NPC_SPRITESHEET: BufferedImage = ImageIO.read(getClass.getResourceAsStream("/img/Custom/npc_spritesheet.png"))

  val NPC_IDLE_DOWN: Animation = new Animation(readSpriteSheat(NPC_SPRITESHEET, 0, 0, 6, SPRITE_SIZE), 10)
  val NPC_IDLE_LEFT: Animation = new Animation(readSpriteSheat(NPC_SPRITESHEET, 0, 16, 6, SPRITE_SIZE), 10)
  val NPC_IDLE_RIGHT: Animation = new Animation(readSpriteSheat(NPC_SPRITESHEET, 0, 32, 6, SPRITE_SIZE), 10)
  val NPC_IDLE_UP: Animation = new Animation(readSpriteSheat(NPC_SPRITESHEET, 0, 48, 6, SPRITE_SIZE), 10)

  val npcIdleMap: Map[Direction, Animation] = Map[Direction, Animation](
    Direction.Up -> NPC_IDLE_UP,
    Direction.Down -> NPC_IDLE_DOWN,
    Direction.Left -> NPC_IDLE_LEFT,
    Direction.Right -> NPC_IDLE_RIGHT
  )

  /****** BAT SPRITE SHEET ******/

  val BAT_SPRITESHEET: BufferedImage = ImageIO.read(getClass.getResourceAsStream("/img/Enemies_Sprites/Pinkbat_Sprites/pinkbat_spritesheet.png"))

  val BAT_IDLE_LEFT: Animation = new Animation(readSpriteSheat(BAT_SPRITESHEET, 0, 48, 5, SPRITE_SIZE), 6)
  val BAT_IDLE_RIGHT: Animation = new Animation(readSpriteSheat(BAT_SPRITESHEET, 0, 64, 5, SPRITE_SIZE), 6)

  val BAT_RUNNING_LEFT: Animation = new Animation(readSpriteSheat(BAT_SPRITESHEET, 96, 48, 3, SPRITE_SIZE), 6)
  val BAT_RUNNING_RIGHT: Animation = new Animation(readSpriteSheat(BAT_SPRITESHEET, 96, 64, 3, SPRITE_SIZE), 6)
  
  val batIdleMap: Map[Direction, Animation] = Map[Direction, Animation](
    Direction.Left -> BAT_IDLE_LEFT,
    Direction.Right -> BAT_IDLE_RIGHT
  )

  val batRunningMap: Map[Direction, Animation] = Map[Direction, Animation](
    Direction.Left -> BAT_RUNNING_LEFT,
    Direction.Right -> BAT_RUNNING_RIGHT
  )

  /****** HUD ******/

  val HEARTS_IMAGE: BufferedImage = ImageIO.read(getClass.getResourceAsStream("/img/Hud_Ui/hearts.png"))

  val heartImages: Vector[BufferedImage] = readSpriteSheat(HEARTS_IMAGE, 0, 0, 2, SPRITE_SIZE)
  val FULL_HEART: BufferedImage = heartImages(0)
  val DEPLETED_HEART: BufferedImage = heartImages(1)
}
