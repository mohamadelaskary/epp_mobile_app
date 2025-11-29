package net.gbs.epp_project.Model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class TransactMultiLine(
    @SerializedName("line_id"     ) var lineId     : Int?            = null,
    @SerializedName("linE_NUMBER" ) var linENUMBER : Int?            = null,
    @SerializedName("lots"        ) var lots       : List<LotQty>? = listOf(),
    @Transient var froMSUBINVENTORYCODE       : String? = null,
    @Transient var tOSUBINVENTORYCODE         : String? = null,
    @Transient var froMLOCATORCode            : String? = null,
    @Transient var inventorYITEMCODE          : String? = null,
    @Transient var quantity                   : Double? = null,
    @Transient var loTCONTROLCODE             : String? = null,
    ){
    fun mustHaveLot():Boolean = loTCONTROLCODE=="2"
    companion object {
        fun toJson(line:TransactMultiLine):String = Gson().toJson(line)
        fun fromJson(line:String):TransactMultiLine = Gson().fromJson(line,TransactMultiLine::class.java)
    }
}
