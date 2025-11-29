package net.gbs.epp_project.Model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class Locator(
    @SerializedName("org_id"                  ) var orgId                   : Int?    = null,
    @SerializedName("locator_id"              ) var locatorId               : Int?    = null,
    @SerializedName("locatorCode"             ) var locatorCode             : String?    = null,
    @SerializedName("subinventorycode"        ) var subinventorycode        : String? = null,
    @SerializedName("subinventorydescription" ) var subinventorydescription : String? = null,
) {
    override fun toString(): String {
        return locatorCode!!
    }

    companion object{
        fun toJson(locator: Locator):String{
            return Gson().toJson(locator)
        }
        fun fromJson(locator: String):Locator{
            return Gson().fromJson(locator, Locator::class.java)
        }
    }
}
