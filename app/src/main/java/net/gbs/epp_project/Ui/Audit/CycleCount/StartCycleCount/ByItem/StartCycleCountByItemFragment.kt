package net.gbs.epp_project.Ui.Audit.CycleCount.StartCycleCount.ByItem

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
import net.gbs.epp_project.Model.Item
import net.gbs.epp_project.Model.NavigationKeys.CYCLE_COUNT_HEADER_KEY
import net.gbs.epp_project.Model.NavigationKeys.ITEM_KEY
import net.gbs.epp_project.Model.NavigationKeys.ORGANIZATION_CODE_KEY
import net.gbs.epp_project.Model.Status
import net.gbs.epp_project.R
import net.gbs.epp_project.Tools.EditTextActionHandler
import net.gbs.epp_project.Tools.Tools
import net.gbs.epp_project.Tools.Tools.attachButtonsToListener
import net.gbs.epp_project.Tools.Tools.back
import net.gbs.epp_project.Tools.Tools.changeFragmentTitle
import net.gbs.epp_project.Tools.Tools.containsOnlyDigits
import net.gbs.epp_project.Tools.Tools.getEditTextText
import net.gbs.epp_project.Tools.Tools.showSuccessAlerter
import net.gbs.epp_project.Tools.Tools.warningDialog
import net.gbs.epp_project.Tools.ZebraScanner
import net.gbs.epp_project.databinding.FragmentStartCycleCountByItemBinding

class StartCycleCountByItemFragment :
    BaseFragmentWithViewModel<StartCycleCountByItemViewModel,FragmentStartCycleCountByItemBinding>(),OnClickListener,
//    DataListener,StatusListener
ZebraScanner.OnDataScanned
{

    companion object {
        fun newInstance() = StartCycleCountByItemFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentStartCycleCountByItemBinding
        get() = FragmentStartCycleCountByItemBinding::inflate
    private lateinit var scanner: ZebraScanner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    private lateinit var cycleCountHeader: CycleCountHeader
    private lateinit var item: Item
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scanner = ZebraScanner(requireActivity(),this)
        cycleCountHeader = CycleCountHeader.fromJson(arguments?.getString(CYCLE_COUNT_HEADER_KEY)!!)
        item = Item.fromJson(arguments?.getString(ITEM_KEY)!!)
        binding.itemCode.text = item.itemDescription
        Log.d(TAG, "onViewCreatedStartCycleHeaderId: ${cycleCountHeader.id}")
        EditTextActionHandler.OnEnterKeyPressed(binding.locatorCode) {
            val locatorCode = getEditTextText(binding.locatorCode)
            viewModel.getLocatorData(locatorCode)
        }
        observeGettingLocatorData()
        observeSavingCycleDetails()
        observeFinishingCycleCount()
        Tools.clearInputLayoutError(binding.qty,binding.locatorCode)
        attachButtonsToListener(this,binding.save,binding.onHands,binding.finishCount)
    }

    private fun observeFinishingCycleCount() {
        viewModel.finishCycleCountStatus.observe(requireActivity()){
            when(it.status){
                Status.LOADING -> loadingDialog!!.show()
                Status.SUCCESS ->{
                    loadingDialog!!.hide()
                    back(this)
                    showSuccessAlerter(it.message,requireActivity())
                }
                else -> {
                    loadingDialog!!.hide()
                    warningDialog(requireContext(),it.message)
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
                    showSuccessAlerter(it.message,requireActivity())
                }
                else -> {
                    loadingDialog!!.hide()
                    warningDialog(requireContext(),it.message)
                }
            }
        }
    }

    private fun clearData() {
        selectedLocatorCode = null
        binding.locatorCode.editText?.setText("")
        binding.qty.editText?.setText("")
    }

    private var selectedLocatorCode:String? = null
    private fun observeGettingLocatorData() {
        viewModel.getLocatorDataStatus.observe(requireActivity()){
            when(it.status){
                Status.LOADING -> loadingDialog!!.show()
                Status.SUCCESS -> loadingDialog!!.hide()
                else -> {
                    loadingDialog!!.hide()
                    binding.locatorCode.error = it.message
                }
            }
        }
        viewModel.getLocatorDataLiveData.observe(requireActivity()){
            if (it.isNotEmpty()){
                selectedLocatorCode = it[0].locatorCode
                binding.locatorCode.editText?.setText(it[0].locatorCode)
            } else {
                selectedLocatorCode = null
                binding.locatorCode.error = getString(R.string.wrong_locator)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        changeFragmentTitle(getString(R.string.by_item),requireActivity())
        scanner.onResume()
    }

    override fun onPause() {
        super.onPause()
        scanner.onPause()
    }

//    override fun onData(scan: ScanDataCollection?) {
//        requireActivity().runOnUiThread {
//            val scannedText = scanner.onData(scan)
//            viewModel.getLocatorData(scannedText)
//            scanner.restartReadData()
//        }
//    }
//
//    override fun onStatus(p0: StatusData?) {
//       scanner.onStatus(p0)
//    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.save -> {
                val qtyText = binding.qty.editText?.text.toString().trim()
                if (isReadyToSave(qtyText)){
                    viewModel.saveCycleCount(
                        itemCode = item.itemCode!!,
                        locatorCode = selectedLocatorCode!!,
                        cycleCountHeaderId = cycleCountHeader.id!!,
                        qty = qtyText.toDouble(),
                        orgCode = item.orgCode!!
                    )
                }
            }
            R.id.on_hands ->{
                val bundle = Bundle()
                bundle.putString(ORGANIZATION_CODE_KEY, item.orgCode)
                bundle.putInt(CYCLE_COUNT_HEADER_KEY, cycleCountHeader.id!!)
                findNavController().navigate(R.id.action_startCycleCountByItemFragment_to_onHandFragment,bundle)
            }
            R.id.finish_count -> viewModel.finishCycleCount(headerId = cycleCountHeader.id!!)
        }
    }

    private fun isReadyToSave(qtyText:String):Boolean{
        var isReady = true
        if (selectedLocatorCode==null){
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
        viewModel.getLocatorData(scannedText)
    }

}