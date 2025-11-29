package net.gbs.epp_project.Model.ApiRequestBody

import com.google.gson.annotations.SerializedName
import net.gbs.epp_project.Model.LotQty

data class TransactItemsBody (
    @SerializedName("org_id"                 ) var orgId                : Int?    = null,
    @SerializedName("line_id"                ) var lineId               : Int?    = null,
    @SerializedName("linE_NUMBER"            ) var lineNumber           : Int?    = null,
    @SerializedName("lots"                   ) var lots                 : List<LotQty>? = null,
    @SerializedName("user_id"                ) var userId               : Int?    = null,
    @SerializedName("employee_id"            ) var employeeId           : Int?    = null,
    @SerializedName("program_application_id" ) var programApplicationId : Int?    = null,
    @SerializedName("program_id"             ) var programId            : Int?    = null,
    @SerializedName("deviceSerialNo"         ) var deviceSerialNo       : String? = null,
    @SerializedName("transaction_date"       ) var transaction_date       : String?          = null,
    @SerializedName("applang"                ) var appLang              : String? = null,
    @SerializedName("is_FinalProduct"        ) var isFinalProducts              : Boolean? = null,
)
