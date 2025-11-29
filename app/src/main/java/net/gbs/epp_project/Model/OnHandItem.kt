package net.gbs.epp_project.Model

import com.google.gson.annotations.SerializedName

data class OnHandItem(
    @SerializedName("organization_id"            ) var organizationId          : Int?    = null,
    @SerializedName("organization_code"          ) var organizationCode        : String? = null,
    @SerializedName("organization_name"          ) var organizationName        : String? = null,
    @SerializedName("item_id"                    ) var itemId                  : Int?    = null,
    @SerializedName("item_code"                  ) var itemCode                : String? = null,
    @SerializedName("item_description"           ) var itemDescription         : String? = null,
    @SerializedName("item_type"                  ) var itemType                : String? = null,
    @SerializedName("inventory_item_status_code" ) var inventoryItemStatusCode : String? = null,
    @SerializedName("primary_uom_code"           ) var primaryUomCode          : String? = null,
    @SerializedName("primary_unit_of_measure"    ) var primaryUnitOfMeasure    : String? = null,
    @SerializedName("subinventorycode"           ) var subinventorycode        : String? = null,
    @SerializedName("subinventorydescription"    ) var subinventorydescription : String? = null,
    @SerializedName("locator_id"                 ) var locatorId               : Int?    = null,
    @SerializedName("locatordescription"         ) var locatordescription      : String? = null,
    @SerializedName("onhand_quantity"            ) var onhandQuantity          : Double?    = null
)
