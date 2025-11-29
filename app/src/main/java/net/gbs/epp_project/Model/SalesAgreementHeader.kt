package net.gbs.epp_project.Model

import com.google.gson.annotations.SerializedName

data class SalesAgreementHeader (
    @SerializedName("salesAgrHeaderId" ) var salesAgrHeaderId : Int? = null,
    @SerializedName("salesAgrNumber"   ) var salesAgrNumber   : String? = null
){
    override fun toString(): String {
        return salesAgrNumber!!
    }
}
