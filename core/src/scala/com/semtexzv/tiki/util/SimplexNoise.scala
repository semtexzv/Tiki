package com.semtexzv.tiki.util

import java.util.Random

import com.badlogic.gdx.math.RandomXS128

/**
  * Created by Semtexzv on 1/29/2016.
  */
class SimplexNoise(seed:Long) {

  private object Grads{
    final val grad3: Array[Array[Int]] = Array(Array(1, 1, 0), Array(-1, 1, 0), Array(1, -1, 0), Array(-1, -1, 0), Array(1, 0, 1), Array(-1, 0, 1), Array(1, 0, -1), Array(-1, 0, -1), Array(0, 1, 1), Array(0, -1, 1), Array(0, 1, -1), Array(0, -1, -1))
  }

  private val perm: Array[Int] = new Array[Int](512)
  private val rand: Random = new RandomXS128(seed)

  for(i<- 0 until 256 ){
    perm(i) = rand.nextInt(255)
  }
  for(i<- 256 until 512){
    perm(i) = perm(i & 255)
  }

  private def fastfloor(x: Double): Int = {
    if (x > 0) x.toInt else x.toInt - 1
  }

  private def dot(g: Array[Int], x: Double, y: Double): Double = {
     g(0) * x + g(1) * y
  }

  //Range -1..1
  def noise(xin: Double, yin: Double): Double = {
    var n0: Double = 0
    var n1: Double = 0
    var n2: Double = 0

    val F2: Double = 0.5 * (Math.sqrt(3.0) - 1.0)

    val s: Double = (xin + yin) * F2

    val i: Int = fastfloor(xin + s)
    val j: Int = fastfloor(yin + s)

    val G2: Double = (3.0 - Math.sqrt(3.0)) / 6.0
    val t: Double = (i + j) * G2
    val X0: Double = i - t
    val Y0: Double = j - t
    val x0: Double = xin - X0
    val y0: Double = yin - Y0

    var i1: Int = 0
    var j1: Int = 0

    if (x0 > y0) {
      i1 = 1
      j1 = 0
    }
    else {
      i1 = 0
      j1 = 1
    }
    val x1: Double = x0 - i1 + G2
    val y1: Double = y0 - j1 + G2
    val x2: Double = x0 - 1.0 + 2.0 * G2
    val y2: Double = y0 - 1.0 + 2.0 * G2
    val ii: Int = i & 255
    val jj: Int = j & 255
    val gi0: Int = perm(ii + perm(jj)) % 12
    val gi1: Int = perm(ii + i1 + perm(jj + j1)) % 12
    val gi2: Int = perm(ii + 1 + perm(jj + 1)) % 12
    var t0: Double = 0.5 - x0 * x0 - y0 * y0
    if (t0 < 0) n0 = 0.0
    else {
      t0 *= t0
      n0 = t0 * t0 * dot(Grads.grad3(gi0), x0, y0)
    }
    var t1: Double = 0.5 - x1 * x1 - y1 * y1
    if (t1 < 0) n1 = 0.0
    else {
      t1 *= t1
      n1 = t1 * t1 * dot(Grads.grad3(gi1), x1, y1)
    }
    var t2: Double = 0.5 - x2 * x2 - y2 * y2
    if (t2 < 0) n2 = 0.0
    else {
      t2 *= t2
      n2 = t2 * t2 * dot(Grads.grad3(gi2), x2, y2)
    }
     70.0 * (n0 + n1 + n2)
  }
  //Range 0..1
  def octavedNoise(x: Double, y: Double, octaves: Int, persistence: Double): Double = {
    var total: Double = 0
    var frequency: Double = 1
    var amplitude: Double = 1
    var maxValue: Double = 0
    var i: Int = 0
    while (i < octaves) {
      {
        total += noise(x * frequency, y * frequency) * amplitude
        maxValue += amplitude
        amplitude *= persistence
        frequency *= 2
      }
      i += 1
      i - 1
    }
    (total / maxValue)/2f+0.5f
  }
  //Range is 0..1
  def ridgedNoise(x: Double, y: Double, octaves: Int, persistence: Double): Double = {
    var total: Double = 0
    var frequency: Double = 1
    var amplitude: Double = 1
    var maxValue: Double = 0
    var i: Int = 0
    while (i < octaves) {
      {
        total += math.abs(noise(x * frequency, y * frequency) * amplitude)
        maxValue += amplitude
        amplitude *= persistence
        frequency *= 2
      }
      i += 1
      i - 1
    }
    total / maxValue
  }
}
