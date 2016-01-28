package com.semtexzv.tiki

import com.artemis.World
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.{CircleShape, PolygonShape, FixtureDef, BodyDef}
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.{Game => gGame}
import com.semtexzv.tiki.components.{FocusCompoent, PhysComponent}
import com.semtexzv.tiki.screens.GameScreen

/**
  * Created by Semtexzv on 1/27/2016.
  */
object Game extends  gGame {
  final val MapWidth = 32
  final val MapHeight = 32
  var zoom: Float = 1f

  lazy val camera = new OrthographicCamera(1,1)
  lazy val viewport = new ExtendViewport(64,64,camera)
  lazy val renderer = new ShapeRenderer()

  lazy val gameScreen = new GameScreen

  var world :World = null

  override def create(): Unit = {
    this.setScreen(gameScreen)
  }

  override def resize(width: Int, height: Int): Unit = {
    viewport.update(width,height)
    super.resize(width, height)
  }


  def player(x:Float,y:Float): Int = {
    var e = world.create()

    world.edit(e).add(new PhysComponent(new Vector2(x,y),0.6f,1.2f)).add(new FocusCompoent)
    e
  }

}
