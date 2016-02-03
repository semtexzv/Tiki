package com.semtexzv.tiki.entities

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d.{Fixture, Body}
import com.semtexzv.tiki.{FixtureType, TileManager}
import com.badlogic.gdx.physics.box2d._

/**
  * Created by Semtexzv on 1/31/2016.
  */
class Treasure(world:World)  extends  Entity(world,EntityType.Treasure){
  var fixt: Fixture = null

  object fixtControl extends  FixtureControl{
    override var typ: Short = FixtureType.Treasure
    override def shouldCollide(other: FixtureControl, normal: Vector2): Boolean = {
      true
    }
    override def onEndContact(other: FixtureControl): Unit = {}
    override def onBeginContact(other: FixtureControl): Unit = {}
  }

  override def update(delta: Float): Unit = {

  }

  override def render(batch: SpriteBatch): Unit = {
    batch.draw(TileManager.playerRegion,x-0.2f,y-0.2f,0.4f,0.4f)
  }

  override def createBody(x:Float,y:Float): Body = {
    var bdef = new BodyDef
    bdef.`type` = BodyType.StaticBody
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

    fdef.filter.categoryBits = FixtureType.Treasure
    fdef.filter.maskBits = (FixtureType.LadderBlock | FixtureType.EntityCore | FixtureType.WallBlock).toShort

    fixt = body.createFixture(fdef)
    fixt.setUserData(fixtControl)
    body
  }
}
