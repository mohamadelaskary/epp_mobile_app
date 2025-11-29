package net.gbs.epp_project.Ui.Gate.ConfirmArrival.TrucksList

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.gbs.epp_project.Base.BaseViewModel
import net.gbs.epp_project.Model.Status
import net.gbs.epp_project.Model.StatusWithMessage
import net.gbs.epp_project.Model.Vehicle
import net.gbs.epp_project.R
import net.gbs.epp_project.Tools.ResponseDataHandler
import net.gbs.epp_project.Tools.SingleLiveEvent
import net.gbs.epp_project.Ui.Gate.GateRepository

class TruckListViewModel(private val application: Application,activity: Activity) : BaseViewModel(application,activity) {
    private val repository = GateRepository(activity)
    val viewArrivalVehicles = SingleLiveEvent<List<Vehicle>>()
    val viewArrivalVehiclesStatus = SingleLiveEvent<StatusWithMessage>()
    fun viewArrivalVehicles(){
        viewArrivalVehiclesStatus.postValue(StatusWithMessage(Status.LOADING))
        try {
            CoroutineScope(Dispatchers.IO).launch {
                ResponseDataHandler(repository.getArrivedVehicles(),viewArrivalVehicles,viewArrivalVehiclesStatus,application).handleData("ViewArrivalVehicle")
            }
        } catch (ex: Exception){
            viewArrivalVehiclesStatus.postValue(StatusWithMessage(
                Status.NETWORK_FAIL,application.getString(
                    R.string.error_in_connection)))
            Log.e("TruckListViewModel", "viewArrivalVehicles: ",ex)
        }
    }
}