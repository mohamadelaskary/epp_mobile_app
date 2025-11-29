package net.gbs.epp_project.Model

import com.google.gson.annotations.SerializedName

data class PODetailsItem(
    @SerializedName("org_id"                  ) var orgId                : Int?    = null,
    @SerializedName("po_header_id"            ) var poHeaderId           : Int?    = null,
    @SerializedName("po_line_id"              ) var poLineId             : Int?    = null,
    @SerializedName("shipment_num"            ) var shipmentNum          : Int?    = null,
    @SerializedName("ship_to_organization_id" ) var shipToOrganizationId : Int?    = null,
    @SerializedName("ship_to_location_id"     ) var shipToLocationId     : Int?    = null,
    @SerializedName("itemlineno"              ) var itemlineno           : Int?    = null,
    @SerializedName("shipto"                  ) var shipto               : String? = null,
    @SerializedName("itemcode"                ) var itemcode             : String? = null,
    @SerializedName("itemcategory"            ) var itemcategory         : String? = null,
    @SerializedName("itemdesc"                ) var itemdesc             : String? = null,
    @SerializedName("itemuom"                 ) var itemuom              : String? = null,
    @SerializedName("itemqty"                 ) var itemQty              : Double?    = null,
    @SerializedName("itemqtyreceived"         ) var itemQtyReceived      : Double?    = null,
    @SerializedName("itemqtyaccepted"         ) var itemqtyaccepted      : Double?    = null,
    @SerializedName("itemqtyrejected"         ) var itemqtyrejected      : Double?    = null,
    @SerializedName("itemqtybilled"           ) var itemqtybilled        : Double?    = null,
    @SerializedName("itemqtycancelled"        ) var itemqtycancelled     : Double?    = null,
    @SerializedName("itemprice"               ) var itemprice            : Double?    = null,
    @SerializedName("need_by_date"            ) var needByDate           : String? = null,
    @SerializedName("loT_CONTROL_CODE"        ) var lotControlCode       : String? = null,
    @SerializedName("loT_CONTROL_NAME"        ) var lotControlName       : String? = null,

) {
    fun mustProvideLot():Boolean = lotControlCode == "2"
    private val re = Regex("[^A-Za-z0-9 ]")
    val itemDescription
        get() = re.replace(itemdesc as String,"")
    var currentReceivedQty = 0.0
    val remainingQty:Double
        get() = itemQty!! - itemQtyReceived!!
    var isAdded = false
}