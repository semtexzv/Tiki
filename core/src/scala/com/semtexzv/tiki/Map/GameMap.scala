package com.semtexzv.tiki.Map

import com.badlogic.gdx.physics.box2d.World
import com.semtexzv.tiki.{SimplexJava, Game}

import scala.util.Random

/**
  * Created by Semtexzv on 1/27/2016.
  */
class GameMap(world: World) {
  var blocks  = new Array[Block](Game.MapWidth*Game.MapHeight)

  var noise = new SimplexJava(Random.nextInt())
  def setBlock(x:Int,y:Int,value:Block) = blocks(y*Game.MapWidth+x) = value
  def getBlock(x:Int,y:Int) : Block = {
    val i = y*Game.MapWidth+x
    if(i<blocks.length&& i>=0)
      blocks(y*Game.MapWidth+x)
    else
      null
  }
  for (x<-0 until Game.MapWidth) {

    val tile = OctavePerlin(x.toFloat/ Game.MapWidth*3,1,32,0.5f)
    for (y <- 0 until Game.MapHeight) {
      var block = if ( (y.toFloat/Game.MapHeight) -0.5f < tile) {
        new Block(x, y, world)
      } else null

      setBlock(x, y, block)
    }
  }

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
    total / maxValue
  }
  def scaledNoise(x: Int, y: Int, res: Double): Double = {
     noise.noise(x / res.toDouble, y / res.toDouble)
  }


}
