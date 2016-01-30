package com.semtexzv.tiki

import com.semtexzv.tiki.EntityType.EntityType

/**
  * Created by Semtexzv on 1/28/2016.
  */
abstract class Entity(typ:EntityType) {

  def update(delta:Float)
  def x: Float
  def y: Float

}
