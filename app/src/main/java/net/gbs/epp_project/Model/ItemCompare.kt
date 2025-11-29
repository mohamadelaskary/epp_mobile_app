package net.gbs.epp_project.Model

import com.google.gson.annotations.SerializedName

data class ItemCompare (
    @SerializedName("inventoryItemId"   ) var inventoryItemId   : Int?     = null,
    @SerializedName("itemId"            ) var itemId            : Int?     = null,
    @SerializedName("itemCode"          ) var itemCode          : String?  = null,
    @SerializedName("itemDescription"   ) var itemDescription   : String?  = null,
    @SerializedName("onHandQty"         ) var onHandQty         : Double?     = null,
    @SerializedName("locatorIdOnHand"   ) var locatorIdOnHand   : Int?     = null,
    @SerializedName("locatorCodeOnHand" ) var locatorCodeOnHand : String?  = null,
    @SerializedName("isScanned"         ) var isScanned         : Boolean? = null,
    @SerializedName("countingQty"       ) var countingQty       : Double?     = null
)
