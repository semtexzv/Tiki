package com.semtexzv.tiki.Map

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.{Color, Texture, Pixmap}
import com.badlogic.gdx.math.{Vector2, RandomXS128}
import com.semtexzv.tiki.Game
import com.semtexzv.tiki.Map.BlockType._
import com.semtexzv.tiki.util.SimplexNoise

import scala.collection.script.Start
import scala.util.Random

/**
  * Created by Semtexzv on 1/31/2016.
  */

class DungeonGen() {

  val sx = 0
  val sy = 0

  val rw = 10
  val rh = 8

  val rnx = 4
  val rny = 4
  val w = rnx * rw + 1
  val h = rny * rh + 1


  final val Empty = 0
  final val Wall = 1
  final val Crate = 2
  final val Ladder = 3
  final val Wall50 = 4
  final val Wall33 = 5
  final val Enemy = 6
  final val Spikes = 7
  final val Spikes50 = 8
  final val Spikes33 = 9
  final val Treasure = 10
  final val Obstacle53 = 11
  var colors = new Array[Color](64)

  colors(Empty)= Color.WHITE
  colors(Wall)= Color.BLUE
  colors(Ladder)= Color.BROWN
  colors(Wall50)= Color.RED
  colors(Wall33)= Color.PINK
  colors(Enemy)= Color.YELLOW
  colors(Spikes)= Color.ORANGE
  colors(Spikes50)= Color.PURPLE
  colors(Spikes33)= Color.CYAN
  colors(Treasure)= Color.GOLD
  colors(Obstacle53)= Color.GRAY

  var map: Array[Int] = new Array[Int](w * h)
  var coinSpawnPoints: scala.collection.mutable.Set[Vector2] = scala.collection.mutable.Set[Vector2]()

  var pixmap = new Pixmap(w, h, Pixmap.Format.RGB888)
  var texture: Texture = null

  var noise: SimplexNoise = null
  var random: RandomXS128 = null


  final val Left = 0
  final val Right = 1
  final val Up = 2
  final val Down = 3

  def yIncr(dir:Int):Int = {
    if(dir == Up) -1 else if(dir == Down) 1 else 0
  }
  def xIncr(dir:Int):Int = {
    if(dir == Left) -1 else if(dir == Right) 1 else 0
  }


  class Room(var x: Int, var y: Int, var cost: Int)
  {

  }


  var rooms: Array[Room] = null
  var start: Room = null
  var end: Room = null

  def ri(x: Int, y: Int) = x + y * rny

  def getDirs(rx:Int,ry:Int): Array[Boolean]={
    (0 until 4).map(i =>
      rx + xIncr(i) >= 0 &&
        rx + xIncr(i) < rnx &&
        ry + yIncr(i) >= 0 &&
        ry + yIncr(i) < rny && //Make sure we are still in boundaries
        rooms(ri(rx+xIncr(i),ry+yIncr(i))) == null // Make sure there is empty room
    ).toArray
  }

  def goRoom(parent: Room): Unit = {
    if (parent != null) {
      val rx = parent.x
      val ry = parent.y
      var dirs =getDirs(rx,ry)

      while (dirs.count(_ == true) != 0) {
        // Value of 2 seems to be producing some dead ends ,but not many
        var dir = random.nextInt(4)
        while (dirs(dir) != true) {
          dir = random.nextInt(4)
        }
        var nx = rx + xIncr(dir)
        var ny = ry + yIncr(dir)
        val room = new Room(nx,ny, parent.cost + 1)
        rooms(ri(nx, ny)) = room
        map(index(nx*rw,ny*rh)) =  Wall33

        goRoom(room)

        dirs = getDirs(rx,ry)
      }
      // Todo , find appropriate template and apply it
    }
  }


  def generate(seed: Long): Unit = {
    noise = new SimplexNoise(seed)
    random = new RandomXS128(seed)
    map = Array.fill(rnx * rw * rny * rh * 2)(0)

    val rx = 0
    val ry = rny - 1
    rooms = Array.fill(rnx * rny)(null)

    start = new Room(rx, ry, 0)
    //carveRoom(sx + rx * rw, sy + ry * rh)
    rooms(ri(rx, ry)) = start
    goRoom(start)
    //extendLadders()
  }


  def index(x: Int, y: Int) = x + w * y

  def preRender(): Unit = {
    for (x <- 0 until w) {
      for (y <- 0 until h) {
        var m = map(index(x,  y ))
        var col: Color = colors(m)
        pixmap.drawPixel(x, y, Integer.reverseBytes(col.toIntBits))
      }
    }
    texture = new Texture(pixmap, Pixmap.Format.RGB888, false)
  }


  def render(batch: SpriteBatch): Unit = {
    batch.draw(texture, -w / 2, -h / 2, w, h)
  }

  var t = new RoomTemplate(
      "1 0 0 0 0 0 0 0 0 0\n" +
      "1 L 0 0 0 2 0 0 0 0\n" +
      "1 L 0 0 0 2 0 0 0 0\n" +
      "1 L 0 0 0 2 0 0 0 0\n" +
      "1 L 0 0 0 2 0 0 0 0\n" +
      "1 L 0 0 0 2 0 0 0 0\n" +
      "1 L 0 0 0 2 0 0 0 0\n" +
      "1 L 0 0 0 2 0 0 0 0\n")

  final
  class RoomTemplate(level: String) {
    /* Level Template language
        0 - Empty
        1 - Wall
        2 - Crate-Pushable L-R
        L - Ladder, all ladders are extended downwards after level has been generated
        2 - 50% Wall
        3 - 33$ Wall
        E - Enemy spawn point - Goomba style enemy
        6 - Spikes
        7 - 50% Spikes
        8 - 33% Spikes
        T - Treasure
        O - 5x3 Obstacle

        --  Template is a string with spaces and newlines, IT is parsed from left to right, from top to bottom
            Levels will also have to be numbered from Top to bottom
     */
    var blocks = parseTemplate()
    true
    def parseTemplate(): Array[Int] = {
      level.toUpperCase.split(Array(' ','\n')).map(a =>{
        println(a)
        a match {
          case "0" => Empty
          case "1" => Wall
          case "L" => Ladder
          case "2" => Wall50
          case "3" => Wall33
          case "E" => Enemy
          case "6" => Spikes
          case "7" => Spikes50
          case "8" => Spikes33
          case "T" => Treasure
          case "O" => Obstacle53
          case _ => Empty
        }
      })
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
