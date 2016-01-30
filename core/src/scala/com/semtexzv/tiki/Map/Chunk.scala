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

  def render(batch: SpriteBatch): Unit ={
    blocks.foreach((a) => if (a != null){ a.render(batch)})
  }
}
