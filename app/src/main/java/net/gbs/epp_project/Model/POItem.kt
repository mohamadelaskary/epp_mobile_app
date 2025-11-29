package net.gbs.epp_project.Model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class POItem(
    @SerializedName("po_header_id"            ) var poHeaderId            : Int?    = null,
    @SerializedName("po_line_id"              ) var poLineId              : Int?    = null,
    @SerializedName("ship_to_organization_id" ) var shipToOrganizationId  : Int?    = null,
    @SerializedName("organization_code"       ) var organizationCode      : String? = null,
    @SerializedName("organization_name"       ) var organizationName      : String? = null,
    @SerializedName("pono"                    ) var pono                  : String? = null,
    @SerializedName("supplier"                ) var supplier              : String? = null,
    @SerializedName("receiver"                ) var receiver              : String? = null,
    @SerializedName("receiptno"               ) var receiptno             : String? = null,
    @SerializedName("receiptdate"             ) var receiptdate           : String? = null,
    @SerializedName("itemcode"                ) var itemcode              : String? = null,
    @SerializedName("itemcategory"            ) var itemcategory          : String? = null,
    @SerializedName("itemdesc"                ) var itemdesc              : String? = null,
    @SerializedName("itemuom"                 ) var itemuom               : String? = null,
    @SerializedName("po_line_qty"             ) var poLineQty             : Double?    = null,
    @SerializedName("transactioN_ID"          ) var transactioNID         : Int?    = null,
    @SerializedName("transactioN_TYPE"        ) var transactioNTYPE       : String? = null,
    @SerializedName("transQty"                ) var transQty              : Double?    = null,
    @SerializedName("loT_CONTROL_CODE"        ) var loTCONTROLCODE        : String? = null,
    @SerializedName("loT_CONTROL_NAME"        ) var loTCONTROLNAME        : String? = null,
    @SerializedName("inventorY_ITEM_ID"       ) var inventorYITEMID       : Int?    = null,
    @SerializedName("parenT_TRANSACTION_ID"   ) var parenTTRANSACTIONID   : Int?    = null,
    @SerializedName("parenT_TRANSACTION_TYPE" ) var parenTTRANSACTIONTYPE : String? = null,
    var isSelected:Boolean,

) {
    fun mustHaveLot():Boolean = loTCONTROLCODE == "2"
    override fun toString(): String {
        return "$itemcode  $isSelected"
    }
}