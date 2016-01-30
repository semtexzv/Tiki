package com.semtexzv.tiki.Map

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.semtexzv.tiki.Game

/**
  * Created by Semtexzv on 1/28/2016.
  */
class Chunk(var x:Float) {
  //x marks leftmost block column
  var blocks: Array[Block] = new Array[Block](Game.ChunkHeight * Game.ChunkWidth)

  def index(x: Int, y: Int) = x + y * Game.ChunkWidth

  /* X can be world or chunk position, thanks size of chunks being PoT ,
   simple AND acts as modulo operator*/

  def getBlock(x: Int, y: Int): Block = {
    if (y >= 0 && y < Game.ChunkHeight && x >= 0 ) {
      blocks(index(x & Game.ChunkMask, y))
    } else null
  }
  def setBlock(x: Int, y: Int, block: Block): Unit = {
    val i = index(x & Game.ChunkMask, y)
    if (i < blocks.length) {
      blocks(i) = block
    }
  }
  def render(batch: SpriteBatch): Unit ={
    blocks.foreach((a) => if (a != null){ a.render(batch)})
  }
}
