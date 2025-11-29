package net.gbs.epp_project.Model

import com.google.gson.annotations.SerializedName

data class LotQty (
    @SerializedName("locator_id" ) var locatorId : String? = null,
    @SerializedName("lotName" ) var lotName : String? = null,
    @SerializedName("qty"     ) var qty     : Double?    = null,
    var locatorFromCode:String? = null,
){
    override fun toString(): String {
        return "$lotName ( $qty )"
    }
}