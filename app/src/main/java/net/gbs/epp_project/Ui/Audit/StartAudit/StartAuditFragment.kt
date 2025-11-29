package net.gbs.epp_project.Ui.Audit.StartAudit

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.OnClickListener
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController

import net.gbs.epp_project.Base.BaseFragmentWithViewModel
import net.gbs.epp_project.Model.AuditLocator
import net.gbs.epp_project.Model.AuditOrder
import net.gbs.epp_project.Model.AuditOrderSubinventory
import net.gbs.epp_project.Model.NavigationKeys.AUDIT_ORDER_KEY
import net.gbs.epp_project.Model.Status
import net.gbs.epp_project.R
import net.gbs.epp_project.Tools.EditTextActionHandler
import net.gbs.epp_project.Tools.Tools
import net.gbs.epp_project.Tools.Tools.changeFragmentTitle
import net.gbs.epp_project.Tools.Tools.clearInputLayoutError
import net.gbs.epp_project.Tools.Tools.containsOnlyDigits
import net.gbs.epp_project.Tools.Tools.getEditTextText
import net.gbs.epp_project.Tools.Tools.warningDialog
import net.gbs.epp_project.Ui.Audit.StartAudit.AuditDataDialog.AuditItemsDialog.AuditItemsDialog
import net.gbs.epp_project.Ui.Audit.StartAudit.AuditDataDialog.AuditLocatorsDialog.AuditLocatorsDialog
import net.gbs.epp_project.Ui.SplashAndSignIn.SignInFragment.Companion.isAllowChangeQuantity
import net.gbs.epp_project.Ui.SplashAndSignIn.SignInFragment.Companion.manualEnter
import net.gbs.epp_project.databinding.FragmentStartAuditBinding
import net.gbs.epp_project.Tools.ZebraScanner
class StartAuditFragment :
    BaseFragmentWithViewModel<StartAuditViewModel, FragmentStartAuditBinding>(),OnClickListener,
