package net.gbs.epp_project.Ui.Gate.CheckIn.CheckIn

import android.app.Activity
import android.app.Application
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.gbs.epp_project.Base.BaseViewModel
import net.gbs.epp_project.Model.ApiRequestBody.MobileApproveData
import net.gbs.epp_project.Model.ApiRequestBody.SaveCheckInData
import net.gbs.epp_project.Model.Status
import net.gbs.epp_project.Model.StatusWithMessage
import net.gbs.epp_project.R
import net.gbs.epp_project.Tools.ResponseHandler
import net.gbs.epp_project.Tools.SingleLiveEvent
import net.gbs.epp_project.Ui.Gate.GateRepository
import net.gbs.epp_project.Ui.SplashAndSignIn.SignInFragment.Companion.USER

class CheckInViewModel(private val application: Application, activity: Activity) : BaseViewModel(application,activity) {
    private val repository = GateRepository(activity)
    val mobileApproveStatus = SingleLiveEvent<StatusWithMessage>()
    fun checkIn(
        id: Int,
        checkInDateTime : String
    ){
        mobileApproveStatus.postValue(StatusWithMessage(Status.LOADING))
        try {
            CoroutineScope(Dispatchers.IO).launch {
                ResponseHandler(repository.checkIn(
                    SaveCheckInData(
                        id = id,
                        applang = lang,
                        userId = USER?.notOracleUserId!!,
                        dateCheckIn = checkInDateTime
                    )
                ),mobileApproveStatus,application).handleData("MobileApprove")
            }
        } catch (ex: Exception){
            mobileApproveStatus.postValue(StatusWithMessage(
                Status.NETWORK_FAIL,application.getString(
                    R.string.error_in_connection)))
            Log.e("ConfirmArrivalViewModel", "mobileApprove: ",ex)
        }
    }
}