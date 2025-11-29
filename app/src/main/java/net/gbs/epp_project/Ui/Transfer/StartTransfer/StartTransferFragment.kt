package net.gbs.epp_project.Ui.Transfer.StartTransfer

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter

import net.gbs.epp_project.Base.BaseFragmentWithViewModel
import net.gbs.epp_project.Base.BundleKeys.ORGANIZATION_ID_KEY
import net.gbs.epp_project.Model.ApiRequestBody.TransferMaterialBody
import net.gbs.epp_project.Model.Locator
import net.gbs.epp_project.Model.Lot
import net.gbs.epp_project.Model.OnHandItemForAllocate
import net.gbs.epp_project.Model.Status
import net.gbs.epp_project.Model.SubInventory
import net.gbs.epp_project.R
import net.gbs.epp_project.Tools.EditTextActionHandler
import net.gbs.epp_project.Tools.Tools
import net.gbs.epp_project.Tools.Tools.changeFragmentTitle
import net.gbs.epp_project.Tools.Tools.clearInputLayoutError
import net.gbs.epp_project.Tools.Tools.getEditTextText
import net.gbs.epp_project.Tools.Tools.showBackButton
import net.gbs.epp_project.Tools.Tools.showSuccessAlerter
import net.gbs.epp_project.Tools.Tools.warningDialog
import net.gbs.epp_project.Ui.Issue.EppOrganizations.TransactMoveOrderChemicalFactory.TransactMoveOrderFragment
import net.gbs.epp_project.databinding.FragmentStartTransferBinding
import net.gbs.epp_project.Tools.ZebraScanner
class StartTransferFragment : BaseFragmentWithViewModel<StartTransferViewModel,FragmentStartTransferBinding>(),
//    DataListener,StatusListener
        ZebraScanner.OnDataScanned
{

    companion object {
        fun newInstance() = StartTransferFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentStartTransferBinding
        get() = FragmentStartTransferBinding::inflate
    private lateinit var barcodeReader:ZebraScanner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    private var orgId: Int = -1
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        barcodeReader = ZebraScanner(requireActivity(),this)
        orgId = arguments?.getInt(ORGANIZATION_ID_KEY)!!
        observeGettingData()
        setUpSpinners()
        setUpDateDialog()
        clearInputLayoutError(
            binding.subInventoryFrom,
            binding.locatorFrom,
            binding.itemCode,
            binding.transferQty,
            binding.subInventoryTo,
            binding.locatorTo,
            binding.lotNumber
            )
        binding.transfer.setOnClickListener {
            val qty = getEditTextText(binding.transferQty)
            val transferDate = getEditTextText(binding.date)
            if (isReadyToSave(qty,transferDate)){
                val transferBody = TransferMaterialBody(
                    organizationId = orgId,
                    inventoryItemId = itemData[0].inventorYITEMID,
                    subinventoryCodeFrom = selectedSubInventoryCodeFrom,
                    locatorCodeFrom = selectedLocatorCodeFrom,
                    subinventoryCodeTo = selectedSubInventoryCodeTo,
                    locatorCodeTo = selectedLocatorCodeTo,
                    lotNum = selectedLot?.lotName,
                    qty = qty.toDouble(),
                    transactionDate = viewModel.getTodayDate()
                )
                viewModel.transferMaterial(transferBody)
            }
        }



        EditTextActionHandler.OnEnterKeyPressed(binding.itemCode){
            val itemCode = getEditTextText(binding.itemCode)
            itemCodeScanned(itemCode)
        }
    }

    private var lotList = listOf<Lot>()
    private lateinit var lotAdapter: ArrayAdapter<Lot>
    private var selectedLot: Lot? = null
    private fun setUpLotSpinner() {
        binding.lotNumberSpinner.setOnItemClickListener { _, _, selectedPosition, _ ->
            selectedLot = lotList[selectedPosition]
        }
    }
    private fun observeGettingLotList() {
        viewModel.getLotListStatus.observe(requireActivity()){
            when(it.status){
                Status.LOADING ->{
                    loadingDialog!!.show()
                }
                Status.SUCCESS -> {
                    loadingDialog!!.hide()
                }
                else -> {
                    loadingDialog!!.hide()
//                    warningDialog(requireContext(),it.message)
                }
            }
        }
        viewModel.getLotListLiveData.observe(requireActivity()){
            Log.d(TAG, "observeGettingLotList: ${it.size}")
            lotList = it
            lotAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,lotList)
            binding.lotNumberSpinner.setAdapter(lotAdapter)
        }
    }
    private fun setUpDateDialog() {
        binding.date.setOnClickListener {
            Tools.datePicker(requireContext(), binding.date)
                .show(requireActivity().supportFragmentManager,getString(R.string.select_a_date))

        }
    }

    private fun setUpSpinners() {
        setUpSubInventorySpinner()
        setUpLocatorsSpinner()
        setUpLotSpinner()
    }

    private fun observeGettingData() {
//        observeGettingDate()
        observeGettingSubInventoryList()
        observeGettingLocatorsList()
        observeGettingItemData()
        observeTransferingItem()
        observeGettingLotList()
    }

    private fun observeTransferingItem() {
        viewModel.transferMaterialStatus.observe(requireActivity()){
            when(it.status){
                Status.LOADING -> loadingDialog!!.show()
                Status.SUCCESS -> {
                    loadingDialog!!.hide()
                    showSuccessAlerter(it.message,requireActivity())
                    clearItemData()
                }
                else -> {
                    loadingDialog!!.hide()
                    warningDialog(requireContext(),it.message)
                }
            }
        }
    }

    private fun clearItemData() {
        itemData = listOf()
        binding.itemCode.editText?.setText("")
        binding.itemDesc.visibility = GONE
        binding.onHandQty.visibility = GONE
        binding.subInventoryFromSpinner.setText("",false)
        clearSubInventoryFrom()
        clearLocatorFrom()
        binding.lotNumberSpinner.setText("",false)
        binding.lotNumber.visibility = GONE
        binding.transferQty.editText?.setText("")
        binding.subInventoryToSpinner.setText("",false)
        binding.locatorToSpinner.setText("",false)
    }

    private fun clearLocatorFrom() {
        locatorsFromList = mutableListOf()
        locatorsFromAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,locatorsFromList)
        binding.locatorFromSpinner.setAdapter(locatorsFromAdapter)
        binding.locatorFromSpinner.setText("",false)
    }

    private fun clearSubInventoryFrom() {
        subInventoryFromList = mutableListOf()
        subInventoryFromAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,subInventoryFromList)
        binding.subInventoryFromSpinner.setAdapter(subInventoryFromAdapter)
        binding.subInventoryFromSpinner.setText("",false)
    }

    private var itemData:List<OnHandItemForAllocate> = listOf()
    private fun observeGettingItemData() {
        viewModel.getItemsListStatus.observe(requireActivity()){
            when(it.status){
                Status.LOADING -> loadingDialog!!.show()
                Status.SUCCESS -> loadingDialog!!.hide()
                else -> {
                    loadingDialog!!.hide()
                    warningDialog(requireContext(),it.message)
                    clearItemData()
                }
            }
        }
        viewModel.getItemsListLiveData.observe(requireActivity()){
            if (it.isNotEmpty()){
                clearItemData()
                itemData = it
                if (itemData[0].mustHaveLot()) {
                    binding.lotNumber.visibility = VISIBLE
                } else {
                    binding.lotNumber.visibility = GONE
                }
                fillItemData()
                fillSubInventoryList()
            } else {
                clearItemData()
                binding.itemCode.error =
                    getString(R.string.scanned_item_code_is_wrong_or_doesn_t_belong_to_selected_sub_inventory_or_selected_locator)
                itemData = listOf()
            }
        }
    }

    private fun fillSubInventoryList() {
        itemData.forEach(fun(item: OnHandItemForAllocate) {
            val subInventory = subInventoryFromList.find { it.subInventoryCode == item.subinventory }
            if (subInventory==null)
                subInventoryFromList.add(
                    SubInventory(
                        subInventoryCode = item.subinventory
                    )
                )

        })
        subInventoryFromAdapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                subInventoryFromList
            )
        binding.subInventoryFromSpinner.setAdapter(subInventoryFromAdapter)
        if (subInventoryFromList.size==1) {
            if (itemData.isNotEmpty()) {
                binding.subInventoryFromSpinner.setText(subInventoryFromList[0].subInventoryCode)
                selectedSubInventoryCodeFrom = subInventoryFromList[0].subInventoryCode
                fillFromLocatorList()
                subInvType = TransactMoveOrderFragment.SubInvType.FROM
            } else {
                warningDialog(requireContext(),getString(R.string.please_scan_or_enter_item_code))
            }
        }
    }
    var selectedItemData :OnHandItemForAllocate? = null
    private fun fillItemData() {
        selectedItemData = null
        binding.onHandQty.visibility = GONE
        binding.itemCode.editText?.setText(itemData[0].iteMCODE)
        binding.itemDesc.visibility = VISIBLE
        binding.itemDesc.text = itemData[0].iteMDESCRIPTION
        if (itemData.size==1){
            binding.onHandQty.visibility = VISIBLE
            binding.onHandQty.text = itemData[0].onhand.toString()
            selectedSubInventoryCodeFrom = itemData[0].subinventory
            binding.subInventoryFromSpinner.setText(selectedSubInventoryCodeFrom)
            selectedLocatorCodeFrom = itemData[0].locator
            binding.locatorFromSpinner.setText(itemData[0].locator)
            selectedItemData = itemData[0]
        }
        viewModel.getSubInvertoryList(orgId)
    }

