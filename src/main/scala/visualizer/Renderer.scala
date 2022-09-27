package visualizer

import logic.{Bat, Direction, Npc, Player, Sprite, Stage, Tile, World}
import visualizer.ResourceHelper.{batIdleMap, batRunningMap, npcIdleMap, playerAttackingMap, playerIdleMap, playerRunningMap, playerTakingDamageMap}

import java.awt.image.{BufferedImage, ImageObserver}
import java.awt.{BasicStroke, Color, Font, Graphics2D, RenderingHints}
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
      // Special check for rendering tree stumps always below the player
      if tile.src._2 == 11 && tile.src._1 >= 3 && tile.src._1 <= 6 then tilesBelowPlayer.append(tile)
      else tilesAbovePlayer.append(tile)
    }
    // Draw all tiles below sprites
    drawTiles(g, tilesBelowPlayer.toVector)
    // Draw sprites
    drawSprites(g, world.sprites)
    // Draw all tiles above sprites
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
    val animation = new Animation({
      if player.dyingTimer != 0 then ResourceHelper.PLAYER_DYING
      else animationMap(player.facingDirection)
    })
    if animation.frames != playerAnimation.frames then
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
    val sortedSprites = sprites.sortBy(_.yPos)
    for (sprite <- sortedSprites) {
      var image: Option[BufferedImage] = None
      sprite match
        case bat: Bat =>
          val animationMap = {
            if bat.target.isDefined then batRunningMap
            else batIdleMap
          }
          val animationDefined = spriteAnimations.contains(bat)
          val animation = new Animation(animationMap(bat.facingDirection))
          val previousAnimation = if animationDefined then spriteAnimations(bat) else animation
          if animation.frames != previousAnimation.frames || !animationDefined then
            previousAnimation.reset()
            spriteAnimations += (bat -> animation)
          image = Option(previousAnimation.getFrame)
        case npc: Npc =>
          val animationMap = npcIdleMap
          val animationDefined = spriteAnimations.contains(npc)
          val animation = new Animation(animationMap(npc.facingDirection))
          val previousAnimation = if animationDefined then spriteAnimations(npc) else animation
          if animation.frames != previousAnimation.frames || !animationDefined then
            previousAnimation.reset()
            spriteAnimations += (npc -> animation)
          image = Option(previousAnimation.getFrame)
        case player: Player => drawPlayer(g, player)
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

  def drawDialog(g: Graphics2D, dialog: String): Unit = {
    val dialogMargin = 100
    val dialogHeight = 80
    val textMarginTop = 10
    val textMarginLeft = 20
    // Improve text quality
    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB)
    // Draw black box
    g.setColor(new Color(0, 0, 0, 150))
    g.fillRect(
      dialogMargin,
      dialogMargin,
      camera.windowWidth.toInt - dialogMargin * 2,
      dialogHeight
    )
    g.setColor(Color.BLACK)
    g.setStroke(new BasicStroke(6))
    g.drawRect(
      dialogMargin,
      dialogMargin,
      camera.windowWidth.toInt - dialogMargin * 2,
      dialogHeight
    )
    // Draw text
    g.setColor(Color.WHITE)
    g.setFont(new Font("TimesRoman", Font.PLAIN, 20))
    val fontHeight = g.getFontMetrics.getHeight
    val maxLineWidth = camera.windowWidth - 2 * dialogMargin - 2 * textMarginLeft
    var line = ""
    var currentLine = 1
    // Automatically break lines when the line becomes bigger
    val words = dialog.split(" ")
    for (word <- words) {
      val newLineWidth = g.getFontMetrics.getStringBounds(s"$line $word", g).getWidth
      if newLineWidth > maxLineWidth then
        g.drawString(line, dialogMargin + textMarginLeft, dialogMargin + textMarginTop + currentLine * fontHeight)
        line = word
        currentLine += 1
      else
        line = if line.isEmpty then word else s"$line $word"
    }
    g.drawString(line, dialogMargin + textMarginLeft, dialogMargin + textMarginTop + currentLine * fontHeight)
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
