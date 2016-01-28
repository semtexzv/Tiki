package com.semtexzv.tiki.screens

import com.badlogic.gdx.Screen
import com.semtexzv.tiki.GameWorld

/**
  * Created by Semtexzv on 1/27/2016.
  */
class GameScreen extends  Screen{
  lazy val world : GameWorld = new GameWorld
  override def show(): Unit = {

  }
  override def hide(): Unit = {

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
}
