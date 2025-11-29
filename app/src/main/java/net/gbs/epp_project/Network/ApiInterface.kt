package net.gbs.epp_project.Network.ApiInterface



import net.gbs.epp_project.Model.ApiRequestBody.AllocateItemsBody
import net.gbs.epp_project.Model.ApiRequestBody.InspectMaterialBody
import net.gbs.epp_project.Model.ApiRequestBody.ItemsReceivingBody
import net.gbs.epp_project.Model.ApiRequestBody.MobileApproveData
import net.gbs.epp_project.Model.ApiRequestBody.MobileLogBody
import net.gbs.epp_project.Model.ApiRequestBody.PhysicalInventory_CountBody
import net.gbs.epp_project.Model.ApiRequestBody.PutawayMaterialBody
import net.gbs.epp_project.Model.ApiRequestBody.ReturnMaterialBody
import net.gbs.epp_project.Model.ApiRequestBody.ReturnToWarehouseItemsBody
import net.gbs.epp_project.Model.ApiRequestBody.SaveCheckInData
import net.gbs.epp_project.Model.ApiRequestBody.SaveNewVehicleRecordData
import net.gbs.epp_project.Model.ApiRequestBody.SignInBody
import net.gbs.epp_project.Model.ApiRequestBody.TransactItemsBody
import net.gbs.epp_project.Model.ApiRequestBody.TransactMultiItemsBody
import net.gbs.epp_project.Model.ApiRequestBody.TransferMaterialBody
import net.gbs.epp_project.Model.ApiResponse.CreateNewCycleCountOrderResponse
import net.gbs.epp_project.Model.ApiResponse.CycleCountOrder_StockCompareResponse
import net.gbs.epp_project.Model.ApiResponse.DeliverLotListResponse
import net.gbs.epp_project.Model.ApiResponse.EppGbsSalesAgreementDGetListResponse
import net.gbs.epp_project.Model.ApiResponse.EppGbsSalesAgreementHGetListResponse
import net.gbs.epp_project.Model.ApiResponse.FeBookingsGetListResponse
import net.gbs.epp_project.Model.ApiResponse.GetDateResponse
import net.gbs.epp_project.Model.ApiResponse.GovernoratesGetListResponse
import net.gbs.epp_project.Model.ApiResponse.LocatorListResponse
import net.gbs.epp_project.Model.ApiResponse.LotListResponse
import net.gbs.epp_project.Model.ApiResponse.MoveOrderGetByHeaderIdResponse
import net.gbs.epp_project.Model.ApiResponse.MoveOrderGetDetailsByHEADER_IDResponse
import net.gbs.epp_project.Model.ApiResponse.MoveOrderLinesGetByHeaderIdResponse
import net.gbs.epp_project.Model.ApiResponse.MoveOrdersListResponse
import net.gbs.epp_project.Model.ApiResponse.OnHandForAllocateResponse
import net.gbs.epp_project.Model.ApiResponse.OnHandListResponse
import net.gbs.epp_project.Model.ApiResponse.OnHandLocatorDetailsResponse
import net.gbs.epp_project.Model.ApiResponse.OnHandLotResponse
import net.gbs.epp_project.Model.ApiResponse.OrganizationAuditListResponse
import net.gbs.epp_project.Model.ApiResponse.OrganizationListResponse
import net.gbs.epp_project.Model.ApiResponse.OrganizationsNextReceiptNoResponse
import net.gbs.epp_project.Model.ApiResponse.PhysicalInventory_CountResponse
import net.gbs.epp_project.Model.ApiResponse.PurchaseOrderGetByIDResponse
import net.gbs.epp_project.Model.ApiResponse.PurchaseOrderGetByPoNoResponse
import net.gbs.epp_project.Model.ApiResponse.PurchaseOrderGetOrganizationsResponse
import net.gbs.epp_project.Model.ApiResponse.PurchaseOrderItemList_ReturnResponse
import net.gbs.epp_project.Model.ApiResponse.PurchaseOrderLinesGetByIDResponse
import net.gbs.epp_project.Model.ApiResponse.PurchaseOrderReceiptNoListResponse
import net.gbs.epp_project.Model.ApiResponse.ReceiptNoListResponse
import net.gbs.epp_project.Model.ApiResponse.ReturnWorkOrderLinesGetByHEADER_IDResponse
import net.gbs.epp_project.Model.ApiResponse.SaveCycleCountOrderDetailsResponse
import net.gbs.epp_project.Model.ApiResponse.SubInvListResponse
import net.gbs.epp_project.Model.ApiResponse.ViewArrivalRegistrationVehicleResponse
import net.gbs.epp_project.Model.ApiResponse.ViewArrivalVehicleResponse
import net.gbs.epp_project.Model.ApiResponse.ViewAwaitingCheckinVehicleResponse
import net.gbs.epp_project.Model.ApiResponse.WorkOrderList_ReturnMaterialToInventoryResponse
import net.gbs.epp_project.Model.ApiResponse.WorkOrdersListResponse
import net.gbs.epp_project.Model.Response.GetItemListResponse
import net.gbs.epp_project.Model.Response.GetLocatorListResponse
import net.gbs.epp_project.Model.Response.GetPhysicalInventoryOrderCounting_TransactionsResponse
import net.gbs.epp_project.Model.Response.GetPhysicalInventoryOrderListResponse
import net.gbs.epp_project.Model.Response.NoDataResponse
import net.gbs.epp_project.Model.Response.SignInResponse
import net.gbs.epp_project.Network.ApiFactory.ApiFactory
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


