package visualizer

import logic.{Bat, Direction, Player, Sprite, Stage, Tile, World}
import visualizer.ResourceHelper.{batIdleMap, batRunningMap, playerAttackingMap, playerIdleMap, playerRunningMap, playerTakingDamageMap}

import java.awt.image.{BufferedImage, ImageObserver}
import java.awt.{BasicStroke, Color, Graphics2D}
import scala.annotation.unused
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class Renderer(val imageObserver: ImageObserver, camera: Camera, tileSize: Int) {
  private val scalingFactor = tileSize.toFloat / 16f

  private var playerAnimation: Animation = ResourceHelper.PLAYER_IDLE_DOWN
  private val spriteAnimations: mutable.HashMap[Sprite, Animation] = mutable.HashMap[Sprite, Animation]()

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
    // * tiles that should be rendered above sprites and
    // * tiles that should be rendered below sprites
    val player = world.player
    val tilesBelowPlayer = ArrayBuffer[Tile]()
    val tilesAbovePlayer = ArrayBuffer[Tile]()
    for (tile <- filterVisibleTiles(world.stage.tilesLayer2)) {
      if tile.src._2 < 11 then tilesAbovePlayer.append(tile)
      else tilesBelowPlayer.append(tile)
    }
    // Draw all tiles below the player
    drawTiles(g, tilesBelowPlayer.toVector)
    // Draw sprites
    drawPlayer(g, player)
    drawSprites(g, world.sprites)
    // Draw all tiles above the player
    drawTiles(g, tilesAbovePlayer.toVector)
    // Draw HUD on top of everything else
    drawHUD(g, player)
  }

  def drawPlayer(g: Graphics2D, player: Player): Unit = {
    if player.shouldBeDeleted then return
    val animationMap = {
      if player.attackingTimer != 0 then playerAttackingMap
      else if player.takingDamageTimer != 0 then playerTakingDamageMap
      else if player.input._1 == 0 && player.input._2 == 0 then playerIdleMap
      else playerRunningMap
    }
    val animation = {
      if player.dyingTimer != 0 then ResourceHelper.PLAYER_DYING
      else animationMap(player.facingDirection)
    }
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

  def drawSprites(g: Graphics2D, sprites: ArrayBuffer[Sprite]): Unit = {
    for (sprite <- sprites) {
      var image: Option[BufferedImage] = None
      sprite match
        case bat: Bat =>
          val animationMap = {
            if bat.target.isDefined then batRunningMap
            else batIdleMap
          }
          val animation = animationMap(bat.facingDirection)
          val previousAnimation = if spriteAnimations.contains(bat) then spriteAnimations(bat) else animation
          if animation != previousAnimation then
            previousAnimation.reset()
            spriteAnimations += (bat -> animation)
          image = Option(animation.getFrame)
        case _ =>

      if image.isDefined then
        val coords = calculateDisplayCoords(sprite.xPos, sprite.yPos)
        g.drawImage(
          image.get,
          Math.round(coords._1 - image.get.getWidth * scalingFactor * 0.5f),
          Math.round(coords._2 - image.get.getHeight * scalingFactor * 0.5f - tileSize * 0.2f),
          Math.round(image.get.getWidth * scalingFactor),
          Math.round(image.get.getHeight * scalingFactor),
          imageObserver
        )
    }
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

  def drawHUD(g: Graphics2D, player: Player): Unit = {
    val heartSize = 36
    val heartPadding = 2
    val barPadding = 12
    g.setColor(new Color(0, 0, 0, 150))
    g.fillRect(barPadding, barPadding, (heartSize + heartPadding) * 3 + heartPadding, heartSize + heartPadding * 2)
    g.setColor(new Color(0, 0, 0))
    g.setStroke(new BasicStroke(4))
    g.drawRect(barPadding, barPadding, (heartSize + heartPadding) * 3 + heartPadding, heartSize + heartPadding * 2)
    for (i <- 0 until 3) {
      val image = if i < player.health then ResourceHelper.FULL_HEART else ResourceHelper.DEPLETED_HEART
      g.drawImage(
        image,
        barPadding + heartPadding + i * (heartSize + heartPadding),
        barPadding + heartPadding,
        heartSize,
        heartSize,
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
