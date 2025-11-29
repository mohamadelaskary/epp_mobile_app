package net.gbs.epp_project.Ui.Return

import android.app.Activity
import android.content.Context
import net.gbs.epp_project.Base.BaseRepository
import net.gbs.epp_project.Model.ApiRequestBody.ReturnMaterialBody
import net.gbs.epp_project.Model.ApiRequestBody.ReturnToWarehouseItemsBody
import net.gbs.epp_project.Model.Response.NoDataResponse
import net.gbs.epp_project.Ui.SplashAndSignIn.SignInFragment
import retrofit2.Response

class ReturnRepository (context: Context) : BaseRepository(context) {
    suspend fun getPurchaseOrderByPoNo(poNum:String) = apiInterface.getPurchaseOrderGetByPoNo(userId!!,deviceSerialNo,lang,poNum)
    suspend fun getPurchaseOrderItemListReturn(poNum:String) = apiInterface.getPurchaseOrderItemListReturn(userId!!,deviceSerialNo,lang,poNum)
    suspend fun returnMaterial(
        body: ReturnMaterialBody
    ):Response<NoDataResponse> {
        body.applang = lang
        body.deviceSerialNo = deviceSerialNo
        body.employeeId = SignInFragment.EMPLOYEE_ID
        body.programApplicationId = SignInFragment.PROGRAM_APPLICATION_ID
        body.programId = SignInFragment.PROGRAM_ID
        body.userId = userId
        return apiInterface.ReturnMaterial(body)
    }
    suspend fun returnToWarehouseMaterial(
        body: ReturnToWarehouseItemsBody
    ):Response<NoDataResponse> {
        body.applang = lang
        body.deviceSerialNo = deviceSerialNo
        body.employee_id = SignInFragment.EMPLOYEE_ID.toString()
        body.program_application_id= SignInFragment.PROGRAM_APPLICATION_ID
        body.program_id = SignInFragment.PROGRAM_ID
        body.user_id = userId!!
        return apiInterface.ReturnItems(body)
    }
    suspend fun getOrganizations() = apiInterface.getOrganizationsList(userId!!,deviceSerialNo,lang)
    suspend fun getLocatorList(
        orgId:Int,
        subInvCode:String,
        itemId:Int
    ) = apiInterface.getLocatorListByItemId(
        orgId = orgId.toString(),
        subinv_code = subInvCode,
        itemId = itemId
    )
    suspend fun getReturnWorkOrdersList(orgId:Int) = apiInterface.getWorkOrderList_ReturnMaterialToInventory(userId!!,deviceSerialNo,lang,orgId,100)
    suspend fun getReturnWorkOrderLinesList(orgId:Int,headerId:Int) = apiInterface.getReturnWorkOrderLinesGetByHEADER_ID(userId!!,deviceSerialNo,lang,orgId,headerId)
    suspend fun getReturnWorkOrderLinesList(orgId:Int,workOrderName:String) = apiInterface.getReturnLinesGetByWorkOrderName(userId!!,deviceSerialNo,lang,orgId,workOrderName)

}