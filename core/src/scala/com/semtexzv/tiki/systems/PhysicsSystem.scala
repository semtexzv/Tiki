package com.semtexzv.tiki.systems

import com.artemis.annotations.Wire
import com.artemis.{ComponentMapper, Aspect}
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.semtexzv.tiki.Game
import com.semtexzv.tiki.Map.GameMap
import com.semtexzv.tiki.components.PhysComponent

/**
  * Created by Semtexzv on 1/27/2016.
  */
@Wire
class PhysicsSystem(map:GameMap) extends  IteratingSystem(Aspect.all(classOf[PhysComponent])) {
  var pm: ComponentMapper[PhysComponent] = null

  var gravity = -30f

  var dv = new Vector2()
  var maxVelY = 50

  override def process(entityId: Int): Unit = {
    //Skip physics, frame too long , might cause problems
    if (world.delta > 0.4f) {

      return
    }
    val e = pm.get(entityId)

    dv.set(world.delta, world.delta)
    e.position.add(dv.scl(e.velocity))

    e.velocity.add(0,world.delta*gravity)

    e.velocity.y = math.signum(e.velocity.y) * math.min(math.abs(e.velocity.y), maxVelY)

    val minX: Int = (e.position.x-e.hw -2).toInt
    val maxX: Int = (e.position.x+e.hw +2).toInt
    val minY: Int = (e.position.y-e.hh -2).toInt
    val maxY: Int = (e.position.y+e.hh +2).toInt


    var gnd = false
    var again = true
    var iter = 0

    while (again && iter < 3) {
      again = false
      iter += 1

      var ccx = 0f
      var ccy = 0f


      for (y <- minY to maxY) {
        for (x <- minX to maxX) {
          var b = map.getBlock(x, y)
          if (b != null) {


            val left = e.x - e.hw
            val right = e.x + e.hw
            val top = e.y + e.hh
            val bot = e.y - e.hh

            val oLeft = b.x - 0.5f
            val oRight = b.x + 0.5f
            val oTop = b.y + 0.5f
            val oBot = b.y - 0.5f

            var cx = 0f
            var cy = 0f

            if (left < oRight && right > oLeft && top > oBot && bot < oTop) {
              //overlap occured
              again = true
              var nx = e.x - b.x
              var ny = e.y - b.y
              //direction vector
              val x1 = math.abs(right - oLeft)
              val x2 = math.abs(left - oRight)
              val y1 = math.abs(bot - oTop)
              val y2 = math.abs(top - oBot)

              if (nx > 0) {
                //Right
                cx += oRight - left
              }
              else {
                //left
                cx += oLeft - right
              }

              if (ny > 0) {
                cy += oTop - bot
              } else {
                cy += oBot - top
              }
              ccx += cx
              ccy += cy
              if (math.abs(cx) > math.abs(cy)+0.002f) {
                e.position.y += cy
                nx = 0
                ny = math.signum(ny)
                if(ny > 0)
                  gnd = true

              }
              else {
                e.position.x += cx
                nx = math.signum(nx)
                ny = 0
              }
              e.velocity.x -= ((1 + e.bounce) * e.velocity.x * nx) * nx
              e.velocity.y -= ((1 + e.bounce) * e.velocity.y * ny) * ny
            }
          }
        }
      }
    }
    e.grounded = gnd
  }
}
