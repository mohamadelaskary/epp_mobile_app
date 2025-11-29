package net.gbs.epp_project.Repositories

import android.app.Activity
import android.content.Context
import net.gbs.epp_project.Base.BaseRepository
import net.gbs.epp_project.Model.ApiRequestBody.PhysicalInventory_CountBody
import net.gbs.epp_project.Model.ApiResponse.PhysicalInventory_CountResponse
import net.gbs.epp_project.Model.Response.NoDataResponse
import net.gbs.epp_project.Ui.SplashAndSignIn.SignInFragment.Companion.USER
import retrofit2.Response

class AuditRepository(context: Context) : BaseRepository(context) {
    val notOracleUserId = USER?.notOracleUserId!!
    suspend fun getAuditOrdersList() = apiInterface.getOrdersList(notOracleUserId,deviceSerialNo,lang)
    suspend fun getLocatorData(locatorCode:String) = apiInterface.getLocatorData(deviceSerialNo,lang, locatorCode)
    suspend fun getAllItems(orgCode:String) = apiInterface.getAllItems(deviceSerialNo,lang,orgCode)
    suspend fun getLocatorData(orgCode:String,locatorCode: String) = apiInterface.getLocatorData(deviceSerialNo,lang,orgCode,locatorCode)
    suspend fun getOrganizations() = apiInterface.getOrganizationsList2(deviceSerialNo,lang)
    suspend fun getItemData(itemCode:String,orgCode:String) = apiInterface.getItemData(deviceSerialNo,lang,itemCode,orgCode)
    suspend fun PhysicalInventory_Count(body: PhysicalInventory_CountBody): Response<PhysicalInventory_CountResponse> {
        body.DeviceSerialNo = deviceSerialNo
        body.UserID = notOracleUserId
        body.applang = lang
        return apiInterface.PhysicalInventory_Count(body)
    }
    suspend fun getTransactionsList(headerId:Int) = apiInterface.GetPhysicalInventoryOrderCounting_Transactions(notOracleUserId,deviceSerialNo,lang,headerId)
    suspend fun createNewCycleCountOrderByLocator(locatorId:Int,organizationCode:String) = apiInterface.createNewCycleCountOrderByLocator(notOracleUserId,deviceSerialNo,lang, locatorId,organizationCode )
    suspend fun createNewCycleCountOrderByItem(itemCode:String,organizationCode:String) = apiInterface.createNewCycleCountOrderByItem(notOracleUserId,deviceSerialNo,lang, itemCode, organizationCode )
    suspend fun saveCycleCountOrderDetails(itemCode:String,locatorCode: String,cycleCountHeaderId:Int,qty:Double,orgCode:String)
    = apiInterface.saveCycleCountOrderDetails(
        userId = notOracleUserId,
        deviceSerialNo = deviceSerialNo,
        appLang = lang,
        itemCode = itemCode,
        locatorCode = locatorCode,
        cycleCountHeaderId = cycleCountHeaderId,
        qty = qty,
        organizationCode = orgCode
    )
    suspend fun getOnHands(
        headerId: Int,organizationCode:String
    ) = apiInterface.getCycleCountOrder_StockCompare(
        notOracleUserId,deviceSerialNo,lang, organizationCode,headerId
    )


    suspend fun finishTracking(
        subInventoryCode:String,headerId: Int
    ) = apiInterface.finishTracking(
        notOracleUserId,deviceSerialNo,lang, headerId, subInventoryCode
    )
    suspend fun finishCycleCount(
        headerId: Int
    ) = apiInterface.finishCycleCountOrder(
        notOracleUserId,deviceSerialNo,lang, headerId
    )

}