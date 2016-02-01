package com.semtexzv.tiki.Map.blocks

import com.badlogic.gdx.physics.box2d.Filter
import com.semtexzv.tiki.Map.{Block, BlockType}
import com.semtexzv.tiki.{FixtureType, Game}

/**
  * Created by Semtexzv on 1/31/2016.
  */
class LadderBlock(x:Int,y:Int) extends Block(x,y,BlockType.Ladder){
  val filter = new Filter()
  filter.categoryBits = FixtureType.LadderBlock
  filter.maskBits = -1
  //Todo, proper ladder code, curent one doesnt allow jumps from the top and bugs out
  override def configureBody(): Unit = {
    val fixt = body.getFixtureList.first()
    fixt.setSensor(true)
    fixt.setDensity(0f)
    fixt.setFriction(0f)
    fixt.setRestitution(0f)
    fixt.setFilterData(filter)
    fixt.setUserData(FixtureType.LadderBlock)
  }
}
