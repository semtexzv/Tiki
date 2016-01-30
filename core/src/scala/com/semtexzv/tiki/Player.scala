package com.semtexzv.tiki

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d._

/**
  * Created by Semtexzv on 1/28/2016.
  */
class Player(world:World,xx:Float,yy:Float) extends Entity(EntityType.Player){
  var body : Body = null

  var bodyFixt: Fixture = null
  var feetFixt: Fixture = null
  var wideFixt: Fixture = null

  var gndContacts :Int = 0
  var wideContacts:Int = 0
  def standing :Boolean = math.abs(body.getLinearVelocity.y ) < 0.01f && gndContacts >0

  val acc = 80f
  var mx = 10f


  val w = 0.6f
  val h = 1.3f
  var bdef = new BodyDef
  bdef.`type` = BodyType.DynamicBody
  bdef.fixedRotation=true
  bdef.allowSleep = false

  body = world.createBody(bdef)

  body.setTransform( xx,yy,0)

  var fdef = new FixtureDef
  fdef.density = 1f
  fdef.friction = 1f
  fdef.restitution = 0f
  var shape = new PolygonShape()
  fdef.shape = shape
  shape.setAsBox(w,h)

  bodyFixt = body.createFixture(fdef)
  bodyFixt.setUserData(FixtureType.PlayerBody)


  shape.setAsBox(w*0.9f,0.3f,new Vector2(0,-1.4f),0)
  fdef.isSensor = true

  feetFixt = body.createFixture(fdef)

  feetFixt.setUserData(FixtureType.PlayerFeet)

  shape.setAsBox(w*1.5f,h*0.95f,new Vector2(0,0f),0)

  wideFixt = body.createFixture(fdef)
  wideFixt.setUserData(FixtureType.PlayerWide)


  def position: Vector2 = body.getPosition


  var vel = new Vector2()
  def update(delta:Float): Unit ={
    vel.set(body.getLinearVelocity)

    if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
      vel.set(Math.min(vel.x + acc * delta, mx), vel.y)
    }
    else if (Gdx.input.isKeyPressed(Keys.LEFT)) {
      vel.set(Math.max(vel.x - acc * delta, -mx), vel.y)
    }
    else {
      vel.set(0, vel.y)
    }

    if (Gdx.input.isKeyPressed(Keys.UP) && standing) {
      vel.set(vel.x, 15)
    }
    body.setLinearVelocity(vel)
  }

  override def x: Float = position.x

  override def y: Float = position.y
}
