package com.semtexzv.tiki.systems

import com.artemis.annotations.Wire
import com.artemis.{ComponentMapper, Aspect}
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.semtexzv.tiki.Game
import com.semtexzv.tiki.components.{PhysComponent, FocusCompoent}

/**
  * Created by Semtexzv on 1/27/2016.
  */
@Wire
class InputSystem extends
  IteratingSystem(Aspect.all(classOf[FocusCompoent]).all(classOf[PhysComponent])){
  var pm : ComponentMapper[PhysComponent] = null
  val acc = 100f
  var mx = 10f
  override def process(entityId: Int): Unit = {
    val e =pm.get(entityId)
    if(Gdx.input.isKeyPressed(Keys.RIGHT)){
      e.velocity.x= Math.min(e.velocity.x+acc*world.delta,mx)
    }
    else if(Gdx.input.isKeyPressed(Keys.LEFT)){
      e.velocity.x= Math.max(e.velocity.x-acc*world.delta,-mx)
    }
    else {
      e.velocity.x *=0
    }
    if(Gdx.input.isKeyPressed(Keys.UP) && e.grounded ){
      e.velocity.y+=15
    }
  }
}