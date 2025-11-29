package net.gbs.epp_project.Ui.Transfer.StartTransfer

import android.app.Activity
import android.app.Application
import android.content.ContentValues
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.gbs.epp_project.Base.BaseViewModel
import net.gbs.epp_project.Model.ApiRequestBody.AllocateItemsBody
import net.gbs.epp_project.Model.ApiRequestBody.MobileLogBody
import net.gbs.epp_project.Model.ApiRequestBody.TransferMaterialBody
import net.gbs.epp_project.Model.Locator
import net.gbs.epp_project.Model.Lot
import net.gbs.epp_project.Model.OnHandItemForAllocate
import net.gbs.epp_project.Model.Status
import net.gbs.epp_project.Model.StatusWithMessage
import net.gbs.epp_project.Model.SubInventory
import net.gbs.epp_project.R
import net.gbs.epp_project.Repositories.IssueRepository
import net.gbs.epp_project.Tools.ResponseDataHandler
import net.gbs.epp_project.Tools.ResponseHandler
import net.gbs.epp_project.Tools.SingleLiveEvent
import net.gbs.epp_project.Ui.SplashAndSignIn.SignInFragment
import net.gbs.epp_project.Ui.SplashAndSignIn.SignInFragment.Companion.USER

class StartTransferViewModel(private val application: Application,activity: Activity) : BaseViewModel(application, activity) {
    val repository = IssueRepository(activity)

    fun getTodayDate() = repository.getTodayDate()
    fun getDisplayTodayDate() = repository.getTodayDate().substring(0,10)
//    fun getTodayFullDate() = repository.getStoredActualFullDateTime()
    val getSubInvertoryListLiveData = SingleLiveEvent<List<SubInventory>>()
    val getSubInvertoryListStatus   = SingleLiveEvent<StatusWithMessage>()
    fun getSubInvertoryList(orgId:Int){
        job = CoroutineScope(Dispatchers.IO).launch {
            getSubInvertoryListStatus.postValue(StatusWithMessage(Status.LOADING))
            try {
                val response = repository.getSubInventoryList(orgId)
                ResponseDataHandler(response,getSubInvertoryListLiveData,getSubInvertoryListStatus,application).handleData("SubInvList")
                if (response.body()?.responseStatus?.errorMessage!=null)
                    repository.MobileLog(
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
    fun getLocatorsList(orgId:Int,subInvCode:String){
        job = CoroutineScope(Dispatchers.IO).launch {
            getLocatorsListStatus.postValue(StatusWithMessage(Status.LOADING))
            try {
                val response = repository.getLocatorList(orgId,subInvCode)
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
                Log.d(ContentValues.TAG, "===ErrorgetLocatorsList: ${ex.message}")
            }
        }
    }

    val transferMaterialStatus = SingleLiveEvent<StatusWithMessage>()
    fun transferMaterial(body: TransferMaterialBody){
        transferMaterialStatus.postValue(StatusWithMessage(Status.LOADING))
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = repository.transferMaterial(body)
                ResponseHandler(response,transferMaterialStatus,application).handleData("TransferMaterial")
                if (response.body()?.responseStatus?.errorMessage!=null)
                    repository.MobileLog(
                        MobileLogBody(
                            userId = USER?.notOracleUserId,
                            errorMessage = response.body()?.responseStatus?.errorMessage,
                            apiName = "TransferMaterial"
                        )
                    )
            } catch (ex:Exception){
                transferMaterialStatus.postValue(StatusWithMessage(Status.NETWORK_FAIL,application.getString(R.string.error_in_connection)))
                Log.d(ContentValues.TAG, "===ErrorgetLocatorsList: ${ex.message}")
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
    fun getLotList(orgId:Int,itemId:Int?,subInvCode: String?){
        job = CoroutineScope(Dispatchers.IO).launch {
            getLotListStatus.postValue(StatusWithMessage(Status.LOADING))
            try {
                val response = repository.getLotList(orgId.toString(),itemId,subInvCode,null)
                ResponseDataHandler(response,getLotListLiveData,getLotListStatus,application).handleData("LotList")
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
            }
        }
    }
}