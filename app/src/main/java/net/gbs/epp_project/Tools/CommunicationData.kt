package net.gbs.epp_project.Tools

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences

class CommunicationData(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("communication_prefs", Context.MODE_PRIVATE)

    private val PROTOCOL_TYPE_NAME = "protocol"
    private val IP_ADDRESS_NAME = "Ip_address"
    private val PORT_NUM_NAME = "port_num"

    fun getProtocol(): String {
        return sharedPreferences.getString(PROTOCOL_TYPE_NAME, "http") ?: "http"
    }

    fun getIpAddress(): String {
        return sharedPreferences.getString(IP_ADDRESS_NAME, "41.196.137.5") ?: "41.196.137.5"
    }

    fun getPortNumber(): String {
        return sharedPreferences.getString(PORT_NUM_NAME, "608") ?: "608"
    }

    fun saveProtocol(protocol: String?) {
        sharedPreferences.edit().putString(PROTOCOL_TYPE_NAME, protocol).apply()
    }

    fun saveIPAddress(ipAddress: String?) {
        sharedPreferences.edit().putString(IP_ADDRESS_NAME, ipAddress).apply()
    }

    fun savePortNum(portNum: String?) {
        sharedPreferences.edit().putString(PORT_NUM_NAME, portNum).apply()
    }
}