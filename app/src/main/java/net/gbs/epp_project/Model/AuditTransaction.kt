package net.gbs.epp_project.Model

import com.google.gson.annotations.SerializedName

data class AuditTransaction (
    @SerializedName("physicalInventoryCountingId" ) var physicalInventoryCountingId : Int?    = null,
    @SerializedName("physicalInventoryHeaderId"   ) var physicalInventoryHeaderId   : Int?    = null,
    @SerializedName("subInventoryId"              ) var subInventoryId              : Int?    = null,
    @SerializedName("subInventoryCode"            ) var subInventoryCode            : String? = null,
    @SerializedName("subInventoryDesc"            ) var subInventoryDesc            : String? = null,
    @SerializedName("orgId"                       ) var orgId                       : Int?    = null,
    @SerializedName("orgCode"                     ) var orgCode                     : String? = null,
    @SerializedName("orgName"                     ) var orgName                     : String? = null,
    @SerializedName("locatorId"                   ) var locatorId                   : Int?    = null,
    @SerializedName("locatorCode"                 ) var locatorCode                 : String? = null,
    @SerializedName("buildingCode"                ) var buildingCode                : String? = null,
    @SerializedName("itemId"                      ) var itemId                      : Int?    = null,
    @SerializedName("inventoryItemId"             ) var inventoryItemId             : Int?    = null,
    @SerializedName("itemCode"                    ) var itemCode                    : String? = null,
    @SerializedName("itemDescription"             ) var itemDescription             : String? = null,
    @SerializedName("category"                    ) var category                    : String? = null,
    @SerializedName("subCategory"                 ) var subCategory                 : String? = null,
    @SerializedName("qty"                         ) var qty                         : Double?    = null,
    @SerializedName("uom"                         ) var uom                         : String? = null,
    @SerializedName("onHandQty"                   ) var onHandQty                   : Double?    = null,
    @SerializedName("userIdCount"                 ) var userIdCount                 : String? = null,
    @SerializedName("dateCount"                   ) var dateCount                   : String? = null,
    @SerializedName("userName_Count"              ) var userNameCount               : String? = null
)