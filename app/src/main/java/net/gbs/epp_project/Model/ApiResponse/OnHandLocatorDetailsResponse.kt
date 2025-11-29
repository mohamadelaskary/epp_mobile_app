package net.gbs.epp_project.Model.ApiResponse

import com.google.gson.annotations.SerializedName
import net.gbs.epp_project.Base.BaseResponse

class OnHandLocatorDetailsResponse : BaseResponse<List<LocatorItems>>() {
    @SerializedName("getList")
    val getList: List<LocatorItems> = listOf()
    override fun getData(): List<LocatorItems> {
        return getList
    }
}

data class LocatorItems(
    @SerializedName("orG_ID")
    val orgId: Int,

    @SerializedName("orG_CODE")
    val orgCode: String,

    @SerializedName("orG_NAME")
    val orgName: String,

    @SerializedName("inventorY_ITEM_ID")
    val inventoryItemId: Int,

    @SerializedName("iteM_CODE")
    val itemCode: String,

    @SerializedName("iteM_DESCRIPTION")
    val itemDescription: String,

    @SerializedName("maiN_CATEGORY")
    val mainCategory: String,

    @SerializedName("category")
    val category: String,

    @SerializedName("subinventory")
    val subinventory: String,

    @SerializedName("onhand")
    val onHand: Double,

    @SerializedName("uom")
    val uom: String,

    @SerializedName("locator")
    val locator: String,

    @SerializedName("availblE_QTY")
    val availableQty: Double
)
