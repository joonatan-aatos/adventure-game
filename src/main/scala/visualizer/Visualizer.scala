package visualizer

import logic.World

import java.awt.{Canvas, Color, Graphics, Graphics2D, RenderingHints}
import javax.swing.{JFrame, WindowConstants}

class Visualizer {
  val WINDOW_WIDTH = 800
  val WINDOW_HEIGHT = 800
  val TILE_SIZE = 30

  val frame = new JFrame()
  frame.setTitle("Rikki")
  frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT)
  frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
  frame.setResizable(false)
  frame.setLocationRelativeTo(null)

  val canvas = new Canvas()
  frame.add(canvas)

  def render(world: World): Unit = {
    val canvasGraphics = canvas.getGraphics
    if canvasGraphics == null then return
    val canvasImage = canvas.createImage(WINDOW_WIDTH, WINDOW_HEIGHT)
    val graphics = canvasImage.getGraphics.asInstanceOf[Graphics2D]

    //graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    //graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

    drawSprites(graphics, world)

    canvasGraphics.drawImage(canvasImage, 0, 0, canvas)
  }

  private def drawSprites(g: Graphics2D, world: World): Unit = {
    g.setColor(Color.RED)
    for (sprite <- world.getSprites) {
      g.fillRect(
        Math.round((sprite.xPos - 0.5f) * TILE_SIZE),
        Math.round((sprite.yPos - 0.5f) * TILE_SIZE),
        TILE_SIZE,
        TILE_SIZE
      )
    }
  }
}