interface ApiInterface {
    companion object {

//        var BASE_URL: String = "https://api.example.com/" // Default Base URL
        fun getRetrofitInstance (baseUrl:String) = ApiFactory.getInstance(baseUrl)
    }
    @POST("SignIn")
    suspend fun signIn(@Body body: SignInBody):Response<SignInResponse>

    @GET("GetPhysicalInventoryOrderList")
    suspend fun getOrdersList(
        @Query("UserID") UserID: String,
        @Query("DeviceSerialNo") DeviceSerialNo: String,
        @Query("applang") appLang: String,
    ):Response<GetPhysicalInventoryOrderListResponse>
    @GET("GetLocatorList")
    suspend fun getLocatorData(
        @Query("DeviceSerialNo") DeviceSerialNo: String,
        @Query("applang") appLang: String,
        @Query("locatorCode") locatorCode: String,

    ):Response<GetLocatorListResponse>
    @GET("GetItemList")
    suspend fun getItemData(
        @Query("DeviceSerialNo") DeviceSerialNo: String,
        @Query("applang") appLang: String,
        @Query("itemCode") itemCode: String,
        @Query("OrgCode") OrgCode: String,
    ):Response<GetItemListResponse>

//    @GET("GetLocatorList")
//    suspend fun getAllLocators(
//        @Query("UserID") UserID: Int,
//        @Query("DeviceSerialNo") DeviceSerialNo: String,
//        @Query("applang") appLang: String,
//        @Query("OrgCode") orgCode: String,
//        ):Response<GetLocatorListResponse>
    @GET("GetLocatorList")
    suspend fun getLocatorData(
        @Query("DeviceSerialNo") DeviceSerialNo: String,
        @Query("applang") appLang: String,
        @Query("OrgCode") orgCode: String,
        @Query("LocatorCode") locatorCode: String,
    ):Response<GetLocatorListResponse>
    @GET("GetItemList")
    suspend fun getAllItems(
        @Query("DeviceSerialNo") DeviceSerialNo: String,
        @Query("applang") appLang: String,
        @Query("OrgCode") OrgCode: String,
    ):Response<GetItemListResponse>
    @GET("OrganizationsList")
    suspend fun getOrganizationsList(
        @Query("UserID") UserID: Int,
        @Query("DeviceSerialNo") DeviceSerialNo: String,
        @Query("applang") appLang: String,
    ):Response<OrganizationListResponse>
    @GET("GetOrgList")
    suspend fun getOrganizationsList2(
        @Query("DeviceSerialNo") DeviceSerialNo: String,
        @Query("applang") appLang: String,
    ):Response<OrganizationAuditListResponse>
    @GET("GetPhysicalInventoryOrderCounting_Transactions")
    suspend fun GetPhysicalInventoryOrderCounting_Transactions(
        @Query("UserID") UserID: String,
        @Query("DeviceSerialNo") DeviceSerialNo: String,
        @Query("applang") appLang: String,
        @Query("PhysicalInventoryHeaderId") physicalInventoryHeaderId: Int,
    ):Response<GetPhysicalInventoryOrderCounting_TransactionsResponse>
    @POST("PhysicalInventory_Count")
    suspend fun PhysicalInventory_Count(@Body body: PhysicalInventory_CountBody):Response<PhysicalInventory_CountResponse>

