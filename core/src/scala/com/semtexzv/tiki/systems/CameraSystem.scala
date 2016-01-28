package com.semtexzv.tiki.systems

import com.artemis.annotations.Wire
import com.artemis.{ComponentMapper, Entity, Aspect}
import com.artemis.systems.{IteratingSystem, EntityProcessingSystem}
import com.semtexzv.tiki.Game
import com.semtexzv.tiki.components.{PhysComponent, FocusCompoent}

/**
  * Created by Semtexzv on 1/27/2016.
  */
@Wire
class CameraSystem extends
  IteratingSystem(Aspect.all(classOf[FocusCompoent]).all(classOf[PhysComponent])){
  var pm : ComponentMapper[PhysComponent] = null

  override def process(entityId: Int): Unit = {
    val e =pm.get(entityId)
    Game.camera.position.set(e.position.x,e.position.y,0)
    Game.camera.zoom = Game.zoom
    Game.camera.update()
  }
}
