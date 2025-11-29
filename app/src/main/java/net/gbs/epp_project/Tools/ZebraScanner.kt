package net.gbs.epp_project.Tools

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import net.gbs.epp_project.R
import net.gbs.epp_project.Tools.ScannerStrings.ACTION_DATAWEDGE
import net.gbs.epp_project.Tools.ScannerStrings.ACTION_RESULT
import net.gbs.epp_project.Tools.ScannerStrings.ACTION_RESULT_NOTIFICATION
import net.gbs.epp_project.Tools.ScannerStrings.EXTRA_CREATE_PROFILE
import net.gbs.epp_project.Tools.ScannerStrings.EXTRA_EMPTY
import net.gbs.epp_project.Tools.ScannerStrings.EXTRA_GET_VERSION_INFO
import net.gbs.epp_project.Tools.ScannerStrings.EXTRA_KEY_APPLICATION_NAME
import net.gbs.epp_project.Tools.ScannerStrings.EXTRA_KEY_NOTIFICATION_TYPE
import net.gbs.epp_project.Tools.ScannerStrings.EXTRA_KEY_VALUE_SCANNER_STATUS
import net.gbs.epp_project.Tools.ScannerStrings.EXTRA_PROFILENAME
import net.gbs.epp_project.Tools.ScannerStrings.EXTRA_REGISTER_NOTIFICATION
import net.gbs.epp_project.Tools.ScannerStrings.EXTRA_SEND_RESULT
import net.gbs.epp_project.Tools.ScannerStrings.EXTRA_SET_CONFIG
import net.gbs.epp_project.Tools.ScannerStrings.EXTRA_UNREGISTER_NOTIFICATION


class ZebraScanner(private var activity: Activity,  private var onDataScanned: OnDataScanned) {
    init {
        try {
        CreateProfile()
        // Register for status change notification
        // Use REGISTER_FOR_NOTIFICATION: http://techdocs.zebra.com/datawedge/latest/guide/api/registerfornotification/
        val b = Bundle()
        b.putString(EXTRA_KEY_APPLICATION_NAME, activity.packageName)
        b.putString(
            EXTRA_KEY_NOTIFICATION_TYPE,
            "SCANNER_STATUS"
        ) // register for changes in scanner status

        sendDataWedgeIntentWithExtra(
            ACTION_DATAWEDGE,
            EXTRA_REGISTER_NOTIFICATION,
            b
        )

        registerReceivers()

        // Get DataWedge version
        // Use GET_VERSION_INFO: http://techdocs.zebra.com/datawedge/latest/guide/api/getversioninfo/

        // Get DataWedge version
        // Use GET_VERSION_INFO: http://techdocs.zebra.com/datawedge/latest/guide/api/getversioninfo/
        sendDataWedgeIntentWithExtra(
            ACTION_DATAWEDGE,
            EXTRA_GET_VERSION_INFO,
            EXTRA_EMPTY
        )
        } catch (ex:Exception){
            Log.d(TAG, "ZebraScannerInit: ${ex.message}")
        }
    }

    private fun sendDataWedgeIntentWithExtra(action: String, extraKey: String, extras: Bundle) {
        try{
            val dwIntent = Intent()
            dwIntent.setAction(action)
            dwIntent.putExtra(extraKey, extras)
            dwIntent.putExtra(EXTRA_SEND_RESULT, "true")
            activity.sendBroadcast(dwIntent)
        } catch (ex:Exception){
            Log.d(TAG, "ZebraScannerSendDataWedgeIntentWithExtra: ${ex.message}")
        }
    }

    private fun sendDataWedgeIntentWithExtra(action: String, extraKey: String, extraValue: String) {
        try {
            val dwIntent = Intent()
            dwIntent.setAction(action)
            dwIntent.putExtra(extraKey, extraValue)
            dwIntent.putExtra(EXTRA_SEND_RESULT, "true")
            activity.sendBroadcast(dwIntent)
        } catch (ex:Exception){
            Log.d(TAG, "ZebraScannerSendDataWedgeIntentWithExtra: ${ex.message}")
        }
    }

    private fun registerReceivers() {
        try{
            val filter = IntentFilter()
            filter.addAction(ACTION_RESULT_NOTIFICATION) // for notification result
            filter.addAction(ACTION_RESULT) // for error code result
            filter.addCategory(Intent.CATEGORY_DEFAULT) // needed to get version info

            // register to received broadcasts via DataWedge scanning
            filter.addAction(activity.resources.getString(R.string.activity_intent_filter_action))
            filter.addAction(activity.resources.getString(R.string.activity_action_from_service))
            activity.registerReceiver(myBroadcastReceiver, filter)
        } catch (ex:Exception){
            Log.d(TAG, "ZebraScannerRegisterReceivers: ${ex.message}")
        }
    }
    private val myBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            val b = intent.extras