    @POST("MobileLog")
    suspend fun MobileLog(@Body body: MobileLogBody):Response<NoDataResponse>
    @GET("PhysicalInventoryOrder_FinishTracking")
    suspend fun finishTracking(
        @Query("UserID") UserID: String,
        @Query("DeviceSerialNo") DeviceSerialNo: String,
        @Query("applang") appLang: String,
        @Query("PhysicalInventoryHeaderId") physicalInventoryHeaderId: Int,
        @Query("SubInventoryCode") SubInventoryCode: String,
    ):Response<NoDataResponse>

    @GET("PurchaseOrderGetByID")
    suspend fun getPurchaseOrdersList(
        @Query("UserID")  userId : Int,
        @Query("DeviceSerialNo")  DeviceSerialNo : String,
        @Query("applang")  appLang : String,
        @Query("pono")  poNo : String,
        @Query("org_id")  orgId : String,
    ) : Response<PurchaseOrderGetByIDResponse>
    @GET("PurchaseOrderGetOrganizations")
    suspend fun getPoOrganizations(
        @Query("UserID")  userId : Int,
        @Query("DeviceSerialNo")  DeviceSerialNo : String,
        @Query("applang")  appLang : String,
        @Query("po_header_id")  poHeaderId : String,
    ) : Response<PurchaseOrderGetOrganizationsResponse>
    @GET("OrganizationsNextReceiptNo")
    suspend fun getOrganizationsNextReceiptNo(
        @Query("UserID")  userId : Int,
        @Query("DeviceSerialNo")  DeviceSerialNo : String,
        @Query("applang")  appLang : String,
        @Query("org_id")  orgId : Int,
    ) : Response<OrganizationsNextReceiptNoResponse>

    @GET("PurchaseOrderGetByPoNo")
    suspend fun getPurchaseOrderGetByPoNo(
        @Query("UserID")  userId : Int,
        @Query("DeviceSerialNo")  DeviceSerialNo : String,
        @Query("applang")  appLang : String,
        @Query("pono")  poNo : String,
    ) : Response<PurchaseOrderGetByPoNoResponse>


    @GET("WorkOrderList_ReturnMaterialToInventory")
    suspend fun getWorkOrderList_ReturnMaterialToInventory(
        @Query("UserID")  userId : Int,
        @Query("DeviceSerialNo")  DeviceSerialNo : String,
        @Query("applang")  appLang : String,
        @Query("org_id")  org_id : Int,
        @Query("Top")  top: Int,
    ) : Response<WorkOrderList_ReturnMaterialToInventoryResponse>
    @GET("PurchaseOrderLinesGetByID")
    suspend fun getPurchaseOrderDetailsList(
        @Query("UserID")  userId : Int,
        @Query("DeviceSerialNo")  DeviceSerialNo : String,
        @Query("applang")  appLang : String,
        @Query("ship_to_organization_id")  orgId : Int,
        @Query("po_header_id")  poHeaderId : String,
    ) : Response<PurchaseOrderLinesGetByIDResponse>
    @GET("ReturnWorkOrderLinesGetByHEADER_ID")
    suspend fun getReturnWorkOrderLinesGetByHEADER_ID(
        @Query("UserID")  userId : Int,
        @Query("DeviceSerialNo")  DeviceSerialNo : String,
        @Query("applang")  appLang : String,
        @Query("org_id")  orgId : Int,
        @Query("HEADER_ID")  headerId : Int,
    ) : Response<ReturnWorkOrderLinesGetByHEADER_IDResponse>

