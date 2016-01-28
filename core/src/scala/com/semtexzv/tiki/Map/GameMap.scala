package com.semtexzv.tiki.Map

import com.badlogic.gdx.physics.box2d.World
import com.semtexzv.tiki.Game

import scala.util.Random

/**
  * Created by Semtexzv on 1/27/2016.
  */
class GameMap() {
  var blocks  = new Array[Block](Game.MapWidth*Game.MapHeight)
  def setBlock(x:Int,y:Int,value:Block) = blocks(y*Game.MapWidth+x) = value
  def getBlock(x:Int,y:Int) : Block = {
    val i = y*Game.MapWidth+x
    if(i<blocks.length&& i>=0)
      blocks(y*Game.MapWidth+x)
    else
      null
  }
  for (y<-0 until Game.MapHeight)
    for( x<-0 until Game.MapWidth){
      setBlock(x,y,(if(Random.nextInt(5)<1) new Block(x,y) else null))
    }

}
