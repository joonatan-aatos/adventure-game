package logic

enum Direction(val vector: (Float, Float)):
  case Up extends Direction((0f, -1f))
  case Down extends Direction((0f, 1f))
  case Left extends Direction((-1f, 0f))
  case Right extends Direction((1f, 0f))
