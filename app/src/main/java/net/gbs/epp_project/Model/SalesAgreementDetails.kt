package net.gbs.epp_project.Model

import com.google.gson.annotations.SerializedName

data class SalesAgreementDetails (
    @SerializedName("salesAgrDetailId"     ) var salesAgrDetailId     : Int?    = null,
    @SerializedName("itemCode"             ) var itemCode             : String? = null,
    @SerializedName("salesAgreementLineId" ) var salesAgreementLineId : Int?    = null
){
    override fun toString(): String {
        return "$salesAgreementLineId - $itemCode"
    }
}
