package net.gbs.epp_project.Model.ApiRequestBody

data class InspectMaterialBody(
    val DeviceSerialNo: String,
    val applang: String,
    val employee_id: Int,
    val itemqtyaccepted: Double,
    val po_header_id: Int,
    val po_line_id: Int,
    val program_application_id: Int,
    val program_id: Int,
    val receiptno: String,
    val ship_to_organization_id: Int,
    val transaction_date: String,
    val user_id: Int
)