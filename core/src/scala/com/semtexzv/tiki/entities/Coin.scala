package com.semtexzv.tiki.entities

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d.{Fixture, Body}
import com.semtexzv.tiki.{FixtureType, TileManager}
import com.badlogic.gdx.physics.box2d._

/**
  * Created by Semtexzv on 1/31/2016.
  */
class Coin(world:World)  extends  Entity(world,EntityType.Coin){
  var coinFixt: Fixture = null


  override def update(delta: Float): Unit = {

  }

  override def render(batch: SpriteBatch): Unit = {
    batch.draw(TileManager.playerRegion,x-0.2f,y-0.2f,0.4f,0.4f)
  }

  override def createBody(x:Float,y:Float): Body = {
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

    fdef.filter.categoryBits = FixtureType.Coin
    fdef.filter.maskBits = (FixtureType.LadderBlock | FixtureType.PlayerCore | FixtureType.WallBlock).toShort

    coinFixt = body.createFixture(fdef)
    coinFixt.setUserData(FixtureType.Coin)
    body
  }
}
