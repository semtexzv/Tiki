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
class Block(val x:Float,val y:Float,val typ:BlockType)  {

  val Hash: Int = x.toInt+ Random.nextInt()
  var regions : Array[TextureRegion] = new Array[TextureRegion](4)

  var health : Float = 100f
  var body : Body = null

  def obtainBody(): Unit ={
    if(typ != BlockType.None && body == null&& health > 0) {
      body = Game.world.bodyPool.obtain()
      body.setUserData(this)
      val fixt = body.getFixtureList.first()
      fixt.setDensity(0f)
      fixt.setFriction(0f)
      fixt.setRestitution(0f)
      fixt.setUserData(FixtureType.GroundBlock)
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
  def damage(amount:Float): Unit ={
    health -=amount
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

  override def hashCode(): Int = {
    Hash
  }

  override def equals(obj: Any): Boolean = {
    if(obj.isInstanceOf[Block]){
      return Hash == obj.asInstanceOf[Block].Hash
    }
    return false
    /*obj match {
      case obj:Block => Hash == obj.Hash
      case _ => false
    }*/
  }

}