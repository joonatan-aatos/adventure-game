package visualizer

import logic.{Directions, Player, Sprite, Stage, Tile, World}
import visualizer.ResourceHelper.{playerAttackingMap, playerIdleMap, playerRunningMap}

import java.awt.image.ImageObserver
import java.awt.{Color, Graphics2D}
import scala.annotation.unused
import scala.collection.mutable.ArrayBuffer

class Renderer(val imageObserver: ImageObserver, camera: Camera, tileSize: Int) {
  private val scalingFactor = tileSize.toFloat / 16f

  private var playerAnimation: Animation = ResourceHelper.PLAYER_IDLE_DOWN

  def calculateDisplayCoords(worldXPos: Float, worldYPos: Float): (Float, Float) =
    (worldXPos * tileSize - camera.xPos, worldYPos * tileSize - camera.yPos)

  def filterVisibleTiles(tiles: Vector[Tile]): Vector[Tile] = tiles.filter(tile =>
    tile.pos._1 + 1 > camera.xPos / tileSize &&
    tile.pos._1 < (camera.xPos + camera.windowWidth) / tileSize &&
    tile.pos._2 + 1 > camera.yPos / tileSize &&
    tile.pos._2 < (camera.yPos + camera.windowHeight) / tileSize
  )

  def draw(g: Graphics2D, world: World): Unit = {
    // Draw first layer of tiles
    drawTiles(g, filterVisibleTiles(world.stage.tilesLayer1))
    // Devide second layer of tiles into two:
    // * tiles that should be rendered above the player and
    // * tiles that should be rendered below the player
    val player = world.player
    val tilesBelowPlayer = ArrayBuffer[Tile]()
    val tilesAbovePlayer = ArrayBuffer[Tile]()
    for (tile <- filterVisibleTiles(world.stage.tilesLayer2)) {
      if tile.src._2 < 11 then tilesAbovePlayer.append(tile)
      else tilesBelowPlayer.append(tile)
    }
    // Draw all tiles below the player
    drawTiles(g, tilesBelowPlayer.toVector)
    // Draw the player itself
    drawPlayer(g, player)
    // Draw all tiles above the player
    drawTiles(g, tilesAbovePlayer.toVector)
  }

  def drawPlayer(g: Graphics2D, player: Player): Unit = {
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
    val coords = calculateDisplayCoords(player.xPos, player.yPos)
    g.drawImage(
      image,
      Math.round(coords._1 - image.getWidth * scalingFactor * 0.5f),
      Math.round(coords._2 - image.getHeight * scalingFactor * 0.5f - tileSize * 0.2f),
      Math.round(image.getWidth * scalingFactor),
      Math.round(image.getHeight * scalingFactor),
      imageObserver
    )
  }

  def drawTiles(g: Graphics2D, tiles: Vector[Tile]): Unit = {
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

  @unused
  def drawCollisionBorders(g: Graphics2D, stage: Stage): Unit = {
    g.setColor(Color.BLACK)
    for (x <- 0 until stage.worldWidth) {
      for (y <- 0 until stage.worldHeight) {
        if !stage.collisionMap(x)(y) then
          val coords = (x * tileSize / 2f - camera.xPos, y * tileSize / 2f - camera.yPos)
          g.fillRect(coords._1.toInt, coords._2.toInt, tileSize / 2, tileSize / 2)
      }
    }
  }
}
