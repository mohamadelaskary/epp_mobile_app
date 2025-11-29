package net.gbs.epp_project.Model.ApiResponse

import com.google.gson.annotations.SerializedName
import net.gbs.epp_project.Base.BaseResponse
import net.gbs.epp_project.Model.SalesAgreementHeader

class EppGbsSalesAgreementHGetListResponse : BaseResponse<List<SalesAgreementHeader>>() {
    @SerializedName("getList"        ) var getList        : ArrayList<SalesAgreementHeader> = arrayListOf()
    override fun getData(): List<SalesAgreementHeader> {
        return getList
    }
}
