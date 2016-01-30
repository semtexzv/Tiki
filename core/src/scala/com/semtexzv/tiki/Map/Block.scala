package com.semtexzv.tiki.Map

import com.badlogic.gdx.graphics.g2d.{SpriteBatch, TextureRegion}
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d._
import com.semtexzv.tiki.{TileManager, GameWorld, FixtureType, Game}

/**
  * Created by Semtexzv on 1/27/2016.
  */
class Block(var x:Float,var y:Float,val typ:Int,gameMap: GameMap) {

  var regions : Array[TextureRegion] = new Array[TextureRegion](4)

  var health : Float = 100f
  var body : Body = null

  def getBody(): Unit ={
    if(typ != Game.Air&& body == null&& health > 0) {
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
    if (health< 0){
      this.freeBody()
    }
  }

  def updateRegions(state:Int): Unit ={
    for (i<- 0 until 4){
      regions(i) = TileManager.getSubTile(typ,state,i)
    }
  }


  def render(batch: SpriteBatch): Unit ={
    batch.setColor(Game.colors(typ))
    if(health > 0) {
      //0-TL
      if(regions(0) != null){
        batch.draw(regions(0),x-0.5f,y,0.5f,0.5f)
      }
      //1-TR
      if(regions(1) != null){
        batch.draw(regions(1),x,y,0.5f,0.5f)
      }
      //2-BL
      if(regions(2) != null){
        batch.draw(regions(2),x-0.5f,y-0.5f,0.5f,0.5f)
      }
      //3-BR
      if(regions(3) != null){
        batch.draw(regions(3),x,y-0.5f,0.5f,0.5f)
      }
    }
  }
}