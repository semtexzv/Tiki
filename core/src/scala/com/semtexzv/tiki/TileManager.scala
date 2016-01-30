package com.semtexzv.tiki

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.semtexzv.tiki.Map.BlockType
import com.semtexzv.tiki.Map.BlockType
import com.semtexzv.tiki.Map.BlockType.BlockType

import scala.collection.immutable.HashMap

/**
  * Created by Semtexzv on 1/30/2016.
  */
object TileManager {
  lazy val sheet : Texture  = new Texture("sheetGS.png")


  final val testBlock = 1

  final val TL = 0
  final val TR = 1
  final val BL = 2
  final val BR = 3


  /*
Tile number is combination of these numbers, if the bit is set, the tile is missing/loaded ?
1   64   2
16   0   32
4   128  8
 */


  final val Left = 16
  final val Right = 32
  final val Top = 64
  final val Bottom = 128

  final val DiagTopLeft = 1
  final val DiagTopRight = 2
  final val DiagBottomLeft = 4
  final val DiagBottomRight = 8

  final val MaskTL = Top+Left+DiagTopLeft
  final val MaskTR = Top+Right+DiagTopRight
  final val MaskBL = Bottom+Left+DiagBottomLeft
  final val MaskBR = Bottom+Right+DiagBottomRight

  final val masks: Array[Int] = Array(MaskTL,MaskTR,MaskBL,MaskBR)

  final val StateIncrements : Array[Int] = Array(DiagBottomLeft,Bottom,DiagBottomRight,
    Left,0,Right,
    DiagTopLeft,Top, DiagTopRight)
  def getIncrement(x:Int, y:Int): Int ={
    StateIncrements(x+1+(y+1)*3)
  }
  var testBlockMap : HashMap[Int,TextureRegion] = HashMap(
    Top+Left -> getRegion(0,0),
    Top -> getRegion(1,0),
    Top+Right -> getRegion(2,0),
    Left -> getRegion(0,1),
    0 -> getRegion(1,1),
    Right-> getRegion(2,1),
    Bottom+Left -> getRegion(0,2),
    Bottom -> getRegion(1,2),
    Bottom+Right -> getRegion(2,2),

    DiagTopLeft -> getRegion(4,1),
    DiagTopRight -> getRegion(3,1),
    DiagBottomLeft  -> getRegion(4,0),
    DiagBottomRight -> getRegion(3,0)
  )

  var tileMap : HashMap[BlockType,HashMap[Int,TextureRegion]] = HashMap(BlockType.Dirt -> testBlockMap)

  def getSubTile(blockType:BlockType,tileState:Int,tileIndex:Int): TextureRegion = {
    if(tileMap.contains(blockType)) {
      val s = tileState & masks(tileIndex)
      if (s > 0x0F) {
       return tileMap(blockType)(tileState & masks(tileIndex) & 0xF0)
      }
      else {
        return tileMap(blockType)(tileState & masks(tileIndex) & 0x0f)
      }
    }
     null
  }

  final val SubTileSize = 16
  private def  getRegion(x:Int,y:Int): TextureRegion ={
     new TextureRegion(sheet,x*SubTileSize,y*SubTileSize,SubTileSize,SubTileSize)
  }

}
