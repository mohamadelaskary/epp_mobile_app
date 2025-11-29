package net.gbs.epp_project.Ui.Issue.EppOrganizations.TransactMoveOrderChemicalFactory

import android.app.Activity
import android.app.Application
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.gbs.epp_project.Base.BaseViewModel
import net.gbs.epp_project.Model.ApiRequestBody.AllocateItemsBody
import net.gbs.epp_project.Model.ApiRequestBody.MobileLogBody
import net.gbs.epp_project.Model.ApiRequestBody.TransactItemsBody
import net.gbs.epp_project.Model.ApiResponse.LocatorItems
import net.gbs.epp_project.Model.IssueOrderLists
import net.gbs.epp_project.Model.Locator
import net.gbs.epp_project.Model.Lot
import net.gbs.epp_project.Model.MoveOrder
import net.gbs.epp_project.Model.MoveOrderLine
import net.gbs.epp_project.Model.OnHandItemForAllocate
import net.gbs.epp_project.Model.Status
import net.gbs.epp_project.Model.StatusWithMessage
import net.gbs.epp_project.Model.SubInventory
import net.gbs.epp_project.R
import net.gbs.epp_project.Repositories.IssueRepository
import net.gbs.epp_project.Tools.ResponseDataHandler
import net.gbs.epp_project.Tools.ResponseHandler
import net.gbs.epp_project.Tools.SingleLiveEvent
import net.gbs.epp_project.Ui.SplashAndSignIn.SignInFragment.Companion.USER

class TransactMoveOrderViewModel(private val application: Application,val activity: Activity) : BaseViewModel(application,activity) {
    var moveOrder: MoveOrder? = null
    private val issueRepository = IssueRepository(activity)
    val getMoveOrderLiveData = SingleLiveEvent<MoveOrder>()
    val getMoveOrderStatus   = SingleLiveEvent<StatusWithMessage>()
    fun getMoveOrder(headerId:Int?,moveOrderNumber:Int,orgId:Int){
        job = CoroutineScope(Dispatchers.IO).launch {
            getMoveOrderStatus.postValue(StatusWithMessage(Status.LOADING))
            try {
                val response = issueRepository.getMoveOrderByHeaderId(headerId, moveOrderNumber, orgId)
                ResponseDataHandler(response,getMoveOrderLiveData,getMoveOrderStatus,application).handleData("MoveOrderGetByHEADER_ID")
                if (response.body()?.responseStatus?.errorMessage!=null)
                    issueRepository.MobileLog(
                        MobileLogBody(
                            userId = USER?.notOracleUserId,
                            errorMessage = response.body()?.responseStatus?.errorMessage,
                            apiName = "MoveOrderGetByHEADER_ID"
                        )
                    )
            } catch (ex:Exception){
                getMoveOrderStatus.postValue(StatusWithMessage(Status.NETWORK_FAIL,application.getString(
                    R.string.error_in_getting_data)))
            }
        }
    }



