package net.gbs.epp_project.Model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class AuditOrder(
    @SerializedName("physicalInventoryHeaderId" ) var physicalInventoryHeaderId : Int?                      = null,
    @SerializedName("orderNo"                   ) var orderNo                   : String?                   = null,
    @SerializedName("orderDesc"                 ) var orderDesc                 : String?                   = null,
    @SerializedName("orderStatus"               ) var orderStatus               : String?                   = null,
    @SerializedName("orderStartDate"            ) var orderStartDate            : String?                   = null,
    @SerializedName("userIdCreated"             ) var userIdCreated             : String?                   = null,
    @SerializedName("dateCreated"               ) var dateCreated               : String?                   = null,
    @SerializedName("isClosed"                  ) var isClosed                  : Boolean?                  = null,
    @SerializedName("userIdClosed"              ) var userIdClosed              : String?                   = null,
    @SerializedName("dateClosed"                ) var dateClosed                : String?                   = null,
    @SerializedName("subInventories"            ) var subInventories            : ArrayList<AuditOrderSubinventory> = arrayListOf()

){



    fun editedSubInventoriesList():List<AuditOrderSubinventory>{
        val editedSubInventories = mutableListOf<AuditOrderSubinventory>()
        subInventories.forEach { subInv ->
            val alreadyAddedSubInv = editedSubInventories.find { it.subInventoryId == subInv.subInventoryId }
            if (alreadyAddedSubInv == null)
                editedSubInventories.add(subInv)
        }
        return editedSubInventories
    }

    fun getLocatorsForInSubInventory(subInvCode:String):List<AuditOrderSubinventory>{
        val locators = mutableListOf<AuditOrderSubinventory>()
        subInventories.forEach {locator->
            if (locator.subInventoryCode == subInvCode){
                val alreadyAddedLocator = locators.find { it.locatorCode== locator.locatorCode}
                if (alreadyAddedLocator==null){
                    val notScanned = subInventories.filter { it.locatorCode == locator.locatorCode && it.countingQty==0.0 }
                    locator.isFullyScannedLocator = notScanned.isEmpty()
                    locators.add(locator)
                }
            }
        }
        return locators
    }

    fun getItemsForLocatorCodeAndSubInventory(subInvCode:String,locatorCode:String):List<AuditOrderSubinventory>{
        val items = mutableListOf<AuditOrderSubinventory>()
        subInventories.forEach {
            if (it.subInventoryCode==subInvCode&&it.locatorCode==locatorCode){
                items.add(it)
            }
        }
        return items
    }

    fun subInventoriesString():String{
        var subInventoriesString = ""
        val editedSubInventoryList = editedSubInventoriesList()
        editedSubInventoryList.forEachIndexed { index, subInventory ->
            subInventoriesString += if (index==0) {
                "${subInventory.subInventoryCode}"
            } else {
                ", ${subInventory.subInventoryCode}"
            }
        }
        return subInventoriesString
    }
    companion object {
        fun toJson(auditOrder:AuditOrder) = Gson().toJson(auditOrder)
        fun fromJson(auditOrder:String) = Gson().fromJson(auditOrder,AuditOrder::class.java)
    }
}
