package visualizer

import java.awt.image.BufferedImage

class Animation(val frames: Vector[BufferedImage], val delay: Int) {
  private var frameIndex = 0
  private var timer = 0

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
