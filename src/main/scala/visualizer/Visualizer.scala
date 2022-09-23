package visualizer

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
}