//    private fun observeGettingDate() {
//        viewModel.getDateStatus.observe(requireActivity()){
//            when(it.status){
//                Status.LOADING -> {
//                    loadingDialog!!.show()
//                    binding.date.isEnabled = false
//                }
//                Status.SUCCESS -> {
//                    loadingDialog!!.hide()
//                    binding.date.isEnabled = false
//                    viewModel.getSubInvertoryList(orgId)
//                }
//                else -> {
//                    loadingDialog!!.hide()
//                    binding.date.error = it.message
//                    binding.date.isEnabled = true
//                    viewModel.getSubInvertoryList(orgId)
//                }
//            }
//        }
//        viewModel.getDateLiveData.observe(requireActivity()){
//            binding.date.editText?.setText(it.substring(0,10))
//        }
//    }
    private var subInventoryFromList = mutableListOf<SubInventory>()
    private var subInventoryList = listOf<SubInventory>()
    private lateinit var subInventoryToAdapter: ArrayAdapter<SubInventory>
    private lateinit var subInventoryFromAdapter: ArrayAdapter<SubInventory>
    private var selectedSubInventoryCodeFrom: String? = null
    private var selectedSubInventoryCodeTo: String? = null
    private var subInvType: TransactMoveOrderFragment.SubInvType = TransactMoveOrderFragment.SubInvType.FROM
    private fun observeGettingSubInventoryList() {
        viewModel.getSubInvertoryListStatus.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> loadingDialog!!.show()
                Status.SUCCESS -> loadingDialog!!.hide()
                else -> {
                    loadingDialog!!.hide()
                    Tools.warningDialog(requireContext(), it.message)
                }
            }
        }
        viewModel.getSubInvertoryListLiveData.observe(requireActivity()) {
            if (it.isNotEmpty()) {
                subInventoryList = it
                subInventoryToAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    subInventoryList
                )
                binding.subInventoryToSpinner.setAdapter(subInventoryToAdapter)
            } else {
                Tools.warningDialog(
                    requireContext(),
                    getString(R.string.no_sub_inventories_for_this_org_id)
                )
            }
        }
    }

    private fun setUpSubInventorySpinner() {
        binding.subInventoryFromSpinner.setOnItemClickListener { _, _, selectedIndex, _ ->
            selectedSubInventoryCodeFrom = subInventoryFromList[selectedIndex].subInventoryCode
            fillFromLocatorList()
            subInvType = TransactMoveOrderFragment.SubInvType.FROM
        }
        binding.subInventoryToSpinner.setOnItemClickListener { _, _, selectedIndex, _ ->
            selectedSubInventoryCodeTo = subInventoryList[selectedIndex].subInventoryCode
            viewModel.getLocatorsList(orgId, selectedSubInventoryCodeTo!!)
            subInvType = TransactMoveOrderFragment.SubInvType.To
            if (selectedItemData?.mustHaveLot()!!){
                viewModel.getLotList(orgId, itemData[0].inventorYITEMID, selectedSubInventoryCodeFrom)
            }
        }
    }

    private fun fillOnHandQty() {
        if (selectedItemData!=null){
            binding.onHandQty.visibility = VISIBLE
            binding.onHandQty.text = selectedItemData?.onhand.toString()
        }
    }

    private fun fillFromLocatorList() {
        itemData.forEach(fun(item: OnHandItemForAllocate) {
            if (item.subinventory == selectedSubInventoryCodeFrom && item.iteMCODE == getEditTextText(
                    binding.itemCode
                )
            ) {
                val locator = locatorsFromList.find { it.locatorCode == item.locator}
                if (locator==null)
                    locatorsFromList.add(
                        Locator(
                            locatorCode = item.locator
                        )
                    )
            }
        })
        locatorsFromAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,locatorsFromList)
        binding.locatorFromSpinner.setAdapter(locatorsFromAdapter)

        if (locatorsFromList.size==1){
            binding.locatorFromSpinner.setText(locatorsFromList[0].locatorCode)
            selectedLocatorCodeFrom = locatorsFromList[0].locatorCode
            selectedItemData = itemData.find { it.locator == selectedLocatorCodeFrom && it.subinventory == selectedSubInventoryCodeFrom }
            fillOnHandQty()
        }
    }

    private var locatorsFromList = mutableListOf<Locator>()
    private var locatorsToList = listOf<Locator>()
    private lateinit var locatorsFromAdapter: ArrayAdapter<Locator>
    private var selectedLocatorCodeFrom: String? = null
    private lateinit var locatorsToAdapter: ArrayAdapter<Locator>
    private var selectedLocatorCodeTo: String? = null

    private fun observeGettingLocatorsList() {
        viewModel.getLocatorsListStatus.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> loadingDialog!!.show()
                Status.SUCCESS -> loadingDialog!!.hide()
                else -> {
                    loadingDialog!!.hide()
                    Tools.warningDialog(requireContext(), it.message)
                }
            }
        }
        viewModel.getLocatorsListLiveData.observe(requireActivity()) {
            if (it.isNotEmpty()) {
//                if (subInvType==TransactMoveOrderFragment.SubInvType.FROM) {
//                    locatorsFromList = it
//                    locatorsFromAdapter = ArrayAdapter(
//                        requireContext(),
//                        android.R.layout.simple_list_item_1,
//                        locatorsFromList
//                    )
//                    binding.locatorFromSpinner.setAdapter(locatorsFromAdapter)
//                } else {
                    locatorsToList = it
                    locatorsToAdapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_list_item_1,
                        locatorsToList
                    )
                    binding.locatorToSpinner.setAdapter(locatorsToAdapter)
//                }

            } else {
                Tools.warningDialog(
                    requireContext(),
                    getString(R.string.no_locators_for_this_sub_inventory)
                )
            }
        }
    }

    private fun setUpLocatorsSpinner() {
        locatorsFromAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            locatorsFromList
        )
        binding.locatorFromSpinner.setAdapter(locatorsFromAdapter)
        binding.locatorFromSpinner.setOnItemClickListener { _, _, selectedIndex, _ ->
            selectedLocatorCodeFrom = locatorsFromList[selectedIndex].locatorCode
            selectedItemData = itemData.find { it.locator == selectedLocatorCodeFrom }
            fillOnHandQty()

        }
        locatorsToAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            locatorsToList
        )
        binding.locatorToSpinner.setAdapter(locatorsToAdapter)
        binding.locatorToSpinner.setOnItemClickListener{ _, _, selectedIndex, _ ->
            selectedLocatorCodeTo = locatorsToList[selectedIndex].locatorCode
        }
    }
    


    override fun onResume() {
        super.onResume()
        changeFragmentTitle(getString(R.string.start_transfer),requireActivity())
        showBackButton(requireActivity())
//        viewModel.getTodayDate()

        binding.date.editText?.setText(viewModel.getDisplayTodayDate())
        barcodeReader.onResume()
    }



    override fun onPause() {
        super.onPause()
        barcodeReader.onPause()
    }

