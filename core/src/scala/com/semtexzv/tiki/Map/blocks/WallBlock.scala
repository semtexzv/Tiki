package com.semtexzv.tiki.Map.blocks

import com.badlogic.gdx.physics.box2d.Filter
import com.semtexzv.tiki.FixtureType
import com.semtexzv.tiki.Map.{BlockType, Block}

/**
  * Created by Semtexzv on 1/31/2016.
  */
class WallBlock(x:Int,y:Int) extends Block(x,y,BlockType.Wall){
  val filter = new Filter()
  filter.categoryBits = FixtureType.WallBlock
  filter.maskBits = (FixtureType.EntityBody | FixtureType.EntityFeet |FixtureType.EntityWide).toShort
  override def configureBody(): Unit = {
    val fixt = body.getFixtureList.first()
    fixt.setSensor(false)
    fixt.setDensity(0f)
    fixt.setFriction(0f)
    fixt.setRestitution(0f)
    fixt.setFilterData(filter)
    fixt.setUserData(FixtureType.WallBlock)
  }
}
