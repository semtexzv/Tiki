package com.semtexzv.tiki.screens

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.{Input, Gdx, InputProcessor, Screen}
import com.badlogic.gdx.graphics.{Color, Texture, Pixmap}
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.semtexzv.tiki.{Game, GameWorld}

/**
  * Created by Semtexzv on 1/27/2016.
  */
class GameScreen extends  Screen with  InputProcessor{
  lazy val world : GameWorld = new GameWorld

  override def show(): Unit = {

    Game.input.addProcessor(this)
  }
  override def hide(): Unit = {

    Game.input.removeProcessor(this)
  }

  override def render(delta: Float): Unit = {
    world.render(delta)
  }
  override def resize(width: Int, height: Int): Unit = {
  }


  override def pause(): Unit = {

  }

  override def resume(): Unit = {

  }

  override def dispose(): Unit = {

  }


  override def mouseMoved(screenX: Int, screenY: Int): Boolean = false

  override def keyTyped(character: Char): Boolean = false

  override def keyDown(keycode: Int): Boolean = false

  override def touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = {

    if(pointer ==  Input.Buttons.LEFT ) {
      world.clicked = true
      val w = Game.viewport.unproject(new Vector2(screenX, screenY))
      world.clickX = math.round(w.x)
      world.clickY = math.round(w.y)
    }
    true

  }
  override def touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean = {
    //Todo, store screen position, and unproject every time player moves
    val w = Game.viewport.unproject(new Vector2(screenX, screenY))
    world.clickX = math.round(w.x)
    world.clickY = math.round(w.y)
    true
  }

  override def touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = {
    world.clicked = false
    true
  }
  override def keyUp(keycode: Int): Boolean = false

  override def scrolled(amount: Int): Boolean = {
    if(amount > 0)
      Game.zoom *=1.2f
    else
      Game.zoom *=0.8f
    true
  }


}
