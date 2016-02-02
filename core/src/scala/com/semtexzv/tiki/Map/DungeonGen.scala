package com.semtexzv.tiki.Map

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.{Color, Texture, Pixmap}
import com.badlogic.gdx.math.{Vector2, RandomXS128}
import com.semtexzv.tiki.Game
import com.semtexzv.tiki.Map.BlockType._
import com.semtexzv.tiki.util.SimplexNoise


/**
  * Created by Semtexzv on 1/31/2016.
  */

class DungeonGen(rnx:Int,rny:Int) {

  val rw = 10
  val rh = 8

  val w = rnx * rw + 2*rw
  val h = rny * rh + 2*rh

  val sx = rw
  val sy = rh

  //todo, merge constants from game to common structure
  final val Empty : Int= 0
  final val Wall : Int= 1
  final val Crate : Int= 2
  final val Ladder : Int= 3
  final val Wall50: Int = 4
  final val Wall33 : Int= 5
  final val Enemy : Int= 6
  final val Spikes : Int= 7
  final val Spikes50 : Int= 8
  final val Spikes33 : Int= 9
  final val Treasure : Int= 10
  final val Obstacle53: Int = 11
  var colors = new Array[Color](64)

  colors(Empty)= Color.WHITE
  colors(Wall)= Color.GRAY
  colors(Crate) = Color.BLUE
  colors(Ladder)= Color.BROWN
  colors(Wall50)= Color.BLACK
  colors(Wall33)= Color.BLACK
  colors(Enemy)= Color.RED
  colors(Spikes)= Color.RED
  colors(Spikes50)= Color.BLACK
  colors(Spikes33)= Color.BLACK
  colors(Treasure)= Color.GOLD
  colors(Obstacle53)= Color.DARK_GRAY

  var map: Array[Int] = new Array[Int](w * h)
  var levelStart = new Vector2
  var levelExit = new Vector2

  var pixmap = new Pixmap(w, h, Pixmap.Format.RGB888)
  var texture: Texture = null

  var noise: SimplexNoise = null
  var random: RandomXS128 = null


  final val Left = 0
  final val Right = 1
  final val Up = 2
  final val Down = 3

  def yIncr(dir:Int):Int = {
    if(dir == Up) 1 else if(dir == Down) -1 else 0
  }

  def xIncr(dir:Int):Int = {
    if(dir == Left) -1 else if(dir == Right) 1 else 0
  }

  def fillEdges(): Unit = {
    for (y <- 0 until h; x <- 0 until w) {
      if (x < sx || x > (w - sx - 1) || y < sy || y > (h - sy - 1)) {
        if (map(index(x, y)) == Empty) map(index(x, y)) = Wall
      }
    }
  }

  def extendLadders(): Unit = {
    for (y <- (0 until h).reverse ; x <- 0 until w) {
      if (map(index(x, y)) == Ladder && map(index(x, y+yIncr(Down))) == Empty) {
        map(index(x, y +yIncr(Down))) = Ladder
      }
    }
  }
  def placeChanceBlocks(): Unit = {
    for (y <- 0 until h; x <- 0 until w) {
      if ((map(index(x, y)) == Wall50 && testChance(50)) ||
        (map(index(x, y)) == Wall33 && testChance(33))) {
        map(index(x, y)) = Wall
      } else if ((map(index(x, y)) == Spikes50 && testChance(50)) ||
        (map(index(x, y)) == Spikes33 && testChance(33))) {
        map(index(x, y)) = Spikes
      }
    }
  }
  def testChance(chance:Int): Boolean ={
     1 + random.nextInt(101) < chance
  }
  def validateMap(): Boolean ={
    map.forall( a=> a!= Obstacle53 && a!= Wall33 && a != Wall50 && a != Spikes33 && a!= Spikes50)
    //todo add check for one exit and one start
  }
  class Room(var x: Int, var y: Int, var parent:Room,var cost: Int);

  var rooms: Array[Room] = null
  var startRoom: Room = null
  var endRoom: Room = null

  def ri(x: Int, y: Int) = x + y * rny