//    override fun onData(p0: ScanDataCollection?) {
//        requireActivity().runOnUiThread {
//            val scannedText = barcodeReader.onData(p0)
//            onBarcodeReadData(scannedText)
//            barcodeReader.restartReadData()
//        }
//    }

    private fun onBarcodeReadData(scannedText:String) {
        Log.d(TAG, "onBarcodeReadData: $scannedText")
        Log.d(TAG, "onBarcodeReadData: ${getEditTextText(binding.itemCode)}")
        if (getEditTextText(binding.itemCode).isEmpty()){
            itemCodeScanned(scannedText)
        } else  {
            if (getEditTextText(binding.locatorFrom).isEmpty())
                locatorFromCodeScanned(scannedText)
            else
                locatorToCodeScanned(scannedText)
        }
    }

    private fun itemCodeScanned(itemCode: String) {
            viewModel.getItemInfo(orgId,itemCode)
    }

    private fun locatorFromCodeScanned(locatorFrom: String) {
        if (selectedSubInventoryCodeFrom!=null){
            val locator = locatorsFromList.find { it.locatorCode == locatorFrom }
            if (locator!=null){
                selectedLocatorCodeFrom = locator.locatorCode
                binding.locatorFrom.editText?.setText(locator.locatorCode)
            } else {
                binding.locatorFromSpinner.setText("",false)
                warningDialog(requireContext(),getString(R.string.wrong_locator))
            }
        } else {
            warningDialog(requireContext(),getString(R.string.please_select_from_sub_inventory))
        }
    }
    private fun locatorToCodeScanned(locatorTo: String) {
        if (selectedSubInventoryCodeTo!=null){
            val locator = locatorsToList.find { it.locatorCode == locatorTo }
            if (locator!=null){
                selectedLocatorCodeTo = locator.locatorCode
                binding.locatorTo.editText?.setText(locator.locatorCode)
            } else {
                binding.locatorToSpinner.setText("",false)
                warningDialog(requireContext(),getString(R.string.wrong_locator))
            }
        } else {
            warningDialog(requireContext(),getString(R.string.please_select_to_sub_inventory))
        }
    }

