package com.semtexzv.tiki.Map.blocks

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Filter
import com.semtexzv.tiki.FixtureType
import com.semtexzv.tiki.Map.{BlockType, Block}
import com.semtexzv.tiki.entities.FixtureControl

/**
  * Created by Semtexzv on 1/31/2016.
  */
class WallBlock(x:Int,y:Int) extends Block(x,y,BlockType.Wall){
  object control extends  FixtureControl{
    override var typ: Short = FixtureType.WallBlock

    override def shouldCollide(other: FixtureControl, normal: Vector2): Boolean = {true}

    override def onEndContact(other: FixtureControl): Unit = {}

    override def onBeginContact(other: FixtureControl): Unit = {}
  }
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
    fixt.setUserData(control)
  }
}
