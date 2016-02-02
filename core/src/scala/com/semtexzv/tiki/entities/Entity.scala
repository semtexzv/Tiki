package com.semtexzv.tiki.entities

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.physics.box2d._
import com.semtexzv.tiki.entities.EntityType.EntityType

/**
  * Created by Semtexzv on 1/28/2016.
  */
abstract class Entity(world:World, typ:EntityType) {
  var body: Body = null
  var needsBlocks = false
  def createBody(x:Float,y:Float) : Body
  def onSpawn(x:Float,y:Float): Unit ={
    body = createBody(x,y)
  }
  def onDespawn() : Unit = {
    world.destroyBody(body)
  }
  def update(delta:Float)
  def render(batch:SpriteBatch)
  def x = body.getPosition.x
  def y = body.getPosition.y

}
