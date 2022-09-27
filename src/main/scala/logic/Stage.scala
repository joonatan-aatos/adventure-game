package logic

import play.api.libs.json.{JsValue, Json, Reads}

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import scala.collection.mutable.ArrayBuffer
import scala.io.Source

/**
 * The Tile class represents a single tile in a stage.
 * @param pos
 * @param src
 */
class Tile(val pos: (Int, Int), val src: (Int, Int)) {
  override def toString: String = f"pos: $pos, src: $src"
}

/**
 * The Stage class loads and parses the world file that is in JSON format.
 * It also acts as an interface for interacting with the stage.
 */
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
  private val tilesLayer1JSON = layerInstancesJSON(3)("gridTiles").as[Seq[JsValue]]
  private val tilesLayer2JSON = layerInstancesJSON(2)("gridTiles").as[Seq[JsValue]]
  val tilesLayer1: Vector[Tile] = tilesLayer1JSON.map((tile: JsValue) => parseTile(tile)).toVector
  val tilesLayer2: Vector[Tile] = tilesLayer2JSON.map((tile: JsValue) => parseTile(tile)).toVector

  // Collision map (effects player movement)
  private val collisionMapJSON = layerInstancesJSON(1)
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

  // Entities (Defines spawn points for entities)
  private val entitiesJSON = layerInstancesJSON(0)
  private val entityInstancesSeq = entitiesJSON("entityInstances").as[Seq[JsValue]]
  val entities: Vector[(String, (Int, Int), Option[Any])] = {
    val entitiesArray = ArrayBuffer[(String, (Int, Int), Option[Any])]()
    for (entityJSON <- entityInstancesSeq) {
      val entityType = entityJSON("__identifier").as[String]
      val positionSeq = entityJSON("__grid").as[Seq[Int]]
      var additionalData: Option[Any] = None
      // Find additional data
      if entityType == "Npc" then {
        // Read the npc's name and dialog
        val fieldInstances = entityJSON("fieldInstances").as[Seq[JsValue]]
        var name = ""
        var dialog = Vector[String]()
        for (field <- fieldInstances) {
          val identifier = field("__identifier").as[String]
          identifier match {
            case "Name" =>
              name = field("__value").as[String]
            case "Dialog" =>
              dialog = field("__value").as[Seq[String]].toVector
          }
        }
        additionalData = Option((name, dialog))
      }
      val entity: (String, (Int, Int), Option[Any]) = (entityType, (positionSeq.head, positionSeq(1)), additionalData)
      entitiesArray += entity
    }
    entitiesArray.toVector
  }

  // Size of a tile in the collision map is half a tile
  def canBeInPosition(xPos: Float, yPos: Float): Boolean = collisionMap(math.floor(xPos*2).toInt)(math.floor(yPos*2).toInt)
}
