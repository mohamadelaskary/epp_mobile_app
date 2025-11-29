package net.gbs.epp_project.Ui.Return.ReturnToWarehouse

import android.app.Activity
import android.app.Application
import android.content.ContentValues
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.gbs.epp_project.Base.BaseViewModel
import net.gbs.epp_project.Model.ApiRequestBody.MobileLogBody
import net.gbs.epp_project.Model.ApiRequestBody.ReturnMaterialBody
import net.gbs.epp_project.Model.ApiRequestBody.ReturnToWarehouseItemsBody
import net.gbs.epp_project.Model.ApiRequestBody.TransactItemsBody
import net.gbs.epp_project.Model.Locator
import net.gbs.epp_project.Model.Lot
import net.gbs.epp_project.Model.Organization
import net.gbs.epp_project.Model.ReturnWorkOrder
import net.gbs.epp_project.Model.ReturnWorkOrderLine
import net.gbs.epp_project.Model.Status
import net.gbs.epp_project.Model.StatusWithMessage
import net.gbs.epp_project.Model.SubInventory
import net.gbs.epp_project.R
import net.gbs.epp_project.Tools.ResponseDataHandler
import net.gbs.epp_project.Tools.ResponseHandler
import net.gbs.epp_project.Tools.SingleLiveEvent
import net.gbs.epp_project.Ui.Return.ReturnRepository
import net.gbs.epp_project.Ui.SplashAndSignIn.SignInFragment.Companion.USER

class ReturnToWarehouseViewModel(private val application: Application, val activity: Activity) : BaseViewModel(application,activity) {
    private val returnRepository = ReturnRepository(activity)

    val getReturnWorkOrderLinesLiveData = SingleLiveEvent<List<ReturnWorkOrderLine>>()
    val getReturnWorkOrderLinesStatus   = SingleLiveEvent<StatusWithMessage>()
    fun getReturnWorkOrderLines(workOrderName:String,orgId:Int){
        job = CoroutineScope(Dispatchers.IO).launch {
            getReturnWorkOrderLinesStatus.postValue(StatusWithMessage(Status.LOADING))
            try {
                val response = returnRepository.getReturnWorkOrderLinesList(orgId,workOrderName)
                ResponseDataHandler(response,getReturnWorkOrderLinesLiveData,getReturnWorkOrderLinesStatus,application).handleData("ReturnLinesGetByWorkOrderName")
                if (response.body()?.responseStatus?.errorMessage!=null)
                    returnRepository.MobileLog(
                        MobileLogBody(
                            userId = USER?.notOracleUserId,
                            errorMessage = response.body()?.responseStatus?.errorMessage,
                            apiName = "ReturnLinesGetByWorkOrderName"
                        )
                    )
            } catch (ex:Exception){
                getReturnWorkOrderLinesStatus.postValue(StatusWithMessage(Status.NETWORK_FAIL,application.getString(
                    R.string.error_in_getting_data)))
                Log.d(ContentValues.TAG, "===ErrorgetMoveOrderLines: ${ex.message}")
            }
        }
    }
    val getSubInvertoryListLiveData = SingleLiveEvent<List<SubInventory>>()
    val getSubInvertoryListStatus   = SingleLiveEvent<StatusWithMessage>()
    fun getSubInvertoryList(orgId:Int){
        job = CoroutineScope(Dispatchers.IO).launch {
            getSubInvertoryListStatus.postValue(StatusWithMessage(Status.LOADING))
            try {
                val response = returnRepository.getSubInventoryList(orgId)
                ResponseDataHandler(response,getSubInvertoryListLiveData,getSubInvertoryListStatus,application).handleData("SubInvList")
                if (response.body()?.responseStatus?.errorMessage!=null)
                    returnRepository.MobileLog(
                        MobileLogBody(
                            userId = USER?.notOracleUserId,
                            errorMessage = response.body()?.responseStatus?.errorMessage,
                            apiName = "SubInvList"
                        )
                    )
            } catch (ex:Exception){
                getSubInvertoryListStatus.postValue(StatusWithMessage(Status.NETWORK_FAIL,application.getString(
                    R.string.error_in_getting_data)))
                Log.d(ContentValues.TAG, "===ErrorgetSubInvertoryList: ${ex.message}")
            }
        }
    }

