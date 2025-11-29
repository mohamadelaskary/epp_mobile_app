package net.gbs.epp_project.Model.ApiRequestBody

data class PhysicalInventory_CountBody(
    var DeviceSerialNo: String? = null,
    val ItemCode: String,
    val LocatorCode: String,
    val PhysicalInventoryHeaderId: Int,
    val Qty: Double,
    val SubInventoryCode: String,
    var UserID: String?=null,
    var applang: String?=null,
    val OrgCode:String,
)