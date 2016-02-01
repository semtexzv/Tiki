package com.semtexzv.tiki.entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d._
import com.semtexzv.tiki.{TileManager, FixtureType}

/**
  * Created by Semtexzv on 1/28/2016.
  */
class Player(world:World) extends Entity(world:World,EntityType.Player){

  var bodyFixt: Fixture = null
  var feetFixt: Fixture = null
  var wideFixt: Fixture = null
  var coreFixt: Fixture = null

  var gndContacts :Int = 0
  var wideContacts:Int = 0
  var ladderContacts: Int = 0

  val ladderUpSpeed =2f
  val ladderDownSpeed =2f
  def standing :Boolean = math.abs(body.getLinearVelocity.y ) < 0.01f && gndContacts >0

  val acc = 80f
  var mx = 5f


  val w = 0.3f
  val h = 0.3f

  def position: Vector2 = body.getPosition
  var double = true

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
    if(ladderContacts>0){
      body.setGravityScale(0f)
      if (Gdx.input.isKeyPressed(Keys.UP) ) {
        vel.set(vel.x, ladderUpSpeed)
      }
      else if (Gdx.input.isKeyPressed(Keys.DOWN) ) {
        vel.set(vel.x, -ladderDownSpeed)
      }
      else {
        vel.set(vel.x,0f)
      }
    }
    else{
      body.setGravityScale(1f)
      if (Gdx.input.isKeyPressed(Keys.UP) && standing) {
        vel.set(vel.x, 12)
      }
    }


    body.setLinearVelocity(vel)
  }

  override def render(batch: SpriteBatch): Unit = {
    batch.draw(TileManager.playerRegion,super.x-0.3f,super.y-0.3f,0.6f,0.6f)
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
    shape.setAsBox(w,h)

    fdef.filter.categoryBits = FixtureType.PlayerBody
    fdef.filter.maskBits = FixtureType.WallBlock
    bodyFixt = body.createFixture(fdef)
    bodyFixt.setUserData(FixtureType.PlayerBody)


    fdef.density = 0f
    fdef.isSensor = true
    shape.setAsBox(w*0.9f,0.1f,new Vector2(0,-h/2),0)
    fdef.filter.categoryBits = FixtureType.PlayerFeet
    fdef.filter.maskBits = FixtureType.WallBlock
    feetFixt = body.createFixture(fdef)
    feetFixt.setUserData(FixtureType.PlayerFeet)

    shape.setAsBox(w*1.25f,h*0.95f,new Vector2(0,0f),0)
    fdef.filter.categoryBits = FixtureType.PlayerWide
    fdef.filter.maskBits = FixtureType.WallBlock
    wideFixt = body.createFixture(fdef)
    wideFixt.setUserData(FixtureType.PlayerWide)


    shape.setAsBox(0.1f,0.1f,new Vector2(0,0f),0)
    fdef.filter.categoryBits = FixtureType.PlayerCore
    fdef.filter.maskBits = (FixtureType.Treasure | FixtureType.LadderBlock | FixtureType.LevelExit).toShort
    coreFixt = body.createFixture(fdef)
    coreFixt.getFilterData.categoryBits = FixtureType.PlayerCore
    coreFixt.setUserData(FixtureType.PlayerCore)

    body
  }
}
