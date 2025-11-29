package net.gbs.epp_project.Model.ApiRequestBody

import com.google.gson.annotations.SerializedName
import net.gbs.epp_project.Model.Container

data class SaveNewVehicleRecordData (
    @SerializedName("applang"           ) var applang           : String?                     = null,
    @SerializedName("userId"            ) var userId            : String?                     = null,
    @SerializedName("bookingId"         ) var bookingId         : Int?                        = null,
    @SerializedName("salesOrderNumber"  ) var salesOrderNumber  : String?                     = null,
    @SerializedName("salesAgrDetailId"  ) var salesAgrDetailId  : Int?                        = null,
    @SerializedName("customerName"      ) var customerName      : String?                     = null,
    @SerializedName("plateNo"           ) var plateNo           : String?                     = null,
    @SerializedName("listOfTrailerNos"  ) var listOfTrailerNos  : MutableList<String>         = arrayListOf(),
    @SerializedName("listOfContainers"  ) var listOfContainers  : MutableList<Container>      = arrayListOf(),
    @SerializedName("driverName"        ) var driverName        : String?                     = null,
    @SerializedName("driverPhone"       ) var driverPhone       : String?                     = null,
    @SerializedName("driverIdNo"        ) var driverIdNo        : String?                     = null,
    @SerializedName("governorateId"     ) var governorateId     : Int?                        = null,
    @SerializedName("isCreatedByMobile" ) var isCreatedByMobile : Boolean?                    = true
)
