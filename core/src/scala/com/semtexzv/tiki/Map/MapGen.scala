package com.semtexzv.tiki.Map

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.{Color, Texture, Pixmap}
import com.badlogic.gdx.math.RandomXS128
import com.semtexzv.tiki.{SimplexJava, Game}
import net.dermetfan.utils.math.Noise

/**
  * Created by Semtexzv on 1/29/2016.
  */
class MapGen(w:Int,h:Int) {

  var map: Array[Int] = new Array[Int](w * h)
  var pixmap = new Pixmap(w, h, Pixmap.Format.RGB888)
  var texture: Texture = null
  var heights: Array[Float] = new Array[Float](w)

  var noise: SimplexJava = null
  var random: RandomXS128 = null

  val bedrockBegin = 0
  val bedrockHeight = 16

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


  def generate(seed: Long): Unit = {
    noise = new SimplexJava(seed)
    random = new RandomXS128(seed)

    //Lay down Bedrock, Rock and Dirt
    for (x <- 0 until w) {
      //Normal perlin is from -1 to 1
      val yBedrock = bedrockBegin + bedrockHeight * (OctavePerlin(x / 32f, 8, 8, 0.4f) )
      val yRock = rockBegin + rockHeight + rockFade * (OctavePerlin(x / 64f, 4, 8, 0.5f) )
      val yDirt = groundBegin + groundHeight * (OctavePerlin(x / 1024f, 1, 8, 0.6f) )
      for (y <- 0 until h) {
        map(index(x, y)) =
          if (y < yBedrock) {
            Bedrock
          }
          else if (y < yRock) {
            if (OctavePerlin(x / 32f, y / 32f, 8, 0.4f) < 0.25f) {
              Dirt
            }
            else Rock
          }
          else if (y < yDirt) {
            if (OctavePerlin(1 + x / 16f, 1 + y / 32f, 8, 0.45f) < 0.3f) {
              Rock
            } else Dirt
          } else Air

      }
    }
    for (x <- 0 until w) {
      for (y <- bedrockBegin until skyBegin.toInt) {
        val ore = OctavePerlin(-x / 16f, -y / 16f, 16, 0.4f)
        if(map(index(x, y)) != Air){
          if(ore < 0.25f)
            map(index(x, y)) = Ore
        }
      }
    }
    //Cave Generation
    for (x <- 0 until w) {
      for (y <- bedrockBegin until (groundBegin + groundHeight).toInt) {
        var alpha = if(y>groundBegin) (groundBegin+groundHeight*1.5f -y)/(groundHeight.toFloat*1.5f) else 1
        val cave = (1-RidgedPerlin(x / 80f, y /  64f, 16, 0.3f))  *alpha
        val cave2 = OctavePerlin(-x / 40f, -y / 32f, 16, 0.15f) * alpha
        if (cave > 0.82f || cave2 > 0.66f) {
          if( map(index(x, y))!= Bedrock) {
            map(index(x, y)) = Air
          }
        }
      }
    }
    //Todo, create parametrized way to place ores, parameters will be minHeight,MaxHeight, clumpSize,amount
    //Placing objects
    //Todo, rework , so that we just create x places for random objects to spawn, then distribute all our objects randombly into those spaces
    var chests = 5000
    while (chests> 0){

      val rx = random.nextInt(w)
      var ry = rockBegin + random.nextInt(h-(rockHeight)+rockFade)
      if(map(index(rx,ry)) == Air){
        while(map(index(rx,ry-1)) == Air){
          ry-=1
        }
        if(map(index(rx,ry-1))!= Air &&map(index(rx,ry-1))!= Chest && map(index(rx,ry+1))==Air) {
          map(index(rx, ry)) = Chest
          chests -= 1
        }
      }

    }
/*
    for (elem <- (0 to 1)) {
      for (x <- 0 until w) {
        for (y <- bedrockBegin + bedrockHeight until (groundBegin + groundHeight * 0.5f).toInt) {
          if (map(index(x, y)) == Cave) {
            if (checkCount(x, y, Cave) < 4) {
              map(index(x, y)) = Dirt
            }
          }
          else{
            if (checkCount(x, y, Cave) >4 ) {
              map(index(x, y)) = Cave
            }
          }

        }
      }
    }
*/
    def checkCount(x: Int, y: Int,block:Int): Int = {
      var count = 0
      for (xx <- -1 until 1) {
        for (yy <- -1 until 1) {
          count += (if (map(index(xx + x, yy + y)) == block) 1 else 0)
        }
      }
      count
    }
  }

  def index(x:Int, y:Int) = x+ w*y

  /* Octaved perlin noise, range is 0-1 */
  def OctavePerlin(x: Double, y: Double, octaves: Int, persistence: Double): Double = {
    var total: Double = 0
    var frequency: Double = 1
    var amplitude: Double = 1
    var maxValue: Double = 0
    var i: Int = 0
    while (i < octaves) {
      {
        total += noise.noise(x * frequency, y * frequency) * amplitude
        maxValue += amplitude
        amplitude *= persistence
        frequency *= 2
      }
      i += 1
      i - 1
    }
    (total / maxValue)/2f+0.5f
  }

  /* Ridged perlin noise, range is 0-1 */
  def RidgedPerlin(x: Double, y: Double, octaves: Int, persistence: Double): Double = {
    var total: Double = 0
    var frequency: Double = 1
    var amplitude: Double = 1
    var maxValue: Double = 0
    var i: Int = 0
    while (i < octaves) {
      {
        total += math.abs(noise.noise(x * frequency, y * frequency) * amplitude)
        maxValue += amplitude
        amplitude *= persistence
        frequency *= 2
      }
      i += 1
      i - 1
    }
    total / maxValue
  }

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
