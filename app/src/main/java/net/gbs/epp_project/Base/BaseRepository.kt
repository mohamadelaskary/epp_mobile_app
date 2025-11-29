package net.gbs.epp_project.Base

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.provider.Settings
import android.util.Log
import com.honeywell.aidc.BuildConfig
import net.gbs.epp_project.Model.ApiRequestBody.MobileLogBody
import net.gbs.epp_project.Network.ApiFactory.ApiFactory
import net.gbs.epp_project.Network.ApiFactory.BaseUrlProvider.Companion.updateBaseUrl
import net.gbs.epp_project.Network.ApiFactory.RetrofitInstance
import net.gbs.epp_project.Network.ApiInterface.ApiInterface
import net.gbs.epp_project.Tools.CommunicationData
import net.gbs.epp_project.Tools.FormatDateTime
import net.gbs.epp_project.Tools.LocaleHelper
import net.gbs.epp_project.Ui.SplashAndSignIn.SignInFragment
import net.gbs.epp_project.Ui.SplashAndSignIn.SignInFragment.Companion.USER
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

open class BaseRepository(private val context: Context) {
    var apiInterface: ApiInterface
    init {
        val communicationData = CommunicationData(context)
        val baseUrl = updateBaseUrl(communicationData.getProtocol(),communicationData.getIpAddress(),communicationData.getPortNumber())
        Log.d(TAG, "BaseRepository: ${baseUrl}")
        apiInterface = RetrofitInstance.apiService(baseUrl)
    }


    val localStorage = LocalStorage(context)
    val lang = LocaleHelper.getLanguage(context)!!
    val deviceSerialNo = Settings.Secure.getString(
        context?.contentResolver,
        Settings.Secure.ANDROID_ID
    )
    val userId = USER?.userId
    suspend fun getSubInventoryList(
        orgId:Int
    ) = apiInterface.getSubInvList(
        orgId = orgId.toString()
    )


    suspend fun MobileLog(body: MobileLogBody) = apiInterface.MobileLog(body)


    suspend fun getLotList(orgId:String,itemId:Int?,subInventoryCode:String?,locatorCode: String?) = apiInterface.getLotList(
        userId = userId!!,
        deviceSerialNo = deviceSerialNo,
        appLang = lang,
        orgId = orgId,
        itemId = itemId,
        SUBINVENTORY_CODE = subInventoryCode,
        locatorCode = locatorCode
    )
    suspend fun getDeliverLotList(orgId:String,itemId:Int?,subInventoryCode:String?,locatorCode:String?) = apiInterface.getLotList(
        userId = userId!!,
        deviceSerialNo = deviceSerialNo,
        appLang = lang,
        orgId = orgId,
        itemId = itemId,
        SUBINVENTORY_CODE = subInventoryCode,
        locatorCode = locatorCode
    )

    suspend fun mobileLog(body: MobileLogBody){
        body.userId = USER?.notOracleUserId
        apiInterface.MobileLog(body)
    }
    suspend fun getDate() = apiInterface.getDate()

    //    fun getStoredDeviceDate()            = localStorage.getStoredDeviceDate()
//    fun getStoredActualDate()            = localStorage.getStoredActualDate()
//    fun getStoredActualFullDateTime()    = localStorage.getStoredActualFullDateTime()
    fun setStoredDeviceDate(date:String) = localStorage.setStoredDeviceDate(date)
    fun setStoredActualDate(date:String) = localStorage.setStoredActualDate(date)

    fun getTodayDate():String{
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date = FormatDateTime(sdf.format(Date()))
        Log.d(TAG, "getTodayDate: ${date.year()}-${date.month()}-${date.day()} ${date.hours()}:${date.minutes()}:00")
        return "${date.year()}-${date.month()}-${date.day()}T${date.hours()}:${date.minutes()}:00.00"
    }
}
