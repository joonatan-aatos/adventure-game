package visualizer

class Camera(var xPos: Float, var yPos: Float, val windowWidth: Float, val windowHeight: Float, tileSize: Float) {
  private val cameraVelocity = 0.06f
  private val moveThreshold = tileSize * 0.5f

  def updatePosition(targetXPos: Float, targetYPos: Float, stageWidth: Int, stageHeight: Int): Unit = {
    val targetCameraPosition = (
      targetXPos * tileSize - windowWidth / 2f,
      targetYPos * tileSize - windowHeight / 2f
    )

    // X-Position
    if xPos - moveThreshold < targetCameraPosition._1 && targetCameraPosition._1 > xPos + moveThreshold then
      xPos += cameraVelocity * (targetCameraPosition._1 - xPos - moveThreshold)
    else if xPos - moveThreshold > targetCameraPosition._1 && targetCameraPosition._1 < xPos + moveThreshold then
      xPos += cameraVelocity * (targetCameraPosition._1 - xPos + moveThreshold)

    // Y-Position
    if yPos - moveThreshold < targetCameraPosition._2 && targetCameraPosition._2 > yPos + moveThreshold then
      yPos += cameraVelocity * (targetCameraPosition._2 - yPos - moveThreshold)
    else if yPos - moveThreshold > targetCameraPosition._2 && targetCameraPosition._2 < yPos + moveThreshold then
      yPos += cameraVelocity * (targetCameraPosition._2 - yPos + moveThreshold)

    // Make sure the camera doesn't go out of bounds
    val maxXPos = stageWidth * tileSize - windowWidth
    val maxYPos = stageHeight * tileSize - windowHeight
    if xPos < 0 then xPos = 0
    else if xPos > maxXPos then xPos = maxXPos
    if yPos < 0 then yPos = 0
    else if yPos > maxYPos then yPos = maxYPos
  }
}
