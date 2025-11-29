package net.gbs.epp_project.Ui.Audit.CycleCount.StartCycleCount.ByItem

import android.app.Activity
import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.gbs.epp_project.Base.BaseViewModel
import net.gbs.epp_project.Model.ApiRequestBody.MobileLogBody
import net.gbs.epp_project.Model.CycleCountHeader
import net.gbs.epp_project.Model.LocatorAudit
import net.gbs.epp_project.Model.Status
import net.gbs.epp_project.Model.StatusWithMessage
import net.gbs.epp_project.R
import net.gbs.epp_project.Repositories.AuditRepository
import net.gbs.epp_project.Tools.ResponseDataHandler
import net.gbs.epp_project.Tools.ResponseHandler
import net.gbs.epp_project.Tools.SingleLiveEvent
import net.gbs.epp_project.Ui.SplashAndSignIn.SignInFragment.Companion.USER

class StartCycleCountByItemViewModel(private val application: Application,activity: Activity) : BaseViewModel(application, activity) {
    val auditRepository = AuditRepository(activity)
    val getLocatorDataLiveData = SingleLiveEvent<List<LocatorAudit>>()
    val getLocatorDataStatus = SingleLiveEvent<StatusWithMessage>()
    fun getLocatorData(locatorCode:String){
        getLocatorDataStatus.postValue(StatusWithMessage(Status.LOADING))
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = auditRepository.getLocatorData(locatorCode)
                ResponseDataHandler(response,getLocatorDataLiveData,getLocatorDataStatus,application).handleData("GetLocatorList")
                if (response.body()?.responseStatus?.errorMessage!=null)
                    auditRepository.MobileLog(
                        MobileLogBody(
                            userId = USER?.notOracleUserId,
                            errorMessage = response.body()?.responseStatus?.errorMessage,
                            apiName = "GetLocatorList"
                        )
                    )
            } catch (ex:Exception){
                getLocatorDataStatus.postValue(StatusWithMessage(Status.NETWORK_FAIL,application.getString(
                    R.string.error_in_connection)))
            }
        }
    }
    val saveCycleCountStatus = SingleLiveEvent<StatusWithMessage>()
    val saveCycleCountLiveData = SingleLiveEvent<CycleCountHeader>()

    fun saveCycleCount(itemCode:String,locatorCode: String,cycleCountHeaderId:Int,qty:Double,orgCode:String){
        saveCycleCountStatus.postValue(StatusWithMessage(Status.LOADING))
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = auditRepository.saveCycleCountOrderDetails(
                    itemCode = itemCode,
                    locatorCode = locatorCode,
                    cycleCountHeaderId= cycleCountHeaderId,
                    qty = qty,
                    orgCode = orgCode
                )
                ResponseDataHandler(response,saveCycleCountLiveData,saveCycleCountStatus,application).handleData("SaveCycleCountOrderDetails")

                if (response.body()?.responseStatus?.errorMessage!=null)
                    auditRepository.MobileLog(
                        MobileLogBody(
                            userId = USER?.notOracleUserId,
                            errorMessage = response.body()?.responseStatus?.errorMessage,
                            apiName = "SaveCycleCountOrderDetails"
                        )
                    )
            } catch (ex:Exception){
                saveCycleCountStatus.postValue(StatusWithMessage(Status.NETWORK_FAIL,application.getString(R.string.error_in_connection)))
            }
        }
    }

    val finishCycleCountStatus = SingleLiveEvent<StatusWithMessage>()

    fun finishCycleCount(headerId:Int){
        saveCycleCountStatus.postValue(StatusWithMessage(Status.LOADING))
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = auditRepository.finishCycleCount(
                    headerId = headerId,
                )
                ResponseHandler(response,finishCycleCountStatus,application).handleData("CycleCountOrder_Finish")
                if (response.body()?.responseStatus?.errorMessage!=null)
                    auditRepository.MobileLog(
                        MobileLogBody(
                            userId = USER?.notOracleUserId,
                            errorMessage = response.body()?.responseStatus?.errorMessage,
                            apiName = "CycleCountOrder_Finish"
                        )
                    )
            } catch (ex:Exception){
                saveCycleCountStatus.postValue(StatusWithMessage(Status.NETWORK_FAIL,application.getString(R.string.error_in_connection)))
            }
        }
    }
}