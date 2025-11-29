package net.gbs.epp_project.Ui.SplashAndSignIn

import android.app.Application
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.gbs.epp_project.Base.BaseFragmentWithViewModel
import net.gbs.epp_project.Base.LocalStorage
import net.gbs.epp_project.MainActivity.MainActivity
import net.gbs.epp_project.Network.ApiFactory.BaseUrlProvider.Companion.updateBaseUrl
import net.gbs.epp_project.R
import net.gbs.epp_project.Tools.ChangeSettingsDialog
import net.gbs.epp_project.Tools.CommunicationData
import net.gbs.epp_project.Tools.LoadingDialog
import net.gbs.epp_project.Tools.Tools
import net.gbs.epp_project.Tools.Tools.hideToolBar
import net.gbs.epp_project.databinding.FragmentSplashBinding
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.log


class SplashFragment : BaseFragmentWithViewModel<SignInViewModel,FragmentSplashBinding>(),ChangeSettingsDialog.OnButtonsClicked {


    private lateinit var changeSettingsDialog: ChangeSettingsDialog
    private lateinit var communicationData: CommunicationData
    private lateinit var localStorage: LocalStorage
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        communicationData = CommunicationData(requireActivity())
        changeSettingsDialog = ChangeSettingsDialog(requireContext(),requireActivity(),this)
        changeSettingsDialog.setCancelable(false)
        localStorage = LocalStorage(requireActivity())
        Handler().postDelayed({
            if (isAdded)
                if (!localStorage.getFirstTime()){
                    hasInternetConnection(
                        communicationData.getProtocol(),
                        communicationData.getIpAddress(),
                        communicationData.getPortNumber()
                    )
                }
                else changeSettingsDialog.show()
        }, 2000)
//        navController.navigate(R.id.action_splashFragment_to_signInFragment)
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSplashBinding
        get() = FragmentSplashBinding::inflate

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

//        hideToolBar(activity as MainActivity)
        hideToolBar(requireActivity())
    }

    fun hasInternetConnection(protocol: String,ipAddress:String,portNum:String){
        loadingDialog!!.show()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val url = URL("$protocol://$ipAddress:$portNum/api/GBSEPPWMS/CheckConnection")
//                val url = URL("$protocol://$ipAddress:$portNum/BasicDataSetUp/api/GBSEPPWMS/CheckConnection")
                val urlc = url.openConnection() as HttpURLConnection
                urlc.setRequestProperty(
                    "User-Agent",
                    "Android Application:" + Build.VERSION.SDK_INT
                )
                Log.d(TAG, "hasInternetConnection: $url")
                urlc.setRequestProperty("Connection", "close")
                urlc.connectTimeout = 1000 * 30 // mTimeout is in seconds
                urlc.connect()
                print(urlc.responseCode)
                if (urlc.responseCode == 200) {
                    communicationData.saveProtocol(protocol)
                    communicationData.saveIPAddress(ipAddress)
                    communicationData.savePortNum(portNum)
                    Log.d(TAG, "hasInternetConnectionlocal: ${communicationData.getIpAddress()}")
                    withContext(Dispatchers.Main) {
                        updateBaseUrl(communicationData.getProtocol(),communicationData.getIpAddress(),communicationData.getPortNumber())
//                        setBaseUrl(communicationData.getProtocol(),communicationData.getIpAddress(),communicationData.getPortNumber())
//                        Log.d(TAG, "hasInternetConnection: $BASE_URL")
                        viewModel.refreshRepository()
                        findNavController().navigate(R.id.action_splashFragment_to_signInFragment)
                        localStorage.setFirstTime(false)
                        loadingDialog!!.hide()
                        if (changeSettingsDialog.isShowing)
                            changeSettingsDialog.dismiss()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        loadingDialog!!.hide()
                        changeSettingsDialog.show()
                    }
                }
            } catch (e1: IOException) {
                e1.printStackTrace()
                withContext(Dispatchers.Main) {
                    loadingDialog!!.hide()
                    changeSettingsDialog.show()
//                    Tools.warningDialog(requireContext(), getString(R.string.wrong_connection_data))
                }
            }
        }
    }

    override fun OnSaveButtonClicked(protocol: String, ipAddress: String, portNum: String) {
        hasInternetConnection(
            protocol, ipAddress, portNum
        )
    }

    override fun OnDialogDismessed(protocol: String, ipAddress: String, portNum: String) {
        hasInternetConnection(
            protocol, ipAddress, portNum
        )
    }
}