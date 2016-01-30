package com.semtexzv.tiki.inventory

import com.semtexzv.tiki.inventory.ItemType.ItemType

/**
  * Created by Semtexzv on 1/30/2016.
  */
class Item(var typ:ItemType,var count:Int) {

  def maxStack() :Int ={
   return typ match {
      case ItemType.Dirt => 64
      case ItemType.Stone => 64
      case _ => 64
    }
  }
}