  def getDirs(rx:Int,ry:Int): Array[Boolean]= {
    val res = (0 until 4).map(i =>
      rx + xIncr(i) >= 0 &&
        rx + xIncr(i) < rnx &&
        ry + yIncr(i) >= 0 &&
        ry + yIncr(i) < rny && //Make sure we are still in boundaries
        rooms(ri(rx + xIncr(i), ry + yIncr(i))) == null // Make sure there is empty room
    ).toArray
    res(Up) = false; // we don't wanna go up, this means that our path will always be sideways /down
    res
  }

  def goRoom(parent: Room): Unit = {
    if (parent != null) {
      val rx = parent.x
      val ry = parent.y
      var dirs =getDirs(rx,ry)

      while (dirs.count(_ == true) != 0) {
        var dir = random.nextInt(4)
        while (dirs(dir) != true) {
          dir = random.nextInt(4)
        }
        var nx = rx + xIncr(dir)
        var ny = ry + yIncr(dir)
        val room = new Room(nx,ny, parent,parent.cost + 1)
        rooms(ri(nx, ny)) = room
        goRoom(room)
        dirs = getDirs(rx,ry)
      }
    }
  }



  def generate(seed: Long): Unit = {
    noise = new SimplexNoise(seed)
    random = new RandomXS128(seed)
    map = Array.fill(w*h)(0)

    val rx = random.nextInt(rnx)
    val ry = rny-1 // we start at the top
    rooms = Array.fill(rnx * rny)(null)

    startRoom = new Room(rx, ry,null, 0)

    rooms(ri(rx, ry)) = startRoom
    goRoom(startRoom)

    rooms.filter(_!=null).foreach(a=>
      placeCorrectTemplate(a.x,a.y)
    )
    placeChanceBlocks()
    extendLadders()
    fillEdges()
    if(!validateMap()){
      throw new Exception("Map invalid")
    }
      //Todo, proper level start and end search
    val lsx = sx+startRoom.x * rw +rw/2
    val lsy = sy+startRoom.y * rh +rh/2
    levelStart.set(lsx,lsy)
    placeBlockBuffer(lsx-1,lsy-1,3,3,startBuf,false)

  }

  def index(x: Int, y: Int) = x + w * y

  def preRender(): Unit = {
    for (y <- 0 until h; x <- 0 until w) {
      var m = map(index(x, h-y-1))
      var col: Color = colors(m)
      pixmap.drawPixel(x, y, Integer.reverseBytes(col.toIntBits))
    }

    pixmap.drawPixel(levelStart.x.toInt,h -1- levelStart.y.toInt, Integer.reverseBytes(Color.PINK.toIntBits))
    pixmap.drawPixel(levelExit.x.toInt, h -1- levelExit.y.toInt, Integer.reverseBytes(Color.GREEN.cpy().lerp(Color.WHITE,0.2f).toIntBits))

    texture = new Texture(pixmap, Pixmap.Format.RGB888, false)
  }

  def render(batch: SpriteBatch): Unit = {
    batch.draw(texture, -w / 2, -h / 2, w, h)
  }

  def placeCorrectTemplate(x:Int,y:Int): Unit = {
    var room = rooms(ri(x, y))

    val leftConnected: Boolean =
      (room.parent != null &&
        room.parent.x == x + xIncr(Left)) ||
        rooms.exists(a =>  a!= null && a.parent == room && a.x == x + xIncr(Left))

    val rightConnected: Boolean = (
      room.parent != null &&
      room.parent.x == x + xIncr(Right)) ||
      rooms.exists(a =>  a!= null && a.parent == room && a.x == x + xIncr(Right))

    val botConnected: Boolean = (
      room.parent != null &&
      room.parent.y  == room.y + yIncr(Down)) ||
      rooms.exists(a =>  a!= null && a.parent == room && a.y == room.y + yIncr(Down))

    var ttyp = 0
    var tflip = false
    if (botConnected) {
      if (leftConnected && rightConnected) {
        ttyp =3
        tflip = random.nextBoolean()
        //both sides
      } else if (leftConnected && !rightConnected) {
        ttyp = 2
        tflip = false
        // only left
      } else if (!leftConnected && rightConnected) {
        ttyp = 2
        tflip = true
        //only right
      }
    }
    else {
      // no bot conenction
      if (leftConnected && rightConnected) {
        ttyp = 1
        tflip = random.nextBoolean()
        //both sides
      } else if (leftConnected && !rightConnected) {
        ttyp = 0
        tflip = false
        // only left
      } else if (!leftConnected && rightConnected) {
        ttyp = 0
        tflip = true
        //only right
      }
    }
    var templ :RoomTemplate=  templates(ttyp)(random.nextInt(templates(ttyp).length))
    placeBlockBuffer(sx+x*rw,sy+y*rh,rw,rh,templ.blocks,tflip)
  }

