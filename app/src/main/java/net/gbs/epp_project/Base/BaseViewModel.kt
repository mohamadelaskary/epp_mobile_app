package net.gbs.epp_project.Base

import android.app.Activity
import android.app.Application
import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.gbs.epp_project.Model.Lot
import net.gbs.epp_project.Model.Status
import net.gbs.epp_project.Model.StatusWithMessage
import net.gbs.epp_project.R
import net.gbs.epp_project.Tools.FormatDateTime
import net.gbs.epp_project.Tools.LocaleHelper
import net.gbs.epp_project.Tools.ResponseDataHandler
import net.gbs.epp_project.Tools.SingleLiveEvent
import net.gbs.epp_project.Ui.SplashAndSignIn.SignInFragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

open class BaseViewModel (private val application: Application,private val activity: Activity): AndroidViewModel(application) {
    var job: Job? = null
    val lang = LocaleHelper.getLanguage(application.applicationContext)!!
    val deviceSerialNo = Settings.Secure.getString(
        application.contentResolver,
        Settings.Secure.ANDROID_ID
    )




//    val sharedPref: SharedPreferences =
//        application.getSharedPreferences(LOCATION_KEY, Context.MODE_PRIVATE)
//    fun savePlantToLocalStorage (plant: Plant){
//        sharedPref.edit().putString(PLANT_KEY,Plant.toJson(plant)).apply()
//    }
//    fun saveWarehouseToLocalStorage (warehouse: Warehouse){
//        sharedPref.edit().putString(WAREHOUSE_KEY,Warehouse.toJson(warehouse)).apply()
//    }
//    fun getPlantFromLocalStorage ():Plant? {
//        val plant = sharedPref.getString(PLANT_KEY,null)
//        return if (plant == null)
//            null
//        else
//            Plant.fromJson(plant)
//    }
//    fun getWarehouseFromLocalStorage ():Warehouse? {
//        val warehouse = sharedPref.getString(WAREHOUSE_KEY,null)
//        return if (warehouse == null)
//            null
//        else
//            Warehouse.fromJson(warehouse)
//    }
    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

    fun getDeviceTodayDate():String{
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date = FormatDateTime(sdf.format(Date()))
        return "${date.year()}-${date.month()}-${date.day()} ${date.hours()}:${date.minutes()}:00"
    }
}