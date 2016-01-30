package com.semtexzv.tiki.Map

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.math.RandomXS128
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d._
import com.semtexzv.tiki.{TileManager, FixtureType, Game}

import scala.util.Random

/**
  * Created by Semtexzv on 1/27/2016.
  */
class GameMap(world: World) {
  var chunks : Array[Chunk]= new Array[Chunk](64)

  val chCount = 4

  var gen = new MapGen(chCount * Game.ChunkWidth,Game.ChunkHeight)
  gen.generate(Random.nextLong())

  for (chx <- 0 until chCount) {
    if (chunks(chx) == null) {
      chunks(chx) = new Chunk(chx * Game.ChunkWidth)
    }
    val chunk = chunks(chx)
    val baseX = chx * Game.ChunkWidth

    for (y <- 0 until Game.ChunkHeight) {
      for (x <- 0 until Game.ChunkWidth) {
        if(gen.map(gen.index(x+baseX,y)) != Game.Air)
          chunk.setBlock(x,y,new Block(x+baseX,y,gen.map(gen.index(x+baseX,y)),this))
      }
    }
    for (y <- 0 until Game.ChunkHeight) {
      for (x <- 0 until Game.ChunkWidth) {
        if(gen.map(gen.index(x+baseX,y)) != Game.Air) {
          updateBlockTexture(x+baseX, y)
        }
      }
    }
  }

  def updateBlockTexture(x:Int,y:Int): Unit = {
    var chunk = getChunk(x)
    var block = chunk.getBlock(x, y)
    if (block != null) {
      var state = 0
      for (yy <- -1 to 1) {
        for (xx <- -1 to 1) {
          var neighbor = chunk.getBlock(x + xx, y + yy);
          if (neighbor == null || neighbor.typ != block.typ) {
            state |= TileManager.getIncrement(xx, yy)
          }
        }
      }
      block.updateRegions(state)
    }
  }



  var batch = new SpriteBatch()

  def setBlock(x:Int,y:Int,block:Block) = chunks(x/Game.ChunkWidth).setBlock(x,y,block)
  def getBlock(x:Int,y:Int) : Block = {
    val i = x / Game.ChunkWidth
    if (i >= 0 && chunks(i) != null) {
      return chunks(x / Game.ChunkWidth).getBlock(x, y)
    }
    null
  }

  def getChunk(x:Int): Chunk ={
    if(x>=0 && x < Game.ChunkWidth*64) {
      chunks(x / Game.ChunkWidth)
    } else null
  }

  def setChunk(x:Int,chunk:Chunk): Unit ={
    if(x>=0 && x < Game.ChunkWidth*64) {
      chunks(x / Game.ChunkWidth) = chunk
    }
  }

  def render(x:Int, y:Int): Unit ={
    batch.setProjectionMatrix(Game.camera.combined)
    batch.begin()
    chunks.foreach((a) => if (a != null){ a.render(batch)})
    batch.end()
  }



}