//    DataListener,StatusListener
        ZebraScanner.OnDataScanned
{

    companion object {
        fun newInstance() = StartAuditFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentStartAuditBinding
        get() = FragmentStartAuditBinding::inflate

//    private var barcodeReader:ZebraScanner? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    private lateinit var auditOrder: AuditOrder
    private lateinit var barcodeReader:ZebraScanner
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        barcodeReader = ZebraScanner(requireActivity(),this)
        try {
//            barcodeReader = ZebraScanner(scanner!!, this, this)
            if (viewModel.locatorCode!=null){
                binding.locatorCode.editText?.setText(viewModel.locatorCode)
            }
            if (viewModel.subinventory!=null){
                selectedSubInventory = viewModel.subinventory
                binding.itemDataGroup.visibility = VISIBLE
                binding.itemCode.editText?.setText(selectedSubInventory?.itemCode)
                binding.scannedQty.editText?.setText(viewModel.scannedQty)
                binding.itemDesc.text = selectedSubInventory?.itemDescription
                binding.orgDesc.text  = selectedSubInventory?.orgCode
                binding.uom.text      = selectedSubInventory?.uom
                viewModel.subinventory = null
                viewModel.scannedQty = null
            }
            Tools.attachButtonsToListener(this, binding.itemInfo!!,binding.locatorsListInfo!!,binding.save, binding.auditList, binding.finishAudit,binding.clearLocatorCode)
            clearInputLayoutError(binding.subInventory,binding.locatorCode,binding.itemCode)
            observeSavingData()
            handleManualAuthority()
            Tools.clearInputLayoutError(binding.scannedQty, binding.subInventory)
            setUpAutoSaveToggleButton()
            setUpAuditDataDialogs()
            EditTextActionHandler.OnEnterKeyPressed(binding.locatorCode) {
                val locatorCode = Tools.getEditTextText(binding.locatorCode)
                if (locatorCode.isNotEmpty()){
                    if (binding.subInventory.editText?.text.toString().isNotEmpty()) {
                        val subinventory =
                            selectedSubInventoryList.find { it.locatorCode == locatorCode }
                        if (subinventory==null){
                            binding.locatorCode.editText?.setText("")
                            binding.locatorCode.setError(getString(R.string.wrong_locator_code_or_this_locator_not_assigned_to_that_user))
                        }
                    } else
                        binding.locatorCode.setError(getString(R.string.please_select_sub_inventory_first))
                } else {
                    binding.locatorCode.error = getString(R.string.please_scan_valid_code)
                }

            }
            EditTextActionHandler.OnEnterKeyPressed(binding.itemCode) {
                val itemCode = Tools.getEditTextText(binding.itemCode)
                if (getEditTextText(binding.locatorCode).isNotEmpty()) {
                    if (itemCode.isNotEmpty()) {
                        val subinventory = selectedSubInventoryList.find {
                            it.locatorCode == getEditTextText(binding.locatorCode) && it.itemCode == itemCode
                        }
                        Log.d(TAG, "onDataSubInvLocatorCode: ${subinventory?.itemCode}")
                        if (subinventory != null) {
                            selectedSubInventory = subinventory
                            fillItemData()
                        } else {
                            warningDialog(
                                requireContext(),
                                getString(R.string.item_code_is_wrong_or_not_match_with_subinventory_and_locator)
                            )
                        }
                    } else {
                        binding.itemCode.error = getString(R.string.please_scan_valid_code)
                    }
                } else {
                    warningDialog(requireContext(), getString(R.string.please_scan_locator_first))
                }
            }
            observeFinishTracking()
            watchItemCodeText()
        } catch (ex:Exception){
            warningDialog(requireContext(),ex.message.toString())
        }
    }
    private lateinit var locatorsDialog:AuditLocatorsDialog
    private lateinit var itemsDialog: AuditItemsDialog
    private fun setUpAuditDataDialogs() {
        locatorsDialog = AuditLocatorsDialog(requireContext())
        itemsDialog    = AuditItemsDialog(requireContext())
    }

    private var autoSave:Boolean = false
    private var firstOpen:Boolean = true
    private fun setUpAutoSaveToggleButton() {
        binding.autoSave.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (!isAllowChangeQuantity) {
                if (!firstOpen){
                    warningDialog(requireContext(),getString(R.string.you_are_not_allowed_to_turn_off_auto_save_switch))
                }
                binding.autoSave.isChecked = true
                autoSave = true
                firstOpen = false
            } else {
                autoSave = isChecked
            }
            Log.d(TAG, "setAutoSave: $autoSave")

            setAutoSave()
            if (autoSave&&!isItemSaved&& getEditTextText(binding.itemCode).isNotEmpty()){
                val qty = getEditTextText(binding.scannedQty)
                if (isReadyToSave(qty)) {
                    viewModel.saveData(
                        qty = qty.toDouble(),
                        headerId = auditOrder.physicalInventoryHeaderId!!,
                        itemCode = selectedSubInventory?.itemCode!!,
                        locatorCode = selectedSubInventory?.locatorCode!!,
                        subInventoryCode = selectedSubInventory?.subInventoryCode!!,
                        orgCode = selectedSubInventory?.orgCode!!,
                    )
                }
            }
            if (!autoSave){
                binding.itemCode.editText?.setText("")
            }
        }
    }

    private fun setAutoSave() {

        if (autoSave){
            binding.save.isEnabled = false
            binding.scannedQty.isEnabled = false
        } else {
            binding.save.isEnabled = true
            binding.scannedQty.isEnabled = true
        }
    }

    private fun observeFinishTracking() {
        viewModel.finishTrackingStatus.observe(viewLifecycleOwner){
            when(it.status){
                Status.LOADING -> loadingDialog!!.show()
                Status.SUCCESS -> {
                    loadingDialog!!.dismiss()
                    Tools.showSuccessAlerter(it.message, requireActivity())
                }
                else -> {
                    Tools.warningDialog(requireContext(), it.message)
                    loadingDialog!!.dismiss()
                }
            }
        }
    }

    private var isItemSaved = false
    private fun observeSavingData() {
        viewModel.getSavingDataStatus.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> loadingDialog!!.show()
                Status.SUCCESS -> {
                    loadingDialog!!.dismiss()
                    Tools.showSuccessAlerter(it.message, requireActivity())
//                        binding.itemCode.editText?.setText("")
                    isItemSaved = true
                    binding.save.isEnabled = false
                }

                else -> {
                    loadingDialog!!.dismiss()
                    Tools.warningDialog(requireContext(), it.message)
                    binding.itemCode.editText?.setText("")
                }
            }

        }

        viewModel.getSavingDataLiveData.observe(requireActivity()){
            auditOrder = it[0]
            itemsList = auditOrder.getItemsForLocatorCodeAndSubInventory(
                    selectedSubInventory?.subInventoryCode!!,
                    getEditTextText(binding.locatorCode)
            )
        }
    }



    private fun handleManualAuthority() {
        binding.locatorCode.isEnabled = manualEnter
        binding.itemCode.isEnabled = manualEnter
        binding.scannedQty.isEnabled = manualEnter
        binding.scannedQty.isEnabled = isAllowChangeQuantity
    }

    private fun watchItemCodeText() {
        binding.itemCode.editText?.addTextChangedListener ( object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d(TAG, "beforeTextChanged: ")
            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.itemCode.error = null
                if (text!!.isEmpty())
                    clearItemData()
            }

            override fun afterTextChanged(p0: Editable?) {
                Log.d(TAG, "afterTextChanged: ")
            }

        } )
    }

    private fun clearItemData() {
        selectedSubInventory = null
        binding.itemDataGroup.visibility = GONE
        scannedQty = 0
        itemsList = listOf()
    }


    private var currentSubinventory:AuditOrderSubinventory? = null
    private fun fillItemData() {
        if (autoSave){
            if (getEditTextText(binding.itemCode)==selectedSubInventory?.itemCode){
                scannedQty++
            } else {
                scannedQty = 1
            }
            binding.itemDataGroup.visibility = VISIBLE
            currentSubinventory = selectedSubInventory
            save()
        } else {
            if (isItemSaved){
                scannedQty = 1
                currentSubinventory = selectedSubInventory
            } else {
                if (getEditTextText(binding.itemCode).isNotEmpty()) {
                    if (getEditTextText(binding.itemCode) == selectedSubInventory?.itemCode) {
                        scannedQty++
                        currentSubinventory = selectedSubInventory
                    } else {
                        warningDialog(
                            requireContext(),
                            getString(R.string.please_click_save_before_scan_different_item_code)
                        )
                        selectedSubInventory = currentSubinventory
                    }
                } else {
                    scannedQty++
                    currentSubinventory = selectedSubInventory
                }
            }
            isItemSaved = false
        }
        binding.itemDataGroup.visibility = VISIBLE
        binding.itemCode.editText?.setText(selectedSubInventory?.itemCode)
        binding.scannedQty.editText?.setText(scannedQty.toString())
        binding.itemDesc.text = selectedSubInventory?.itemDescription
        binding.orgDesc.text  = selectedSubInventory?.orgCode
        binding.uom.text      = selectedSubInventory?.uom
    }

    private fun save() {
        if (isReadyToSave("1")) {
            viewModel.saveData(
                qty = 1.0,
                headerId = auditOrder.physicalInventoryHeaderId!!,
                itemCode = selectedSubInventory?.itemCode!!,
                locatorCode = selectedSubInventory?.locatorCode!!,
                subInventoryCode = selectedSubInventory?.subInventoryCode!!,
                orgCode = selectedSubInventory?.orgCode!!,
            )
        }
    }

    private var subInventoriesList:List<AuditOrderSubinventory> = listOf()
    private lateinit var subInventoriesAdapter:ArrayAdapter<AuditOrderSubinventory>
    private var selectedSubInventoryList:MutableList<AuditOrderSubinventory> = mutableListOf()
    private var selectedSubInventory :AuditOrderSubinventory?=null
    private var locatorsForSubinventory : AuditLocator? = null
    private fun fillSubInventorySpinner() {
        subInventoriesList = auditOrder.editedSubInventoriesList()
        subInventoriesAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,subInventoriesList)
        binding.subInventorySpinner.setAdapter(subInventoriesAdapter)
        binding.subInventorySpinner.setOnItemClickListener { adapterView, view, position, l ->
            val selectedSubInventory = subInventoriesList[position]
            auditOrder.subInventories.forEach {
                if (it.subInventoryId==selectedSubInventory.subInventoryId){
                    selectedSubInventoryList.add(it)
                }
            }
            locatorsForSubinventory = auditOrder.getLocatorsForInSubInventory(selectedSubInventory.subInventoryCode!!)
            Log.d(TAG, "fillSubInventorySpinner: ${selectedSubInventoryList.size}")
            binding.locatorCode.editText?.setText("")
            binding.itemCode.editText?.setText("")
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.save -> {
                val qty = getEditTextText(binding.scannedQty)
                if (isReadyToSave(qty)) {
                    viewModel.saveData(
                        qty = qty.toDouble(),
                        headerId = auditOrder.physicalInventoryHeaderId!!,
                        itemCode = selectedSubInventory?.itemCode!!,
                        locatorCode = selectedSubInventory?.locatorCode!!,
                        subInventoryCode = selectedSubInventory?.subInventoryCode!!,
                        orgCode = selectedSubInventory?.orgCode!!,
                    )
                }
            }
            R.id.audit_list -> {
                    val bundle = Bundle()
                    bundle.putString(AUDIT_ORDER_KEY, AuditOrder.toJson(auditOrder))
                findNavController().navigate(
                        R.id.action_startAuditFragment_to_auditedListFragment,
                        bundle
                    )
            }
            R.id.finish_audit -> {
                if (selectedSubInventory!=null){
                    showConfirmAlerterDialog(getString(R.string.are_you_sure_to_finish_tracking_in_subinventory)+selectedSubInventory?.subInventoryCode+" ?")
                } else {
                    binding.subInventory.error = getString(R.string.please_select_sub_inventory)
                }
            }

            R.id.clear_locator_code -> {
                binding.locatorCode.editText?.setText("")
            }
            R.id.locators_list_info -> {
                if (locatorsForSubinventory?.auditOrderList?.isNotEmpty()!!){
                    locatorsDialog.auditLocator = locatorsForSubinventory
                    locatorsDialog.show()
                } else {
                    warningDialog(requireContext(),getString(R.string.please_select_sub_inventory_first))
                }
            }
            R.id.item_info -> {
                if (itemsList.isNotEmpty()){
                    itemsDialog.itemsList = itemsList
                    itemsDialog.show()
                } else {
                    warningDialog(requireContext(),getString(R.string.please_scan_locator_first))
                }
            }
        }
    }
    private fun showConfirmAlerterDialog(message:String) {
        val alerterDialog = AlertDialog.Builder(requireContext())
        alerterDialog.setMessage(message)
            .setPositiveButton(getString(R.string.finish)) { dialogInterface, i ->
                viewModel.finishTracking(
                    auditOrder.physicalInventoryHeaderId!!,
                    selectedSubInventory?.subInventoryCode!!
                )
            }.setNegativeButton(getString(R.string.cancel)) { dialogInterface, i ->
                dialogInterface.dismiss()
            }.create().show()
    }
    private fun isReadyToSave(qty:String):Boolean{
        var isReady = true
        if (selectedSubInventory==null){
            isReady = false
            binding.subInventory.error = getString(R.string.please_select_sub_inventory)
        }
        if (getEditTextText(binding.locatorCode).isEmpty()){
            isReady = false
            binding.locatorCode.error = getString(R.string.please_scan_or_enter_locator_code)
        }
//        if (getEditTextText(binding.itemCode).isEmpty()){
//            isReady = false
//            binding.itemCode.error = getString(R.string.please_scan_or_enter_item_code)
//        }
        if (qty.isEmpty()){
            isReady = false
            binding.scannedQty.error = getString(R.string.please_enter_qty)
        } else {
//            if (!containsOnlyDigits(qty)){
//                isReady = false
//                binding.scannedQty.error = getString(R.string.please_enter_valid_qty)
//            }
        }
        return isReady
    }
    private var scannedQty = 0

    override fun onResume() {
        super.onResume()

        try {
//            barcodeReader?.onResume()
//                scanner?.addDataListener(this)
//                scanner?.addStatusListener(this)
//                scanner?.triggerType = Scanner.TriggerType.HARD
//                // Enable the scanner
//                scanner?.enable()
//                // Starts an asynchronous Scan. The method will not turn ON the
//                // scanner. It will, however, put the scanner in a state in which
//                // the scanner can be turned ON either by pressing a hardware
//                // trigger or can be turned ON automatically.
//                scanner?.read()
            barcodeReader.onResume()
            auditOrder = AuditOrder.fromJson(arguments?.getString(AUDIT_ORDER_KEY)!!)
            fillAuditOrderData()
            if (viewModel.autoSave!=null) {
                binding.autoSave.isChecked = viewModel.autoSave!!
            }
            changeFragmentTitle(getString(R.string.start_audit),requireActivity())
            fillSubInventorySpinner()
        } catch (ex:Exception){
            warningDialog(requireContext(),ex.message.toString())
        }
    }

    private fun fillAuditOrderData() {
        binding.orderNo.text = auditOrder.orderDesc
        binding.orderDate.text = auditOrder.orderStartDate?.substring(0,10)
        if (!isAllowChangeQuantity&&!autoSave)
            binding.autoSave.isChecked = true
        scannedQty = 0
    }

    override fun onPause() {
        super.onPause()
//       barcodeReader?.onPause()
        barcodeReader.onPause()
//        scanner?.removeDataListener(this)
        viewModel.autoSave = autoSave
    }

