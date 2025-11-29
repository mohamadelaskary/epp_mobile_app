package net.gbs.epp_project.Model

import com.google.gson.annotations.SerializedName

data class Container (
    @SerializedName("containerNo" ) var containerNo : String? = null,
    @SerializedName("grossWeight" ) var grossWeight : Int?    = null,
    @SerializedName("lotNo"       ) var lotNo       : String? = null,
    @SerializedName("tareWeight"  ) var tareWeight  : Int?    = null
)
