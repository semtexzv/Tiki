package com.semtexzv.tiki.components

import com.artemis.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body

/**
  * Created by Semtexzv on 1/27/2016.
  */
class PhysComponent(var position: Vector2, var hw:Float,var hh:Float) extends Component{
  var velocity: Vector2 = new Vector2
  var weight: Float = 1f

  def x: Float = position.x
  def y: Float = position.y
  def vx: Float = velocity.x
  def vy: Float = velocity.y
  //Bounce coefficient
  var bounce: Float = 0f

  var grounded: Boolean = false
  def standing: Boolean = math.abs(vy)< 0.01f

}
