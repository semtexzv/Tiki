package com.semtexzv.tiki.entities

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d._
import com.semtexzv.tiki.{FixtureType, TileManager}

/**
  * Created by Semtexzv on 1/31/2016.
  */
class Enemy(world:World) extends Entity(world,EntityType.Enemy){
  var fixt :Fixture =null
  override def createBody(x: Float, y: Float): Body = {
    var bdef = new BodyDef
    bdef.`type` = BodyType.DynamicBody
    bdef.fixedRotation=true
    bdef.allowSleep = false

    val body = world.createBody(bdef)
    body.setTransform( x,y,0)
    body.setUserData(this)

    var fdef = new FixtureDef
    fdef.density = 2f
    fdef.friction = 1f
    fdef.restitution = 0f
    var shape = new PolygonShape()
    fdef.shape = shape
    shape.setAsBox(0.45f,0.45f)

    fdef.filter.categoryBits = FixtureType.Enemy
    fdef.filter.maskBits = (FixtureType.LadderBlock | FixtureType.EntityCore | FixtureType.WallBlock | FixtureType.EntityBody).toShort

    fixt = body.createFixture(fdef)
    fixt.setUserData(FixtureType.Enemy)
    body
  }

  override def update(delta: Float): Unit = {

  }

  override def render(batch: SpriteBatch): Unit = {
    batch.draw(TileManager.playerRegion,x-0.3f,y-0.3f,0.6f,0.6f)
  }
}
