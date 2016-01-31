package com.semtexzv.tiki.Map.blocks

import com.badlogic.gdx.physics.box2d.Filter
import com.semtexzv.tiki.FixtureType
import com.semtexzv.tiki.Map.{BlockType, Block}

/**
  * Created by Semtexzv on 1/31/2016.
  */
class ExitBlock(x:Int,y:Int) extends Block(x,y,BlockType.Exit){
  val filter = new Filter()
  filter.categoryBits = FixtureType.LevelExit
  filter.maskBits = -1
  override def configureBody(): Unit = {
    val fixt = body.getFixtureList.first()
    fixt.setSensor(true)
    fixt.setDensity(0f)
    fixt.setFriction(0f)
    fixt.setRestitution(0f)
    fixt.setFilterData(filter)
    fixt.setUserData(FixtureType.LevelExit)
  }
}
