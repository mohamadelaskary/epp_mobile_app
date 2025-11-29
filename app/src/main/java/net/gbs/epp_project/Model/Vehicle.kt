package net.gbs.epp_project.Model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class Vehicle(
    @SerializedName("id"               ) var id               : Int?    = null,
    @SerializedName("salesAgrHeaderId" ) var salesAgrHeaderId : Int?    = null,
    @SerializedName("salesOrderNumber" ) var salesOrderNumber : String? = null,
    @SerializedName("salesAgrDetailId" ) var salesAgrDetailId : Int?    = null,
    @SerializedName("customerName"     ) var customerName     : String? = null,
    @SerializedName("agreementDate"    ) var agreementDate    : String? = null,
    @SerializedName("itemCode"         ) var itemCode         : String? = null,
    @SerializedName("plateNo"          ) var plateNo          : String? = null,
    @SerializedName("trailerNo"        ) var trailerNo        : String? = null,
    @SerializedName("listOfContainers" ) var listOfContainers : List<String> = listOf(),
    @SerializedName("driverName"       ) var driverName       : String? = null,
    @SerializedName("receivingDate"    ) var receivingDate    : String? = null,
    @SerializedName("driverPhone"      ) var driverPhone      : String? = null,
    @SerializedName("driverIdNo"       ) var driverIdNo       : String? = null,
    @SerializedName("governorateId"    ) var governorateId    : Int?    = null,
    @SerializedName("governorate"      ) var governorate      : String? = null,
    @SerializedName("dateAdd"          ) var dateAdd          : String? = null,
    @SerializedName("securityNumber"   ) var securityNumber   : String? = null,
) {
    companion object {
        fun toJson(vehicle: Vehicle):String {
            return Gson().toJson(vehicle)
        }
        fun fromJson(vehicle: String):Vehicle {
            return Gson().fromJson(vehicle, Vehicle::class.java)
        }
    }
}
