package net.gbs.epp_project.Ui.Receiving.EppOrganizations.Inspection.StartInspection

import android.app.Activity
import android.app.Application

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.gbs.epp_project.Base.BaseViewModel
import net.gbs.epp_project.Model.ApiRequestBody.MobileLogBody
import net.gbs.epp_project.Model.Status
import net.gbs.epp_project.Model.StatusWithMessage
import net.gbs.epp_project.R
import net.gbs.epp_project.Repositories.ReceivingRepository
import net.gbs.epp_project.Tools.LoadingDialog
import net.gbs.epp_project.Tools.ResponseDataHandler
import net.gbs.epp_project.Tools.ResponseHandler
import net.gbs.epp_project.Tools.SingleLiveEvent
import net.gbs.epp_project.Tools.Tools
import net.gbs.epp_project.Tools.Tools.back
import net.gbs.epp_project.Ui.SplashAndSignIn.SignInFragment.Companion.USER

class StartInspectionViewModel(private val app:Application,val activity: Activity) : BaseViewModel(app,activity) {
    val receivingRepository = ReceivingRepository(activity)
    val inspectStatus : SingleLiveEvent<StatusWithMessage> = SingleLiveEvent()
    fun InspectMaterial(
        poHeaderId: Int,
        poLineId: Int,
        receiptNo: String,
        shipToOrganizationId: Int,
        acceptedQty: Double,
        transactionDate:String,
    ) {
        inspectStatus.postValue(StatusWithMessage(Status.LOADING))
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = receivingRepository.InspectMaterial(
                poHeaderId = poHeaderId,
                poLineId = poLineId,
                receiptNo = receiptNo,
                shipToOrganizationId = shipToOrganizationId,
                acceptedQty = acceptedQty,
                transactionDate = transactionDate
                )
                ResponseHandler(response,inspectStatus,app).handleData("InspectMaterial")
                if (response.body()?.responseStatus?.errorMessage!=null)
                    receivingRepository.MobileLog(
                        MobileLogBody(
                            userId = USER?.notOracleUserId,
                            errorMessage = response.body()?.responseStatus?.errorMessage,
                            apiName = "InspectMaterial"
                        )
                    )
            } catch (ex:Exception){
                inspectStatus.postValue(StatusWithMessage(Status.NETWORK_FAIL,activity.getString(R.string.error_in_connection)))
            }


        }
    }

//    val getDateLiveData = SingleLiveEvent<String>()
//    val getDateStatus   = SingleLiveEvent<StatusWithMessage>()
//    fun getTodayDate(){
//        getDateStatus.postValue(StatusWithMessage(Status.LOADING))
//        job = CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val response = receivingRepository.getDate()
//                ResponseDataHandler(response,getDateLiveData,getDateStatus,app).handleData()
//            } catch (ex:Exception){
//                getDateStatus.postValue(StatusWithMessage(Status.NETWORK_FAIL,app.getString(R.string.error_in_getting_data)))
//            }
//        }
//    }
    fun getTodayDate() = receivingRepository.getTodayDate()
    fun getDisplayTodayDate()= receivingRepository.getTodayDate().substring(0,10)
//    fun getTodayFullDate() = receivingRepository.getStoredActualFullDateTime()
}