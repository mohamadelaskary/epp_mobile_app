package net.gbs.epp_project.Model.ApiResponse

import com.google.gson.annotations.SerializedName
import net.gbs.epp_project.Base.BaseResponse
import net.gbs.epp_project.Model.Governorate

class GovernoratesGetListResponse : BaseResponse<List<Governorate>>() {
    @SerializedName("getList"        ) var getList        : List<Governorate> = listOf()
    override fun getData(): List<Governorate> {
        return getList
    }
}
