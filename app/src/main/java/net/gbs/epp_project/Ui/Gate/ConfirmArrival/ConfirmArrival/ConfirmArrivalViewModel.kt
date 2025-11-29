package net.gbs.epp_project.Ui.Gate.ConfirmArrival.ConfirmArrival

import android.app.Activity
import android.app.Application
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.gbs.epp_project.Base.BaseViewModel
import net.gbs.epp_project.Model.ApiRequestBody.MobileApproveData
import net.gbs.epp_project.Model.Status
import net.gbs.epp_project.Model.StatusWithMessage
import net.gbs.epp_project.R
import net.gbs.epp_project.Tools.ResponseHandler
import net.gbs.epp_project.Tools.SingleLiveEvent
import net.gbs.epp_project.Ui.Gate.GateRepository
import net.gbs.epp_project.Ui.SplashAndSignIn.SignInFragment.Companion.USER

class ConfirmArrivalViewModel(private val application: Application,activity: Activity) : BaseViewModel(application,activity) {
    private val repository = GateRepository(activity)
    val mobileApproveStatus = SingleLiveEvent<StatusWithMessage>()
    fun mobileApprove(
        id: Int,
        plateNo: String,
        container: String,
        driverNationalId: String,
        securityNumber: String,
        arrivalDateTime: String
    ){
        mobileApproveStatus.postValue(StatusWithMessage(Status.LOADING))
        try {
            CoroutineScope(Dispatchers.IO).launch {
                ResponseHandler(repository.mobileApprove(
                    MobileApproveData(
                        id = id,
                        applang = lang,
                        userId = USER?.notOracleUserId!!,
                        arrivalTime = arrivalDateTime,
                        driverIdNo = driverNationalId,
                        plateNo = plateNo,
                        containers = arrayListOf(container),
                        securityNumber = securityNumber
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