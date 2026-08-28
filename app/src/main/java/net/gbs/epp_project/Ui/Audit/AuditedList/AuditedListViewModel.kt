package net.gbs.epp_project.Ui.Audit.AuditedList

import android.app.Activity
import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.gbs.epp_project.Base.BaseViewModel
import net.gbs.epp_project.Model.ApiRequestBody.MobileLogBody
import net.gbs.epp_project.Model.AuditTransaction
import net.gbs.epp_project.Model.Status
import net.gbs.epp_project.Model.StatusWithMessage
import net.gbs.epp_project.R
import net.gbs.epp_project.Repositories.AuditRepository
import net.gbs.epp_project.Tools.ResponseDataHandler
import net.gbs.epp_project.Tools.SingleLiveEvent
import net.gbs.epp_project.Ui.SplashAndSignIn.SignInFragment.Companion.USER
import java.lang.Exception

class AuditedListViewModel(private val application: Application,val activity: Activity) : BaseViewModel(application,activity) {
    private val auditRepository = AuditRepository(activity)
    val getAuditTransactionsListLiveData = SingleLiveEvent<List<AuditTransaction>>()
    val getAuditTransactionsListStatus   = SingleLiveEvent<StatusWithMessage>()
    fun getTransactionsList(headerId:Int){
        getAuditTransactionsListStatus.postValue(StatusWithMessage(Status.LOADING))
        try {
            job = CoroutineScope(Dispatchers.IO).launch {
                try{
                    val response = auditRepository.getTransactionsList(headerId)
                    ResponseDataHandler(
                        response,
                        getAuditTransactionsListLiveData,
                        getAuditTransactionsListStatus,
                        application
                    ).handleData("GetPhysicalInventoryOrderCounting_Transactions")
                    if (response.body()?.responseStatus?.errorMessage!=null)
                        auditRepository.MobileLog(
                            MobileLogBody(
                                userId = USER?.notOracleUserId,
                                errorMessage = response.body()?.responseStatus?.errorMessage,
                                apiName = "GetPhysicalInventoryOrderCounting_Transactions"
                            )
                        )
                } catch (ex:Exception){
                   // getAuditTransactionsListStatus.postValue(StatusWithMessage(Status.NETWORK_FAIL,application.getString(
                       // R.string.error_in_getting_data)))
                    getAuditTransactionsListStatus.postValue(StatusWithMessage(Status.NETWORK_FAIL,ex.message!!))
                }
            }
        } catch (ex:Exception){
            Log.e(TAG, "getTransactionsList: ", ex)
//            getAuditTransactionsListStatus.postValue(StatusWithMessage(Status.NETWORK_FAIL,application.getString(
//                R.string.error_in_getting_data)))
            getAuditTransactionsListStatus.postValue(StatusWithMessage(Status.NETWORK_FAIL,ex.message!!))
        }
    }
}