            if (action == activity.resources.getString(R.string.activity_intent_filter_action)) {
                //  Received a barcode scan
                try {
                    sendScanResult(intent, "via Broadcast")
                } catch (e: Exception) {
                    //  Catch error if the UI does not exist when we receive the broadcast...
                    Log.d(TAG, "onReceive: ")
                }
            }
        }
    }
    private fun sendScanResult(initiatingIntent: Intent, howDataReceived: String) {
        // store decoded data
        try{
            val decodedData =
                initiatingIntent.getStringExtra(activity.resources.getString(R.string.datawedge_intent_key_data))
            // store decoder type
            Log.d(TAG, "sendScanResult: $decodedData")
           onDataScanned.onDataScanned(decodedData!!)
        } catch (ex:Exception){
            Log.d(TAG, "ZebraScannerSendScanResult: ${ex.message}")
        }
    }

    fun onResume() {

        registerReceivers()
    }
    fun CreateProfile() {
       try {
           val profileName: String = EXTRA_PROFILENAME

           // Send DataWedge intent with extra to create profile
           // Use CREATE_PROFILE: http://techdocs.zebra.com/datawedge/latest/guide/api/createprofile/
           sendDataWedgeIntentWithExtra(
               ACTION_DATAWEDGE,
               EXTRA_CREATE_PROFILE,
               profileName
           )

//            val profileBundle = Bundle()
//                profileBundle.putString("PROFILE_NAME",profileName)
//                profileBundle.putString("C")







           // Configure created profile to apply to this app
           val profileConfig = Bundle()
           profileConfig.putString("PROFILE_NAME", EXTRA_PROFILENAME)
           profileConfig.putString("PROFILE_ENABLED", "true")
           profileConfig.putString(
               "CONFIG_MODE",
               "CREATE_IF_NOT_EXIST"
           ) // Create profile if it does not exist

           val appConfig = Bundle()
           appConfig.putString("PACKAGE_NAME", activity.packageName)
           appConfig.putStringArray("ACTIVITY_LIST", arrayOf("*"))
           profileConfig.putParcelableArray("APP_LIST", arrayOf(appConfig))

           sendDataWedgeIntentWithExtra(
               ACTION_DATAWEDGE,
               EXTRA_SET_CONFIG,
               profileConfig
           )

           // Configure barcode input plugin
           val barcodeConfig = Bundle()
           barcodeConfig.putString("PLUGIN_NAME", "BARCODE")
           barcodeConfig.putString("RESET_CONFIG", "true") //  This is the default
           val barcodeProps = Bundle()
           barcodeConfig.putBundle("PARAM_LIST", barcodeProps)
           profileConfig.putBundle("PLUGIN_CONFIG", barcodeConfig)

           // Associate profile with this app
           appConfig.putString("PACKAGE_NAME", activity.packageName)
           appConfig.putStringArray("ACTIVITY_LIST", arrayOf("*"))
           profileConfig.putParcelableArray("APP_LIST", arrayOf(appConfig))
//           profileConfig.remove("PLUGIN_CONFIG")

           // Apply configs
           // Use SET_CONFIG: http://techdocs.zebra.com/datawedge/latest/guide/api/setconfig/
           sendDataWedgeIntentWithExtra(
               ACTION_DATAWEDGE,
               EXTRA_SET_CONFIG,
               profileConfig
           )

           // Configure intent output for captured data to be sent to this app
           val intentConfig = Bundle()
               intentConfig.putString("PLUGIN_NAME", "INTENT")
               intentConfig.putString("RESET_CONFIG", "true")
           val intentProps = Bundle()
               intentProps.putString("intent_output_enabled", "true")
               intentProps.putString("intent_action", "com.zebra.datacapture1.ACTION")
               intentProps.putString("intent_delivery", "2")
               intentConfig.putBundle("PARAM_LIST", intentProps)
               profileConfig.putBundle("PLUGIN_CONFIG", intentConfig)
               sendDataWedgeIntentWithExtra(
               ACTION_DATAWEDGE,
               EXTRA_SET_CONFIG,
               profileConfig
           )
           setKeystrokeOutputPluginConfiguration(profileName)
       }catch (ex:Exception){
           Log.d(TAG, "ZebraScannerCreateProfile: ${ex.message}")
       }
    }

    fun setKeystrokeOutputPluginConfiguration(profileName:String) {
        val configBundle = Bundle()
        configBundle.putString("PROFILE_NAME", profileName)
        configBundle.putString("PROFILE_ENABLED", "true")
        configBundle.putString("CONFIG_MODE", "CREATE_IF_NOT_EXIST")
        val bConfig = Bundle()
        bConfig.putString("PLUGIN_NAME", "KEYSTROKE")
        val bParams = Bundle()
        bParams.putString("keystroke_output_enabled", "false")
        bParams.putString("keystroke_action_char", "9") // 0, 9 , 10, 13
        bParams.putString("keystroke_delay_extended_ascii", "500")
        bParams.putString("keystroke_delay_control_chars", "800")
        bConfig.putBundle("PARAM_LIST", bParams)
        configBundle.putBundle("PLUGIN_CONFIG", bConfig)
        val i = Intent()
        i.setAction("com.symbol.datawedge.api.ACTION")
        i.putExtra("com.symbol.datawedge.api.SET_CONFIG", configBundle)
        i.putExtra("SEND_RESULT", "LAST_RESULT")
        // i.putExtra("SEND_RESULT", "true");  // For versions prior to DataWedge 7.1
        i.putExtra("COMMAND_IDENTIFIER", "KEYSTROKE_API")
        activity.sendBroadcast(i)
    }

    fun onPause() {
        try {
            activity.unregisterReceiver(myBroadcastReceiver)
            unRegisterScannerStatus()
        } catch (ex:Exception){
            Log.d(TAG, "ZebraScannerOnPause: ${ex.message}")
        }
    }

    fun unRegisterScannerStatus() {
        try{
            val b = Bundle()
            b.putString(EXTRA_KEY_APPLICATION_NAME, activity.getPackageName())
            b.putString(
                EXTRA_KEY_NOTIFICATION_TYPE,
                EXTRA_KEY_VALUE_SCANNER_STATUS
            )
            val i = Intent()
            i.setAction(ContactsContract.Intents.Insert.ACTION)
            i.putExtra(EXTRA_UNREGISTER_NOTIFICATION, b)
            activity.sendBroadcast(i)
        } catch (ex:Exception){
            Log.d(TAG, "ZebraScannerUnRegisterScannerStatus: ${ex.message}")
        }
    }

    interface OnDataScanned {
        fun onDataScanned(data:String)
    }

}