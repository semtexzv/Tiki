package com.semtexzv.tiki.inventory

import com.badlogic.gdx.Gdx

/**
  * Created by Semtexzv on 1/30/2016.
  */
class Inventory {
  val items :Array[Item] = new Array[Item](40) //40 slots, 5 rows by 8/4 rows by 10 , whatever will look better

  def addItem(newItem:Item): Boolean ={
    for(i<- 0 until items.length){
      if(items(i) == null){
        if(newItem.count < newItem.maxStack()) {
          items(i) = newItem
        }
        else {
          items(i) = new Item(newItem.typ,newItem.maxStack())
          newItem.count -= newItem.maxStack()
        }
        return true
      }
      if(items(i) != null || (items(i).typ == newItem.typ)){

        var availableToAdd = items(i).maxStack() - items(i).count
        if(availableToAdd> newItem.count){
          items(i).count += availableToAdd
          //todo, Finish this,...too tired
          Gdx.app.log("Inventory","Added "+availableToAdd+" items to slot "+i)
          newItem.count -= availableToAdd
        }
        else {

        }
      }
    }
    newItem.count == 0
  }

}
