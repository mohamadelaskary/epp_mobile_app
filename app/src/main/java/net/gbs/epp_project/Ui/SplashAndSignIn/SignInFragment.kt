package net.gbs.epp_project.Ui.SplashAndSignIn

import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.gbs.epp_project.Base.BaseFragmentWithViewModel
import net.gbs.epp_project.Base.LocalStorage
import net.gbs.epp_project.MainActivity.MainActivity
import net.gbs.epp_project.Model.Status
import net.gbs.epp_project.Model.User
import net.gbs.epp_project.Network.ApiFactory.BaseUrlProvider.Companion.updateBaseUrl
import net.gbs.epp_project.R
import net.gbs.epp_project.Tools.ChangeSettingsDialog
import net.gbs.epp_project.Tools.ChangeSettingsDialog.Companion.refreshUi
import net.gbs.epp_project.Tools.CommunicationData
import net.gbs.epp_project.Tools.FormatDateTime
import net.gbs.epp_project.Tools.LocaleHelper
import net.gbs.epp_project.Tools.Tools.changeFragmentTitle
import net.gbs.epp_project.Tools.Tools.getEditTextText
import net.gbs.epp_project.Tools.Tools.hideBackButton
import net.gbs.epp_project.Tools.Tools.hideLogOutButton
import net.gbs.epp_project.Tools.Tools.showSuccessAlerter
import net.gbs.epp_project.Tools.Tools.showToolBar
import net.gbs.epp_project.Tools.Tools.warningDialog
import net.gbs.epp_project.databinding.FragmentSignInBinding
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.Timer
import kotlin.concurrent.schedule

class SignInFragment : BaseFragmentWithViewModel<SignInViewModel, FragmentSignInBinding>(),ChangeSettingsDialog.OnButtonsClicked {

    companion object {
        val EMPLOYEE_ID = 1210
        var USER: User? = User()
        val PROGRAM_ID = 327660
        val PROGRAM_APPLICATION_ID = 201
        var manualEnter = true
        var isAllowChangeQuantity = false
        fun newInstance() = SignInFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSignInBinding
        get() = FragmentSignInBinding::inflate
    private lateinit var changeSettingsDialog: ChangeSettingsDialog
    var currentLang = ""
    var doubleBackToExitPressedOnce = false
    private lateinit var localStorage: LocalStorage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        communicationData = CommunicationData(requireActivity())
        localStorage = LocalStorage(requireActivity())
        changeSettingsDialog = ChangeSettingsDialog(requireContext(),requireActivity(),this)
        LocaleHelper.onCreate(requireContext())
        currentLang = LocaleHelper.getLanguage(requireContext()).toString()

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                if (doubleBackToExitPressedOnce) {
                    requireActivity().finish()
                    return
                }
                doubleBackToExitPressedOnce = true
                Toast.makeText(requireContext(), "Click BACK again to exit", Toast.LENGTH_SHORT).show()
                Timer().schedule(2000) {
                    doubleBackToExitPressedOnce = false
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this, onBackPressedCallback
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeSignIn()
        binding.signIn.setOnClickListener {
            val userName = getEditTextText(binding.userName)
            val password = getEditTextText(binding.password)
            if (readyToSignIn(userName,password)){
//                navController.navigate(R.id.action_signInFragment_to_mainMenuFragment)
                viewModel.signIn(userName,password)
            }
        }
        binding.changePassword.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_changePasswordFragment)
        }
        binding.settings.setOnClickListener {
            changeSettingsDialog.show()
        }

