package visualizer

import logic.{Directions, Player, Sprite, World}

import java.awt.image.ImageObserver
import java.awt.{Color, Graphics2D}

class Renderer(val imageObserver: ImageObserver) {
  val TILE_SIZE = 48

  private var playerAnimation: Animation = ResourceHelper.PLAYER_IDLE_DOWN

  private val playerIdleMap = Map[Directions.Value, Animation](
    Directions.Up -> ResourceHelper.PLAYER_IDLE_UP,
    Directions.Down -> ResourceHelper.PLAYER_IDLE_DOWN,
    Directions.Left -> ResourceHelper.PLAYER_IDLE_LEFT,
    Directions.Right -> ResourceHelper.PLAYER_IDLE_RIGHT
  )

  private val playerRunningMap = Map[Directions.Value, Animation](
    Directions.Up -> ResourceHelper.PLAYER_RUNNING_UP,
    Directions.Down -> ResourceHelper.PLAYER_RUNNING_DOWN,
    Directions.Left -> ResourceHelper.PLAYER_RUNNING_LEFT,
    Directions.Right -> ResourceHelper.PLAYER_RUNNING_RIGHT
  )

  def drawSprites(g: Graphics2D, world: World): Unit = {
    g.setColor(Color.RED)
    for (sprite <- world.sprites) {
      sprite match {
        case player: Player =>
          val animationMap = if player.input._1 == 0 && player.input._2 == 0 then playerIdleMap else playerRunningMap
          val animation = animationMap(player.facingDirection)
          if animation != playerAnimation then
            playerAnimation.reset()
            playerAnimation = animation
          val image = playerAnimation.getFrame
          g.drawImage(
            image,
            Math.round((sprite.xPos - 0.5f) * TILE_SIZE),
            Math.round((sprite.yPos - 0.5f) * TILE_SIZE),
            TILE_SIZE,
            TILE_SIZE,
            imageObserver
          )
        case _ =>
      }
    }
  }
}
