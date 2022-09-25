package logic

import play.api.libs.json.{JsValue, Json, Reads}

import scala.collection.mutable.ArrayBuffer
import scala.io.Source

class Stage {
  class Tile(val pos: (Int, Int), val src: (Int, Int)) {
    override def toString: String = f"pos: $pos, src: $src"
  }

  private def parseTile(tileJSON: JsValue): Tile = {
    val px = tileJSON("px").as[Seq[Int]]
    val src = tileJSON("src").as[Seq[Int]]
    Tile((px.head/16, px(1)/16), (src.head/16, src(1)/16))
  }

  private val worldInputStream = getClass.getResourceAsStream("/world/world.ldtk")
  private val worldString = Source.fromInputStream(worldInputStream).mkString
  private val worldJSON = Json.parse(worldString)
  private val layerInstancesJSON = worldJSON("levels")(0)("layerInstances")
  private val tilesJSON = layerInstancesJSON(0)("gridTiles").as[Seq[JsValue]]
  val tiles: Vector[Tile] = tilesJSON.map((tile: JsValue) => parseTile(tile)).toVector
  val worldWidth = 20
  val worldHeight = 20
}