    @GET("ReturnLinesGetByWorkOrderName")
    suspend fun getReturnLinesGetByWorkOrderName(
        @Query("UserID")  userId : Int,
        @Query("DeviceSerialNo")  DeviceSerialNo : String,
        @Query("applang")  appLang : String,
        @Query("org_id")  orgId : Int,
        @Query("Work_Order_Name")  Work_Order_Name : String,
    ) : Response<ReturnWorkOrderLinesGetByHEADER_IDResponse>
    @POST("ReceiveMaterial_Multi")
    suspend fun ItemReceiving(
        @Body  body : ItemsReceivingBody
    ) : Response<NoDataResponse>

    @GET("PurchaseOrderReceiptNoList")
    suspend fun getPurchaseOrderReceiptNoList(
        @Query("UserID")  userId : Int,
        @Query("DeviceSerialNo")  deviceSerialNo : String,
        @Query("applang")  appLang : String,
        @Query("pono")  poNo : String,
        @Query("receiptno")  receiptNo : String,
    ) : Response<PurchaseOrderReceiptNoListResponse>
    @GET("PurchaseOrderReceiptNoList_Received")
    suspend fun getPreviousReceiptNos(
        @Query("UserID")  userId : Int,
        @Query("DeviceSerialNo")  deviceSerialNo : String,
        @Query("applang")  appLang : String,
        @Query("ship_to_org_id")  orgId : Int,
        @Query("po_header_id")  poHeaderId : String,
    ) : Response<ReceiptNoListResponse>
    @GET("GetDate")
    suspend fun getDate() : Response<GetDateResponse>
    @GET("SubInvList")
    suspend fun getSubInvList(
        @Query("org_id")  orgId : String,
    ) : Response<SubInvListResponse>

    @GET("LocatorList")
    suspend fun getLocatorList(
        @Query("org_id")  orgId : String,
        @Query("subinv_code")  subinv_code : String,
    ) : Response<LocatorListResponse>

    @GET("LocatorList")
    suspend fun getLocatorListByItemId(
        @Query("org_id")  orgId : String,
        @Query("subinv_code")  subinv_code : String,
        @Query("INVENTORY_ITEM_ID")  itemId : Int,
    ) : Response<LocatorListResponse>


    @POST("InspectMaterial")
    suspend fun InspectMaterial(
        @Body  body : InspectMaterialBody
    ) : Response<NoDataResponse>

    @POST("PutawayMaterial")
    suspend fun PutawayMaterial(
        @Body  body : PutawayMaterialBody
    ) : Response<NoDataResponse>

    @GET("LotList")
    suspend fun getLotList(
        @Query("UserID")  userId : Int,
        @Query("DeviceSerialNo")  deviceSerialNo : String,
        @Query("applang")  appLang : String,
        @Query("org_id")  orgId : String,
        @Query("INVENTORY_ITEM_ID")  itemId : Int?,
        @Query("SUBINVENTORY_CODE")  SUBINVENTORY_CODE : String?,
        @Query("locator_code")  locatorCode : String?,
    ) : Response<LotListResponse>
    @GET("LotList")
    suspend fun getDeliverLotList(
        @Query("UserID")  userId : Int,
        @Query("DeviceSerialNo")  deviceSerialNo : String,
        @Query("applang")  appLang : String,
        @Query("org_id")  orgId : String,
        @Query("INVENTORY_ITEM_ID")  itemId : Int?,
        @Query("SUBINVENTORY_CODE")  SUBINVENTORY_CODE : String?,
    ) : Response<DeliverLotListResponse>

    @GET("CreateNewCycleCountOrder")
    suspend fun createNewCycleCountOrderByLocator(
        @Query("UserID")  userId : String,
        @Query("DeviceSerialNo")  deviceSerialNo : String,
        @Query("applang")  appLang : String,
        @Query("LocatorId")  locatorId : Int,
        @Query("OrgCode")  organizationCode : String,
    ) : Response<CreateNewCycleCountOrderResponse>
    @GET("CreateNewCycleCountOrder")
    suspend fun createNewCycleCountOrderByItem(
        @Query("UserID")  userId : String,
        @Query("DeviceSerialNo")  deviceSerialNo : String,
        @Query("applang")  appLang : String,
        @Query("item_code")  itemCode : String,
        @Query("OrgCode")  organizationCode : String,
    ) : Response<CreateNewCycleCountOrderResponse>
    @GET("SaveCycleCountOrderDetails")
    suspend fun saveCycleCountOrderDetails(
        @Query("UserID")  userId : String,
        @Query("DeviceSerialNo")  deviceSerialNo : String,
        @Query("applang")  appLang : String,
        @Query("ItemCode")  itemCode : String,
        @Query("CycleCountHeaderId")  cycleCountHeaderId : Int,
        @Query("LocatorCode")  locatorCode : String,
        @Query("Qty")  qty : Double,
        @Query("OrgCode")  organizationCode : String,
    ) : Response<SaveCycleCountOrderDetailsResponse>

