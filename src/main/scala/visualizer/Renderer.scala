package visualizer

import logic.{Directions, Player, Sprite, World}
import visualizer.ResourceHelper.{playerIdleMap, playerRunningMap, playerAttackingMap}

import java.awt.image.ImageObserver
import java.awt.{Color, Graphics2D}

class Renderer(val imageObserver: ImageObserver) {
  val TILE_SIZE = 48
  val SCALING_FACTOR = 3f

  private var playerAnimation: Animation = ResourceHelper.PLAYER_IDLE_DOWN

  def drawSprites(g: Graphics2D, world: World): Unit = {
    g.setColor(Color.RED)
    for (sprite <- world.sprites) {
      sprite match {
        case player: Player =>
          val animationMap = {
            if player.attackingTimer != 0 then playerAttackingMap
            else if player.input._1 == 0 && player.input._2 == 0 then playerIdleMap
            else playerRunningMap
          }
          val animation = animationMap(player.facingDirection)
          if animation != playerAnimation then
            playerAnimation.reset()
            playerAnimation = animation
          val image = playerAnimation.getFrame
          g.drawImage(
            image,
            Math.round(sprite.xPos * TILE_SIZE - image.getWidth * SCALING_FACTOR / 2),
            Math.round(sprite.yPos * TILE_SIZE - image.getHeight * SCALING_FACTOR / 2),
            Math.round(image.getWidth * SCALING_FACTOR),
            Math.round(image.getHeight * SCALING_FACTOR),
            imageObserver
          )
        case _ =>
      }
    }
  }
}
