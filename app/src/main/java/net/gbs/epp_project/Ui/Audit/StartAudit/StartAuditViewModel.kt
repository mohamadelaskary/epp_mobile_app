package net.gbs.epp_project.Ui.Audit.StartAudit

import android.app.Activity
import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.gbs.epp_project.Base.BaseViewModel
import net.gbs.epp_project.Model.ApiRequestBody.MobileLogBody
import net.gbs.epp_project.Model.ApiRequestBody.PhysicalInventory_CountBody
import net.gbs.epp_project.Model.AuditOrder
import net.gbs.epp_project.Model.AuditOrderSubinventory
import net.gbs.epp_project.Model.Item
import net.gbs.epp_project.Model.LocatorAudit
import net.gbs.epp_project.Model.Status
import net.gbs.epp_project.Model.StatusWithMessage
import net.gbs.epp_project.R
import net.gbs.epp_project.Repositories.AuditRepository
import net.gbs.epp_project.Tools.ResponseDataHandler
import net.gbs.epp_project.Tools.ResponseHandler
import net.gbs.epp_project.Tools.SingleLiveEvent
import net.gbs.epp_project.Ui.SplashAndSignIn.SignInFragment.Companion.USER

import java.lang.Exception

class StartAuditViewModel(private val application: Application,val activity: Activity) : BaseViewModel(application,activity) {
    var locatorCode: String?=null
    var scannedQty: String? = null
    var subinventory: AuditOrderSubinventory? = null
    var autoSave: Boolean? = null
    val auditRepository = AuditRepository(activity)
    val getLocatorDataLiveData = SingleLiveEvent<List<LocatorAudit>>()
    val getLocatorDataStatus   = SingleLiveEvent<StatusWithMessage>()
    fun getLocatorData(locatorCode:String){
        getLocatorDataStatus.postValue(StatusWithMessage(Status.LOADING))
        try {
            job = CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = auditRepository.getLocatorData(locatorCode)
                ResponseDataHandler(
                    response ,
                    getLocatorDataLiveData,
                    getLocatorDataStatus,
                    application
                ).handleData("GetLocatorList")
                    if (response.body()?.responseStatus?.errorMessage!=null)
                        auditRepository.MobileLog(
                            MobileLogBody(
                                userId = USER?.notOracleUserId,
                                errorMessage = response.body()?.responseStatus?.errorMessage,
                                apiName = "GetLocatorList"
                            )
                        )
            } catch (ex: Exception){
                Log.e("SignInViewModel", "signIn: ", ex)
                getLocatorDataStatus.postValue(StatusWithMessage(Status.NETWORK_FAIL,application.getString(R.string.error_in_sending_data)))
            }

        }
        } catch (ex:Exception){
            Log.e(TAG, "getLocatorData: ", ex)
            getLocatorDataStatus.postValue(StatusWithMessage(Status.NETWORK_FAIL,application.getString(
                R.string.error_in_getting_data)))
        }
    }
    val getItemDataLiveData = SingleLiveEvent<List<Item>>()
    val getItemDataStatus   = SingleLiveEvent<StatusWithMessage>()
    fun getItemData(itemCode:String,orgCode:String){
        getItemDataStatus.postValue(StatusWithMessage(Status.LOADING))
        try {
            job = CoroutineScope(Dispatchers.IO).launch {
                try{
                    val response = auditRepository.getItemData(itemCode,orgCode)
                    ResponseDataHandler(
                        response,
                        getItemDataLiveData,
                        getItemDataStatus,
                        application
                    ).handleData("GetItemList")
                    if (response.body()?.responseStatus?.errorMessage!=null)
                        auditRepository.MobileLog(
                            MobileLogBody(
                                userId = USER?.notOracleUserId,
                                errorMessage = response.body()?.responseStatus?.errorMessage,
                                apiName = "GetItemList"
                            )
                        )
                } catch (ex:Exception){
                    Log.e(TAG, "getItemData: ", ex)
                    getItemDataStatus.postValue(StatusWithMessage(Status.NETWORK_FAIL,application.getString(
                        R.string.error_in_getting_data)))
                }
            }
        } catch (ex:Exception){
            Log.e(TAG, "getItemData: ", ex)
            getItemDataStatus.postValue(StatusWithMessage(Status.NETWORK_FAIL,application.getString(
                R.string.error_in_getting_data)))
        }
    }
    val getSavingDataLiveData = SingleLiveEvent<List<AuditOrder>>()
    val getSavingDataStatus = SingleLiveEvent<StatusWithMessage>()
    fun saveData(
        qty: Double,
        itemCode: String,
        locatorCode: String,
        headerId: Int,
        subInventoryCode: String,
        orgCode: String
    ) {
        getSavingDataStatus.postValue(StatusWithMessage(Status.LOADING))
        try {
            job = CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = auditRepository.PhysicalInventory_Count(
                        PhysicalInventory_CountBody(
                            Qty = qty,
                            ItemCode = itemCode,
                            LocatorCode = locatorCode,
                            PhysicalInventoryHeaderId = headerId,
                            SubInventoryCode = subInventoryCode,
                            OrgCode = orgCode
                        )
                    )
                    ResponseDataHandler(
                        response,
                        getSavingDataLiveData,
                        getSavingDataStatus,
                        application
                    ).handleData("PhysicalInventory_Count")
                    if (response.body()?.responseStatus?.errorMessage!=null)
                        auditRepository.MobileLog(
                            MobileLogBody(
                                userId = USER?.notOracleUserId,
                                errorMessage = response.body()?.responseStatus?.errorMessage,
                                apiName = "PhysicalInventory_Count"
                            )
                        )
                } catch (ex: Exception) {
                    Log.e(TAG, "getSavingData: ", ex)
                    getSavingDataStatus.postValue(
                        StatusWithMessage(
                            Status.NETWORK_FAIL, application.getString(
                                R.string.error_in_getting_data
                            )
                        )
                    )

                }
            }
        } catch (ex: Exception) {
            Log.e(TAG, "getSavingData: ", ex)
            getSavingDataStatus.postValue(
                StatusWithMessage(
                    Status.NETWORK_FAIL, application.getString(
                        R.string.error_in_getting_data
                    )
                )
            )

        }
    }
    val finishTrackingStatus = SingleLiveEvent<StatusWithMessage>()
    fun finishTracking(headerId:Int,subInventoryCode:String){
        finishTrackingStatus.postValue(StatusWithMessage(Status.LOADING))
        try {
            job = CoroutineScope(Dispatchers.IO).launch {
                try{
                    val response = auditRepository.finishTracking(subInventoryCode,headerId)
                    ResponseHandler(response,finishTrackingStatus,application).handleData("PhysicalInventoryOrder_FinishTracking")
                    if (response.body()?.responseStatus?.errorMessage!=null)
                        auditRepository.MobileLog(
                            MobileLogBody(
                                userId = USER?.notOracleUserId,
                                errorMessage = response.body()?.responseStatus?.errorMessage,
                                apiName = "PhysicalInventoryOrder_FinishTracking"
                            )
                        )
                } catch (ex:Exception){
                    finishTrackingStatus.postValue(StatusWithMessage(Status.NETWORK_FAIL,application.getString(
                        R.string.error_in_sending_data)))
                }
            }
        } catch (ex:Exception){
            finishTrackingStatus.postValue(StatusWithMessage(Status.NETWORK_FAIL,application.getString(
                R.string.error_in_sending_data)))
        }
    }
}