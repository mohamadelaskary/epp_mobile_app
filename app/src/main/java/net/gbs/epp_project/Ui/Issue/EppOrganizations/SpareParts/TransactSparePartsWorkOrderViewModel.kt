package net.gbs.epp_project.Ui.Issue.EppOrganizations.SpareParts

import android.app.Activity
import android.app.Application
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.gbs.epp_project.Base.BaseViewModel
import net.gbs.epp_project.MainActivity.MainActivity
import net.gbs.epp_project.Model.ApiRequestBody.MobileLogBody
import net.gbs.epp_project.Model.ApiRequestBody.TransactItemsBody
import net.gbs.epp_project.Model.ApiRequestBody.TransactMultiItemsBody
import net.gbs.epp_project.Model.ApiResponse.LocatorItems
import net.gbs.epp_project.Model.IssueOrderLists
import net.gbs.epp_project.Model.Locator
import net.gbs.epp_project.Model.Lot
import net.gbs.epp_project.Model.MoveOrderLine
import net.gbs.epp_project.Model.Organization
import net.gbs.epp_project.Model.SparePartsMoveOrderLine
import net.gbs.epp_project.Model.Status
import net.gbs.epp_project.Model.StatusWithMessage
import net.gbs.epp_project.Model.SubInventory
import net.gbs.epp_project.Model.WorkOrderOrder
import net.gbs.epp_project.R
import net.gbs.epp_project.Repositories.IssueRepository
import net.gbs.epp_project.Tools.ResponseDataHandler
import net.gbs.epp_project.Tools.ResponseHandler
import net.gbs.epp_project.Tools.SingleLiveEvent
import net.gbs.epp_project.Ui.SplashAndSignIn.SignInFragment.Companion.USER

class TransactSparePartsWorkOrderViewModel(private val application: Application, val activity: Activity) : BaseViewModel(application,activity) {
    var moveOrder: WorkOrderOrder? = null
    private val issueRepository = IssueRepository(activity)

    val getMoveOrderLinesLiveData = SingleLiveEvent<List<MoveOrderLine>>()
    val getMoveOrderLinesStatus   = SingleLiveEvent<StatusWithMessage>()
    fun getMoveOrderLines(workOrderName:String?,orgId:Int){
        job = CoroutineScope(Dispatchers.IO).launch {
            getMoveOrderLinesStatus.postValue(StatusWithMessage(Status.LOADING))
            try {
                val response = issueRepository.getMoveOrderLinesByWorkOrderName(workOrderName, orgId)
                ResponseDataHandler(response,getMoveOrderLinesLiveData,getMoveOrderLinesStatus,application).handleData("MoveOrderLinesGetByWorkOrderName")
                if (response.body()?.responseStatus?.errorMessage!=null)
                    issueRepository.MobileLog(
                        MobileLogBody(
                            userId = USER?.notOracleUserId,
                            errorMessage = response.body()?.responseStatus?.errorMessage,
                            apiName = "MoveOrderLinesGetByWorkOrderName"
                        )
                    )
            } catch (ex:Exception){
                getMoveOrderLinesStatus.postValue(StatusWithMessage(Status.NETWORK_FAIL,application.getString(
                    R.string.error_in_getting_data)))
                Log.d(TAG, "===ErrorgetMoveOrderLines: ${ex.message}")
            }
        }
    }
    val getSubInvertoryListLiveData = SingleLiveEvent<List<SubInventory>>()
    val getSubInvertoryListStatus   = SingleLiveEvent<StatusWithMessage>()
    fun getSubInvertoryList(orgId:Int){
        job = CoroutineScope(Dispatchers.IO).launch {
            getSubInvertoryListStatus.postValue(StatusWithMessage(Status.LOADING))
            try {
                val response = issueRepository.getSubInventoryList(orgId)
                ResponseDataHandler(response,getSubInvertoryListLiveData,getSubInvertoryListStatus,application).handleData("SubInvList")
                if (response.body()?.responseStatus?.errorMessage!=null)
                    issueRepository.MobileLog(
                        MobileLogBody(
                            userId = USER?.notOracleUserId,
                            errorMessage = response.body()?.responseStatus?.errorMessage,
                            apiName = "SubInvList"
                        )
                    )
            } catch (ex:Exception){
                getSubInvertoryListStatus.postValue(StatusWithMessage(Status.NETWORK_FAIL,application.getString(
                    R.string.error_in_getting_data)))
                Log.d(TAG, "===ErrorgetSubInvertoryList: ${ex.message}")
            }
        }
    }

