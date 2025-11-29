package net.gbs.epp_project.Model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class PODetailsItem2 (
    @SerializedName("po_header_id"            ) var poHeaderId            : Int?    = null,
    @SerializedName("po_line_id"              ) var poLineId              : Int?    = null,
    @SerializedName("inventory_item_id"       ) var inventoryItemId  : Int?    = null,
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
    @SerializedName("itemqty_received"        ) var itemqtyReceived       : Double?    = null,
    @SerializedName("itemqty_accepted"        ) var itemqtyAccepted       : Double?    = null,
    @SerializedName("itemqty_rejected"        ) var itemqtyRejected       : Double?    = null,
    @SerializedName("itemqty_delivered"       ) var itemqtyDelivered      : Double?    = null,
    @SerializedName("totalitemqtyreceived"    ) var totalitemqtyreceived  : Double?    = null,
    @SerializedName("totalitemqtyaccepted"    ) var totalitemqtyaccepted  : Double?    = null,
    @SerializedName("totalitemqtyrejected"    ) var totalitemqtyrejected  : Double?    = null,
    @SerializedName("totalitemqtydelivered"   ) var totalitemqtydelivered : Double?    = null,
    @SerializedName("isinspected"             ) var isinspected           : String? = null,
    @SerializedName("isdelivered"             ) var isdelivered: String? = null,
    @SerializedName("is_rejected_delivered"             ) var isRejectedDelivered: String? = null,
    @SerializedName("loT_CONTROL_CODE"        ) var loTCONTROLCODE        : String? = null,
    @SerializedName("loT_CONTROL_NAME"        ) var loTCONTROLNAME        : String? = null,
){

    fun mustHaveLot():Boolean = loTCONTROLCODE=="2"
    companion object{
        fun toJson (poDetailsItem2: PODetailsItem2):String{
            return Gson().toJson(poDetailsItem2)
        }
        fun fromJson(poDetailsItem2: String):PODetailsItem2{
            return Gson().fromJson(poDetailsItem2,PODetailsItem2::class.java)
        }
    }
}