    val getLocatorsListLiveData = SingleLiveEvent<List<Locator>>()
    val getLocatorsListStatus   = SingleLiveEvent<StatusWithMessage>()
    fun getLocatorsList(orgId:Int,subInvCode:String,itemId:Int){
        job = CoroutineScope(Dispatchers.IO).launch {
            getLocatorsListStatus.postValue(StatusWithMessage(Status.LOADING))
            try {
                val response = returnRepository.getLocatorList(orgId,subInvCode, itemId)
                ResponseDataHandler(response,getLocatorsListLiveData,getLocatorsListStatus,application).handleData("LocatorList")
                if (response.body()?.responseStatus?.errorMessage!=null)
                    returnRepository.MobileLog(
                        MobileLogBody(
                            userId = USER?.notOracleUserId,
                            errorMessage = response.body()?.responseStatus?.errorMessage,
                            apiName = "LocatorList"
                        )
                    )
            } catch (ex:Exception){
                getLocatorsListStatus.postValue(StatusWithMessage(Status.NETWORK_FAIL,application.getString(
                    R.string.error_in_getting_data)))
                Log.d(ContentValues.TAG, "===ErrorgetLocatorsList: ${ex.message}")
            }
        }
    }

    val allocateItemsStatus = SingleLiveEvent<StatusWithMessage>()
    fun returnItems(body: ReturnToWarehouseItemsBody){
        allocateItemsStatus.postValue(StatusWithMessage(Status.LOADING))
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = returnRepository.returnToWarehouseMaterial(body)
                ResponseHandler(response,allocateItemsStatus,application).handleData("ReturnItems")
                if (response.body()?.responseStatus?.errorMessage!=null)
                    returnRepository.MobileLog(
                        MobileLogBody(
                            userId = USER?.notOracleUserId,
                            errorMessage = response.body()?.responseStatus?.errorMessage,
                            apiName = "ReturnItems"
                        )
                    )
            } catch (ex:Exception){
                allocateItemsStatus.postValue(StatusWithMessage(Status.NETWORK_FAIL,application.getString(R.string.error_in_connection)))
            }
        }
    }


    val getWorkOrdersListLiveData = SingleLiveEvent<List<ReturnWorkOrder>>()
    val getWorkOrdersListStatus   = SingleLiveEvent<StatusWithMessage>()
    fun getWorkOrdersList(orgId:Int){
        job = CoroutineScope(Dispatchers.IO).launch {
            getWorkOrdersListStatus.postValue(StatusWithMessage(Status.LOADING))
            try {
                val response = returnRepository.getReturnWorkOrdersList(orgId)
                ResponseDataHandler(response,getWorkOrdersListLiveData,getWorkOrdersListStatus,application).handleData("WorkOrderList_ReturnMaterialToInventory")
                if (response.body()?.responseStatus?.errorMessage!=null)
                    returnRepository.MobileLog(
                        MobileLogBody(
                            userId = USER?.notOracleUserId,
                            errorMessage = response.body()?.responseStatus?.errorMessage,
                            apiName = "WorkOrderList_ReturnMaterialToInventory"
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



//    fun getTodayFullDate() = issueRepository.getStoredActualFullDateTime()

    val getOrganizationsListLiveData = SingleLiveEvent<List<Organization>>()
    val getOrganizationsListStatus = SingleLiveEvent<StatusWithMessage>()
    fun getOrganizationsList(){
        getOrganizationsListStatus.postValue(StatusWithMessage(Status.LOADING))
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = returnRepository.getOrganizations()
                ResponseDataHandler(response,getOrganizationsListLiveData,getOrganizationsListStatus,application).handleData("OrganizationsList")
                if (response.body()?.responseStatus?.errorMessage!=null)
                    returnRepository.MobileLog(
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
    fun getLotList(orgId:Int,itemId:Int?,subInvCode:String?){
        Log.d(ContentValues.TAG, "observeGettingLotList: viewModel")
        getLotListStatus.postValue(StatusWithMessage(Status.LOADING))
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = returnRepository.getLotList(orgId.toString(),itemId,subInvCode,null)
                ResponseDataHandler(response,getLotListLiveData,getLotListStatus,application).handleData("LotList")
                Log.d(ContentValues.TAG, "observeGettingLotList: ${response.body()?.getList?.size}")
                if (response.body()?.responseStatus?.errorMessage!=null)
                    returnRepository.MobileLog(
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
                Log.d(ContentValues.TAG, "observeGettingLotList: ${ex.message}")
            }
        }
    }
    fun getTodayDate() = returnRepository.getTodayDate()
    fun getDisplayTodayDate()= returnRepository.getTodayDate().substring(0,10)
//    fun getTodayFullDate() = repository.getStoredActualFullDateTime()
}