    @GET("CycleCountOrder_Finish")
    suspend fun finishCycleCountOrder(
        @Query("UserID")  userId : String,
        @Query("DeviceSerialNo")  deviceSerialNo : String,
        @Query("applang")  appLang : String,
        @Query("CycleCountHeaderId")  physicalInventoryHeaderId : Int,
    ) : Response<NoDataResponse>

    @GET("MoveOrdersList_Factory")
    suspend fun getMoveOrdersList_Factory(
        @Query("UserID")  userId : Int,
        @Query("DeviceSerialNo")  deviceSerialNo : String,
        @Query("applang")  appLang : String,
        @Query("org_id")  orgId : Int,
    ) : Response<MoveOrdersListResponse>
    @GET("MoveOrdersList_FinishProduct")
    suspend fun getMoveOrdersList_FinishProduct(
        @Query("UserID")  userId : Int,
        @Query("DeviceSerialNo")  deviceSerialNo : String,
        @Query("applang")  appLang : String,
        @Query("org_id")  orgId : Int,
    ) : Response<MoveOrdersListResponse>
    @GET("MoveOrdersList_SpareParts")
    suspend fun getWorkOrdersList(
        @Query("UserID")  userId : Int,
        @Query("DeviceSerialNo")  deviceSerialNo : String,
        @Query("applang")  appLang : String,
        @Query("org_id")  orgId : Int,
    ) : Response<WorkOrdersListResponse>

    @GET("MoveOrdersList_IndirectChemical")
    suspend fun getJobOrdersList(
        @Query("UserID")  userId : Int,
        @Query("DeviceSerialNo")  deviceSerialNo : String,
        @Query("applang")  appLang : String,
        @Query("org_id")  orgId : Int,
    ) : Response<WorkOrdersListResponse>

    @GET("OnHandList")
    suspend fun getOnHands(
        @Query("UserID")  userId : Int,
        @Query("DeviceSerialNo")  deviceSerialNo : String,
        @Query("applang")  appLang : String,
        @Query("item_code")  itemCode : String,
        @Query("org_id")  orgId : Int,
    ) : Response<OnHandListResponse>

    @GET("GetCycleCountOrder_StockCompare")
    suspend fun getCycleCountOrder_StockCompare(
        @Query("UserID")  userId :String,
        @Query("DeviceSerialNo")  deviceSerialNo : String,
        @Query("applang")  appLang : String,
        @Query("OrgCode")  orgCode : String,
        @Query("CycleCountHeaderId")  headerID : Int,
    ) : Response<CycleCountOrder_StockCompareResponse>
    @GET("OnHandList")
    suspend fun getOnHands(
        @Query("UserID")  userId : Int,
        @Query("DeviceSerialNo")  deviceSerialNo : String,
        @Query("applang")  appLang : String,
        @Query("locator_id")  locatorId : Int,
    ) : Response<OnHandListResponse>
    @GET("MoveOrderGetByHEADER_ID")
    suspend fun getMoveOrderByHeaderId(
        @Query("UserID")  userId : Int,
        @Query("DeviceSerialNo")  deviceSerialNo : String,
        @Query("applang")  appLang : String,
        @Query("HEADER_ID")  headerId : Int?,
        @Query("REQUEST_NUMBER")  moveOrderNumber : Int,
        @Query("org_id")  orgId : Int,
    ) : Response<MoveOrderGetByHeaderIdResponse>
    @GET("MoveOrderLinesGetByHEADER_ID")
    suspend fun getMoveOrderLinesByHeaderId(
        @Query("UserID")  userId : Int,
        @Query("DeviceSerialNo")  deviceSerialNo : String,
        @Query("applang")  appLang : String,
        @Query("HEADER_ID")  headerId : Int?,
        @Query("org_id")  orgId : Int,
    ) : Response<MoveOrderLinesGetByHeaderIdResponse>

