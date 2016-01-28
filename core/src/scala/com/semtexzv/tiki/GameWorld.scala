package com.semtexzv.tiki

import com.badlogic.gdx.{InputProcessor, Gdx}
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d._
import com.semtexzv.tiki.EntityType.EntityType
import com.semtexzv.tiki.Map.{Block, GameMap}
/**
  * Created by Semtexzv on 1/27/2016.
  */
class GameWorld extends  ContactListener {
  Box2D.init()
  var render = new Box2DDebugRenderer()


  val world = new box2d.World(new Vector2(0, -30f), true)
  world.setContactListener(this)
  world.setContinuousPhysics(true)
  var player: Player = new Player(world, 1, Game.MapHeight)
  var map: GameMap = new GameMap(world)

  def render(delta: Float): Unit = {
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

    Game.camera.position.set(player.position.x, player.position.y, 0)
    Game.camera.zoom = Game.zoom
    Game.camera.update()

    player.update(delta)

    world.step(delta, 3, 3)
    render.render(world, Game.camera.combined)

  }

  override def postSolve(contact: Contact, impulse: ContactImpulse): Unit = {

  }

  override def endContact(contact: Contact): Unit = {
    var typeA = contact.getFixtureA.getUserData.asInstanceOf[EntityType]
    var typeB = contact.getFixtureB.getUserData.asInstanceOf[EntityType]
    if (typeA == FixtureType.PlayerFeet && typeB == FixtureType.GroundBlock) {
      player.gndContacts -= 1
    }
    if (typeA == FixtureType.PlayerWide && typeB == FixtureType.GroundBlock) {
      player.wideContacts -= 1
    }
  }

  override def beginContact(contact: Contact): Unit = {
    var typeA = contact.getFixtureA.getUserData.asInstanceOf[EntityType]
    var typeB = contact.getFixtureB.getUserData.asInstanceOf[EntityType]
    if (typeA == FixtureType.PlayerFeet && typeB == FixtureType.GroundBlock) {
      player.gndContacts += 1
    }
    if (typeA == FixtureType.PlayerWide && typeB == FixtureType.GroundBlock) {
      player.wideContacts += 1
    }
  }

  override def preSolve(contact: Contact, oldManifold: Manifold): Unit = {

    var typeA = contact.getFixtureA.getUserData.asInstanceOf[EntityType]
    var typeB = contact.getFixtureB.getUserData.asInstanceOf[EntityType]
    if (typeA == FixtureType.PlayerBody && typeB == FixtureType.GroundBlock) {
      if (player.gndContacts <= 0 && contact.getWorldManifold.getNormal.y == -1) {
        contact.setEnabled(false)
      }
      if (player.wideContacts <= 0 && contact.getWorldManifold.getNormal.x != 0) {
        contact.setEnabled(false)
      }
    }
  }
}

