package visualizer

import logic.World

import java.awt.event.{KeyListener, MouseListener}
import java.awt.image.ImageObserver
import java.awt.{Canvas, Color, Graphics, Graphics2D, RenderingHints}
import java.util.EventListener
import javax.swing.{JFrame, WindowConstants}

class Visualizer {
  val WINDOW_WIDTH = 800
  val WINDOW_HEIGHT = 800

  val frame = new JFrame()
  frame.setTitle("Rikki")
  frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT)
  frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
  frame.setResizable(false)
  frame.setLocationRelativeTo(null)

  val canvas = new Canvas()
  val renderer = new Renderer(canvas.asInstanceOf[ImageObserver])
  frame.add(canvas)

  def addEventListener(eventListener: EventListener): Unit = {
    eventListener match {
      case e: KeyListener => canvas.addKeyListener(e)
      case e: MouseListener => canvas.addMouseListener(e)
    }
  }

  def render(world: World): Unit = {
    val canvasGraphics = canvas.getGraphics
    if canvasGraphics == null then return
    val canvasImage = canvas.createImage(WINDOW_WIDTH, WINDOW_HEIGHT)
    val graphics = canvasImage.getGraphics.asInstanceOf[Graphics2D]

    //graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    //graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

    renderer.drawStage(graphics, world)
    renderer.drawSprites(graphics, world)

    canvasGraphics.drawImage(canvasImage, 0, 0, canvas)
  }
}
