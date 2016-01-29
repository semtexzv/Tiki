package com.semtexzv.tiki.Map

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d._
import com.semtexzv.tiki.{GameWorld, FixtureType, Game}

/**
  * Created by Semtexzv on 1/27/2016.
  */
class Block(var x:Float,var y:Float,val typ:Int,gameMap: GameMap) {

  var body : Body = null

  def getBody(): Unit ={
    if(typ != Game.Air&& body == null) {
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


  def render(renderer:ShapeRenderer): Unit ={
    renderer.setColor(Game.colors(typ))
    renderer.rect(x-0.4f,y-0.4f,0.8f,0.8f)
  }
}