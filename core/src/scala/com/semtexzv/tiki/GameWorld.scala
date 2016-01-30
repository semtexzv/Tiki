package com.semtexzv.tiki

import com.badlogic.gdx.utils.Pool
import com.badlogic.gdx.{Input, InputProcessor, Gdx}
import com.badlogic.gdx.graphics.{Color, GL20}
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d._
import com.semtexzv.tiki.EntityType.EntityType
import com.semtexzv.tiki.Map.{Block, GameMap}
/**
  * Created by Semtexzv on 1/27/2016.
  */
class GameWorld extends ContactListener with InputProcessor {
  Game.world = this
  Game.input.addProcessor(this)
  object bodyPool extends  Pool[Body]{
    override def newObject(): Body = {
      var bdef = new BodyDef
      bdef.`type` = BodyType.StaticBody
      bdef.fixedRotation = true
      var body = world.createBody(bdef)
      body.setUserData(this)
      var fdef = new FixtureDef
      fdef.density = 0f
      fdef.friction = 0f
      fdef.restitution = 0f
      var shape = new PolygonShape()
      fdef.shape = shape
      shape.setAsBox(0.5f, 0.5f)
      var fixt = body.createFixture(fdef)
      fixt.setUserData(FixtureType.GroundBlock)
      return body
    }
  }


  Box2D.init()
  var render = new Box2DDebugRenderer()

  val world = new box2d.World(new Vector2(0, -30f), true)
  world.setContactListener(this)
  world.setContinuousPhysics(true)
  var player: Player = new Player(world, 1, Game.ChunkHeight)
  var map: GameMap = new GameMap(world)
  var retainTime = 0f
  var clicked = false

  var clickX : Int = 0
  var clickY : Int = 0


  def render(delta: Float): Unit = {
    val ccl  =Color.CYAN
    Gdx.gl.glClearColor(ccl.r,ccl.g,ccl.b,1.0f)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

    Game.camera.position.set(player.position.x, player.position.y, 0)
    Game.camera.zoom = Game.zoom
    Game.camera.update()

    if(clicked){
      var b = map.getBlock(clickX,clickY)
      if(b != null){
        b.damage(delta*100)
      }
    }


    player.update(delta)
    var px: Int = player.position.x.toInt
    var py: Int = player.position.y.toInt


    val time = System.nanoTime()

    for (y<- -10 to 10){
      for (x<- -10 to 10){
        if(x > -4 && x < 4 && y> -4 && y< 4) {
          var block = map.getBlock(x + px, y + py)
          if (block != null && block.body == null && block.typ != Game.Air) {
            block.getBody()
          }
        }
        else {
          var block = map.getBlock(x + px, y + py)
          if (block != null && block.body != null) {
            block.freeBody()
          }
        }
      }
    }

    world.step(delta, 3, 3)
    //println("Time: "+(System.nanoTime()-time)/1000000f)

    map.render(px,py)
    render.render(world, Game.camera.combined)
  }


  override def postSolve(contact: Contact, impulse: ContactImpulse): Unit = {

  }

  override def endContact(contact: Contact): Unit = {
    var typeA = contact.getFixtureA.getUserData.asInstanceOf[Int]
    var typeB = contact.getFixtureB.getUserData.asInstanceOf[Int]
    if (typeA == FixtureType.PlayerFeet && typeB == FixtureType.GroundBlock) {
      player.gndContacts -= 1
    }
    if (typeA == FixtureType.PlayerWide && typeB == FixtureType.GroundBlock) {
      player.wideContacts -= 1
    }
  }

  override def beginContact(contact: Contact): Unit = {
    val typeA = contact.getFixtureA.getUserData.asInstanceOf[Int]
    val typeB = contact.getFixtureB.getUserData.asInstanceOf[Int]
    if (typeA == FixtureType.PlayerFeet && typeB == FixtureType.GroundBlock) {
      player.gndContacts += 1
    }
    if (typeA == FixtureType.PlayerWide && typeB == FixtureType.GroundBlock) {
      player.wideContacts += 1
    }
  }

  override def preSolve(contact: Contact, oldManifold: Manifold): Unit = {

    val typeA = contact.getFixtureA.getUserData.asInstanceOf[Int]
    val typeB = contact.getFixtureB.getUserData.asInstanceOf[Int]
    if (typeA == FixtureType.PlayerBody && typeB == FixtureType.GroundBlock) {
      if (player.gndContacts <= 0 && contact.getWorldManifold.getNormal.y == -1) {
        contact.setEnabled(false)
      }
      if (player.wideContacts <= 0 && contact.getWorldManifold.getNormal.x != 0) {
        contact.setEnabled(false)
      }
    }
  }

  def worldClicked(x:Float,y:Float): Unit = {
    println("xd "+x+" yd "+y)
    println("x "+math.round(x)+" y "+math.round(y))
  }

  override def mouseMoved(screenX: Int, screenY: Int): Boolean = false

  override def keyTyped(character: Char): Boolean = false

  override def keyDown(keycode: Int): Boolean = false

  override def touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = {

    if(pointer ==  Input.Buttons.LEFT ) {
      clicked = true
      val w = Game.viewport.unproject(new Vector2(screenX, screenY))
      clickX = math.round(w.x)
      clickY = math.round(w.y)
    }
    true

  }
  override def touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean = {
    val w = Game.viewport.unproject(new Vector2(screenX, screenY))
    clickX = math.round(w.x)
    clickY = math.round(w.y)
    true
  }

  override def touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = {
    clicked = false
    true
  }
  override def keyUp(keycode: Int): Boolean = false

  override def scrolled(amount: Int): Boolean = false


}

