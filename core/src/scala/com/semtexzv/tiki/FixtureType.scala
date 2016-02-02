package com.semtexzv.tiki

/**
  * Created by Semtexzv on 1/28/2016.
  */
object FixtureType {

  /* Charater based entities - Player, Enemy*/
  final val EntityBody: Short = 1
  final val EntityFeet: Short = 2
  final val EntityWide: Short = 4
  final val EntityCore:Short = 8
  /* Blocks */
  final val WallBlock: Short = 16
  final val LadderBlock: Short = 32
  /* Aditional entites, coins spikes level exit */
  final val Treasure : Short = 64
  final val Spikes: Short = 128
  final val LevelExit :Short = 256
  /* There will be 2 levels of filtering,
     First, box2d standard bitflag filtering for body,core,feet and wide fixtures with blocks,
      Then User filtering based on function Entit.shouldCollideWithEntiy(e:Entity) that will be used for
      filtering collisions between player/enemy , enemy/Treasure.
      todo, finish this tomorrow
   */
}
