package net.gbs.epp_project.Model

import com.google.gson.annotations.SerializedName

data class FeBooking (
    @SerializedName("bookingId"     ) var bookingId     : Int?    = null,
    @SerializedName("bookingNumber" ) var bookingNumber : String? = null
) {
    override fun toString(): String {
        return bookingNumber!!
    }
}