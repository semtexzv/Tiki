package com.semtexzv.tiki.Map

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d._
import com.semtexzv.tiki.{FixtureType, Game}

/**
  * Created by Semtexzv on 1/27/2016.
  */
class Block(var x:Float,var y:Float,bWorld: World) {

  var bdef = new BodyDef
  bdef.`type` = BodyType.StaticBody
  bdef.fixedRotation=true

  var body = bWorld.createBody(bdef)
  body.setUserData(this)

  var fdef = new FixtureDef
  fdef.density = 0f
  fdef.friction = 0f
  fdef.restitution =0f
  var shape = new PolygonShape()
  fdef.shape = shape
  shape.setAsBox(0.5f,0.5f)

  var fixt = body.createFixture(fdef)
  fixt.setUserData(FixtureType.GroundBlock)
  body.setTransform(x,y,0)

}