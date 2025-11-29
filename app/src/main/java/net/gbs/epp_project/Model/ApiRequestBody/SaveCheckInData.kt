package net.gbs.epp_project.Model.ApiRequestBody

import com.google.gson.annotations.SerializedName

data class SaveCheckInData (
    @SerializedName("applang"     ) var applang     : String? = null,
    @SerializedName("userId"      ) var userId      : String? = null,
    @SerializedName("id"          ) var id          : Int?    = null,
    @SerializedName("dateCheckIn" ) var dateCheckIn : String? = null
)