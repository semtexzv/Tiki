package com.semtexzv.tiki.Map

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.{Color, Texture, Pixmap}
import com.badlogic.gdx.math.RandomXS128
import com.semtexzv.tiki.Game
import com.semtexzv.tiki.Map.BlockType._
import com.semtexzv.tiki.util.SimplexNoise

/**
  * Created by Semtexzv on 1/31/2016.
  */
class DungeonGen(val w:Int, val h:Int) {
  var map: Array[Int] = new Array[Int](w * h)

  var pixmap = new Pixmap(w, h, Pixmap.Format.RGB888)
  var texture: Texture = null

  var noise: SimplexNoise = null
  var random: RandomXS128 = null

  final val None = 0
  final val Wall = 1
  final val Ladder = 2
  final val Coin = 3
  final val Exit = 4
  val colors = new Array[Color](64)
  colors(None) = Color.WHITE
  colors(Wall) = Color.BLACK
  colors(Ladder) = Color.BROWN
  colors(Exit) = Color.RED
  colors(Coin) = Color.GOLD


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
  def generate(seed: Long): Unit = {
    noise = new SimplexNoise(seed)
    random = new RandomXS128(seed)
    for (x <- 0 until w) {
      for (y <- 0 until h) {
        map(index(x, y)) = Wall
      }
    }
    val sx = w / 2
    val sy = h / 2



    crawl(sx, sy,crawlCount)

    for (x <- 0 until w) {
      for (y <- (0 until h).reverse) {
        if( map(index(x, y)) == Ladder && map(index(x, y-1))==None){
          map(index(x, y-1)) = Ladder
        }

      }
    }
    for (y <- (0 until h).reverse) {
      for (x <- 0 until w) {
        if( map(index(x, y)) == None ){
          var upSpace = 0
          while (map(index(x,y+upSpace))!=Wall){
            upSpace+=1
          }
          var downSpace = 0
          while (map(index(x,y-downSpace))!=Wall){
            downSpace+=1
          }
          var leftUpSpace = 0
          while (map(index(x-leftUpSpace,y+1))!=Wall){
            leftUpSpace+=1
          }
          var rightUpSpace = 0
          while (map(index(x+rightUpSpace,y+1))!=Wall){
            rightUpSpace+=1
          }
          if(upSpace >= 2 && downSpace == 2 &&leftUpSpace >=3 && rightUpSpace >=3){
            if (random.nextInt(15) <12) {
              map(index(x, y)) = Wall
            }
          }
        }

      }
    }
    var coinsLeft = coinCount
    while (coinsLeft >0){
      var x = random.nextInt(w)
      var y = random.nextInt(h)
      if(map(index(x,y))== None){
        while ( map(index(x,y-1))!= Wall){
          y-=1
        }
        map(index(x,y)) = Coin
        coinsLeft-=1
      }
    }
    for (yyy <- 0 to 2) {
      for (xxx <- -1 to 1) {
        if(map(index(sx+xxx,sy+yyy))==Wall) {
          map(index(sx + xxx, sy + yyy)) = None
        }
      }
    }

  }

  def getDir(random: RandomXS128, oldDir: Int): Int = {
    var dir = random.nextInt(10)
    if(dir <=3){
      dir = Right
    }else if(dir <=6){
      dir = Left
    }else if(dir <=8){
      dir = Up
    } else dir = Down
    while (dir == Left && oldDir == Right || dir == Right && oldDir == Left) {
      dir = getDir(random,oldDir)
    }
    dir
  }

  def crawl(sx: Int, sy: Int,stack:Int): Unit = {
    var x = sx
    var y = sy
    var oldDir = -1


    for (i <- 0 until dirChangeCount) {
      val dir = getDir(random, oldDir)
      oldDir = dir
      var count = minSteps + random.nextInt(maxSteps-minSteps)

      for (step <- 0 until count) {
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
      val roomW = minRoomW + random.nextInt( maxRoomW-minRoomW)
      val roomH = minRoomH + random.nextInt( maxRoomH-minRoomH)
      for (yy <- -1 until roomH-1) {
        for (xx <- -roomW until roomW) {
          if(map(index(x+xx,y+yy)) == Wall) {
            map(index(x + xx, y + yy)) = None
          }

        }
      }

    }
    if(stack > 0){
      crawl(x,y,stack-1)
    }
    else{
      while ( map(index(x,y-1))!= Wall){
        y-=1
      }
      map(index(x,y)) = Exit
    }
    //generate small room

  }


  def getBlockType(mapTileType:Int): BlockType ={
    mapTileType match {
      case Wall => BlockType.Wall
      case _ => null
    }
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
