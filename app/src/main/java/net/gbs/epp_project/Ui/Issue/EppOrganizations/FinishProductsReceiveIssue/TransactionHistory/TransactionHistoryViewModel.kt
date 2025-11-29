package net.gbs.epp_project.Ui.Issue.EppOrganizations.FinishProductsReceiveIssue.TransactionHistory

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
import net.gbs.epp_project.Model.Lot
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
    fun transactItems(body: TransactItemsBody){
        transactItemsStatus.postValue(StatusWithMessage(Status.LOADING))
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = repository.transactItems(body)
                ResponseHandler(response,transactItemsStatus,application).handleData("TransactItems")
                if (response.body()?.responseStatus?.errorMessage!=null)
                    repository.MobileLog(
                        MobileLogBody(
                            userId = USER?.notOracleUserId,
                            errorMessage = response.body()?.responseStatus?.errorMessage,
                            apiName = "TransactItems"
                        )
                    )
            } catch (ex:Exception){
                transactItemsStatus.postValue(StatusWithMessage(Status.NETWORK_FAIL,application.getString(R.string.error_in_connection)))
            }
        }
    }

    val getLotListLiveData = SingleLiveEvent<List<Lot>>()
    val getLotListStatus   = SingleLiveEvent<StatusWithMessage>()
    fun getLotList(orgId:Int,itemId:Int?,subInvCode:String?){
        Log.d(TAG, "observeGettingLotList: viewModel")
        getLotListStatus.postValue(StatusWithMessage(Status.LOADING))
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = repository.getLotList(orgId.toString(),itemId,subInvCode,null)
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
}