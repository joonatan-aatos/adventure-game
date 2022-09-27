package visualizer

import logic.World

import java.awt.event.{KeyListener, MouseListener}
import java.awt.image.{BufferedImage, BufferedImageOp, ConvolveOp, ImageObserver, Kernel}
import java.awt.{Canvas, Color, Graphics, Graphics2D, Image, RenderingHints}
import java.util.EventListener
import javax.swing.{JFrame, WindowConstants}

class Visualizer {
  val WINDOW_WIDTH = 800
  val WINDOW_HEIGHT = 800
  val TILE_SIZE = 60

  val frame = new JFrame()
  frame.setTitle("Seikkailupeli")
  frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT)
  frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
  frame.setResizable(false)
  frame.setLocationRelativeTo(null)

  val canvas = new Canvas()
  val camera = new Camera(0f, 0f, WINDOW_WIDTH.toFloat, WINDOW_HEIGHT.toFloat, TILE_SIZE.toFloat)
  val renderer = new Renderer(canvas.asInstanceOf[ImageObserver], camera, TILE_SIZE)
  frame.add(canvas)

  def addEventListener(eventListener: EventListener): Unit = {
    eventListener match {
      case e: KeyListener => canvas.addKeyListener(e)
      case e: MouseListener => canvas.addMouseListener(e)
    }
  }

  def render(world: World, dialog: Option[String]): Unit = {
    val canvasGraphics = canvas.getGraphics
    if canvasGraphics == null then return
    val canvasImage = canvas.createImage(WINDOW_WIDTH, WINDOW_HEIGHT)
    val graphics = canvasImage.getGraphics.asInstanceOf[Graphics2D]

    camera.updatePosition(world.player.xPos, world.player.yPos, world.stage.worldWidth, world.stage.worldHeight)

    renderer.draw(graphics, world)
    if dialog.isDefined then
      renderer.drawDialog(graphics, dialog.get)

    canvasGraphics.drawImage(canvasImage, 0, 0, canvas)
  }
}