//    override fun onData(p0: ScanDataCollection?) {
//        requireActivity().runOnUiThread {
//            try {
//                val scannedText = p0?.scanData?.get(0)?.data
//                Log.d(TAG, "onData: $scannedText")
//                if (scannedText?.isNotEmpty() == true) {
//                    if (getEditTextText(binding.locatorCode).isEmpty()) {
//                        if (binding.subInventory.editText?.text.toString().isNotEmpty()) {
//                            val subinventory =
//                                selectedSubInventoryList.find { it.locatorCode == scannedText }
//
//                            if (subinventory!=null){
//                                binding.locatorCode.editText?.setText(scannedText)
//                            } else {
//                                binding.locatorCode.setError(getString(R.string.wrong_locator_code_or_this_locator_not_assigned_to_that_user))
//                            }
//                        } else
//                            binding.locatorCode.setError(getString(R.string.please_select_sub_inventory_first))
//                    } else {
//    //                    if (itemData==null)
//                    if (isAllowChangeQuantity) {
//                        binding.save.isEnabled = true
//                    }
//
//                        val subinventory = selectedSubInventoryList.find { it.locatorCode==getEditTextText(binding.locatorCode)&&it.itemCode== scannedText }
//                        Log.d(TAG, "onDataSubInvLocatorCode: ${subinventory?.itemCode}")
//                        if (subinventory!=null){
//                            selectedSubInventory = subinventory
//                            fillItemData()
//                        } else {
//                            warningDialog(requireContext(),
//                                getString(R.string.item_code_is_wrong_or_not_match_with_subinventory_and_locator))
//                        }
//                    }
//                } else {
//                    warningDialog(requireContext(),getString(R.string.please_scan_valid_code))
//                }
////               barcodeReader?.restartReadData()
//                scanner?.disable()
//                scanner?.cancelRead()
//                scanner?.enable()
//                // Starts an asynchronous Scan. The method will not turn ON the
//                // scanner. It will, however, put the scanner in a state in which
//                // the scanner can be turned ON either by pressing a hardware
//                // trigger or can be turned ON automatically.
//                scanner?.read()
//            } catch (ex:Exception){
////                warningDialog(requireContext(),"Error in initializing scanner")
//                Log.d(TAG, "onStatusScan: ${ex.message}")
//            }
//        }
//    }
//
//    override fun onStatus(p0: StatusData?) {
////        barcodeReader?.onStatus(p0)
//        try {
//            if (p0!!.state.name == "IDLE"){
////                scanner!!.enable()
////                // Starts an asynchronous Scan. The method will not turn ON the
////                // scanner. It will, however, put the scanner in a state in which
////                // the scanner can be turned ON either by pressing a hardware
////                // trigger or can be turned ON automatically.
//                scanner?.read()
//            }
//        } catch (ex:Exception){
////            warningDialog(requireContext(),"Error in initializing scanner")
//            Log.d(ContentValues.TAG, "onStatus: ${ex.message}")
//        }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        val locatorCode = getEditTextText(binding.locatorCode)
        if (locatorCode.isNotEmpty()){
            viewModel.locatorCode = locatorCode
        }
        if (!isItemSaved){
            viewModel.subinventory = selectedSubInventory
            viewModel.scannedQty = getEditTextText(binding.scannedQty)
        }
    }
    private var itemsList = listOf<AuditOrderSubinventory>()
    override fun onDataScanned(data: String) {
        val scannedText = data
        Log.d(TAG, "onData: $scannedText")
        if (scannedText.isNotEmpty()) {
            if (getEditTextText(binding.locatorCode).isEmpty()) {
                if (binding.subInventory.editText?.text.toString().isNotEmpty()) {
                    val subinventory =
                        selectedSubInventoryList.find { it.locatorCode == scannedText }
                    if (subinventory!=null){
                        binding.locatorCode.editText?.setText(scannedText)
                        itemsList = auditOrder.getItemsForLocatorCodeAndSubInventory(subinventory.subInventoryCode!!,subinventory.locatorCode!!)
                    } else {
                        binding.locatorCode.setError(getString(R.string.wrong_locator_code_or_this_locator_not_assigned_to_that_user))
                    }
                } else
                    binding.locatorCode.setError(getString(R.string.please_select_sub_inventory_first))
            } else {
                //                    if (itemData==null)
                if (isAllowChangeQuantity) {
                    binding.save.isEnabled = true
                }

                val subinventory = selectedSubInventoryList.find { it.locatorCode==getEditTextText(binding.locatorCode)&&it.itemCode== scannedText }
                Log.d(TAG, "onDataSubInvLocatorCode: ${subinventory?.itemCode}")
                if (subinventory!=null){
                    selectedSubInventory = subinventory
                    fillItemData()
                } else {
                    warningDialog(requireContext(),
                        getString(R.string.item_code_is_wrong_or_not_match_with_subinventory_and_locator))
                }
            }
        } else {
            warningDialog(requireContext(),getString(R.string.please_scan_valid_code))
        }
    }
}