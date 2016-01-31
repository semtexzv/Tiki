package com.semtexzv.tiki.Map

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.{Color, Texture, Pixmap}
import com.badlogic.gdx.math.RandomXS128
import com.semtexzv.tiki.util.SimplexNoise
import com.semtexzv.tiki.{ Game}
import net.dermetfan.utils.math.Noise

/**
  * Created by Semtexzv on 1/29/2016.
  */

class MapGen(val w:Int, val h:Int) {
  /*

  var map: Array[Int] = new Array[Int](w * h)

  var pixmap = new Pixmap(w, h, Pixmap.Format.RGB888)
  var texture: Texture = null
  var heights: Array[Float] = new Array[Float](w)

  var noise: SimplexNoise = null
  var random: RandomXS128 = null

  val bedrockBegin = 0
  val bedrockHeight = h / 16

  val rockBegin = bedrockBegin + bedrockHeight
  val rockHeight = h / 2
  val rockFade = h / 8

  val groundBegin = rockBegin + rockHeight + rockFade
  val groundHeight = h / 4

  val skyBegin = groundBegin + groundHeight

  val Air = 0
  val Dirt = 1
  val Rock = 2
  val Bedrock = 3
  val Cave = 4
  val Chest = 5
  val Ore = 6

  val colors = new Array[Color](64)
  colors(Air) = Color.WHITE
  colors(Dirt) = Color.BROWN
  colors(Rock) = Color.GRAY
  colors(Bedrock) = Color.DARK_GRAY
  colors(Cave) = Color.RED
  colors(Chest) = Color.BLACK
  colors(Ore) = Color.GOLD

/*
  def generateTerrariaLike(seed: Long): Unit = {
    noise = new SimplexNoise(seed)
    random = new RandomXS128(seed)

    //Lay down Bedrock, Rock and Dirt
    for (x <- 0 until w) {
      //Normal perlin is from -1 to 1
      val yBedrock = bedrockBegin + bedrockHeight * (noise.octavedNoise(x / 32f, 8, 8, 0.4f))
      val yRock = rockBegin + rockHeight + rockFade * (noise.octavedNoise(x / 64f, 4, 8, 0.5f))
      val yDirt = groundBegin + groundHeight * (noise.octavedNoise(x / 1024f, 1, 8, 0.6f))
      for (y <- 0 until h) {
        map(index(x, y)) =
          if (y < yBedrock) {
            Bedrock
          }
          else if (y < yRock) {
            if (noise.octavedNoise(x / 32f, y / 32f, 8, 0.4f) < 0.25f) {
              Dirt
            }
            else Rock
          }
          else if (y < yDirt) {
            if (noise.octavedNoise(1 + x / 16f, 1 + y / 32f, 8, 0.45f) < 0.3f) {
              Rock
            } else Dirt
          } else Air

      }
    }
    for (x <- 0 until w) {
      for (y <- bedrockBegin until skyBegin.toInt) {
        var alpha = if (y > rockBegin) (rockBegin + rockHeight * 8 - y) / (rockHeight.toFloat * 8f) else 1
        val ore = (1 - noise.octavedNoise(-x / 16f, -y / 16f, 16, 0.4f)) * alpha
        if (map(index(x, y)) != Air && map(index(x, y)) != Bedrock) {
          if (ore > 0.70f)
            map(index(x, y)) = Ore
        }
      }
    }
    //Cave Generation
    for (x <- 0 until w) {
      for (y <- bedrockBegin until (groundBegin + groundHeight).toInt) {
        var alpha = if (y > groundBegin) (groundBegin + groundHeight * 1.5f - y) / (groundHeight.toFloat * 1.5f) else 1
        val cave = (1 - noise.ridgedNoise(x / 80f, y / 64f, 16, 0.3f)) * alpha
        val cave2 = noise.octavedNoise(-x / 40f, -y / 32f, 16, 0.15f) * alpha
        if (cave > 0.82f || cave2 > 0.66f) {
          if (map(index(x, y)) != Bedrock) {
            map(index(x, y)) = Air
          }
        }
      }
    }
    //Todo, create parametrized way to place ores, parameters will be minHeight,MaxHeight, clumpSize,amount
    //Placing objects
    //Todo, rework , so that we just create x places for random objects to spawn, then distribute all our objects randombly into those spaces
    var chests = 50
    while (chests > 0) {
      val rx = random.nextInt(w)
      var ry = rockBegin + random.nextInt(h - (rockHeight) + rockFade)
      if (map(index(rx, ry)) == Air) {
        while (map(index(rx, ry - 1)) == Air) {
          ry -= 1
        }
        if (map(index(rx, ry - 1)) != Air && map(index(rx, ry - 1)) != Chest && map(index(rx, ry + 1)) == Air) {
          map(index(rx, ry)) = Chest
          chests -= 1
        }
      }

    }
  }
  */




  val minSteps = 10
  val maxSteps = 20

  val dirChangeCount = 2
  val crawlCount = 5

  val minRoomW = 5
  val maxRoomW = 8
  val minRoomH = 3
  val maxRoomH = 6


  final val Left = 0
  final val Right = 1
  final val Up = 2
  final val Down = 3
  def generate(seed: Long): Unit = {
    noise = new SimplexNoise(seed)
    random = new RandomXS128(seed)
    for (x <- 0 until w) {
      for (y <- 0 until h) {
        map(index(x, y)) = Dirt
      }
    }
    var x = w / 2
    var y = h / 2

    for (yy <- -1 until 1) {
      for (xx <- 1 until 1) {
          map(index(x + xx, y + yy)) = Game.Air

      }
    }

    crawl(x, y,crawlCount)

    for (x <- 0 until w) {
      for (y <- (0 until h).reverse) {
        if( map(index(x, y)) == Cave && map(index(x, y-1))==Air){
          map(index(x, y-1)) = Cave
        }

      }
    }

  }


  def crawl(sx: Int, sy: Int,stack:Int): Unit = {
    var x = sx
    var y = sy
    var oldDir = -1
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

    for (i <- 0 until dirChangeCount) {
      val dir = getDir(random, oldDir)
      oldDir = dir
      var count = minSteps + random.nextInt(maxSteps-minSteps)

      for (step <- 0 until count) {
        map(index(x,y))= Air
        if (dir == Up) {
          map(index(x, y)) = Cave
          y += 1
          //Up
        }
        if (dir == Down) {
          map(index(x, y)) = Cave
          y -= 1
          //down
        }
        if (dir == Left) {
          map(index(x, y)) = Air
          x -= 1

          //left
        }
        if (dir == Right) {
          map(index(x, y)) = Air
          x += 1
          //right
        }
      }
      val roomW = minRoomW + random.nextInt( maxRoomW-minRoomW)
      val roomH = minRoomH + random.nextInt( maxRoomH-minRoomH)
      for (yy <- -1 until roomH-1) {
        for (xx <- -roomW until roomW) {
          if(map(index(x+xx,y+yy)) == Dirt) {
            map(index(x + xx, y + yy)) = Game.Air
          }

        }
      }

    }
    if(stack > 0){
      crawl(x,y,stack-1)
    }
    else{
      map(index(x,y)) = Chest
    }
    //generate small room

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



*/
}
