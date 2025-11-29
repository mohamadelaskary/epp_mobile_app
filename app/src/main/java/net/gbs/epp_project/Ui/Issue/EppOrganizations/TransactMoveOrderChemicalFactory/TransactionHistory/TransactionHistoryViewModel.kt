package net.gbs.epp_project.Ui.Issue.EppOrganizations.TransactMoveOrderChemicalFactory.TransactionHistory

import android.app.Activity
import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.gbs.epp_project.Base.BaseViewModel
import net.gbs.epp_project.Model.ApiRequestBody.MobileLogBody
import net.gbs.epp_project.Model.ApiRequestBody.TransactItemsBody
import net.gbs.epp_project.Model.ApiRequestBody.TransactMultiItemsBody
import net.gbs.epp_project.Model.Locator
import net.gbs.epp_project.Model.Lot
import net.gbs.epp_project.Model.OnHandItemForAllocate
import net.gbs.epp_project.Model.Status
import net.gbs.epp_project.Model.StatusWithMessage
import net.gbs.epp_project.R
import net.gbs.epp_project.Repositories.IssueRepository
import net.gbs.epp_project.Tools.ResponseDataHandler
import net.gbs.epp_project.Tools.ResponseHandler
import net.gbs.epp_project.Tools.SingleLiveEvent
import net.gbs.epp_project.Ui.SplashAndSignIn.SignInFragment
import net.gbs.epp_project.Ui.SplashAndSignIn.SignInFragment.Companion.USER

class TransactionHistoryViewModel(private val application: Application,activity: Activity) : BaseViewModel(application,activity) {
    val repository = IssueRepository(activity)
    fun getTodayFullDate() = repository.getTodayDate()
    fun getDisplayTodayDate()= repository.getTodayDate().substring(0,10)


    val transactItemsStatus = SingleLiveEvent<StatusWithMessage>()
     fun transactMultiItems(body: TransactMultiItemsBody){
        transactItemsStatus.postValue(StatusWithMessage(Status.LOADING))
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = repository.transactMultiItems(body)
                ResponseHandler(response,transactItemsStatus,application).handleData("TransactMultiItems")
                if (response.body()?.responseStatus?.errorMessage!=null)
                    repository.MobileLog(
                        MobileLogBody(
                            userId = USER?.notOracleUserId,
                            errorMessage = response.body()?.responseStatus?.errorMessage,
                            apiName = "TransactMultiItems"
                        )
                    )
            } catch (ex:Exception){
                transactItemsStatus.postValue(StatusWithMessage(Status.NETWORK_FAIL,application.getString(R.string.error_in_connection)))
            }
        }
    }

    val getItemsListLiveData = SingleLiveEvent<List<OnHandItemForAllocate>>()
    val getItemsListStatus = SingleLiveEvent<StatusWithMessage>()
    fun getItemInfo(orgId:Int,itemCode:String){
        getItemsListStatus.postValue(StatusWithMessage(Status.LOADING))
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = repository.getItemInfo(itemCode,orgId)
                ResponseDataHandler(response,getItemsListLiveData,getItemsListStatus,application).handleData("OnHandForAllocate")
                if (response.body()?.responseStatus?.errorMessage!=null)
                    repository.MobileLog(
                        MobileLogBody(
                            userId = USER?.notOracleUserId,
                            errorMessage = response.body()?.responseStatus?.errorMessage,
                            apiName = "OnHandForAllocate"
                        )
                    )

            } catch (ex:Exception){
                getItemsListStatus.postValue(
                    StatusWithMessage(
                        Status.NETWORK_FAIL,application.getString(
                            R.string.error_in_getting_data)+"\n${ex.message}")
                )
            }
        }
    }

    val getLotListLiveData = SingleLiveEvent<List<Lot>>()
    val getLotListStatus   = SingleLiveEvent<StatusWithMessage>()
    fun getLotList(orgId:Int,itemId:Int?,subInvCode:String?,locatorCode: String?){
        Log.d(TAG, "observeGettingLotList: viewModel")
        getLotListStatus.postValue(StatusWithMessage(Status.LOADING))
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = repository.getLotList(orgId.toString(),itemId,subInvCode,locatorCode)
                ResponseDataHandler(response,getLotListLiveData,getLotListStatus,application).handleData("LotList")
                Log.d(TAG, "observeGettingLotList: ${response.body()?.getList?.size}")
                if (response.body()?.responseStatus?.errorMessage!=null)
                    repository.MobileLog(
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
                Log.d(TAG, "observeGettingLotList: ${ex.message}")
            }
        }
    }

    val getLocatorsListLiveData = SingleLiveEvent<List<Locator>>()
    val getLocatorsListStatus   = SingleLiveEvent<StatusWithMessage>()
    fun getLocatorsListByItemId(orgId:Int,subInvCode:String,itemId: Int){
        job = CoroutineScope(Dispatchers.IO).launch {
            getLocatorsListStatus.postValue(StatusWithMessage(Status.LOADING))
            try {
                val response = repository.getLocatorListByItemId(orgId,subInvCode,itemId)
                ResponseDataHandler(response,getLocatorsListLiveData,getLocatorsListStatus,application).handleData("LocatorList")
                if (response.body()?.responseStatus?.errorMessage!=null)
                    repository.MobileLog(
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
}