    @GET("MoveOrderLinesGetByWorkOrderName")
    suspend fun getMoveOrderLinesGetByWorkOrderName(
        @Query("UserID")  userId : Int,
        @Query("DeviceSerialNo")  deviceSerialNo : String,
        @Query("applang")  appLang : String,
        @Query("Work_Order_Name")  Work_Order_Name : String?,
        @Query("org_id")  orgId : Int,
    ) : Response<MoveOrderLinesGetByHeaderIdResponse>

    @POST("AllocateItems")
    suspend fun AllocateItems(
        @Body  body : AllocateItemsBody
    ) : Response<NoDataResponse>

    @POST("TransferMaterial")
    suspend fun transferMaterial(
        @Body  body : TransferMaterialBody
    ) : Response<NoDataResponse>
    @POST("TransactItems")
    suspend fun TransactItems(
        @Body  body : TransactItemsBody
    ) : Response<NoDataResponse>
    @POST("TransactMultiItems")
    suspend fun TransactMultiItems(
        @Body  body : TransactMultiItemsBody
    ) : Response<NoDataResponse>
    @GET("PurchaseOrderItemList_Return")
    suspend fun getPurchaseOrderItemListReturn(
        @Query("UserID")  userId : Int,
        @Query("DeviceSerialNo")  DeviceSerialNo : String,
        @Query("applang")  appLang : String,
        @Query("pono")  poNo : String,
    ) : Response<PurchaseOrderItemList_ReturnResponse>
    @POST("ReturnMaterial_Multi")
    suspend fun ReturnMaterial(
        @Body  body : ReturnMaterialBody
    ) : Response<NoDataResponse>
    @POST("ReturnItems")
    suspend fun ReturnItems(
        @Body  body : ReturnToWarehouseItemsBody
    ) : Response<NoDataResponse>

    @GET("PurchaseOrderReceiptNoList")
    suspend fun getItemInfo_receiving(
        @Query("UserID")  userId : Int,
        @Query("DeviceSerialNo")  DeviceSerialNo : String,
        @Query("applang")  appLang : String,
        @Query("itemcode")  itemCode : String,
    ) : Response<PurchaseOrderReceiptNoListResponse>
    @GET("OnHand_Lot")
    suspend fun getOnHandLot(
        @Query("UserID")  userId : Int,
        @Query("DeviceSerialNo")  DeviceSerialNo : String,
        @Query("applang")  appLang : String,
        @Query("item_code")  itemCode : String,
        @Query("org_id")  orgId : Int,
    ) : Response<OnHandLotResponse>
    @GET("PurchaseOrderReceiptNoList")
    suspend fun getPoReceivedList(
        @Query("UserID")  userId : Int,
        @Query("DeviceSerialNo")  DeviceSerialNo : String,
        @Query("applang")  appLang : String,
        @Query("pono")  pono : String,
    ) : Response<PurchaseOrderReceiptNoListResponse>

    @GET("MoveOrderGetDetailsByHEADER_ID")
    suspend fun getMoveOrderGetDetailsByHEADER_ID(
        @Query("UserID")  userId : Int,
        @Query("DeviceSerialNo")  DeviceSerialNo : String,
        @Query("applang")  appLang : String,
        @Query("REQUEST_NUMBER")  requestNumber : String,
        @Query("org_id")  orgId : Int,
    ) : Response<MoveOrderGetDetailsByHEADER_IDResponse>


    @GET("OnHandForAllocate")
    suspend fun getOnHangItemInfo(
        @Query("UserID")  userId : Int,
        @Query("DeviceSerialNo")  DeviceSerialNo : String,
        @Query("applang")  appLang : String,
        @Query("item_code")  itemCode : String,
        @Query("org_id")  orgId : Int,
    ) : Response<OnHandForAllocateResponse>
    @GET("OnHandForAllocate")
    suspend fun getItemInfo_issue(
        @Query("UserID")  userId : Int,
        @Query("DeviceSerialNo")  DeviceSerialNo : String,
        @Query("applang")  appLang : String,
        @Query("item_code")  itemCode : String,
        @Query("subinv_code")  subInvCode: String,
        @Query("locator_code")  locatorCode : String?,
        @Query("org_id")  orgId : Int,
    ) : Response<OnHandForAllocateResponse>

