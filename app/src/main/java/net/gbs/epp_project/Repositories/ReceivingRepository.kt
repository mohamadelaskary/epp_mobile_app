package net.gbs.epp_project.Repositories

import android.app.Activity
import android.content.ContentValues.TAG
import android.util.Log
import net.gbs.epp_project.Base.BaseRepository
import net.gbs.epp_project.Model.ApiRequestBody.InspectMaterialBody
import net.gbs.epp_project.Model.ApiRequestBody.ItemsReceivingBody
import net.gbs.epp_project.Model.ApiRequestBody.PutawayMaterialBody
import net.gbs.epp_project.Model.PoLine
import net.gbs.epp_project.Model.Response.NoDataResponse
import net.gbs.epp_project.Ui.SplashAndSignIn.SignInFragment.Companion.EMPLOYEE_ID
import retrofit2.Response


class ReceivingRepository(activity: Activity) :
    BaseRepository(activity) {

    private val PROGRAM_ID = 32766
    private val PROGRAM_APPLICATION_ID = 201
    suspend fun getPurchaseOrderByPoNo(poNum:String) = apiInterface.getPurchaseOrderGetByPoNo(userId!!,deviceSerialNo,lang,poNum)

    suspend fun getPoOrganizations(poHeaderId:String) = apiInterface.getPoOrganizations(userId!!,deviceSerialNo,lang,poHeaderId)
    suspend fun getLocatorList(
        orgId:Int,
        subInvCode:String,
        itemId:Int
    ) = apiInterface.getLocatorListByItemId(
        orgId = orgId.toString(),
        subinv_code = subInvCode,
        itemId = itemId
    )
    suspend fun getPurchaseOrdersList(poNum:String,orgNum: String) = apiInterface.getPurchaseOrdersList(userId!!,deviceSerialNo,lang,poNum,orgNum)
        suspend fun getPurchaseOrderDetailsList(orgId:Int,poHeaderId: String) = apiInterface.getPurchaseOrderDetailsList(userId!!,deviceSerialNo,lang, orgId, poHeaderId)

    suspend fun getOrganizationsNextReceiptNo(orgId:Int) = apiInterface.getOrganizationsNextReceiptNo(userId!!,deviceSerialNo,lang,orgId)
        suspend fun ItemReceiving(poHeaderId: Int,
                                  poLines :List<PoLine>,
                                  trasnactionDate: String): Response<NoDataResponse> {
            Log.d(TAG, "ItemReceiving: $userId")
            return apiInterface.ItemReceiving(
                ItemsReceivingBody(
                    employeeId = EMPLOYEE_ID,
                    userId = userId,
                    poHeaderId = poHeaderId,
                    poLines = poLines,
                    programId = PROGRAM_ID,
                    programApplicationId = PROGRAM_APPLICATION_ID,
                    transactionDate = trasnactionDate,
                    deviceSerialNo = deviceSerialNo,
                    applang = lang,
                )
            )
        }

    suspend fun getPurchaseOrderReceiptNoList(poNum:String,receiptNo:String) = apiInterface.getPurchaseOrderReceiptNoList(
        userId!!,deviceSerialNo,lang,poNum, receiptNo)
    suspend fun getPreviousReceiptNoList(orgId:Int,poHeaderId:String) = apiInterface.getPreviousReceiptNos(
        userId = userId!!,
        deviceSerialNo = deviceSerialNo,
        poHeaderId = poHeaderId,
        appLang = lang,
        orgId = orgId,
    )

    suspend fun getItemInfo(itemCode:String) = apiInterface.getItemInfo_receiving(
        userId = userId!!,
        DeviceSerialNo = deviceSerialNo,
        appLang = lang,
        itemCode = itemCode
    )

    suspend fun getPoReceivedInfo(pono:String) = apiInterface.getPoReceivedList(
        userId = userId!!,
        DeviceSerialNo = deviceSerialNo,
        appLang = lang,
        pono = pono
    )


    suspend fun InspectMaterial(poHeaderId: Int,
                              poLineId :Int,
                              acceptedQty :Double,
                              receiptNo: String,
                                shipToOrganizationId:Int,
                            transactionDate: String) =
        apiInterface.InspectMaterial(
            InspectMaterialBody(
                employee_id = EMPLOYEE_ID,
                user_id = userId!!,
                po_header_id = poHeaderId,
                po_line_id = poLineId,
                itemqtyaccepted = acceptedQty,
                receiptno = receiptNo,
                ship_to_organization_id = shipToOrganizationId,
                transaction_date = transactionDate,
                program_application_id = PROGRAM_APPLICATION_ID,
                program_id = PROGRAM_ID,
                DeviceSerialNo = deviceSerialNo,
                applang = lang
            )
        )
    suspend fun PutAwayMaterial(body: PutawayMaterialBody) =
        apiInterface.PutawayMaterial(
            body
        )


}