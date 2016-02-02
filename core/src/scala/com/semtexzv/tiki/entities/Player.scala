package com.semtexzv.tiki.entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d._
import com.semtexzv.tiki.{Game, TileManager, FixtureType}

/**
  * Created by Semtexzv on 1/28/2016.
  */
class Player(world:World) extends Entity(world:World,EntityType.Player){

  object bodyControl extends  FixtureControl{
    override var typ: Short = FixtureType.EntityBody

    /* Normal will point from this body to the other,IF this is the B body in collision we reverse the normal ,
     since bo2d reports it from A to B */
    override def shouldCollide(other: FixtureControl,normal: Vector2): Boolean = {
      !(other.typ==FixtureType.WallBlock &&( gndContacts == 0 && normal.y <0)|| (wideContacts ==0 && normal.x !=0))
    }
    override def onBeginContact(other: FixtureControl): Unit = {
    }
    override def onEndContact(other: FixtureControl): Unit = {
    }
  }
  object feetControl extends  FixtureControl{
    override var typ: Short = FixtureType.EntityFeet

    override def shouldCollide(other: FixtureControl, normal: Vector2): Boolean = {true}

    override def onBeginContact(other: FixtureControl): Unit = {
      if(other.typ == FixtureType.WallBlock)
        Player.this.gndContacts +=1
      else if(other.typ == FixtureType.LadderBlock)
        Player.this.ladderFeetContacts +=1
    }

    override def onEndContact(other: FixtureControl): Unit = {
    if(other.typ == FixtureType.WallBlock)
      Player.this.gndContacts -=1
    else if(other.typ == FixtureType.LadderBlock)
      Player.this.ladderFeetContacts -=1
    }

  }
  object wideControl extends FixtureControl{
    override var typ: Short = FixtureType.EntityWide

    override def shouldCollide(other: FixtureControl, normal: Vector2): Boolean = ???

    override def onBeginContact(other: FixtureControl): Unit = {
      if(other.typ == FixtureType.WallBlock){
        Player.this.wideContacts +=1
      }
    }
    override def onEndContact(other: FixtureControl): Unit = {
      if(other.typ == FixtureType.WallBlock){
        Player.this.wideContacts -=1
      }
    }

  }
  object coreControl extends  FixtureControl{
    override var typ: Short = FixtureType.EntityCore

    override def shouldCollide(other: FixtureControl, normal: Vector2): Boolean = ???

    override def onBeginContact(other: FixtureControl): Unit = {
      if(other.typ == FixtureType.LadderBlock){
        ladderCoreContacts +=1
      }
    }
    override def onEndContact(other: FixtureControl): Unit = {
      if(other.typ == FixtureType.LadderBlock){
        ladderCoreContacts -=1
      }
    }

  }

  var bodyFixt: Fixture = null
  var feetFixt: Fixture = null
  var wideFixt: Fixture = null
  var coreFixt: Fixture = null

  var gndContacts :Int = 0
  var wideContacts:Int = 0
  var ladderCoreContacts: Int = 0
  var ladderFeetContacts: Int = 0
  var spikesContacts:Int = 0

  var lastDamageTime = 0f
  var damageCooldown = 1f
  var health = 100f

  def isInDamageCooldown = Game.time -lastDamageTime < damageCooldown
  def causeDamage(amount:Float): Unit ={
    if(Game.time-lastDamageTime>damageCooldown ){
      lastDamageTime = Game.time
      health -= amount
    }
  }

  val ladderUpSpeed =2f
  val ladderDownSpeed =2f
  def standing :Boolean =  gndContacts >0 || ladderFeetContacts >0

  val acc = 60f
  var mx = 4f
  val jumpSpeed =12f

  val gscale = 3f

  val w = 0.3f
  val h = 0.3f

  needsBlocks = true
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
    if(ladderCoreContacts >0){
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
      body.setGravityScale(gscale)
      if (Gdx.input.isKeyPressed(Keys.UP) && standing) {
        vel.set(vel.x, jumpSpeed)
      }
    }
    body.setLinearVelocity(vel)

    if(spikesContacts > 0 ){
      causeDamage(1)
    }
  }

  override def render(batch: SpriteBatch): Unit = {
    if(isInDamageCooldown) {
      if ((Game.time * 10).toInt % 2 == 1) {
        batch.draw(TileManager.playerRegion, super.x - 0.3f, super.y - 0.3f, 0.6f, 0.6f)
      }
    }else{
      batch.draw(TileManager.playerRegion, super.x - 0.3f, super.y - 0.3f, 0.6f, 0.6f)
    }
  }

  override def createBody(x:Float,y:Float): Body = {
    var bdef = new BodyDef
    bdef.`type` = BodyType.DynamicBody
    bdef.fixedRotation=true
    bdef.allowSleep = false
    bdef.bullet = true


    val body = world.createBody(bdef)
    body.setTransform( x,y,0)

    body.setUserData(this)

    var fdef = new FixtureDef
    fdef.density = 1f
    fdef.friction = 1f
    fdef.restitution =0.1f
    var shape = new PolygonShape()
    //var shape = new CircleShape()
    fdef.shape = shape
    //shape.setRadius(0.45f)
    shape.setAsBox(w,h)

    fdef.filter.categoryBits = FixtureType.EntityBody
    fdef.filter.maskBits = (FixtureType.WallBlock | FixtureType.LadderBlock | FixtureType.Spikes).toShort
    bodyFixt = body.createFixture(fdef)
    bodyFixt.setUserData(FixtureType.EntityBody)


    fdef.density = 0f
    fdef.isSensor = true
    shape.setAsBox(w*0.8f,0.1f,new Vector2(0,-h),0)
    fdef.filter.categoryBits = FixtureType.EntityFeet
    fdef.filter.maskBits = (FixtureType.WallBlock | FixtureType.LadderBlock ).toShort
    feetFixt = body.createFixture(fdef)
    feetFixt.setUserData(FixtureType.EntityFeet)

    shape.setAsBox(w*1.25f,h*0.85f,new Vector2(0,0f),0)
    fdef.filter.categoryBits = FixtureType.EntityWide
    fdef.filter.maskBits = FixtureType.WallBlock
    wideFixt = body.createFixture(fdef)
    wideFixt.setUserData(FixtureType.EntityWide)



    shape.setAsBox(0.15f,0.15f,new Vector2(0,0f),0)
    fdef.filter.categoryBits = FixtureType.EntityCore
    fdef.filter.maskBits = (FixtureType.Treasure | FixtureType.LadderBlock | FixtureType.LevelExit | FixtureType.Spikes).toShort
    coreFixt = body.createFixture(fdef)
    coreFixt.setUserData(FixtureType.EntityCore)

    body
  }
}