    @GET("OnHandForAllocate_GroupedBySubInv")
    suspend fun getOnHandForAllocate_GroupedBySubInv(
        @Query("UserID")  userId : Int,
        @Query("DeviceSerialNo")  DeviceSerialNo : String,
        @Query("applang")  appLang : String,
        @Query("item_code")  itemCode : String,
        @Query("subinv_code")  subInvCode: String,
        @Query("org_id")  orgId : Int,
    ) : Response<OnHandForAllocateResponse>

    @GET("ViewArrivalVehicle")
    suspend fun viewArrivalVehicle(
        @Query("UserID")  userId : Int,
        @Query("DeviceSerialNo")  deviceSerialNo : String,
        @Query("applang")  appLang : String,
    ) : Response<ViewArrivalVehicleResponse>

    @GET("ViewArrivalRegistrationVehicle")
    suspend fun viewArrivalRegistrationVehicles(
        @Query("UserID")  userId : Int,
        @Query("DeviceSerialNo")  deviceSerialNo : String,
        @Query("applang")  appLang : String,
    ) : Response<ViewArrivalRegistrationVehicleResponse>

    @POST("MobileApprove")
    suspend fun mobileApprove(
        @Body approveData : MobileApproveData,
    ) : Response<NoDataResponse>

    @GET("ViewAwaitingCheckinVehicle")
    suspend fun viewAwaitingCheckinVehicle(
        @Query("UserID")  userId : Int,
        @Query("DeviceSerialNo")  deviceSerialNo : String,
        @Query("applang")  appLang : String,
    ) : Response<ViewAwaitingCheckinVehicleResponse>

    @POST("SaveCheckIn")
    suspend fun saveCheckIn(
        @Body approveData : SaveCheckInData,
    ) : Response<NoDataResponse>

    @GET("GovernoratesGetList")
    suspend fun getGovernoratesList(
        @Query("UserID")  userId : Int,
        @Query("DeviceSerialNo")  deviceSerialNo : String,
        @Query("applang")  appLang : String,
    ) : Response<GovernoratesGetListResponse>
    @GET("FeBookingsGetList")
    suspend fun getFeBookingsList(
        @Query("UserID")  userId : Int,
        @Query("DeviceSerialNo")  deviceSerialNo : String,
        @Query("applang")  appLang : String,
    ) : Response<FeBookingsGetListResponse>
    @GET("EppGbsSalesAgreementHGetList")
    suspend fun getEppGbsSalesAgreementHList(
        @Query("UserID")  userId : Int,
        @Query("DeviceSerialNo")  deviceSerialNo : String,
        @Query("applang")  appLang : String,
    ) : Response<EppGbsSalesAgreementHGetListResponse>
    @GET("EppGbsSalesAgreementDGetList")
    suspend fun getEppGbsSalesAgreementDList(
        @Query("UserID")  userId : Int,
        @Query("DeviceSerialNo")  deviceSerialNo : String,
        @Query("applang")  appLang : String,
        @Query("SalesAgrHeaderId")  salesAgrHeaderId: Int,
    ) : Response<EppGbsSalesAgreementDGetListResponse>

    @POST("SaveNewVehicleRecord")
    suspend fun saveNewVehicleRecord(
        @Body saveNewVehicleRecordData : SaveNewVehicleRecordData,
    ) : Response<NoDataResponse>

    @GET("OnHandLocatorDetails")
    suspend fun getOnHandLocatorDetails(
        @Query("UserID")  userId : Int,
        @Query("DeviceSerialNo")  deviceSerialNo : String,
        @Query("applang")  appLang : String,
        @Query("org_id")  orgId : Int,
        @Query("item_code")  itemCode: String,
        @Query("locator_code")  locatorCode: String,
    ) : Response<OnHandLocatorDetailsResponse>
}