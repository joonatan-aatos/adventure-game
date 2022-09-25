package visualizer

import logic.{Directions, Player, Sprite, World}
import visualizer.ResourceHelper.{playerIdleMap, playerRunningMap, playerAttackingMap}

import java.awt.image.ImageObserver
import java.awt.{Color, Graphics2D}

class Renderer(val imageObserver: ImageObserver, camera: Camera, tileSize: Int) {
  private val scalingFactor = tileSize.toFloat / 16f

  private var playerAnimation: Animation = ResourceHelper.PLAYER_IDLE_DOWN

  def calculateDisplayCoords(worldXPos: Float, worldYPos: Float): (Float, Float) =
    (worldXPos * tileSize - camera.xPos, worldYPos * tileSize - camera.yPos)

  def drawSprites(g: Graphics2D, world: World): Unit = {
    g.setColor(Color.RED)
    for (sprite <- world.sprites) {
      val coords = calculateDisplayCoords(sprite.xPos, sprite.yPos)
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
            Math.round(coords._1 - image.getWidth * scalingFactor / 2),
            Math.round(coords._2 - image.getHeight * scalingFactor / 2),
            Math.round(image.getWidth * scalingFactor),
            Math.round(image.getHeight * scalingFactor),
            imageObserver
          )
        case _ =>
      }
    }
  }

  def drawStage(g: Graphics2D, world: World): Unit = {
    val tiles = world.stage.tiles
    for (tile <- tiles) {
      val tileImg = ResourceHelper.overworldTiles(tile.src._1)(tile.src._2)
      val coords = calculateDisplayCoords(tile.pos._1, tile.pos._2)
      g.drawImage(
        tileImg,
        math.round(coords._1),
        math.round(coords._2),
        tileSize,
        tileSize,
        imageObserver
      )
    }
  }
}
