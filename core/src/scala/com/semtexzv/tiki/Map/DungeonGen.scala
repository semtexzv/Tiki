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
class DungeonGen(val w:Int, val h:Int) {
  var map: Array[Int] = new Array[Int](w * h)
  var coinSpawnPoints : scala.collection.mutable.Set[Vector2] = scala.collection.mutable.Set[Vector2] ()

  var pixmap = new Pixmap(w, h, Pixmap.Format.RGB888)
  var texture: Texture = null

  var noise: SimplexNoise = null
  var random: RandomXS128 = null

  final val None = Game.None
  final val Wall = Game.Wall
  final val Ladder = Game.Ladder
  final val Exit = Game.Exit
  val colors = new Array[Color](64)
  colors(Game.None) = Color.WHITE
  colors(Game.Wall) = Color.BLUE
  colors(Game.Ladder) = Color.BROWN
  colors(Game.Exit) = Color.RED

  val minSteps = 10
  val maxSteps = 20

  val dirChangeCount = 2
  val crawlCount = 5

  val minRoomW = 5
  val maxRoomW = 8
  val minRoomH = 4
  val maxRoomH = 8
  val coinCount = 10


  final val Left = 0
  final val Right = 1
  final val Up = 2
  final val Down = 3


  val rw = 8
  val rh = 8

  val sx = w/2
  val sy = h/2

  val rnx = 4
  val rny = 4
  //Divide space into 8 by 4 rooms
  //randomly decide what rooms should be connected
  //



  class Room(var x:Int,var y:Int,var cost:Int){
  }


  var rooms :Array[Room] = null
  var start:Room = null
  var end: Room = null

  def ri(x: Int, y: Int) = x + y * rny


  def goRoom(parent:Room):Unit ={
    if(parent != null){
      val rx = parent.x
      val ry = parent.y
      var dirs = Array(
        rx != 0 && (rooms(ri(rx - 1, ry)) == null), //left
        rx != rnx - 1 && (rooms(ri(rx + 1, ry)) == null), //right
        ry != rny - 1 && (rooms(ri(rx, ry + 1)) == null), //up
        ry != 0 && (rooms(ri(rx, ry - 1)) == null)) //down

      while  (dirs.count(_ == true)>=1) { // Value of 2 seems to be producing some dead ends ,but not many
        var dir = random.nextInt(4)
        while (dirs(dir) != true) {
          dir = random.nextInt(4)
        }
        val xIncr = if (dir == Left) -1 else if (dir == Right) 1 else 0
        val yIncr = if (dir == Down) -1 else if (dir == Up) 1 else 0
        val room = new Room(rx + xIncr,ry+yIncr,parent.cost+1)

        var nx = rx +xIncr
        var ny = ry +yIncr
        rooms(ri(nx,ny)) = room
        dig(sx+parent.x*rw,sy+parent.y*rh,rw,rh,dir)
       // digger(sx+parent.x*rw,sy+parent.y*rh,80)
        goRoom(room)

         dirs = Array(
          rx != 0 && (rooms(ri(rx - 1, ry)) == null), //left
          rx != rnx - 1 && (rooms(ri(rx + 1, ry)) == null), //right
          ry != rny - 1 && (rooms(ri(rx, ry + 1)) == null), //up
          ry != 0 && (rooms(ri(rx, ry - 1)) == null)) //down
      }

    }
  }


  def carveRoom(x:Int,y:Int,w:Int,h:Int): Unit = {
    for (yy <- -h / 2+1 until  h / 2 ) {
      for (xx <- -w / 2+1 until  w / 2  ) {
        map(index(x + xx, y + yy)) = None
      }
    }
  }

  def dig(sx: Int, sy: Int,w:Int,h:Int,dir:Int): Unit = {
    var x = sx
    var y = sy
    val dist = if(dir == Left || dir == Right) w-1 else h-1
    val xdist = if(dir == Left ) -w else if(dir == Right) w else 0
    val ydist = if(dir == Down ) -h else if(dir == Up) h else 0
    carveRoom(x+xdist,y+ydist,w,h)

      for (step <- 0 to dist) {
        map(index(x,y))= None
        if (dir == Up) {
          map(index(x, y)) = Ladder
          y += 1
          //Up
        }
        if (dir == Down) {
          map(index(x, y)) = Ladder
          y -= 1
          //down
        }
        if (dir == Left) {
          map(index(x, y)) = None
          x -= 1
          //left
        }
        if (dir == Right) {
          map(index(x, y)) = None
          x += 1
          //right
        }
      }
    }

