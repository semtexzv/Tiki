package com.semtexzv.tiki.systems

import com.artemis.annotations.Wire
import com.artemis.{ComponentMapper, Aspect}
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.semtexzv.tiki.Game
import com.semtexzv.tiki.Map.GameMap
import com.semtexzv.tiki.components.{PhysComponent, FocusCompoent}
@Wire
class DebugRenderSystem(map:GameMap) extends
  IteratingSystem(Aspect.all(classOf[PhysComponent])){
  var pm : ComponentMapper[PhysComponent] = null



  override def begin(): Unit = {
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    Game.renderer.setProjectionMatrix(Game.camera.combined)
    Game.renderer.begin(ShapeType.Line)
    map.blocks.foreach(a=> {
      if(a!=null) {
        Game.renderer.rect(a.x - 0.5f, a.y - 0.5f, 0.9f, 0.9f)
      }
    })
  }

  override def end(): Unit = {
    Game.renderer.end()
  }

  override def process(entityId: Int): Unit = {
    val pos =pm.get(entityId)
    Game.renderer.rect(pos.position.x-pos.hw,pos.position.y-pos.hh,pos.hw*2,pos.hh*2);


  }
}
