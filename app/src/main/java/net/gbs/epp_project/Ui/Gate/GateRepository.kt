package net.gbs.epp_project.Ui.Gate

import android.app.Activity
import android.content.Context
import net.gbs.epp_project.Base.BaseRepository
import net.gbs.epp_project.Model.ApiRequestBody.MobileApproveData
import net.gbs.epp_project.Model.ApiRequestBody.SaveCheckInData
import net.gbs.epp_project.Model.ApiRequestBody.SaveNewVehicleRecordData
import net.gbs.epp_project.Model.Response.NoDataResponse
import net.gbs.epp_project.Ui.SplashAndSignIn.SignInFragment.Companion.USER
import retrofit2.Response

class GateRepository(context: Context): BaseRepository(context) {
    suspend fun getArrivedVehicles() = apiInterface.viewArrivalRegistrationVehicles(userId!!,deviceSerialNo,lang)
    suspend fun mobileApprove(body: MobileApproveData) = apiInterface.mobileApprove(body)
    suspend fun getWaitingCheckInVehicles() = apiInterface.viewAwaitingCheckinVehicle(userId!!,deviceSerialNo,lang)
    suspend fun checkIn(body: SaveCheckInData) = apiInterface.saveCheckIn(body)
    suspend fun getGovernoratesList() = apiInterface.getGovernoratesList(userId!!,deviceSerialNo,lang)
    suspend fun getFeBookingsList() = apiInterface.getFeBookingsList(userId!!,deviceSerialNo,lang)
    suspend fun getEppGbsSalesAgreementHList() = apiInterface.getEppGbsSalesAgreementHList(userId!!,deviceSerialNo,lang)
    suspend fun getEppGbsSalesAgreementDList(headerId:Int) = apiInterface.getEppGbsSalesAgreementDList(userId!!,deviceSerialNo,lang,headerId)
    suspend fun saveNewVehicleRecord(body: SaveNewVehicleRecordData): Response<NoDataResponse> {
        body.userId = USER?.notOracleUserId!!
        body.applang = lang
        return apiInterface.saveNewVehicleRecord(body)
    }
}