  //Warning !!! w ,h must be dimensions of the buffer
  def placeBlockBuffer(x:Int,y:Int,w:Int,h:Int,buf:Array[Int],flipX:Boolean) {
    for (yy <- 0 until h; xx <- 0 until w) {
      val dx = x + xx
      val dy = y + yy

      val bx = if (flipX) w - xx - 1 else xx
      val by = h-yy -1// block buffer is from player, must reverse Y
      map(index(dx, dy)) = buf(bx + by * w)
    }
  }
  var startBuf = Array( 3,0,3, 3,0,3, 1,1,1)

  var t0 = new RoomTemplate(
      "1 1 1 1 0 0 0 0 0 0 " +
      "1 0 1 E 0 0 0 0 0 0 " +
      "1 0 1 1 1 0 L 0 T 0 " +
      "1 0 1 1 1 1 L 1 1 1 " +
      "1 0 0 0 0 0 L 0 1 0 " +
      "0 0 0 1 1 1 1 0 1 0 " +
      "0 0 0 2 0 T 0 0 0 0 " +
      "1 1 1 1 1 1 1 1 1 1 ",0)

  var t1 = new RoomTemplate(
    """
      |1 0 0 0 0 0 0 0 0 0
      |1 1 1 0 0 0 0 0 T 0
      |1 1 1 1 0 0 0 0 1 0
      |1 1 T 0 0 0 1 0 E 0
      |1 1 1 1 L 1 1 1 1 0
      |0 0 0 0 L 0 0 0 0 0
      |0 0 1 0 L 0 1 0 0 0
      |1 1 1 1 1 1 1 1 1 1
    """.stripMargin,1)

  var t2 = new RoomTemplate(
    """
      |1 0 0 0 L 1 0 0 0 0
      |1 0 0 0 L 0 0 0 0 0
      |1 E 0 0 L 0 0 1 T 0
      |1 1 1 1 L 0 1 1 1 0
      |1 1 0 0 L 0 0 T 0 0
      |0 0 5 0 L 1 1 1 1 0
      |0 0 1 L 5 0 0 0 0 0
      |1 1 1 1 0 0 1 1 1 1
    """.stripMargin,2)

