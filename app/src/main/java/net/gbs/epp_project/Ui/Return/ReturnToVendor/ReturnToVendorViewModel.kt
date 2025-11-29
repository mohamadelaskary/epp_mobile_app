package net.gbs.epp_project.Ui.Return.ReturnToVendor

import android.app.Activity
import android.app.Application
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.gbs.epp_project.Base.BaseViewModel
import net.gbs.epp_project.Model.ApiRequestBody.AllocateItemsBody
import net.gbs.epp_project.Model.ApiRequestBody.MobileLogBody
import net.gbs.epp_project.Model.ApiRequestBody.ReturnMaterialBody
import net.gbs.epp_project.Model.Lot
import net.gbs.epp_project.Model.POItem
import net.gbs.epp_project.Model.POLineReturn
import net.gbs.epp_project.Model.PoLine
import net.gbs.epp_project.Model.PurchaseOrder
import net.gbs.epp_project.Model.Status
import net.gbs.epp_project.Model.StatusWithMessage
import net.gbs.epp_project.R
import net.gbs.epp_project.Tools.ResponseDataHandler
import net.gbs.epp_project.Tools.ResponseHandler
import net.gbs.epp_project.Tools.SingleLiveEvent
import net.gbs.epp_project.Ui.Return.ReturnRepository
import net.gbs.epp_project.Ui.SplashAndSignIn.SignInFragment
import net.gbs.epp_project.Ui.SplashAndSignIn.SignInFragment.Companion.USER

class ReturnToVendorViewModel(private val application: Application,val activity: Activity) : BaseViewModel(application,activity) {
    val repository = ReturnRepository(activity)
    var purchaseOrder:PurchaseOrder? = null
    var poLinesList = mutableListOf<POLineReturn>()
    var poItemsList = arrayListOf<POItem>()
    val purchaseOrderLiveData = SingleLiveEvent<List<PurchaseOrder>>()
    val purchaseOrderStatus = SingleLiveEvent<StatusWithMessage> ()

    fun getPurchaseOrdersList(
        poNum: String,
    ) {
        purchaseOrderStatus.postValue(StatusWithMessage(Status.LOADING))
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = repository.getPurchaseOrderByPoNo(poNum)
                ResponseDataHandler(
                    response,
                    purchaseOrderLiveData,
                    purchaseOrderStatus,
                    getApplication()
                ).handleData("PurchaseOrderGetByPoNo")
                if (response.body()?.responseStatus?.errorMessage!=null)
                    repository.MobileLog(
                        MobileLogBody(
                            userId = USER?.notOracleUserId,
                            errorMessage = response.body()?.responseStatus?.errorMessage,
                            apiName = "PurchaseOrderGetByPoNo"
                        )
                    )
            } catch (ex:Exception){
                purchaseOrderStatus.postValue(
                    StatusWithMessage(
                        Status.NETWORK_FAIL,application.getString(
                    R.string.error_in_getting_data))
                )
            }
        }
    }

    val poItemsLiveData = SingleLiveEvent<ArrayList<POItem>>()
    val poItemsStatus = SingleLiveEvent<StatusWithMessage> ()

    fun getPurchaseOrderItemListReturn(
        poNum: String,
    ) {
        purchaseOrderStatus.postValue(StatusWithMessage(Status.LOADING))
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = repository.getPurchaseOrderItemListReturn(poNum)
                ResponseDataHandler(
                    response,
                    poItemsLiveData,
                    poItemsStatus,
                    getApplication()
                ).handleData("PurchaseOrderItemList_Return")

            } catch (ex:Exception){
                poItemsStatus.postValue(
                    StatusWithMessage(
                        Status.NETWORK_FAIL,application.getString(
                            R.string.error_in_getting_data))
                )
            }
        }
    }

    val returnMaterialStatus = SingleLiveEvent<StatusWithMessage>()
    fun returnMaterial(body: ReturnMaterialBody){


        returnMaterialStatus.postValue(StatusWithMessage(Status.LOADING))
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = repository.returnMaterial(body)
                ResponseHandler(response,returnMaterialStatus,application).handleData("ReturnMaterial_Multi")
            } catch (ex:Exception){
                returnMaterialStatus.postValue(StatusWithMessage(Status.NETWORK_FAIL,application.getString(R.string.error_in_connection)))
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

//    val getDateLiveData = SingleLiveEvent<String>()
//    val getDateStatus   = SingleLiveEvent<StatusWithMessage>()
//    fun getTodayDate(){
//        getDateStatus.postValue(StatusWithMessage(Status.LOADING))
//        job = CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val response = repository.getDate()
//                ResponseDataHandler(response,getDateLiveData,getDateStatus,application).handleData()
//            } catch (ex:Exception){
//                getDateStatus.postValue(StatusWithMessage(Status.NETWORK_FAIL,application.getString(R.string.error_in_getting_data)))
//            }
//        }
//    }
    fun getTodayDate() = repository.getTodayDate()
    fun getDisplayTodayDate()= repository.getTodayDate().substring(0,10)
//    fun getTodayFullDate() = repository.getStoredActualFullDateTime()
}