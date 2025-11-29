package net.gbs.epp_project.Model

import com.google.gson.annotations.SerializedName

data class AuditOrderSubinventory(
    @SerializedName("subInventoryId"   ) var subInventoryId   : Int?    = null,
    @SerializedName("subInventoryCode" ) var subInventoryCode : String? = null,
    @SerializedName("subInventoryDesc" ) var subInventoryDesc : String? = null,
    @SerializedName("orgId"            ) var orgId            : Int?    = null,
    @SerializedName("orgCode"          ) var orgCode          : String? = null,
    @SerializedName("orgName"          ) var orgName          : String? = null,
    @SerializedName("locatorId"        ) var locatorId        : Int?    = null,
    @SerializedName("locatorCode"      ) var locatorCode      : String? = null,
    @SerializedName("itemId"           ) var itemId           : Int?    = null,
    @SerializedName("itemCode"         ) var itemCode         : String? = null,
    @SerializedName("itemDescription"  ) var itemDescription  : String? = null,
    @SerializedName("uom"              ) var uom              : String? = null,
    @SerializedName("countingQty"      ) var countingQty      : Double? = null,
    @SerializedName("onHandQty"      ) var onHandQty      : Double? = null,
) {
    override fun toString(): String {
        return subInventoryDesc.toString()
    }
}