  var t3 = new RoomTemplate(
    """
      |1 1 0 1 0 0 1 1 0 0
      |1 0 0 0 0 1 0 0 1 0
      |0 L 0 1 0 0 0 T 1 0
      |1 L 1 1 1 1 1 0 1 0
      |1 L 0 0 0 1 0 0 T 0
      |0 L 0 0 5 1 L 1 1 0
      |0 L 0 0 0 L L 0 5 0
      |1 1 1 1 0 0 1 1 1 1
    """.stripMargin,3)
/*
var t0 = new RoomTemplate(
    "1 0 0 0 0 0 0 0 0 1 " +
    "1 0 0 0 0 0 0 0 0 1 " +
    "1 0 0 1 1 1 0 0 0 1 " +
    "1 0 0 1 0 1 0 0 0 1 " +
    "1 0 0 1 1 1 0 0 0 1 " +
    "0 0 0 0 0 0 0 0 0 1 " +
    "0 0 0 0 0 0 0 0 0 1 " +
    "1 1 1 1 1 1 1 1 1 1 ",0)

  var t1 = new RoomTemplate(
    "1 0 0 0 0 0 0 0 0 1 " +
      "1 0 0 0 0 1 0 0 0 1 " +
      "1 0 0 0 1 1 0 0 0 1 " +
      "1 0 0 1 0 1 0 0 0 1 " +
      "1 0 0 0 0 1 0 0 0 1 " +
      "0 0 0 0 0 1 0 0 0 0 " +
      "0 0 2 0 0 0 0 0 0 0 " +
      "1 1 1 1 1 1 1 1 1 1 ",1)

  var t2 = new RoomTemplate(
    """
      |1 0 0 0 0 0 0 0 0 1
      |1 0 0 0 0 0 0 0 0 1
      |1 0 0 0 1 1 0 0 0 1
      |1 0 0 1 0 1 0 0 0 1
      |1 0 0 0 0 1 0 0 0 1
      |0 0 0 0 1 1 1 1 0 1
      |0 0 0 0 0 0 0 0 0 1
      |1 1 1 1 0 0 1 1 1 1
    """.stripMargin,2)

  var t3 = new RoomTemplate(
    """
      |1 0 0 0 0 0 0 0 0 1
      |1 0 0 1 1 1 1 0 0 1
      |1 0 0 1 0 0 0 0 0 1
      |1 0 0 1 1 1 1 0 0 1
      |1 0 0 1 0 0 0 0 0 1
      |0 0 0 1 1 0 0 0 0 0
      |0 0 0 0 0 0 0 0 0 0
      |1 1 1 1 0 0 1 1 1 1
    """.stripMargin,3)
*/

  val templates : Array[Array[RoomTemplate]] = Array(Array(t0),Array(t1),Array(t2),Array(t3))

  final val Template0 = 0
  final val Template1 = 1
  final val Template2 = 2
  final val Template3 = 3
  class RoomTemplate(level: String,typ:Int) {
    var blocks : Array[Int] = null
    parseTemplate()
    def parseTemplate(): Unit = {
     blocks = level.toUpperCase.split(Array(' ','\n','\r','\t')).filter(a => a!= "").map(
       _ match {
          case "0" => Empty
          case "1" => Wall
          case "2" => Crate
          case "L" => Ladder
          case "3" => Wall50
          case "4" => Wall33
          case "E" => Enemy
          case "5" => Spikes
          case "6" => Spikes50
          case "7" => Spikes33
          case "T" => Treasure
          case "O" => Obstacle53
          case _ => Empty
        })

      if(blocks.length != rw*rh){
        throw  new Exception("Wrong room size"+blocks.length);
      }
    }
    /* Room types
    0 - One Side Exit- In template L , can be flipped to be R
    1 - Both sides Exit
    2 - One Side + Down - can be flipped along x
    3 - Both Sides and down,
        Top edge is normally empty,

    -- Exits - Normally , side exits are at lowest 2  unfilled
    Standart layout
    * * * * * * * * * *  - Only lower and left edges are filled, ? todo , figure out if its a good idea to lea
    *                 *  - Top edge is almost never filled
    *                 *
    *                 *
    *                 *
    *                 *
    *                 *
    * * * * * * * * * *
    - type 0 - Exits are at least 1 lowest block above ground ( the lowest layer )
    * * 0 0 0 0 0 0 * *
    *                 *
    *  T S            *
    *  * * * L        *
    *                 *
          * * * * L   *
                    * *
    * * * * * * * * * *
    - type 1 - Exits are at least 1 lowest block above ground ( the lowest layer )
    * * 0 0 0 0 0 0 * *
    *                 *
    *  T S    * * * L *
    *  * * * L        *
    *                 *
          * * * * L   *

    * * * * * * * * * *
    - type 2 - Central Exit is 2 blocks in center
    * * 0 0 0 0 0 0 * *
    *                 *
    *  T S            *
    *  * * * L        *
    *                 *
          * * * * L   *
                    * *
    * * * * 0 0 * * * *
    - type 3 - Type 1 and 2 combined
    * * 0 0 0 0 0 0 * *
    *                 *
    *  T S    * * * L *
    *  * * * L        *
    *                 *
          * * * * L   *

    * * * * 0 0 * * * *

   */
  }

}
