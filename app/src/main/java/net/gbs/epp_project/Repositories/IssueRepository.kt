package net.gbs.epp_project.Repositories

import android.app.Activity
import android.content.Context
import net.gbs.epp_project.Base.BaseRepository
import net.gbs.epp_project.Model.ApiRequestBody.AllocateItemsBody
import net.gbs.epp_project.Model.ApiRequestBody.TransactItemsBody
import net.gbs.epp_project.Model.ApiRequestBody.TransactMultiItemsBody
import net.gbs.epp_project.Model.ApiRequestBody.TransferMaterialBody
import net.gbs.epp_project.Model.Response.NoDataResponse
import net.gbs.epp_project.Ui.SplashAndSignIn.SignInFragment
import retrofit2.Response

class IssueRepository(context: Context) : BaseRepository(context) {
    suspend fun getOrganizations() = apiInterface.getOrganizationsList(userId!!,deviceSerialNo,lang)
    suspend fun getLocatorList(
        orgId:Int,
        subInvCode:String
    ) = apiInterface.getLocatorList(
        orgId = orgId.toString(),
        subinv_code = subInvCode
    )
    suspend fun getLocatorListByItemId(
        orgId:Int,
        subInvCode:String,
        itemId:Int
    ) = apiInterface.getLocatorListByItemId(
        orgId = orgId.toString(),
        subinv_code = subInvCode,
        itemId = itemId
    )
    suspend fun getMoveOrderByHeaderId(
        headerId:Int?,
        moveOrderNumber:Int,
        orgId:Int
    ) = apiInterface.getMoveOrderByHeaderId(
        userId!!,
        deviceSerialNo,
        lang,
        headerId = headerId,
        moveOrderNumber=moveOrderNumber,
        orgId = orgId
    )

    suspend fun getMoveOrderLinesByHeaderId(
        headerId:Int?,
        orgId:Int
    ) = apiInterface.getMoveOrderLinesByHeaderId(
        userId!!,
        deviceSerialNo,
        lang,
        headerId = headerId,
        orgId = orgId
    )

    suspend fun getMoveOrderLinesByWorkOrderName(
        workOrderName:String?,
        orgId:Int
    ) = apiInterface.getMoveOrderLinesGetByWorkOrderName(
        userId!!,
        deviceSerialNo,
        lang,
        Work_Order_Name = workOrderName,
        orgId = orgId
    )

    suspend fun getMoveOrdersList_Factory(
        orgId:Int
    ) = apiInterface.getMoveOrdersList_Factory(
        userId!!,
        deviceSerialNo,
        lang,
        orgId = orgId
    )
    suspend fun getMoveOrdersList_FinishProduct(
        orgId:Int
    ) = apiInterface.getMoveOrdersList_FinishProduct(
        userId!!,
        deviceSerialNo,
        lang,
        orgId = orgId,
    )
    suspend fun getWorkOrdersList(
        orgId:Int
    ) = apiInterface.getWorkOrdersList(
        userId!!,
        deviceSerialNo,
        lang,
        orgId = orgId,
    )
    suspend fun getJobOrdersList(
        orgId:Int
    ) = apiInterface.getJobOrdersList(
        userId!!,
        deviceSerialNo,
        lang,
        orgId = orgId,
    )

    suspend fun getIssueOrdersList(
        requestNumber:String,
        orgId: Int
    ) = apiInterface.getMoveOrderGetDetailsByHEADER_ID(
        userId!!,
        deviceSerialNo,
        lang,
         requestNumber = requestNumber,
        orgId = orgId
    )



    suspend fun allocateItems(
        body:AllocateItemsBody
    ):Response<NoDataResponse> {
        body.appLang = lang
        body.deviceSerialNo = deviceSerialNo
        body.employeeId = SignInFragment.EMPLOYEE_ID
        body.programApplicationId = SignInFragment.PROGRAM_APPLICATION_ID
        body.programId = SignInFragment.PROGRAM_ID
        body.userId = userId
        body.transaction_date = getTodayDate()
        return apiInterface.AllocateItems(body)
    }

    suspend fun transferMaterial(
        body:TransferMaterialBody
    ):Response<NoDataResponse> {
        body.applang = lang
        body.deviceSerialNo = deviceSerialNo
        body.employeeId = SignInFragment.EMPLOYEE_ID
        body.userId = userId
        body.userID = userId
        body.transactionDate = getTodayDate()
       return apiInterface.transferMaterial(body)
    }

    suspend fun transactItems(
        body:TransactItemsBody
    ):Response<NoDataResponse> {
        body.appLang = lang
        body.deviceSerialNo = deviceSerialNo
        body.employeeId = SignInFragment.EMPLOYEE_ID
        body.programApplicationId = SignInFragment.PROGRAM_APPLICATION_ID
        body.programId = SignInFragment.PROGRAM_ID
        body.userId = userId
        body.transaction_date = getTodayDate()
        return apiInterface.TransactItems(body)
    }

    suspend fun transactMultiItems(
        body:TransactMultiItemsBody
    ):Response<NoDataResponse> {
        body.applang = lang
        body.deviceSerialNo = deviceSerialNo
        body.employeeId = SignInFragment.EMPLOYEE_ID
        body.programApplicationId = SignInFragment.PROGRAM_APPLICATION_ID
        body.programId = SignInFragment.PROGRAM_ID
        body.userId = userId
        body.transactionDate = getTodayDate()
        return apiInterface.TransactMultiItems(body)
    }


    suspend fun getItemLotInfo(itemCode:String,orgId: Int) =
        apiInterface.getOnHandLot(
            userId = userId!!,
            DeviceSerialNo = deviceSerialNo,
            appLang = lang,
            itemCode = itemCode,
            orgId = orgId
        )
    suspend fun getOnHandLocatorDetails(itemCode:String,locatorCode:String,orgId: Int) =
        apiInterface.getOnHandLocatorDetails(
            userId = userId!!,
            deviceSerialNo = deviceSerialNo,
            appLang = lang,
            itemCode = itemCode,
            orgId = orgId,
            locatorCode = locatorCode
        )
    suspend fun getItemInfo(itemCode:String,orgId: Int) =
        apiInterface.getOnHangItemInfo(
            userId = userId!!,
            DeviceSerialNo = deviceSerialNo,
            appLang = lang,
            itemCode = itemCode,
            orgId = orgId
        )
    suspend fun getItemInfo(itemCode:String,subInvCode: String,orgId: Int) =
        apiInterface.getItemInfo_issue(
            userId = userId!!,
            DeviceSerialNo = deviceSerialNo,
            appLang = lang,
            itemCode = itemCode,
            subInvCode = subInvCode,
            locatorCode = null,
            orgId = orgId
        )

    suspend fun getOnHandQtyGroupedBySubInv(itemCode:String,subInvCode: String,orgId: Int) =
        apiInterface.getOnHandForAllocate_GroupedBySubInv(
            userId = userId!!,
            DeviceSerialNo = deviceSerialNo,
            appLang = lang,
            itemCode = itemCode,
            subInvCode = subInvCode,
            orgId = orgId
        )
}