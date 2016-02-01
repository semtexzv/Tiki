package com.semtexzv.tiki.screens

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.{Color, GL20}
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.{Gdx, InputProcessor, Screen}
import com.semtexzv.tiki.Game
import com.semtexzv.tiki.Map.DungeonGen

import scala.util.Random

/**
  * Created by Semtexzv on 1/29/2016.
  */
class GenScreen extends Screen with  InputProcessor
{
  def w = 256
  def h = 256
  var gen = new DungeonGen(4,4)
  val batch = new SpriteBatch()
  val position = new Vector2()
  val speed = 100f
  gen.generate(Random.nextLong())
  gen.preRender()

  override def hide(): Unit = {

  }

  override def show(): Unit = {
    Game.input.addProcessor(this)
  }

  override def render(delta: Float): Unit = {
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

    if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
      position.add(speed*delta,0)
    }
     if (Gdx.input.isKeyPressed(Keys.LEFT)) {
      position.add(-speed*delta,0)
    }
    if (Gdx.input.isKeyPressed(Keys.UP)) {
      position.add(0,speed*delta)
    }
    if (Gdx.input.isKeyPressed(Keys.DOWN)) {
      position.add(0,-speed*delta)
    }

    Game.camera.position.set(position.x, position.y, 0)
    Game.camera.zoom = Game.zoom
    Game.camera.update()
    batch.setProjectionMatrix(Game.camera.combined)

    batch.begin()
    gen.render(batch)
    batch.end()
  }

  override def resize(width: Int, height: Int): Unit = {

  }


  override def pause(): Unit = {

  }

  override def resume(): Unit = {

  }
  override def dispose(): Unit = {

  }
  override def touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = {
    true
  }
  override def touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = {
    gen = new DungeonGen(4,4)
    gen.generate(Random.nextLong())
    gen.preRender()
    true
  }




  override def mouseMoved(screenX: Int, screenY: Int): Boolean = false

  override def keyTyped(character: Char): Boolean = false

  override def keyDown(keycode: Int): Boolean = false



  override def keyUp(keycode: Int): Boolean = false

  override def scrolled(amount: Int): Boolean = {
    if(amount > 0)
      Game.zoom *=1.2f
    else
      Game.zoom *=0.8f
    true
  }


  override def touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean = false
}
