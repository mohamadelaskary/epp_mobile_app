package net.gbs.epp_project.Model.ApiRequestBody

import com.google.gson.annotations.SerializedName

class MobileApproveData (
    @SerializedName("applang"        ) var applang        : String?           = null,
    @SerializedName("userId"         ) var userId         : String?           = null,
    @SerializedName("id"             ) var id             : Int?              = null,
    @SerializedName("plateNo"        ) var plateNo        : String?           = null,
    @SerializedName("driverIdNo"     ) var driverIdNo     : String?           = null,
    @SerializedName("securityNumber" ) var securityNumber : String?           = null,
    @SerializedName("arrivalTime"    ) var arrivalTime    : String?           = null,
    @SerializedName("containers"     ) var containers     : ArrayList<String> = arrayListOf()
)
