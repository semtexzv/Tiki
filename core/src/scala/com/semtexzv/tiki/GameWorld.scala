package com.semtexzv.tiki

import com.artemis.{WorldConfigurationBuilder, World}
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Box2D
import com.semtexzv.tiki.Map.GameMap
import com.semtexzv.tiki.systems._

/**
  * Created by Semtexzv on 1/27/2016.
  */
class GameWorld {
  Box2D.init()

  val map = new GameMap()
  val config = new WorldConfigurationBuilder().`with`(
    new InputSystem,
    new CameraSystem,
    new PhysicsSystem(map),
    new DebugRenderSystem(map)
  ).build()
  val world = new World(config)
    Game.world = world
  Game.player(0,Game.MapHeight+2)


  def render(delta:Float): Unit ={
    world.setDelta(delta)
    world.process()

  }

}