//    override fun onStatus(p0: StatusData?) {
//        barcodeReader.onStatus(p0)
//    }

    private fun isReadyToSave(qty:String,date:String):Boolean{
        var isReady = true
        if (itemData==null){
            isReady = false
            binding.itemCode.error = getString(R.string.please_scan_or_enter_item_code)
        }
        if (selectedSubInventoryCodeFrom==null){
            isReady = false
            binding.subInventoryFrom.error = getString(R.string.please_select_from_sub_inventory)
        }
        if (selectedSubInventoryCodeTo==null){
            isReady = false
            binding.subInventoryTo.error = getString(R.string.please_select_to_sub_inventory)
        }
        if(locatorsFromList.isNotEmpty()) {
            if (selectedLocatorCodeFrom == null) {
                isReady = false
                binding.locatorFrom.error = getString(R.string.please_select_from_locator)
            }
        }
        if(locatorsToList.isNotEmpty()) {
            if (selectedLocatorCodeTo == null) {
                isReady = false
                binding.locatorTo.error = getString(R.string.please_select_to_locator)
            }
        }
        if (date.isEmpty()){
            isReady = false
            binding.date.error = getString(R.string.please_select_date)
        }
        if (selectedItemData?.mustHaveLot()!!){
            if (selectedLot==null){
                isReady = false
                binding.lotNumber.error = getString(R.string.please_select_lot)
            }
        }
        if (qty.isEmpty()){
            isReady = false
            binding.transferQty.error = getString(R.string.please_enter_qty)
        } else {
            val selectedItemData = itemData.find { it.subinventory == selectedSubInventoryCodeFrom && it.locator == selectedLocatorCodeFrom }
            Log.d(TAG, "isReadyToSave: ${selectedItemData?.onhand}")
            try{
                if (qty.toDouble()>selectedItemData?.onhand!!){
                    isReady = false
                    binding.transferQty.error =
                        getString(R.string.transfer_quantity_must_be_less_than_or_equal_to)+selectedItemData.onhand
                }
            } catch (ex:Exception){
                isReady = false
                binding.transferQty.error = getString(R.string.please_enter_valid_qty)
                Log.d(TAG, "isReadyToSave: ${ex.message}")
            }
        }
        return isReady
    }

    override fun onDataScanned(data: String) {
        val scannedText = data
        onBarcodeReadData(scannedText)
    }


}