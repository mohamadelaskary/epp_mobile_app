package net.gbs.epp_project.Model

import com.google.gson.annotations.SerializedName

data class Governorate (
    @SerializedName("id"   ) var id   : Int?    = null,
    @SerializedName("name" ) var name : String? = null
) {
    override fun toString(): String {
        return name!!
    }
}
