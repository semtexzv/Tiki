package com.semtexzv.tiki.entities

import com.semtexzv.tiki.entities.EntityType.EntityType

/**
  * Created by Semtexzv on 1/28/2016.
  */
abstract class Entity(typ:EntityType) {

  def onSpawn()
  def onDespawn()
  def update(delta:Float)
  def x: Float
  def y: Float

}
