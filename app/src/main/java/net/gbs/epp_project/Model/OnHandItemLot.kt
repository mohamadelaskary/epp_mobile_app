package net.gbs.epp_project.Model

import com.google.gson.annotations.SerializedName

data class OnHandItemLot (
    @SerializedName("orG_ID"               ) var orgId              : Int?    = null,
    @SerializedName("orG_CODE"             ) var orgCode            : String? = null,
    @SerializedName("orG_NAME"             ) var orgName            : String? = null,
    @SerializedName("inventorY_ITEM_ID"    ) var inventoryItemId    : Int?    = null,
    @SerializedName("iteM_CODE"            ) var itemCode           : String? = null,
    @SerializedName("iteM_DESCRIPTION"     ) var itemDescription    : String? = null,
    @SerializedName("maiN_CATEGORY"        ) var mainCategory       : String? = null,
    @SerializedName("category"             ) var category           : String? = null,
    @SerializedName("subinventory"         ) var subinventory       : String? = null,
    @SerializedName("subinventoryDesc"     ) var subinventoryDesc   : String? = null,
    @SerializedName("inventorY_LOCATOR_ID" ) var inventoryLocatorId : Int?    = null,
    @SerializedName("locator"              ) var locator            : String? = null,
    @SerializedName("lot_Num"              ) var lotNum             : String? = null,
    @SerializedName("onhand"               ) var onhand             : Double?    = null,
    @SerializedName("uom"                  ) var uom                : String? = null,
    @SerializedName("availblE_QTY"         ) var availbleQty        : Double?    = null
)