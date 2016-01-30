package com.semtexzv.tiki.entities

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d._
import com.semtexzv.tiki.FixtureType
import com.semtexzv.tiki.inventory.Item

/**
  * Created by Semtexzv on 1/30/2016.
  */
class ItemDrop(var x:Float,var y:Float,var item:Item,world:World) extends Entity(EntityType.ItemDrop){
  var body : Body = null
  var itemFixt: Fixture =null
  var timeLeft = 100f

  var bdef = new BodyDef
  bdef.`type` = BodyType.DynamicBody
  bdef.fixedRotation=true
  bdef.allowSleep = false
  body = world.createBody(bdef)
  body.setUserData(this)
  body.setTransform( x,y,0)

  var fdef = new FixtureDef
  fdef.density = 1f
  fdef.friction = 1f
  fdef.restitution = 0f
  var shape = new PolygonShape()
  fdef.shape = shape
  shape.setAsBox(0.25f,0.25f)

  itemFixt = body.createFixture(fdef)
  itemFixt.setUserData(FixtureType.ItemDrop)

  override def update(delta: Float): Unit = {
    timeLeft -= delta
    x = body.getPosition.x
    y = body.getPosition.y
  }

  override def onSpawn(): Unit = {

  }

  override def onDespawn(): Unit = {
    world.destroyBody(body)
  }
}
