package net.gbs.epp_project.Model.ApiRequestBody

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class PutawayMaterialBody(
    @SerializedName("ship_to_organization_id" ) var shipToOrganizationId : Int?     = null,
    @SerializedName("po_header_id"            ) var poHeaderId           : Int?     = null,
    @SerializedName("po_line_id"              ) var poLineId             : Int?     = null,
    @SerializedName("receiptno"               ) var receiptno            : String?  = null,
    @SerializedName("subinventory_code"       ) var subinventoryCode     : String?  = null,
    @SerializedName("lot_num"                 ) var lotNum               : String?  = null,
    @SerializedName("locator_code"            ) var locatorCode          : String?  = null,
    @SerializedName("IsRejected"              ) var isRejected           : Boolean? = null,
    @SerializedName("user_id"                 ) var userId               : Int?     = null,
    @SerializedName("employee_id"             ) var employeeId           : Int?     = null,
    @SerializedName("transaction_date"        ) var transactionDate      : String?  = null,
    @SerializedName("isFullControl"           ) var isFullControl        : Boolean?  = null,
    @SerializedName("userID"                  ) var userID               : Int?     = null,
    @SerializedName("deviceSerialNo"          ) var deviceSerialNo       : String?  = null,
    @SerializedName("applang"                 ) var applang              : String?  = null
) {
    fun toJson():String{
        return Gson().toJson(this)
    }
}