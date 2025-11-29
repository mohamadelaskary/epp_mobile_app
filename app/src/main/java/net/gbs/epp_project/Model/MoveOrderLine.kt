package net.gbs.epp_project.Model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class MoveOrderLine(
    @SerializedName("linE_ID"                      ) var linEID                     : Int?    = null,
    @SerializedName("linE_NUMBER"                  ) var linENUMBER                 : Int?    = null,
    @SerializedName("movE_ORDER_TYPE"              ) var movEORDERTYPE              : String? = null,
    @SerializedName("movE_ORDER_TYPE_NAME"         ) var movEORDERTYPENAME          : String? = null,
    @SerializedName("froM_SUBINVENTORY_CODE"       ) var froMSUBINVENTORYCODE       : String? = null,
    @SerializedName("froM_LOCATOR_ID"              ) var froMLOCATORID              : Int?    = null,
    @SerializedName("tO_SUBINVENTORY_CODE"         ) var tOSUBINVENTORYCODE         : String? = null,
    @SerializedName("tO_LOCATOR_ID"                ) var tOLOCATORID                : Int?    = null,
    @SerializedName("froM_LOCATOR_Code"            ) var froMLOCATORCode            : String? = null,
    @SerializedName("uoM_CODE"                     ) var uoMCODE                    : String? = null,
    @SerializedName("inventorY_ITEM_ID"            ) var inventorYITEMID            : Int?    = null,
    @SerializedName("inventorY_ITEM_CODE"          ) var inventorYITEMCODE          : String? = null,
    @SerializedName("inventorY_ITEM_DESC"          ) var inventorYITEMDESC          : String? = null,
    @SerializedName("quantity"                     ) var quantity                   : Double?    = null,
    @SerializedName("quantitY_DELIVERED"           ) var quantitYDELIVERED          : Double?    = null,
    @SerializedName("quantitY_DETAILED"            ) var quantitYDETAILED           : Double?    = null,
    @SerializedName("onHAND_QUANTITY"              ) var onHANDQUANTITY             : Double?    = null,
    @SerializedName("linE_STATUS"                  ) var linESTATUS                 : Int?    = null,
    @SerializedName("linE_STATUS_DESCRIPTION"      ) var linESTATUSDESCRIPTION      : String? = null,
    @SerializedName("transactioN_TYPE_ID"          ) var transactioNTYPEID          : Int?    = null,
    @SerializedName("transactioN_TYPE_NAME"        ) var transactioNTYPENAME        : String? = null,
    @SerializedName("transactioN_TYPE_DESCRIPTION" ) var transactioNTYPEDESCRIPTION : String? = null,
    @SerializedName("transactioN_SOURCE_TYPE_ID"   ) var transactioNSOURCETYPEID    : Int?    = null,
    @SerializedName("transactioN_SOURCE_TYPE_DESC" ) var transactioNSOURCETYPEDESC  : String? = null,
    @SerializedName("allocated_QUANTITY"           ) var allocatedQUANTITY          : Double?    = null,
    @SerializedName("uoM_Conversion"               ) var uoMConversion              : Double?    = null,
    @SerializedName("loT_CONTROL_CODE"             ) var loTCONTROLCODE             : String? = null,
    @SerializedName("loT_CONTROL_NAME"             ) var loTCONTROLNAME             : String? = null,
    var isAlreadyAdded :Boolean = false
) {
    val remainingQty:Double
        get() {
            return quantity!! - allocatedQUANTITY!!
        }
    fun mustHaveLot():Boolean = loTCONTROLCODE=="2"
    companion object{
        fun toJson(moveOrderLine: MoveOrderLine):String{
            return Gson().toJson(moveOrderLine)
        }
        fun fromJson(moveOrderLine: String):MoveOrderLine{
            return Gson().fromJson(moveOrderLine,MoveOrderLine::class.java)
        }
    }
}
