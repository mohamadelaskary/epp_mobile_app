package net.gbs.epp_project.Model

import com.google.gson.annotations.SerializedName

data class SparePartsMoveOrderLine (
    @SerializedName("inventorY_ITEM_CODE"    ) var inventorYITEMCODE    : String? = null,
    @SerializedName("inventorY_ITEM_DESC"    ) var inventorYITEMDESC    : String? = null,
    @SerializedName("onHAND_QUANTITY"        ) var onHANDQUANTITY       : Int?    = null,
    @SerializedName("froM_SUBINVENTORY_CODE" ) var froMSUBINVENTORYCODE : String? = null,
    @SerializedName("froM_LOCATOR_Code"      ) var froMLOCATORCode      : String? = null,
    @SerializedName("allocated_QUANTITY"     ) var allocatedQUANTITY    : Int?    = null,
    @SerializedName("loT_CONTROL_CODE"       ) var loTCONTROLCODE       : String? = null,
    @SerializedName("linE_ID"                ) var linEID               : Int?    = null,
    @SerializedName("linE_NUMBER"            ) var linENUMBER           : Int?    = null
)