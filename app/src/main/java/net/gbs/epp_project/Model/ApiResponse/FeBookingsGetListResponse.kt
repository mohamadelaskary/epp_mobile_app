package net.gbs.epp_project.Model.ApiResponse

import com.google.gson.annotations.SerializedName
import net.gbs.epp_project.Base.BaseResponse
import net.gbs.epp_project.Model.FeBooking

class FeBookingsGetListResponse : BaseResponse<List<FeBooking>>() {
    @SerializedName("getList"        ) var getList        : ArrayList<FeBooking> = arrayListOf()
    override fun getData(): List<FeBooking> {
        return getList
    }
}
