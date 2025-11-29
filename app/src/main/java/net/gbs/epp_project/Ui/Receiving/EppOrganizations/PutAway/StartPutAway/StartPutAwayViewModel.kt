package net.gbs.epp_project.Ui.Receiving.EppOrganizations.PutAway.StartPutAway

import android.app.Activity
import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.gbs.epp_project.Base.BaseViewModel
import net.gbs.epp_project.Model.ApiRequestBody.MobileLogBody
import net.gbs.epp_project.Model.ApiRequestBody.PutawayMaterialBody
import net.gbs.epp_project.Model.DeliverLot
import net.gbs.epp_project.Model.Locator
import net.gbs.epp_project.Model.Lot
import net.gbs.epp_project.Model.Status
import net.gbs.epp_project.Model.StatusWithMessage
import net.gbs.epp_project.Model.SubInventory
import net.gbs.epp_project.R
import net.gbs.epp_project.Repositories.ReceivingRepository
import net.gbs.epp_project.Tools.ResponseDataHandler
import net.gbs.epp_project.Tools.ResponseHandler
import net.gbs.epp_project.Tools.SingleLiveEvent
import net.gbs.epp_project.Ui.SplashAndSignIn.SignInFragment
import net.gbs.epp_project.Ui.SplashAndSignIn.SignInFragment.Companion.EMPLOYEE_ID
import net.gbs.epp_project.Ui.SplashAndSignIn.SignInFragment.Companion.USER

class StartPutAwayViewModel(private val app:Application,val activity: Activity) : BaseViewModel(app,activity) {
    val receivingRepository = ReceivingRepository(activity)
    fun getTodayDate()= receivingRepository.getTodayDate()
    fun getDisplayTodayDate()= receivingRepository.getTodayDate().substring(0,10)
    val putAwayStatus = SingleLiveEvent<StatusWithMessage>()
    fun PutAwayMaterial(
        poHeaderId: Int,
        poLineId: Int,
        receiptNo: String,
        shipToOrganizationId: Int,
        locator_code: String?,
        subinventory_code:String,
        transactionDate:String,
        acceptedQty :Double,
        lot_num:String?,
        isRejected: Boolean,
        isFullControl: Boolean
    ) {
        putAwayStatus.postValue(StatusWithMessage(Status.LOADING))
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val body = PutawayMaterialBody(
                    employeeId = EMPLOYEE_ID,
                    userId = receivingRepository.userId!!,
                    poHeaderId = poHeaderId,
                    poLineId = poLineId,
                    locatorCode = locator_code,
                    subinventoryCode = subinventory_code,
                    receiptno = receiptNo,
                    shipToOrganizationId = shipToOrganizationId,
                    transactionDate = transactionDate,
                    applang = lang,
                    deviceSerialNo = deviceSerialNo,
                    lotNum = lot_num,
                    isFullControl = isFullControl,
                    userID = receivingRepository.userId,
                    isRejected = isRejected
                )
                val response = receivingRepository.PutAwayMaterial(
                    body
                )
                ResponseHandler(response,putAwayStatus,app).handleData("PutawayMaterial")
                if (response.body()?.responseStatus?.errorMessage!=null)
                    receivingRepository.MobileLog(
                        MobileLogBody(
                            userId = SignInFragment.USER?.notOracleUserId,
                            errorMessage = response.body()?.responseStatus?.errorMessage,
                            apiName = "PutawayMaterial ${body.toJson()}"
                        )
                    )
            } catch (ex:Exception){
                putAwayStatus.postValue(StatusWithMessage(Status.NETWORK_FAIL,activity.getString(R.string.error_in_connection)))
            }
        }
    }

    val getSubinventoryListStatus = SingleLiveEvent<StatusWithMessage>()
    val getLocatorListStatus = SingleLiveEvent<StatusWithMessage>()
    val getSubinventoryListLiveData = SingleLiveEvent<List<SubInventory>>()
    val getLocatorListLiveData = SingleLiveEvent<List<Locator>>()

    fun getSubInventoryList(org_id:Int){
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = receivingRepository.getSubInventoryList(org_id)
                ResponseDataHandler(response,getSubinventoryListLiveData,getSubinventoryListStatus,app).handleData("SubInvList")
                if (response.body()?.responseStatus?.errorMessage!=null)
                    receivingRepository.MobileLog(
                        MobileLogBody(
                            userId = USER?.notOracleUserId,
                            errorMessage = response.body()?.responseStatus?.errorMessage,
                            apiName = "SubInvList"
                        )
                    )
            } catch (ex:Exception){
                getSubinventoryListStatus.postValue(StatusWithMessage(Status.ERROR,app.getString(R.string.error_in_getting_data)))
            }
        }
    }
    fun getLocatorList(org_id:Int, subInventoryCode:String,itemId:Int){
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = receivingRepository.getLocatorList(org_id,subInventoryCode,itemId)
                ResponseDataHandler(response,getLocatorListLiveData,getLocatorListStatus,app).handleData("LocatorList")
                if (response.body()?.responseStatus?.errorMessage!=null)
                    receivingRepository.MobileLog(
                        MobileLogBody(
                            userId = USER?.notOracleUserId,
                            errorMessage = response.body()?.responseStatus?.errorMessage,
                            apiName = "LocatorList"
                        )
                    )
            } catch (ex:Exception){
                getSubinventoryListStatus.postValue(StatusWithMessage(Status.ERROR,app.getString(R.string.error_in_getting_data)))
            }
        }
    }
    val getLotListLiveData = SingleLiveEvent<List<Lot>>()
    val getLotListStatus = SingleLiveEvent<StatusWithMessage>()

    fun getLotList(orgId:Int,itemId:Int?,subInvCode:String?,locatorCode:String?){
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = receivingRepository.getDeliverLotList(orgId.toString(),itemId,subInvCode, locatorCode = locatorCode)
                ResponseDataHandler(response,getLotListLiveData,getLotListStatus,app).handleData("LotList")
                if (response.body()?.responseStatus?.errorMessage!=null)
                    receivingRepository.MobileLog(
                        MobileLogBody(
                            userId = USER?.notOracleUserId,
                            errorMessage = response.body()?.responseStatus?.errorMessage,
                            apiName = "LotList"
                        )
                    )
            } catch (ex:Exception){
                getLotListStatus.postValue(StatusWithMessage(Status.ERROR,app.getString(R.string.error_in_getting_data)))
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

//    fun getTodayFullDate() = receivingRepository.getStoredActualFullDateTime()
}