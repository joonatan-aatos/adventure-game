package logic

import play.api.libs.json.{JsValue, Json, Reads}

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import scala.collection.mutable.ArrayBuffer
import scala.io.Source

class Tile(val pos: (Int, Int), val src: (Int, Int)) {
  override def toString: String = f"pos: $pos, src: $src"
}

class Stage {

  private def parseTile(tileJSON: JsValue): Tile = {
    val px = tileJSON("px").as[Seq[Int]]
    val src = tileJSON("src").as[Seq[Int]]
    Tile((px.head/16, px(1)/16), (src.head/16, src(1)/16))
  }

  // Load world.ldtk
  private val worldInputStream = getClass.getResourceAsStream("/world/world.ldtk")
  private val worldString = Source.fromInputStream(worldInputStream).mkString

  // Parse world file
  private val worldJSON = Json.parse(worldString)
  private val layerInstancesJSON = worldJSON("levels")(0)("layerInstances")

  // Tiles (only visual)
  private val tilesLayer1JSON = layerInstancesJSON(2)("gridTiles").as[Seq[JsValue]]
  private val tilesLayer2JSON = layerInstancesJSON(1)("gridTiles").as[Seq[JsValue]]
  val tilesLayer1: Vector[Tile] = tilesLayer1JSON.map((tile: JsValue) => parseTile(tile)).toVector
  val tilesLayer2: Vector[Tile] = tilesLayer2JSON.map((tile: JsValue) => parseTile(tile)).toVector

  // Collision map (effects player movement)
  private val collisionMapJSON = layerInstancesJSON(0)
  private val collisionMapSeq = collisionMapJSON("intGridCsv").as[Seq[Int]]
  private val collisionMapWidth = collisionMapJSON("__cWid").as[Int]
  private val collisionMapHeight = collisionMapJSON("__cHei").as[Int]
  val collisionMap: Vector[Vector[Boolean]] = {
    val map = ArrayBuffer[Vector[Boolean]]()
    for (x <- 0 until collisionMapWidth) {
      val column = ArrayBuffer[Boolean]()
      for (y <- 0 until collisionMapHeight) {
        column.append(collisionMapSeq(y * collisionMapWidth + x) == 0)
      }
      map.append(column.toVector)
    }
    map.toVector
  }
  val worldWidth: Int = collisionMapWidth / 2
  val worldHeight: Int = collisionMapHeight / 2

  // Size of a tile in the collision map is half a tile
  def canBeInPosition(xPos: Float, yPos: Float): Boolean = collisionMap(math.floor(xPos*2).toInt)(math.floor(yPos*2).toInt)
}
