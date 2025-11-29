package net.gbs.epp_project.Model.ApiResponse

import com.google.gson.annotations.SerializedName
import net.gbs.epp_project.Base.BaseResponse
import net.gbs.epp_project.Model.SalesAgreementDetails

class EppGbsSalesAgreementDGetListResponse : BaseResponse<List<SalesAgreementDetails>>() {
    @SerializedName("getList"        ) var getList        : ArrayList<SalesAgreementDetails> = arrayListOf()
    override fun getData(): List<SalesAgreementDetails> {
        return getList
    }
}
