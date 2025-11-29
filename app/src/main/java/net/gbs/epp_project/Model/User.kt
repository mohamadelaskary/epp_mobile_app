package net.gbs.epp_project.Model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("userId"              ) var notOracleUserId              : String?  = "ergrd",
    @SerializedName("userName"            ) var userName            : String?  = null,
    @SerializedName("password"            ) var password            : String?  = null,
    @SerializedName("discriminator"       ) var discriminator       : String?  = null,
    @SerializedName("isMobileUser"        ) var isMobileUser        : Boolean = true,
    @SerializedName("isPrintingApp"       ) var isPrintingApp       : Boolean = true,
    @SerializedName("isActive"            ) var isActive            : Boolean = true,
    @SerializedName("scanMode"            ) var scanMode            : Int?  = null,
    @SerializedName("employeeName"        ) var employeeName        : String?  = null,
    @SerializedName("isInspection"        ) var isInspection        : Boolean = true,
    @SerializedName("isScanBarcode"       ) var isScanBarcode       : Boolean = true,
    @SerializedName("isManualEntry"       ) var isManualEntry       : Boolean = true,
    @SerializedName("isAllowEditQuantity" ) var isAllowEditQuantity : Boolean = true,
    @SerializedName("isReceive"           ) var isReceive           : Boolean = true,
    @SerializedName("isDeliver"           ) var isDeliver           : Boolean = true,
    @SerializedName("isDeliverRejected"   ) var isDeliverRejected   : Boolean = true,
    @SerializedName("isItemPos"           ) var isItemPos           : Boolean = true,
    @SerializedName("isFactory"           ) var isFactory           : Boolean = true,
    @SerializedName("isIndirectChemical"  ) var isIndirectChemical  : Boolean = true,
    @SerializedName("isSpareParts"        ) var isSpareParts        : Boolean = true,
    @SerializedName("isIssueFinalProduct" ) var isIssueFinalProduct : Boolean = true,
    @SerializedName("isReceiveFinalProduct" ) var isReceiveFinalProduct : Boolean = true,
    @SerializedName("isItemInfoFinalProduct" ) var isItemInfoFinalProduct : Boolean = true,
    @SerializedName("isReturnToVendor"    ) var isReturnToVendor    : Boolean = true,
    @SerializedName("isReturnToWarehouse" ) var isReturnToWarehouse : Boolean = true,
    @SerializedName("isTransfer"          ) var isTransfer          : Boolean = true,
    @SerializedName("isCycleCount"        ) var isCycleCount        : Boolean = true,
    @SerializedName("isPhysicalInventory" ) var isPhysicalInventory : Boolean = true,
    @SerializedName("isItemInfo"          ) var isItemInfo          : Boolean = true,
    @SerializedName("normalizedUserName"  ) var normalizedUserName  : String?  = null,
    @SerializedName("email"               ) var email               : String?  = null,
    @SerializedName("normalizedEmail"     ) var normalizedEmail     : String?  = null,
    @SerializedName("emailConfirmed"      ) var emailConfirmed      : Boolean = true,
    @SerializedName("phoneNumber"         ) var phoneNumber         : String?  = null,
    @SerializedName("oracleUserId"        ) var userId              : Int?     = 0,
    @SerializedName("serverDateTime"      ) var serverDateTime      : String?  = "26-09-2024",
    @SerializedName("isShowErrorMessage"  ) var isShowErrorMessage  : Boolean = true,
    @SerializedName("apkVersion"          ) var apkVersion          : String?  = "",
    @SerializedName("apkUrl"              ) var apkUrl              : String   = "",
    @SerializedName("organizations"       ) var organizations       : ArrayList<UserOrganization> = arrayListOf()
) {
    val manualEnter: Boolean
        get() = scanMode == 1
}

class UserOrganization (
    @SerializedName("orgId"   ) var orgId   : Int?              = null,
    @SerializedName("orgCode" ) var orgCode : String?           = null,
    @SerializedName("orgName" ) var orgName : String?           = null,
    @SerializedName("dateAdd" ) var dateAdd : String?           = null,
    @SerializedName("items"   ) var items   : ArrayList<String> = arrayListOf()
)