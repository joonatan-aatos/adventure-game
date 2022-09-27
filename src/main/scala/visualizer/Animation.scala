package visualizer

import java.awt.image.BufferedImage

/**
 * The Animation class represents a single animation.
 * If multiple sprites are using the same animation, a copy of the animation should
 * be made for each sprite using the constructor Animation(animation: Animation)
 * @param frames The frames the animation consists of
 * @param delay How long each frame is shown (measured in game ticks)
 */
class Animation(val frames: Vector[BufferedImage], val delay: Int) {
  private var frameIndex = 0
  private var timer = 0

  def this(animation: Animation) =
    this(animation.frames, animation.delay)

  def getFrame: BufferedImage = {
    if delay == 0 then return frames(0)
    timer += 1
    if timer > delay then
      timer = 0
      frameIndex = if frameIndex == frames.length - 1 then 0 else frameIndex + 1
    frames(frameIndex)
  }

  def reset(): Unit = {
    frameIndex = 0
    timer = 0
  }
}
