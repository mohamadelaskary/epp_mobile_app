package net.gbs.epp_project.Ui.Gate.RegisterArrival

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.gbs.epp_project.Base.BaseViewModel
import net.gbs.epp_project.Model.ApiRequestBody.SaveNewVehicleRecordData
import net.gbs.epp_project.Model.Container
import net.gbs.epp_project.Model.FeBooking
import net.gbs.epp_project.Model.Governorate
import net.gbs.epp_project.Model.SalesAgreementDetails
import net.gbs.epp_project.Model.SalesAgreementHeader
import net.gbs.epp_project.Model.Status
import net.gbs.epp_project.Model.StatusWithMessage
import net.gbs.epp_project.R
import net.gbs.epp_project.Tools.ResponseDataHandler
import net.gbs.epp_project.Tools.ResponseHandler
import net.gbs.epp_project.Tools.SingleLiveEvent
import net.gbs.epp_project.Ui.Gate.GateRepository

class RegisterArrivalViewModel(private val application: Application,activity: Activity) : BaseViewModel(application,activity) {
    private val repository = GateRepository(application.applicationContext)


    var selectedGovernorateId : Int? = null
    var selectedBookingId : Int? = null
    var selectedSalesOrderNumber : String? = null
    var selectedAgreementDetailsNoId : Int? = null
    var customerName : String? = null
    var driverName : String? = null
    var driverPhoneNumber : String? = null
    var driverNationalId : String? = null
    var plateNo : String? = null
    var containers : MutableLiveData<MutableList<Container>> = MutableLiveData()
    val trailers : MutableLiveData<MutableList<String>> = MutableLiveData()


    val getGovernoratesList = SingleLiveEvent<List<Governorate>>()
    val getGovernoratesListStatus = SingleLiveEvent<StatusWithMessage>()
    fun getGovernoratesList(){
        getGovernoratesListStatus.postValue(StatusWithMessage(Status.LOADING))
        try{
            CoroutineScope(Dispatchers.IO).launch {
                ResponseDataHandler(
                    repository.getGovernoratesList(),
                    getGovernoratesList,
                    getGovernoratesListStatus,
                    application
                ).handleData("GovernoratesGetList")
            }
        } catch (ex: Exception){
            getGovernoratesListStatus.postValue(StatusWithMessage(Status.NETWORK_FAIL,application.getString(
                R.string.error_in_getting_data)))
            Log.e("RegisterArrivalViewModel", "getGovernoratesList: ", ex)
        }
    }
    val getFeBookingsList = SingleLiveEvent<List<FeBooking>>()
    val getFeBookingsListStatus = SingleLiveEvent<StatusWithMessage>()
    fun getFeBookingsList(){
        getFeBookingsListStatus.postValue(StatusWithMessage(Status.LOADING))
        try{
            CoroutineScope(Dispatchers.IO).launch {
                ResponseDataHandler(
                    repository.getFeBookingsList(),
                    getFeBookingsList,
                    getFeBookingsListStatus,
                    application
                ).handleData("FeBookingsGetList")
            }
        } catch (ex: Exception){
            getFeBookingsListStatus.postValue(StatusWithMessage(Status.NETWORK_FAIL,application.getString(
                R.string.error_in_getting_data)))
            Log.e("RegisterArrivalViewModel", "getFeBookingsList: ", ex)
        }
    }
    val getAgreementHsList = SingleLiveEvent<List<SalesAgreementHeader>>()
    val getAgreementHsListStatus = SingleLiveEvent<StatusWithMessage>()
    fun getAgreementHsList(){
        getAgreementHsListStatus.postValue(StatusWithMessage(Status.LOADING))
        try{
            CoroutineScope(Dispatchers.IO).launch {
                ResponseDataHandler(
                    repository.getEppGbsSalesAgreementHList(),
                    getAgreementHsList,
                    getAgreementHsListStatus,
                    application
                ).handleData("EppGbsSalesAgreementHGetList")
            }
        } catch (ex: Exception){
            getAgreementHsListStatus.postValue(StatusWithMessage(Status.NETWORK_FAIL,application.getString(
                R.string.error_in_getting_data)))
            Log.e("RegisterArrivalViewModel", "getAgreementHsList: ", ex)
        }
    }
    val getAgreementDsList = SingleLiveEvent<List<SalesAgreementDetails>>()
    val getAgreementDsListStatus = SingleLiveEvent<StatusWithMessage>()
    fun getAgreementDsList(headerId:Int){
        getAgreementDsListStatus.postValue(StatusWithMessage(Status.LOADING))
        try{
            CoroutineScope(Dispatchers.IO).launch {
                ResponseDataHandler(
                    repository.getEppGbsSalesAgreementDList(headerId),
                    getAgreementDsList,
                    getAgreementDsListStatus,
                    application
                ).handleData("EppGbsSalesAgreementDGetList")
            }
        } catch (ex: Exception){
            getAgreementDsListStatus.postValue(StatusWithMessage(Status.NETWORK_FAIL,application.getString(
                R.string.error_in_getting_data)))
            Log.e("RegisterArrivalViewModel", "getAgreementDsList: ", ex)
        }
    }

    val saveNewVehicleStatus = SingleLiveEvent<StatusWithMessage>()
    fun saveNewVehicle(body: SaveNewVehicleRecordData){
        saveNewVehicleStatus.postValue(StatusWithMessage(Status.LOADING))
        CoroutineScope(Dispatchers.IO).launch {
            try{
            ResponseHandler(
                repository.saveNewVehicleRecord(body),
                saveNewVehicleStatus,
                application
            ).handleData("EppGbsSalesAgreementDGetList")
            } catch (ex: Exception){
                saveNewVehicleStatus.postValue(StatusWithMessage(Status.NETWORK_FAIL,application.getString(
                    R.string.error_in_getting_data)))
                Log.e("RegisterArrivalViewModel", "saveNewVehicle: ", ex)
            }
        }
    }
}