    val getMoveOrderLinesLiveData = SingleLiveEvent<List<MoveOrderLine>>()
    val getMoveOrderLinesStatus   = SingleLiveEvent<StatusWithMessage>()
    fun getMoveOrderLines(headerId:Int,orgId:Int){
        job = CoroutineScope(Dispatchers.IO).launch {
            getMoveOrderLinesStatus.postValue(StatusWithMessage(Status.LOADING))
            try {
                val response = issueRepository.getMoveOrderLinesByHeaderId(headerId, orgId)
                ResponseDataHandler(response,getMoveOrderLinesLiveData,getMoveOrderLinesStatus,application).handleData("MoveOrderLinesGetByHEADER_ID")
                if (response.body()?.responseStatus?.errorMessage!=null)
                    issueRepository.MobileLog(
                        MobileLogBody(
                            userId = USER?.notOracleUserId,
                            errorMessage = response.body()?.responseStatus?.errorMessage,
                            apiName = "MoveOrderLinesGetByHEADER_ID"
                        )
                    )
            } catch (ex:Exception){
                getMoveOrderLinesStatus.postValue(StatusWithMessage(Status.NETWORK_FAIL,application.getString(
                    R.string.error_in_getting_data)))
                Log.d(TAG, "===ErrorgetMoveOrderLines: ${ex.message}")
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
                Log.e(TAG, "getOnHandLocatorDetails: ${ex.message}", ex)
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

    val getSubInventoryOnHandLiveData = SingleLiveEvent<List<OnHandItemForAllocate>>()
    val getSubInventoryOnHandStatus = SingleLiveEvent<StatusWithMessage>()
    fun getSubInventoryOnHand(orgId:Int,subInvCode: String,itemCode:String){
        getSubInventoryOnHandStatus.postValue(StatusWithMessage(Status.LOADING))
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = repository.getOnHandQtyGroupedBySubInv(itemCode,subInvCode,orgId)
                ResponseDataHandler(response,getSubInventoryOnHandLiveData,getSubInventoryOnHandStatus,application).handleData("OnHandForAllocate")
                if (response.body()?.responseStatus?.errorMessage!=null)
                    repository.MobileLog(
                        MobileLogBody(
                            userId = USER?.notOracleUserId,
                            errorMessage = response.body()?.responseStatus?.errorMessage,
                            apiName = "OnHandForAllocate_GroupedBySubInv"
                        )
                    )

            } catch (ex:Exception){
                getSubInventoryOnHandStatus.postValue(
                    StatusWithMessage(
                        Status.NETWORK_FAIL,application.getString(
                            R.string.error_in_getting_data)+"\n${ex.message}")
                )
            }
        }
    }

    val getLocatorsListLiveData = SingleLiveEvent<List<Locator>>()
    val getLocatorsListStatus   = SingleLiveEvent<StatusWithMessage>()
    fun getLocatorsListByItemId(orgId:Int,subInvCode:String,itemId: Int){
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

    fun getLocatorsList(orgId:Int,subInvCode:String,itemId: Int){
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
    fun allocateItems(body:AllocateItemsBody){
        allocateItemsStatus.postValue(StatusWithMessage(Status.LOADING))
        job = CoroutineScope(Dispatchers.IO).launch { 
            try {
                val response = issueRepository.allocateItems(body)
                ResponseHandler(response,allocateItemsStatus,application).handleData("AllocateItems")
                if (response.body()?.responseStatus?.errorMessage!=null)
                    issueRepository.MobileLog(
                        MobileLogBody(
                            userId = USER?.notOracleUserId,
                            errorMessage = response.body()?.responseStatus?.errorMessage,
                            apiName = "AllocateItems"
                        )
                    )
            } catch (ex:Exception){
                allocateItemsStatus.postValue(StatusWithMessage(Status.NETWORK_FAIL,application.getString(R.string.error_in_connection)))
            }
        }
    }

    fun transactItems(body: TransactItemsBody){
        allocateItemsStatus.postValue(StatusWithMessage(Status.LOADING))
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = issueRepository.transactItems(body)
                ResponseHandler(response,allocateItemsStatus,application).handleData("TransactItems")
                if (response.body()?.responseStatus?.errorMessage!=null)
                    issueRepository.MobileLog(
                        MobileLogBody(
                            userId = USER?.notOracleUserId,
                            errorMessage = response.body()?.responseStatus?.errorMessage,
                            apiName = "TransactItems"
                        )
                    )
            } catch (ex:Exception){
                allocateItemsStatus.postValue(StatusWithMessage(Status.NETWORK_FAIL,application.getString(R.string.error_in_connection)))
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

    private val repository = IssueRepository(activity)
    val getMoveOrdersListLiveData = SingleLiveEvent<List<MoveOrder>>()
    val getMoveOrdersListStatus   = SingleLiveEvent<StatusWithMessage>()
    fun getMoveOrdersList_FinishProduct(orgId:Int){
        job = CoroutineScope(Dispatchers.IO).launch {
            getMoveOrdersListStatus.postValue(StatusWithMessage(Status.LOADING))
            try {
                val response = repository.getMoveOrdersList_FinishProduct( orgId)
                ResponseDataHandler(response,getMoveOrdersListLiveData,getMoveOrdersListStatus,application).handleData("MoveOrdersList_FinishProduct")
                if (response.body()?.responseStatus?.errorMessage!=null)
                    issueRepository.MobileLog(
                        MobileLogBody(
                            userId = USER?.notOracleUserId,
                            errorMessage = response.body()?.responseStatus?.errorMessage,
                            apiName = "MoveOrdersList_FinishProduct"
                        )
                    )
            } catch (ex:Exception){
                getMoveOrdersListStatus.postValue(
                    StatusWithMessage(
                        Status.NETWORK_FAIL,application.getString(
                            R.string.error_in_getting_data))
                )
            }
        }
    }
    fun getMoveOrdersList_Factory(orgId:Int){
        job = CoroutineScope(Dispatchers.IO).launch {
            getMoveOrdersListStatus.postValue(StatusWithMessage(Status.LOADING))
            try {
                val response = repository.getMoveOrdersList_Factory( orgId)
                ResponseDataHandler(response,getMoveOrdersListLiveData,getMoveOrdersListStatus,application).handleData("MoveOrdersList_Factory")
                if (response.body()?.responseStatus?.errorMessage!=null)
                    issueRepository.MobileLog(
                        MobileLogBody(
                            userId = USER?.notOracleUserId,
                            errorMessage = response.body()?.responseStatus?.errorMessage,
                            apiName = "MoveOrdersList_Factory"
                        )
                    )
            } catch (ex:Exception){
                getMoveOrdersListStatus.postValue(
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
                val response = repository.getIssueOrdersList(requestNumber,orgId)
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

    fun getTodayDate() = repository.getTodayDate()
    fun getDisplayTodayDate()= issueRepository.getTodayDate().substring(0,10)
}