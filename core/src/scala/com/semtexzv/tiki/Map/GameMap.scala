package com.semtexzv.tiki.Map

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.math.RandomXS128
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d._
import com.semtexzv.tiki.Map.BlockType.BlockType
import com.semtexzv.tiki.Map.blocks.{ExitBlock, LadderBlock, WallBlock}
import com.semtexzv.tiki.{TileManager, FixtureType, Game}

import scala.util.Random

/**
  * Created by Semtexzv on 1/27/2016.
  */
class GameMap(world: World) {

  val w = 256
  val h = 256
  var blocks: Array[Block] = new Array[Block](w*h)
  var gen = new DungeonGen()

  def generate(): Unit ={
    gen.generate(Random.nextLong())
    for (y <- 0 until h) {
      for (x <- 0 until w) {
        if (gen.map(gen.index(x, y))!=gen.Empty) {
          setBlock(x, y, makeBlock(x,y))
        }
      }
    }
    for (y <- 0 until h) {
      for (x <- 0 until w) {
        if (getBlock(x,y) != null) {
          updateBlockTexture(x, y)
        }
      }
    }
  }
  def makeBlock(x:Int,y:Int): Block ={
    gen.map(gen.index(x,y)) match {
      case Game.Wall => new WallBlock(x,y)
      case Game.Ladder => new LadderBlock(x,y)
      case Game.Exit => new ExitBlock(x,y)
    }
  }


  def updateBlockTexture(x: Int, y: Int): Unit = {
    val block = getBlock(x,y)
    if(block != null) {
      var state = 0
      for (yy <- -1 to 1) {
        for (xx <- -1 to 1) {
          var neighbor = getBlock(x + xx, y + yy)
          if (neighbor == null || neighbor.typ != block.typ) {
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



  def index(x: Int, y: Int) = (x&w-1) + (y&h-1) *w

  def setBlock(x:Int,y:Int,block:Block) :Unit = {
    if (x >= 0 && y> 0) {
      blocks(index(x,y)) = block
    }
  }

  def getBlock(x:Int,y:Int) : Block = {
    if (x >= 0 && y> 0) {
     return blocks(index(x,y))
    }
    null
  }


  def render(batch:SpriteBatch): Unit ={
    blocks.foreach((a) => if (a != null){ a.render(batch)})

  }



}