        binding.language.setOnClickListener {
            if (currentLang == "ar") {
                context?.let { LocaleHelper.setLocale(it, "en") }
                (requireActivity() as MainActivity?)?.let { refreshUi(it) }
            } else if (currentLang == "en") {
                context?.let { LocaleHelper.setLocale(it, "ar") }
                (activity as MainActivity?)?.let { refreshUi(it) }
            }
            (activity as MainActivity?)?.let { refreshUi(it) }
        }
    }

    private fun observeSignIn() {
        viewModel.signInStatus.observe(requireActivity()){
            when(it.status){
                Status.LOADING -> loadingDialog!!.show()
                Status.SUCCESS ->{
                    loadingDialog!!.dismiss()
                }
                else -> {
                    loadingDialog!!.dismiss()
                    warningDialog(requireContext(),it.message)
                }
            }
        }
        viewModel.signInLiveData.observe(requireActivity()){
            USER = it
            MainActivity.startSession()
            try {
                Log.d(TAG, "observeSignIn: ServerTime ${it.serverDateTime!!.replace("T", " ").substring(0, 16)+":00"}")
                Log.d(TAG, "observeSignIn: MobileTime ${viewModel.getDeviceTodayDate()}")
                if (FormatDateTime.compareTwoTimes(
                        it.serverDateTime!!.replace("T", " ").substring(0, 16)+":00",
                        viewModel.getDeviceTodayDate()
                    )
                ) {
                    try {
                        if (getCurrentApkVersion(
                                context?.packageManager!!,
                                context?.packageName!!
                            ) == USER?.apkVersion
                        ) {
                            findNavController().navigate(R.id.action_signInFragment_to_mainMenuFragment)
                            showSuccessAlerter(
                                getString(R.string.you_logged_in_successfully),
                                requireActivity()
                            )
                        } else {
                            findNavController().navigate(R.id.action_signInFragment_to_updateApkFragment)
                            showSuccessAlerter(
                                getString(R.string.update_mobile_application_to_new_version),
                                requireActivity()
                            )
                        }
                    } catch (ex: Exception){
                        warningDialog(requireContext(),ex.message.toString())

                    }
                } else {
                    warningDialog(
                        requireContext(),
                        getString(R.string.device_date_or_time_or_both_not_correct_please_correct_device_date_and_time_and_try_to_sign_in_again) + "\n" + getString(
                            R.string.correct_date_time_is
                        ) + "\n${FormatDateTime(it.serverDateTime!!.replace("T", " ").substring(0, 19)).year()}-${FormatDateTime(it.serverDateTime!!.replace("T", " ").substring(0, 19)).month()}-${
                            FormatDateTime(
                                it.serverDateTime!!.replace("T", " ").substring(0, 19)
                            ).day()
                        } ${FormatDateTime(it.serverDateTime!!.replace("T", " ").substring(0, 19)).hours()}:${FormatDateTime(it.serverDateTime!!.replace("T", " ").substring(0, 19)).minutes()}"
                    )
                }
            } catch (ex:Exception){
                Log.d(TAG, "observeSignIn: ${ex.message}")
            }
            manualEnter = it.manualEnter
            isAllowChangeQuantity = it.isAllowEditQuantity!!
        }
    }





    private fun readyToSignIn(userName:String,password:String): Boolean {
        var isReady = true
        if (userName.isEmpty()) {
            isReady = false
            binding.userName.error = getString(R.string.please_enter_your_user_name)
        }
        if (password.isEmpty()) {
            isReady = false
            binding.password.error = getString(R.string.please_enter_your_password)
        }
        return isReady
    }

    override fun onResume() {
        super.onResume()
//        showToolBar(activity as MainActivity)
        showToolBar(requireActivity())
        hideBackButton(requireActivity())
        hideLogOutButton(requireActivity())
//        changeTitle(getString(R.string.sign_in),activity as MainActivity)
        changeFragmentTitle(getString(R.string.sign_in),requireActivity())
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
    private lateinit var communicationData: CommunicationData
    fun hasInternetConnection(protocol: String,ipAddress:String,portNum:String){
        loadingDialog!!.show()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val url = URL("$protocol://$ipAddress:$portNum/api/GBSEPPWMS/CheckConnection")
                val urlc = url.openConnection() as HttpURLConnection
                urlc.setRequestProperty(
                    "User-Agent",
                    "Android Application:" + Build.VERSION.SDK_INT
                )
                urlc.setRequestProperty("Connection", "close")
                urlc.connectTimeout = 1000 * 30 // mTimeout is in seconds
                urlc.connect()
                print(urlc.responseCode)
                if (urlc.responseCode == 200) {
                    communicationData.saveProtocol(protocol)
                    communicationData.saveIPAddress(ipAddress)
                    communicationData.savePortNum(portNum)
                    withContext(Dispatchers.Main) {
                        updateBaseUrl(communicationData.getProtocol(),communicationData.getIpAddress(),communicationData.getPortNumber())
                        (requireActivity() as MainActivity).updateUrlOnScreen()
                        showSuccessAlerter("Connected successfully",requireActivity())
//                        MainActivity.setBaseUrl(protocol,ipAddress, portNum)
                        viewModel.refreshRepository()
                        localStorage.setFirstTime(false)
                        loadingDialog!!.hide()
                        if (changeSettingsDialog.isShowing)
                            changeSettingsDialog.dismiss()
//                        refreshUi(requireActivity() as MainActivity)
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

    fun getCurrentApkVersion(packageManager: PackageManager,packageName:String):String{
        var versionCode: String = "0"
        try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                versionCode = packageInfo.versionCode.toString()
            } else {
                @Suppress("DEPRECATION")
                versionCode = packageInfo.versionCode.toString()
            }

        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            // Handle the exception
        }
        return versionCode
    }
}