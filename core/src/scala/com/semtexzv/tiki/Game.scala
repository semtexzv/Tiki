package com.semtexzv.tiki

import com.badlogic.gdx.graphics.{Color, OrthographicCamera}
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.{CircleShape, PolygonShape, FixtureDef, BodyDef}
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.{Game => gGame, InputMultiplexer, Gdx}
import com.semtexzv.tiki.screens.{GenScreen, GameScreen}

/**
  * Created by Semtexzv on 1/27/2016.
  */
object Game extends gGame {


  var zoom: Float = 1f

  lazy val camera = new OrthographicCamera(1,1)
  lazy val viewport = new ExtendViewport(64,64,camera)
  lazy val renderer = new ShapeRenderer()
  lazy val input = new InputMultiplexer()

  lazy val gameScreen = new GameScreen
  var world : GameWorld =  null


  var time = 0f

  override def create(): Unit = {
    this.setScreen(new GameScreen)
    Gdx.input.setInputProcessor(input)
  }

  override def resize(width: Int, height: Int): Unit = {
    viewport.update(width,height)
    super.resize(width, height)
  }

  override def render(): Unit = {
    time += Gdx.graphics.getDeltaTime
    super.render()
  }
}
