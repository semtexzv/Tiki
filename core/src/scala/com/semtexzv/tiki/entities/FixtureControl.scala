package com.semtexzv.tiki.entities

import com.badlogic.gdx.math.Vector2

/**
  * Created by Semtexzv on 2/2/2016.
  */
trait FixtureControl {
  var typ:Short
  def onBeginContact(other:FixtureControl)
  def onEndContact(other:FixtureControl)
  def shouldCollide(other: FixtureControl,normal: Vector2): Boolean

}
