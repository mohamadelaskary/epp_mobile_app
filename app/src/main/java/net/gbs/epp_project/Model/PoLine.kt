package net.gbs.epp_project.Model

import com.google.gson.annotations.SerializedName

data class PoLine(
    @SerializedName("ship_to_organization_id" ) var shipToOrganizationId : Int? = null,
    @SerializedName("ship_to_location_id"     ) var shipToLocationId     : Int? = null,
    @SerializedName("po_header_id"            ) var poHeaderId           : Int? = null,
    @SerializedName("receipt_num"             ) var receiptNum           : String? = null,
    @SerializedName("po_line_id"              ) var poLineId             : Int? = null,
    @SerializedName("quantity_received"       ) var quantityReceived     : Double? = null
)