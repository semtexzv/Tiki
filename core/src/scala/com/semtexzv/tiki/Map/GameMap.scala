package com.semtexzv.tiki.Map

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.math.RandomXS128
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d._
import com.semtexzv.tiki.Map.BlockType.BlockType
import com.semtexzv.tiki.{TileManager, FixtureType, Game}

import scala.util.Random

/**
  * Created by Semtexzv on 1/27/2016.
  */
class GameMap(world: World) {
  var chunks: Array[Chunk] = new Array[Chunk](64)

  val chCount = 4

  var gen = new MapGen(chCount * Game.ChunkWidth, Game.ChunkHeight)
  gen.generate(Random.nextLong())

  for (chx <- 0 until chCount) {
    if (chunks(chx) == null) {
      chunks(chx) = new Chunk(chx * Game.ChunkWidth)
    }
    val chunk = chunks(chx)
    val baseX = chx * Game.ChunkWidth

    for (y <- 0 until Game.ChunkHeight) {
      for (x <- 0 until Game.ChunkWidth) {
        if (gen.map(gen.index(x + baseX, y)) != Game.Air)
          chunk.setBlock(x, y,
            new Block(x + baseX, y,
              getBlockType(gen.map(gen.index(x + baseX, y)))))
      }
    }
    for (y <- 0 until Game.ChunkHeight) {
      for (x <- 0 until Game.ChunkWidth) {
        if (gen.map(gen.index(x + baseX, y)) != Game.Air) {
          updateBlockTexture(x + baseX, y)
        }
      }
    }
  }

  def updateBlockTexture(x: Int, y: Int): Unit = {
    var block = getBlock(x,y)
    if(block != null) {
      var state = 0
      for (yy <- -1 to 1) {
        for (xx <- -1 to 1) {
          var neighbor = getBlock(x + xx, y + yy)
          if (neighbor != null && neighbor.typ == block.typ) {}
          else {
            state |= TileManager.getIncrement(xx, yy)
          }
        }
      }
      for (i<- 0 until 4){
        block.regions(i) = TileManager.getSubTile(block.typ,state,i)
      }
    }
  }



  def updateNighbors(x:Int,y:Int): Unit = {
    for (yy <- -1 to 1) {
      for (xx <- -1 to 1) {

        updateBlockTexture(x+xx,y+yy)
      }
    }
  }


  var batch = new SpriteBatch()

  def setBlock(x:Int,y:Int,block:Block) :Unit = {
    val c = chunks(x>>>Game.ChunkShift)
    if (x >= 0 && c != null) {
       c.setBlock(x, y,block)
    }
  }

  def getBlock(x:Int,y:Int) : Block = {
    if (x >= 0) {
      val c = chunks(x>>>Game.ChunkShift)
      if(c!= null) {
        return c.getBlock(x, y)
      }
    }
    null
  }

  def getBlockType(mapTileType:Int): BlockType ={
    mapTileType match {
      case Game.Air => BlockType.None
      case Game.Dirt => BlockType.Dirt
      case Game.Rock => BlockType.Stone
      case Game.Bedrock => BlockType.Bedrock
      case _ => BlockType.None
    }
  }

  def render(x:Int, y:Int): Unit ={
    batch.setProjectionMatrix(Game.camera.combined)
    batch.begin()
    chunks.foreach((a) => if (a != null){ a.render(batch)})
    batch.end()
  }



}
