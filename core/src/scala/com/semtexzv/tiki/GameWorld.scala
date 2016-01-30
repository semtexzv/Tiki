package com.semtexzv.tiki

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Pool
import com.badlogic.gdx.{Input, InputProcessor, Gdx}
import com.badlogic.gdx.graphics.{Color, GL20}
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d._
import com.semtexzv.tiki.entities.{ItemDrop, Player, Entity, EntityType}
import EntityType.EntityType
import com.semtexzv.tiki.Map.{Block, GameMap}

import scala.collection.immutable.HashSet

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
      fdef.density = 0f
      fdef.friction = 0f
      fdef.restitution = 0f
      var shape = new PolygonShape()
      fdef.shape = shape
      shape.setAsBox(0.5f, 0.5f)
      var fixt = body.createFixture(fdef)
      fixt.setUserData(FixtureType.GroundBlock)
      return body
    }
  }


  Box2D.init()
  var render = new Box2DDebugRenderer()

  val world = new box2d.World(new Vector2(0, -30f), true)
  world.setContactListener(this)
  world.setContinuousPhysics(true)

  var player: Player = new Player(1, Game.ChunkHeight,world)
  var entities: scala.collection.mutable.Set[Entity] =  scala.collection.mutable.Set[Entity]()
  var removedEntities: scala.collection.mutable.Set[Entity] =  scala.collection.mutable.Set[Entity]()
  spawnEntity(player )

  var neededBlocks: scala.collection.mutable.Set[Block] =  scala.collection.mutable.Set[Block]()
  var notNeededBlocks: scala.collection.mutable.Set[Block] =  scala.collection.mutable.Set[Block]()

  var map: GameMap = new GameMap(world)
  var retainTime = 0f
  var clicked = false

  var clickX : Int = 0
  var clickY : Int = 0


  var batch = new SpriteBatch()
  var blockFlushTime = 0f
  def render(delta: Float): Unit = {
    val ccl  =Color.CYAN
    Gdx.gl.glClearColor(ccl.r,ccl.g,ccl.b,1.0f)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

    Game.camera.position.set(player.position.x, player.position.y, 0)
    Game.camera.zoom = Game.zoom
    Game.camera.update()

    if(clicked){
      var b = map.getBlock(clickX,clickY)
      if(b != null){
        b.damage(delta*100)
        if(b.health< 0){
          b.freeBody()
          map.setBlock(clickX,clickY,null)
          map.updateNighbors(clickX,clickY)
          spawnEntity(new ItemDrop(clickX,clickY,b.getDrop,world))
        }
      }
    }


    entities.foreach(e => {
      e.update(delta)
      val ex: Int = e.x.toInt
      val ey: Int = e.y.toInt
      for (y<- -7 to 7) {
        for (x <- -7 to 7) {
          var block = map.getBlock(x+ex,y+ey)
          if(block!= null && block.typ != Game.Air){
            if(x > -4 && x < 4 && y> -4 && y< 4){
              neededBlocks += block
            }
            else{
              notNeededBlocks += block
            }
          }
        }
      }
    })
    if(removedEntities.nonEmpty){
      removedEntities.foreach(a=>{
        a.onDespawn()
        entities.remove(a)
      })
      removedEntities.clear()
    }

    notNeededBlocks --= neededBlocks
    neededBlocks.foreach(_.obtainBody())
    neededBlocks.clear()
    blockFlushTime += delta
    if(blockFlushTime > 2f){
      notNeededBlocks.foreach(_.freeBody())
      notNeededBlocks.clear()
      blockFlushTime = 0f
    }

    world.step(delta, 3, 3)
    //println("Time: "+(System.nanoTime()-time)/1000000f)

    map.render(0,0)
    render.render(world, Game.camera.combined)
  }

  def spawnEntity(e: Entity): Unit ={
    e.onSpawn()
    entities.add(e)
  }
  def despawnEntity(e:Entity): Unit ={
    removedEntities.add(e)
  }

  override def postSolve(contact: Contact, impulse: ContactImpulse): Unit = {

  }

  override def endContact(contact: Contact): Unit = {
    var typeA = contact.getFixtureA.getUserData.asInstanceOf[Int]
    var typeB = contact.getFixtureB.getUserData.asInstanceOf[Int]
    if (typeA == FixtureType.PlayerFeet && typeB == FixtureType.GroundBlock) {
      player.gndContacts -= 1
    }
    if (typeA == FixtureType.PlayerWide && typeB == FixtureType.GroundBlock) {
      player.wideContacts -= 1
    }
  }

  override def beginContact(contact: Contact): Unit = {
    val typeA = contact.getFixtureA.getUserData.asInstanceOf[Int]
    val typeB = contact.getFixtureB.getUserData.asInstanceOf[Int]
    if (typeA == FixtureType.PlayerFeet && typeB == FixtureType.GroundBlock) {
      player.gndContacts += 1
    }
    if (typeA == FixtureType.PlayerWide && typeB == FixtureType.GroundBlock) {
      player.wideContacts += 1
    }
  }

  override def preSolve(contact: Contact, oldManifold: Manifold): Unit = {


    val typeA = contact.getFixtureA.getUserData.asInstanceOf[Int]
    val typeB = contact.getFixtureB.getUserData.asInstanceOf[Int]
    if (typeA == FixtureType.PlayerBody)
      if (typeB == FixtureType.GroundBlock) {
        if (player.gndContacts <= 0 && contact.getWorldManifold.getNormal.y == -1) {
          contact.setEnabled(false)
        }
        if (player.wideContacts <= 0 && contact.getWorldManifold.getNormal.x != 0) {
          contact.setEnabled(false)
        }
      }
      else if (typeB == FixtureType.ItemDrop) {
        var drop = contact.getFixtureB.getBody.getUserData.asInstanceOf[ItemDrop]
        if (player.inventory.addItem(drop.item)) {
          despawnEntity(contact.getFixtureB.getBody.getUserData.asInstanceOf[Entity])
        }
        contact.setEnabled(false)
      }
  }


}