    val getLocatorsListLiveData = SingleLiveEvent<List<Locator>>()
    val getLocatorsListStatus   = SingleLiveEvent<StatusWithMessage>()
    fun getLocatorsListByItemId(orgId:Int,subInvCode:String,itemId:Int){
        job = CoroutineScope(Dispatchers.IO).launch {
            getLocatorsListStatus.postValue(StatusWithMessage(Status.LOADING))
            try {
                val response = issueRepository.getLocatorListByItemId(orgId,subInvCode,itemId)
                ResponseDataHandler(response,getLocatorsListLiveData,getLocatorsListStatus,application).handleData("LocatorList")
                if (response.body()?.responseStatus?.errorMessage!=null)
                    issueRepository.MobileLog(
                        MobileLogBody(
                            userId = USER?.notOracleUserId,
                            errorMessage = response.body()?.responseStatus?.errorMessage,
                            apiName = "LocatorList"
                        )
                    )
            } catch (ex:Exception){
                getLocatorsListStatus.postValue(StatusWithMessage(Status.NETWORK_FAIL,application.getString(
                    R.string.error_in_getting_data)))
                Log.d(TAG, "===ErrorgetLocatorsList: ${ex.message}")
            }
        }
    }
    
    val allocateItemsStatus = SingleLiveEvent<StatusWithMessage>()
    fun transactMultiItems(body: TransactMultiItemsBody){
        allocateItemsStatus.postValue(StatusWithMessage(Status.LOADING))
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = issueRepository.transactMultiItems(body)
                ResponseHandler(response,allocateItemsStatus,application).handleData("TransactMultiItems")
                if (response.body()?.responseStatus?.errorMessage!=null)
                    issueRepository.MobileLog(
                        MobileLogBody(
                            userId = USER?.notOracleUserId,
                            errorMessage = response.body()?.responseStatus?.errorMessage,
                            apiName = "TransactMultiItems"
                        )
                    )
            } catch (ex:Exception){
                allocateItemsStatus.postValue(StatusWithMessage(Status.NETWORK_FAIL,application.getString(R.string.error_in_connection)))
            }
        }
    }


    val getWorkOrdersListLiveData = SingleLiveEvent<List<WorkOrderOrder>>()
    val getWorkOrdersListStatus   = SingleLiveEvent<StatusWithMessage>()
    fun getWorkOrdersList(orgId:Int){
        job = CoroutineScope(Dispatchers.IO).launch {
            getWorkOrdersListStatus.postValue(StatusWithMessage(Status.LOADING))
            try {
                val response = issueRepository.getWorkOrdersList(orgId)
                ResponseDataHandler(response,getWorkOrdersListLiveData,getWorkOrdersListStatus,application).handleData("MoveOrdersList_SpareParts")
                if (response.body()?.responseStatus?.errorMessage!=null)
                    issueRepository.MobileLog(
                        MobileLogBody(
                            userId = USER?.notOracleUserId,
                            errorMessage = response.body()?.responseStatus?.errorMessage,
                            apiName = "MoveOrdersList_SpareParts"
                        )
                    )
            } catch (ex:Exception){
                getWorkOrdersListStatus.postValue(
                    StatusWithMessage(
                        Status.NETWORK_FAIL,application.getString(
                            R.string.error_in_getting_data))
                )
            }
        }
    }

    val getLocatorDetailsListLiveData = SingleLiveEvent<List<LocatorItems>>()
    val getLocatorDetailsListStatus   = SingleLiveEvent<StatusWithMessage>()
    fun getOnHandLocatorDetails(orgId:Int,itemCode:String,locatorCode:String){
        job = CoroutineScope(Dispatchers.IO).launch {
            getLocatorDetailsListStatus.postValue(StatusWithMessage(Status.LOADING))
            try {
                val response = issueRepository.getOnHandLocatorDetails(orgId = orgId, itemCode = itemCode, locatorCode = locatorCode)
                ResponseDataHandler(response,getLocatorDetailsListLiveData,getLocatorDetailsListStatus,application).handleData("MoveOrdersList_SpareParts")
                if (response.body()!=null)
                    issueRepository.MobileLog(
                        MobileLogBody(
                            userId = USER?.notOracleUserId,
                            errorMessage = response.body()?.responseStatus?.errorMessage,
                            apiName = "OnHandLocatorDetails"
                        )
                    )
            } catch (ex:Exception){
                getLocatorDetailsListStatus.postValue(
                    StatusWithMessage(
                        Status.NETWORK_FAIL,application.getString(
                            R.string.error_in_getting_data))
                )
            }
        }
    }
    fun getJobOrdersList(orgId:Int){
        job = CoroutineScope(Dispatchers.IO).launch {
            getWorkOrdersListStatus.postValue(StatusWithMessage(Status.LOADING))
            try {
                val response = issueRepository.getJobOrdersList(orgId)
                ResponseDataHandler(response,getWorkOrdersListLiveData,getWorkOrdersListStatus,application).handleData("MoveOrdersList_IndirectChemical")
                if (response.body()?.responseStatus?.errorMessage!=null)
                    issueRepository.MobileLog(
                        MobileLogBody(
                            userId = USER?.notOracleUserId,
                            errorMessage = response.body()?.responseStatus?.errorMessage,
                            apiName = "MoveOrdersList_IndirectChemical"
                        )
                    )
            } catch (ex:Exception){
                getWorkOrdersListStatus.postValue(
                    StatusWithMessage(
                        Status.NETWORK_FAIL,application.getString(
                            R.string.error_in_getting_data))
                )
            }
        }
    }
    val getIssueOrdersListLiveData = SingleLiveEvent<IssueOrderLists>()
    val getIssueOrdersListStatus   = SingleLiveEvent<StatusWithMessage>()
    fun getIssueOrderLists(requestNumber:String,orgId: Int){
        job = CoroutineScope(Dispatchers.IO).launch {
            getIssueOrdersListStatus.postValue(StatusWithMessage(Status.LOADING))
            try {
                val response = issueRepository.getIssueOrdersList(requestNumber,orgId)
                ResponseDataHandler(response,getIssueOrdersListLiveData,getIssueOrdersListStatus,application).handleData("MoveOrderGetDetailsByHEADER_ID")
                if (response.body()?.responseStatus?.errorMessage!=null)
                    issueRepository.MobileLog(
                        MobileLogBody(
                            userId = USER?.notOracleUserId,
                            errorMessage = response.body()?.responseStatus?.errorMessage,
                            apiName = "MoveOrderGetDetailsByHEADER_ID"
                        )
                    )
            } catch (ex:Exception){
                getIssueOrdersListStatus.postValue(StatusWithMessage(Status.NETWORK_FAIL,application.getString(
                    R.string.error_in_getting_data)))
                Log.d(ContentValues.TAG, "===ErrorgetLocatorsList: ${ex.message}")
            }
        }
    }

    fun getTodayDate() = issueRepository.getTodayDate()
    fun getDisplayTodayDate()= issueRepository.getTodayDate().substring(0,10)
