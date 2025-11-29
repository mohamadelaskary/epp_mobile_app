package net.gbs.epp_project.Model

import com.google.gson.annotations.SerializedName

class CycleCountDetails (
    @SerializedName("id"                 ) var id                 : Int?    = null,
    @SerializedName("cycleCountHeaderId" ) var cycleCountHeaderId : Int?    = null,
    @SerializedName("org_id"             ) var orgId              : Int?    = null,
    @SerializedName("itemId"             ) var itemId             : Int?    = null,
    @SerializedName("itemCode"           ) var itemCode           : String? = null,
    @SerializedName("locatorId"          ) var locatorId          : Int?    = null,
    @SerializedName("qty"                ) var qty                : Double?    = null,
    @SerializedName("dt"                 ) var dt                 : String? = null,
    @SerializedName("tm"                 ) var tm                 : String? = null,
    @SerializedName("userId"             ) var userId             : String?    = null
)
