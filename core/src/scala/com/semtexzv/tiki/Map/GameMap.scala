package com.semtexzv.tiki.Map

import com.badlogic.gdx.math.RandomXS128
import com.badlogic.gdx.physics.box2d.World
import com.semtexzv.tiki.{SimplexJava, Game}

import scala.collection.immutable.HashSet
import scala.util.Random

/**
  * Created by Semtexzv on 1/27/2016.
  */
class GameMap(world: World) {
  var chunks : Array[Chunk]= new Array[Chunk](64)
  chunks(0)= new Chunk(0)
  chunks(1)= new Chunk(64)

  var noise = new SimplexJava(Random.nextInt())

  def setBlock(x:Int,y:Int,block:Block) = chunks(x/Game.ChunkWidth).setBlock(x,y,block)
  def getBlock(x:Int,y:Int) : Block = {
    val i = x / Game.ChunkWidth
    if (i > 0 && chunks(i) != null) {
      return chunks(x / Game.ChunkWidth).getBlock(x, y)
    }
    null
  }
  var rand = new RandomXS128()

  var heights:Array[Float] = new Array[Float](Game.MapWidth)

  heights(0) = 0.5f
  heights(Game.MapWidth-1) = 0.5f

  genHeight(0,Game.MapWidth-1,1.1f)

  for (x<-0 until Game.MapWidth) {
    for (y <- 0 until Game.MapHeight) {
      var block = if ((y.toFloat / Game.MapHeight)  < heights(x)) {
        new Block(x, y, world)
      } else null

      setBlock(x, y, block)
    }
  }
  def genHeight(leftIndex:Int, rightIndex:Int, displacement:Float) {
    var roughness = 0.4f
    if((leftIndex + 1) == rightIndex) return
    var midIndex = Math.floor((leftIndex + rightIndex) / 2).toInt
    var change = noise.noise(midIndex/4f,0).toFloat * displacement
    heights(midIndex) = (heights(leftIndex) + heights(rightIndex)) / 2f + change
    var newDisp = displacement * roughness
    genHeight(leftIndex, midIndex, newDisp)
    genHeight(midIndex, rightIndex, newDisp)
  }

  /*
    for (x<-0 until Game.MapWidth) {

      val tile = OctavePerlin(x.toFloat/128f,1,32,0.5f)
      for (y <- 0 until Game.MapHeight) {
        var block = if ( (y.toFloat/Game.MapHeight) -0.5f < tile) {
          new Block(x, y, world)
        } else null

        setBlock(x, y, block)
      }
    }*/

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