//    fun getTodayFullDate() = issueRepository.getStoredActualFullDateTime()

    val getOrganizationsListLiveData = SingleLiveEvent<List<Organization>>()
    val getOrganizationsListStatus = SingleLiveEvent<StatusWithMessage>()
    fun getOrganizationsList(){
        getOrganizationsListStatus.postValue(StatusWithMessage(Status.LOADING))
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = issueRepository.getOrganizations()
                ResponseDataHandler(response,getOrganizationsListLiveData,getOrganizationsListStatus,application).handleData("OrganizationsList")
                if (response.body()?.responseStatus?.errorMessage!=null)
                    issueRepository.MobileLog(
                        MobileLogBody(
                            userId = USER?.notOracleUserId,
                            errorMessage = response.body()?.responseStatus?.errorMessage,
                            apiName = "OrganizationsList"
                        )
                    )
            } catch (ex:Exception){
                getOrganizationsListStatus.postValue(
                    StatusWithMessage(
                        Status.NETWORK_FAIL,application.getString(
                            R.string.error_in_connection))
                )
            }
        }
    }

    val getLotListLiveData = SingleLiveEvent<List<Lot>>()
    val getLotListStatus   = SingleLiveEvent<StatusWithMessage>()
    fun getLotList(orgId:Int,itemId:Int?,subInvCode: String){
        job = CoroutineScope(Dispatchers.IO).launch {
            getLotListStatus.postValue(StatusWithMessage(Status.LOADING))
            try {
                val response = issueRepository.getLotList(orgId.toString(),itemId,subInvCode,null)
                ResponseDataHandler(response,getLotListLiveData,getLotListStatus,application).handleData("LotList")
                if (response.body()?.responseStatus?.errorMessage!=null)
                    issueRepository.MobileLog(
                        MobileLogBody(
                            userId = USER?.notOracleUserId,
                            errorMessage = response.body()?.responseStatus?.errorMessage,
                            apiName = "LotList"
                        )
                    )
            } catch (ex:Exception){
                getLotListStatus.postValue(
                    StatusWithMessage(
                        Status.NETWORK_FAIL,application.getString(
                            R.string.error_in_getting_data))
                )
            }
        }
    }
}