package net.gbs.epp_project.Model.ApiResponse

import com.google.gson.annotations.SerializedName
import net.gbs.epp_project.Base.BaseResponse
import net.gbs.epp_project.Model.Vehicle

class ViewArrivalRegistrationVehicleResponse : BaseResponse<List<Vehicle>>() {
    @SerializedName("getList"        ) var getList        : List<Vehicle> = arrayListOf()
    override fun getData(): List<Vehicle> {
        return getList
    }

}