  def extendLadders(): Unit ={
    for (x <- 0 until w) {
      for (y <- 0 until h) {
        if(map(index(x,y))== Ladder && map(index(x,y+1)) == None){
          map(index(x,y+1)) = Ladder
        }
      }
      for (y <- (0 until h).reverse) {
        if(map(index(x,y))== Ladder && map(index(x,y-1)) == None){
          map(index(x,y-1)) = Ladder
        }
      }

    }
  }

  def generate(seed: Long): Unit = {
    noise = new SimplexNoise(seed)
    random = new RandomXS128(seed)
    for (x <- 0 until w) {
      for (y <- 0 until h) {
        map(index(x, y)) = Wall
      }

    }
    val rx = 0
    val ry = 0
    rooms = Array.fill(rnx*rny)(null)

    start = new Room(rx, ry, 0)
    carveRoom(sx + rx * rw, sy + ry * rh, rw, rh)
    rooms(ri(rx, ry)) = start
    goRoom(start)
    extendLadders()
    //randomBlocks(50)

    //randomClumps(50)
    //map = automata(5,2)
  }

  def randomPlatforms(count:Int): Unit ={
    //todo
  }
  def randomBlocks(count:Int): Unit ={
    var left = count
    while (left >0){
      var x = random.nextInt(w)
      var y = random.nextInt(h)
      if(map(index(x,y))==None){
        map(index(x,y))=Wall
        left -=1
      }
    }
  }
  def randomClumps(count:Int): Unit ={
    var left = count
    while (left >0){
      var x = random.nextInt(w)
      var y = random.nextInt(h)
      if(map(index(x,y))==None){
        var lblck =  2+random.nextInt(3)
        for (i<- 1 until lblck) {
          map(index(x, y)) = Wall
          if(random.nextBoolean()) {
            x += 1 - random.nextInt(3)
          }else {
            y += 1 - random.nextInt(3)
          }
        }
        left -=1
      }
    }
  }
  def automata(liveCount:Int,dieCount:Int): Array[Int] ={
      var nmap = new Array[Int](w*h)

      for (x <- 0 until w) {
        for (y <- 0 until h) {

          if(  map(index(x,y)) == Wall && countNeigbors(x,y) < dieCount){
            nmap(index(x,y)) = None
          }
          else {
            nmap(index(x,y)) = Wall
          }

          if( map(index(x,y)) == None &&countNeigbors(x,y) > liveCount){
            nmap(index(x,y)) = Wall
          }
          else {
            nmap(index(x, y)) = None
          }

        }
      }
      return nmap
  }

  def digger(sx:Int,sy:Int,moves:Int):Unit = {
    var x :Int = sx
    var y :Int = sy
    for (i <- 0 until moves) {
      var dir = random.nextInt(4)
      x = (sx -rw/2) +((x-rw/2)%(rw))
      y = (sy -rh/2) +((y-rh/2)%(rh))
      if (dir == Up) {
        map(index(x, y)) = None
        y += 1
        //Up
      }
      if (dir == Down) {
        map(index(x, y)) = None
        y -= 1
        //down
      }
      if (dir == Left) {
        map(index(x, y)) = None
        x -= 1
        //left
      }
      if (dir == Right) {
        map(index(x, y)) = None
        x += 1
        //right
      }
    }
  }

  def countNeigbors(x:Int,y:Int):Int ={
    var count = 0

    for (xx <- -1 to 1) {
      for (yy <- -1 to 1) {
        if(map(index(x+xx,y+yy)) == Wall){
          count+=1
        }
      }
    }
    count
  }




  def index(x:Int, y:Int) = (x & w-1) + w* (y & h-1)
  def preRender(): Unit ={
    for (x<-0 until w) {
      for (y <- 0 until h) {
        var m = map(index(x, h - y - 1))
        var col: Color = colors(m)
        pixmap.drawPixel(x,y,Integer.reverseBytes(col.toIntBits))
      }
    }
    texture = new Texture(pixmap,Pixmap.Format.RGB888,false)
  }


  def render(batch:SpriteBatch): Unit ={
    batch.draw(texture,-w/2,-h/2,w,h)
  }


}
