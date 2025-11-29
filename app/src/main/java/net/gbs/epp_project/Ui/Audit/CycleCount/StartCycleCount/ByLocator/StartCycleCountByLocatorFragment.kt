package net.gbs.epp_project.Ui.Audit.CycleCount.StartCycleCount.ByLocator

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

import net.gbs.epp_project.Base.BaseFragmentWithViewModel
import net.gbs.epp_project.Model.CycleCountHeader
import net.gbs.epp_project.Model.LocatorAudit
import net.gbs.epp_project.Model.NavigationKeys
import net.gbs.epp_project.Model.NavigationKeys.LOCATOR_KEY
import net.gbs.epp_project.Model.Status
import net.gbs.epp_project.R
import net.gbs.epp_project.Tools.EditTextActionHandler
import net.gbs.epp_project.Tools.Tools
import net.gbs.epp_project.Tools.Tools.back
import net.gbs.epp_project.Tools.Tools.changeFragmentTitle
import net.gbs.epp_project.Tools.Tools.containsOnlyDigits
import net.gbs.epp_project.Tools.Tools.getEditTextText
import net.gbs.epp_project.Tools.ZebraScanner
import net.gbs.epp_project.databinding.FragmentStartCycleCountByLocatorBinding

class StartCycleCountByLocatorFragment
    : BaseFragmentWithViewModel<StartCycleCountByLocatorViewModel,FragmentStartCycleCountByLocatorBinding>(),OnClickListener,
//    DataListener,StatusListener
    ZebraScanner.OnDataScanned
{

    companion object {
        fun newInstance() = StartCycleCountByLocatorFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentStartCycleCountByLocatorBinding
        get() = FragmentStartCycleCountByLocatorBinding::inflate

    private lateinit var scanner: ZebraScanner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    private lateinit var cycleCountHeader:CycleCountHeader
    private lateinit var organizationCode:String
    private lateinit var locator: LocatorAudit
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scanner = ZebraScanner(requireActivity(),this)
        cycleCountHeader = CycleCountHeader.fromJson(arguments?.getString(NavigationKeys.CYCLE_COUNT_HEADER_KEY)!!)
        Log.d(TAG, "observeCreatingCycleCountStart: ${cycleCountHeader.id}")
        organizationCode = arguments?.getString(NavigationKeys.ORGANIZATION_KEY)!!
        locator = LocatorAudit.fromJson(arguments?.getString(LOCATOR_KEY)!!)
        binding.locatorCode.text = locator.locatorCode
        EditTextActionHandler.OnEnterKeyPressed(binding.itemCode) {
            val itemCode = getEditTextText(binding.itemCode)
            viewModel.getItemData(organizationCode,itemCode)
        }
        observeGettingItemsList()
        Tools.clearInputLayoutError(binding.qty,binding.itemCode)
        observeSavingCycleDetails()
        Tools.attachButtonsToListener(this, binding.save,binding.onHands,binding.finishCount)
        observeFinishingCycleCount()
    }

    private fun observeFinishingCycleCount() {
        viewModel.finishCycleCountStatus.observe(requireActivity()){
            when(it.status){
                Status.LOADING -> loadingDialog!!.show()
                Status.SUCCESS ->{
                    loadingDialog!!.hide()
                    Tools.showSuccessAlerter(it.message, requireActivity())
                    back(this)
                }
                else -> {
                    loadingDialog!!.hide()
                    Tools.warningDialog(requireContext(), it.message)
                }
            }
        }
    }

    private fun observeSavingCycleDetails() {
        viewModel.saveCycleCountStatus.observe(requireActivity()){
            when(it.status){
                Status.LOADING -> loadingDialog!!.show()
                Status.SUCCESS ->{
                    loadingDialog!!.hide()
                    clearData()
                    Tools.showSuccessAlerter(it.message, requireActivity())
                }
                else -> {
                    loadingDialog!!.hide()
                    Tools.warningDialog(requireContext(), it.message)
                }
            }
        }
    }

    private fun clearData() {
        selectedItemCode = null
        binding.itemCode.editText?.setText("")
        binding.qty.editText?.setText("")
    }


    private var selectedItemCode: String? = null
    private fun observeGettingItemsList() {
        viewModel.getItemsListStatus.observe(requireActivity()){
            when(it.status){
                Status.LOADING -> loadingDialog!!.show()
                Status.SUCCESS -> loadingDialog!!.hide()
                else -> {
                    loadingDialog!!.hide()
                    Tools.warningDialog(requireContext(), it.message)
                }
            }
        }
        viewModel.getItemsListLiveData.observe(requireActivity()){
            selectedItemCode = it[0].itemCode
            binding.itemCode.editText?.setText(selectedItemCode)
        }
    }

//    override fun onData(p0: ScanDataCollection?) {
//        requireActivity().runOnUiThread{
//            val scannedText = scanner.onData(p0)
//            viewModel.getItemData(organizationCode,scannedText)
//            try {
//                scanner.restartReadData()
//            } catch (ex:Exception){
//                Log.d(TAG, "onDataError: ${ex.cause.toString()}")
//            }
//        }
//    }
//
//    override fun onStatus(p0: StatusData?) {
//        scanner.onStatus(p0)
//    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.save -> {
                val qtyText = binding.qty.editText?.text.toString().trim()
                if (isReadyToSave(qtyText)){
                    viewModel.saveCycleCount(
                        itemCode = getEditTextText(binding.itemCode),
                        locatorCode = locator.locatorCode!!,
                        cycleCountHeaderId = cycleCountHeader.id!!,
                        qty = qtyText.toDouble(),
                        orgCode = organizationCode
                    )
                }
            }
            R.id.on_hands ->{
                val bundle = Bundle()
                bundle.putString(NavigationKeys.ORGANIZATION_CODE_KEY, organizationCode)
                bundle.putInt(NavigationKeys.CYCLE_COUNT_HEADER_KEY, cycleCountHeader.id!!)
                findNavController().navigate(R.id.action_startCycleCountByLocatorFragment_to_onHandFragment,bundle)
            }

            R.id.finish_count -> viewModel.finishCycleCount(headerId = cycleCountHeader.id!!)
        }
    }

    override fun onResume() {
        super.onResume()
        changeFragmentTitle(getString(R.string.by_locator),requireActivity())
        scanner.onResume()
    }

    override fun onPause() {
        super.onPause()
        scanner.onPause()
    }
    private fun isReadyToSave(qtyText:String):Boolean{
        var isReady = true
        if (selectedItemCode == null){
            binding.locatorCode.error = getString(R.string.please_scan_or_enter_valid_locator_code)
            isReady = false
        }

        if (qtyText.isEmpty()){
            binding.qty.error = getString(R.string.please_enter_qty)
            isReady = false
        }
//        if (!containsOnlyDigits(qtyText)){
//            binding.qty.error = getString(R.string.please_enter_valid_qty)
//            isReady = false
//        }
        return isReady
    }

    override fun onDataScanned(data: String) {
        val scannedText = data
        viewModel.getItemData(organizationCode,scannedText)
    }
}