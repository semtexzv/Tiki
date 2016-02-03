package com.semtexzv.tiki

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Pool
import com.badlogic.gdx.{Input, InputProcessor, Gdx}
import com.badlogic.gdx.graphics.{Color, GL20}
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d._
import com.semtexzv.tiki.entities._
import com.semtexzv.tiki.Map.{DungeonGen, Block, GameMap}
import scala.collection.JavaConversions._
import scala.collection.immutable.HashSet
import scala.util.Random

/**
  * Created by Semtexzv on 1/27/2016.
  */
class GameWorld extends ContactListener  {

  Game.world = this
  object bodyPool extends  Pool[Body]{
    override def newObject(): Body = {
      var bdef = new BodyDef
      bdef.`type` = BodyType.StaticBody
      bdef.fixedRotation = true
      var body = world.createBody(bdef)
      body.setUserData(this)
      var fdef = new FixtureDef
      fdef.density = 1000f
      fdef.friction = 0f
      fdef.restitution = 1f
      var shape = new PolygonShape()
      fdef.shape = shape
      shape.setAsBox(0.5f, 0.5f)
      var fixt = body.createFixture(fdef)
      fixt.setUserData(FixtureType.WallBlock)
      return body
    }
  }
  Box2D.init()
  var render = new Box2DDebugRenderer()

  val world = new box2d.World(new Vector2(0, -10f), false)
  world.setContactListener(this)
  world.setContinuousPhysics(true)

  var map: GameMap = new GameMap(world)

  var player: Player = new Player(world)
  var entities: scala.collection.mutable.Set[Entity] =  scala.collection.mutable.Set[Entity]()
  var removedEntities: scala.collection.mutable.Set[Entity] =  scala.collection.mutable.Set[Entity]()
  var neededBlocks: scala.collection.mutable.Set[Block] =  scala.collection.mutable.Set[Block]()
  var notNeededBlocks: scala.collection.mutable.Set[Block] =  scala.collection.mutable.Set[Block]()

  map.generate()
  //todo, change once generation Y is changed
  spawnEntity(map.gen.levelStart.x,map.gen.levelStart.y,player)
  map.treasureSpawnPoints.foreach(s=> spawnEntity(s.x,s.y,new Treasure(world)))

  var clicked = false

  var clickX : Int = 0
  var clickY : Int = 0

  var batch = new SpriteBatch()
  var blockFlushTime = 0f

  def render(delta: Float): Unit = {
    var delta = 0.02f
    val ccl  =Color.CYAN
    Gdx.gl.glClearColor(ccl.r,ccl.g,ccl.b,1.0f)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)


    Game.camera.position.set(player.position.x, player.position.y, 0)
    Game.camera.zoom = Game.zoom
    Game.camera.update()
    if(delta < 0.100f) {
      world.step(delta/2, 3, 3)
      world.step(delta/2, 3, 3)
    }

    batch.setProjectionMatrix(Game.camera.combined)
    batch.begin()

    if(removedEntities.nonEmpty){
      removedEntities.foreach(a=>{
        a.onDespawn()
        entities.remove(a)
      })
      removedEntities.clear()
    }


    entities.foreach(e => {
      e.update(delta)
      e.render(batch)
      if(e.needsBlocks) {
        val ex: Int = e.x.toInt
        val ey: Int = e.y.toInt
        for (y <- -7 to 7; x <- -7 to 7) {
          var block = map.getBlock(x + ex, y + ey)
          if (block != null) {
            if (x > -4 && x < 4 && y > -4 && y < 4) {
              neededBlocks += block
            }
            else {
              notNeededBlocks += block
            }
          }
        }
      }
    })

    notNeededBlocks --= neededBlocks
    neededBlocks.foreach(_.obtainBody())
    neededBlocks.clear()
    blockFlushTime += delta
    if(blockFlushTime > 2f){
      notNeededBlocks.foreach(_.freeBody())
      notNeededBlocks.clear()
      blockFlushTime = 0f
    }


    map.render(batch)
    render.render(world, Game.camera.combined)
    batch.end()

   println(world.getContactList.count(_ =>true))
    //todo, iterate over all contacts, changing needed variables and calling needed functions like causeDamage
  }


  def spawnEntity(x:Float,y:Float,e: Entity): Unit ={
    e.onSpawn(x,y)
    entities.add(e)
  }
  def despawnEntity(e:Entity): Unit ={
    removedEntities.add(e)
  }

  def onTreasureCollected(treasure:Treasure): Unit ={
    Gdx.app.log("Treasure","Collected")
    despawnEntity(treasure)
  }


  override def endContact(contact: Contact): Unit = {
    val typeA = contact.getFixtureA.getUserData.asInstanceOf[FixtureControl]
    val typeB = contact.getFixtureB.getUserData.asInstanceOf[FixtureControl]
    typeA.onEndContact(typeB)
    typeB.onEndContact(typeA)
  }

  override def beginContact(contact: Contact): Unit = {
    val typeA = contact.getFixtureA.getUserData.asInstanceOf[FixtureControl]
    val typeB = contact.getFixtureB.getUserData.asInstanceOf[FixtureControl]
    typeA.onBeginContact(typeB)
    typeB.onBeginContact(typeA)
  }

  override def preSolve(contact: Contact, oldManifold: Manifold): Unit = {
    val typeA = contact.getFixtureA.getUserData.asInstanceOf[FixtureControl]
    val typeB = contact.getFixtureB.getUserData.asInstanceOf[FixtureControl]
    contact.setEnabled(typeA.shouldCollide(typeB,contact.getWorldManifold.getNormal) &&
      typeB.shouldCollide(typeA,contact.getWorldManifold.getNormal.scl(-1f)))
  }
  override def postSolve(contact: Contact, impulse: ContactImpulse): Unit = {

  }


}

