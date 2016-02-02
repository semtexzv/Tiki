package com.semtexzv.tiki.Map

import com.badlogic.gdx.graphics.g2d.{SpriteBatch, TextureRegion}
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d._
import com.semtexzv.tiki.Map.BlockType.BlockType
import com.semtexzv.tiki.{TileManager, GameWorld, FixtureType, Game}

import scala.util.Random

/**
  * Created by Semtexzv on 1/27/2016.
  */
abstract class Block(val x:Int, val y:Int, val typ:BlockType)  {

  var regions : Array[TextureRegion] = new Array[TextureRegion](4)

  var body : Body = null

  def configureBody()
  def obtainBody(): Unit ={
    if(typ != null && body == null) {
      body = Game.world.bodyPool.obtain()

      body.setUserData(this)
      configureBody()
      body.setTransform(x,y,0)
      this.body = body
    }
  }
  def freeBody(): Unit = {
    if (body != null) {
      val b = this.body
      b.setTransform(-10, -10, 0)
      Game.world.bodyPool.free(body)
      this.body = null
    }
  }
  def render(batch: SpriteBatch): Unit = {
   // batch.setColor(Game.colors(typ))
    //0-TL
    if (regions(0) != null) {
      batch.draw(regions(0), x - 0.5f, y, 0.5f, 0.5f)
    }
    //1-TR
    if (regions(1) != null) {
      batch.draw(regions(1), x, y, 0.5f, 0.5f)
    }
    //2-BL
    if (regions(2) != null) {
      batch.draw(regions(2), x - 0.5f, y - 0.5f, 0.5f, 0.5f)
    }
    //3-BR
    if (regions(3) != null) {
      batch.draw(regions(3), x, y - 0.5f, 0.5f, 0.5f)
    }
  }
  def updateTexture(map:GameMap): Unit = {
    var state = 0
    for (yy <- -1 to 1; xx <- -1 to 1) {
      var neighbor = map.getBlock(x + xx, y + yy)
      if (neighbor == null || neighbor.typ != typ) {
        state |= TileManager.getIncrement(xx, yy)
      }
    }
    for (i <- 0 until 4) {
      regions(i) = TileManager.getSubTile(typ, state, i)
    }
  }
}