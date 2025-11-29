package net.gbs.epp_project.Ui.Return.ReturnToWarehouse

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter

import net.gbs.epp_project.Base.BaseFragmentWithViewModel
import net.gbs.epp_project.Base.BundleKeys
import net.gbs.epp_project.Base.BundleKeys.SOURCE_KEY
import net.gbs.epp_project.Model.ApiRequestBody.ReturnToWarehouseItemsBody
import net.gbs.epp_project.Model.Locator
import net.gbs.epp_project.Model.Lot
import net.gbs.epp_project.Model.ReturnToWarehouseLine
import net.gbs.epp_project.Model.ReturnWorkOrder
import net.gbs.epp_project.Model.ReturnWorkOrderLine
import net.gbs.epp_project.Model.Status
import net.gbs.epp_project.Model.SubInventory
import net.gbs.epp_project.R
import net.gbs.epp_project.Tools.EditTextActionHandler
import net.gbs.epp_project.Tools.Tools
import net.gbs.epp_project.Tools.Tools.attachButtonsToListener
import net.gbs.epp_project.Tools.Tools.clearInputLayoutError
import net.gbs.epp_project.Tools.Tools.getEditTextText
import net.gbs.epp_project.Tools.Tools.showSuccessAlerter
import net.gbs.epp_project.Tools.Tools.warningDialog
import net.gbs.epp_project.Ui.Issue.EppOrganizations.TransactMoveOrderChemicalFactory.MoveOrderInfoDialog
import net.gbs.epp_project.Ui.Issue.EppOrganizations.TransactMoveOrderChemicalFactory.MoveOrderLinesDialog.ReturnWorkOrderLinesAdapter
import net.gbs.epp_project.Ui.Issue.EppOrganizations.TransactMoveOrderChemicalFactory.MoveOrderLinesDialog.ReturnWorkOrderLinesDialog
import net.gbs.epp_project.databinding.FragmentReturnToWarehouseBinding
import net.gbs.epp_project.Tools.ZebraScanner
class ReturnToWarehouseFragment : BaseFragmentWithViewModel<ReturnToWarehouseViewModel,FragmentReturnToWarehouseBinding>(),
//    Scanner.DataListener, Scanner.StatusListener,
    ReturnWorkOrderLinesAdapter.OnWorkOrderLineItemClicked,
    ZebraScanner.OnDataScanned,
    View.OnClickListener {

    companion object {
        fun newInstance() = ReturnToWarehouseFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentReturnToWarehouseBinding
        get() = FragmentReturnToWarehouseBinding::inflate

    private lateinit var barcodeReader :ZebraScanner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    private var orgId: Int = -1
    private var source:String? =null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        barcodeReader = ZebraScanner(requireActivity(),this)
        orgId = arguments?.getInt(BundleKeys.ORGANIZATION_ID_KEY)!!
        source = arguments?.getString(SOURCE_KEY)
        setUpMoveOrdersNumbersSpinner()
//        observeGettingIssueOrderLists()
        observeGettingWorkOrdersList()
        observeGettingLotList()
        setUpLotSpinner()





        clearInputLayoutError(binding.lotNum,binding.workOrderNumber, binding.subInventoryTo, binding.itemCode, binding.locatorTo)
        attachButtonsToListener(
            this,
            binding.info,
            binding.doReturn,
            binding.clearItem,
            binding.showLinesListDialog,
            binding.showLinesListDialog,
        )
        setUpReportsDialogs()
        observeGettingMoveOrderLines()
        observeGettingSubInventoryList()
        observeGettingLocatorsList()
        observeAllocatingTransactingItems()
        setUpSubInventorySpinner()
        setUpLocatorsSpinner()


        EditTextActionHandler.OnEnterKeyPressed(binding.itemCode) {
            val itemCode = getEditTextText(binding.itemCode)
            if (moveOrdersLines.isNotEmpty()) {
                scannedItem = moveOrdersLines.find { it.inventorYITEMCODE == itemCode }
                if (scannedItem != null) {
                    fillItemData(scannedItem!!)
                } else {
                    binding.onScanItemViewsGroup.visibility = GONE
                    binding.itemCode.error = getString(R.string.wrong_item_code)
                }
            } else {
                binding.onScanItemViewsGroup.visibility = GONE
                warningDialog(
                    requireContext(),
                    getString(R.string.please_enter_valid_work_order_number)
                )

            }
        }
    }

    private var lotList = listOf<Lot>()
    private lateinit var lotAdapter: ArrayAdapter<Lot>
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
            Log.d(ContentValues.TAG, "observeGettingLotList: ${it.size}")
            lotList = it
            lotAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,lotList)
            binding.lotNumberSpinner.setAdapter(lotAdapter)
        }
    }


    private lateinit var moveOrderInfoDialog: MoveOrderInfoDialog
    private lateinit var linesDialog: ReturnWorkOrderLinesDialog
    private fun setUpReportsDialogs() {

        linesDialog         = ReturnWorkOrderLinesDialog(requireContext(),this)
    }
    private var selectedLot:Lot? = null
    private fun setUpLotSpinner() {
        binding.lotNumberSpinner.setOnItemClickListener { _, _, selectedPosition, _ ->
            selectedLot = lotList[selectedPosition]
        }
    }

//    private fun observeGettingIssueOrderLists() {
//        viewModel.getIssueOrdersListStatus.observe(requireActivity()) {
//            when (it.status) {
//                Status.LOADING -> {
//                    binding.issueOrdersListsLoading?.show()
//                    binding.info.visibility = GONE
//                }
//                Status.SUCCESS -> {
//                    binding.issueOrdersListsLoading?.hide()
//                    binding.info.visibility = VISIBLE
//                }
//                else -> {
//                    binding.issueOrdersListsLoading?.hide()
//                    Tools.warningDialog(requireContext(), it.message)
//                    binding.info.visibility = GONE
//                }
//            }
//        }
//        viewModel.getIssueOrdersListLiveData.observe(requireActivity()) {
//            ordersItemsDialog.items = it.getMoveOrderLinesList
//            onHandItemsDialog.items = it.getOnHandList
//            binding.info.isEnabled = true
//        }
//    }

    private var workOrdersList = mutableListOf<ReturnWorkOrder>()
    private lateinit var moveOrdersAdapter: ArrayAdapter<ReturnWorkOrder>
    private fun setUpMoveOrdersNumbersSpinner() {
        binding.workOrderNumberSpinner?.setOnItemClickListener { _, _, position, _ ->
            selectedMoveOrder = workOrdersList.find { it.workOrderName== getEditTextText(binding.workOrderNumber) }
            binding.info.isEnabled = false
//            viewModel.getIssueOrderLists(selectedMoveOrder?.moveOrderRequestNumber!!, orgId)
            viewModel.getReturnWorkOrderLines(selectedMoveOrder?.workOrderName!!, orgId)

            binding.itemCode.editText?.setText("")
            binding.onScanItemViewsGroup.visibility = GONE
        }
    }

    private fun observeGettingWorkOrdersList() {
        viewModel.getWorkOrdersListStatus.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> loadingDialog!!.show()
                Status.SUCCESS -> loadingDialog!!.hide()
                else -> {
                    warningDialog(requireContext(), it.message)
                    loadingDialog!!.hide()
                }
            }
        }
        viewModel.getWorkOrdersListLiveData.observe(requireActivity()) {
            it.forEach { returnWorkOrder ->
                val workOrder = workOrdersList.find { it.workOrderName==returnWorkOrder.workOrderName }
                if (workOrder==null)
                    workOrdersList.add(returnWorkOrder)
            }
            moveOrdersAdapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, workOrdersList)
            binding.workOrderNumberSpinner.setAdapter(moveOrdersAdapter)

//            refillMoveOrderData()
        }
    }

    private fun refillMoveOrderData() {
        val moveOrderNumber = getEditTextText(binding.workOrderNumber)
        if (moveOrderNumber.isNotEmpty()) {
            selectedMoveOrder = workOrdersList.find { it.workOrderName==moveOrderNumber}
            binding.info.isEnabled = false
//            viewModel.getIssueOrderLists(selectedMoveOrder?.moveOrderRequestNumber!!, orgId)
            viewModel.getReturnWorkOrderLines(selectedMoveOrder?.workOrderName!!, orgId)
            binding.itemCode.editText?.setText("")
            binding.onScanItemViewsGroup.visibility = GONE
        }
    }



    private fun observeAllocatingTransactingItems() {
        viewModel.allocateItemsStatus.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> loadingDialog!!.show()
                Status.SUCCESS -> {
                    loadingDialog!!.hide()
                    clearLineData()
//                    back(this)
                    viewModel.getReturnWorkOrderLines(selectedMoveOrder?.workOrderName!!, orgId)
//                    viewModel.getIssueOrderLists(selectedMoveOrder?.moveOrderRequestNumber!!,orgId)
                    showSuccessAlerter(it.message, requireActivity())
                }

                else -> {
                    loadingDialog!!.hide()
                    warningDialog(requireContext(), it.message)
                }
            }
        }
    }

    private fun clearLineData() {
        binding.onScanItemViewsGroup.visibility = GONE
        binding.itemCode.editText?.setText("")
        binding.allocatedQty.editText?.setText("")
        binding.subInventoryToSpinner.setText("", false)
        scannedItem = null
        selectedLocatorCodeTo = null
        selectedSubInventoryCodeTo = null
    }


    private var subInventoryList = listOf<SubInventory>()
    private lateinit var subInventoryToAdapter: ArrayAdapter<SubInventory>
    private var selectedSubInventoryCodeTo: String? = null
    private var subInvType: SubInvType = SubInvType.FROM
    private fun observeGettingSubInventoryList() {
        viewModel.getSubInvertoryListStatus.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> loadingDialog!!.show()
                Status.SUCCESS -> loadingDialog!!.hide()
                else -> {
                    loadingDialog!!.hide()
                    warningDialog(requireContext(), it.message)
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
                warningDialog(
                    requireContext(),
                    getString(R.string.no_sub_inventories_for_this_org_id)
                )
            }
        }
    }

    private fun setUpSubInventorySpinner() {
        binding.subInventoryToSpinner.setOnItemClickListener { _, _, selectedIndex, _ ->
            selectedSubInventoryCodeTo = subInventoryList[selectedIndex].subInventoryCode
            viewModel.getLocatorsList(orgId, selectedSubInventoryCodeTo!!,scannedItem?.inventorYITEMID!!)
        }
    }


    private var locatorsList = listOf<Locator>()
    private lateinit var locatorsToAdapter: ArrayAdapter<Locator>
    private var selectedLocatorCodeTo: String? = null

    private fun observeGettingLocatorsList() {
        viewModel.getLocatorsListStatus.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> loadingDialog!!.show()
                Status.SUCCESS -> loadingDialog!!.hide()
                else -> {
                    loadingDialog!!.hide()
//                    warningDialog(requireContext(), it.message)
                }
            }
        }
        viewModel.getLocatorsListLiveData.observe(requireActivity()) {
            if (it.isNotEmpty()) {
                locatorsList = it

                locatorsToAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    locatorsList
                )
                binding.locatorToSpinner.setAdapter(locatorsToAdapter)

            } else {
                warningDialog(
                    requireContext(),
                    getString(R.string.no_locators_for_this_sub_inventory)
                )
            }
        }
    }

    private fun setUpLocatorsSpinner() {
        binding.locatorToSpinner.setOnItemClickListener { _, _, selectedIndex, _ ->
            selectedLocatorCodeTo = locatorsList[selectedIndex].locatorCode
        }
    }

    private var moveOrdersLines = listOf<ReturnWorkOrderLine>()
    private fun observeGettingMoveOrderLines() {
        viewModel.getReturnWorkOrderLinesStatus.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> loadingDialog!!.show()
                Status.SUCCESS -> loadingDialog!!.hide()
                else -> {
                    loadingDialog!!.hide()
                    warningDialog(requireContext(), it.message)
                }
            }
        }
        viewModel.getReturnWorkOrderLinesLiveData.observe(requireActivity()) {
            if (it.isNotEmpty()) {
                moveOrdersLines = it
                linesDialog.linesList = moveOrdersLines
                binding.dataGroup.visibility = VISIBLE
            } else {
                warningDialog(
                    requireContext(),
                    getString(R.string.no_lines_for_this_job_order)
                )

            }
        }
    }


    private var selectedMoveOrder: ReturnWorkOrder? = null


    override fun onResume() {
        super.onResume()
        Tools.changeFragmentTitle(getString(R.string.return_to_warehouse), requireActivity())
        Tools.showBackButton(requireActivity())
        viewModel.getWorkOrdersList(orgId)
        viewModel.getSubInvertoryList(orgId)
        binding.transactionDate.editText?.setText(viewModel.getDisplayTodayDate())
        barcodeReader.onResume()
    }

    override fun onPause() {
        super.onPause()
        barcodeReader.onPause()
    }

    private var scannedItem: ReturnWorkOrderLine? = null


    private fun fillItemData(scannedItem: ReturnWorkOrderLine) {
        if (getEditTextText(binding.itemCode).isEmpty())
            binding.itemCode.editText?.setText(scannedItem.inventorYITEMCODE)
        binding.itemDesc.text = scannedItem.inventorYITEMDESC
        binding.onHandQty.text = scannedItem.onHANDQUANTITY.toString()
        binding.onScanItemViewsGroup.visibility = VISIBLE
        binding.dateEditText.setText(viewModel.getDisplayTodayDate())

        val allocatedQty = scannedItem.quantity.toString()
        if (allocatedQty.isNotEmpty()) {
//            binding.allocatedQty.isEnabled = false
            binding.allocatedQty.editText?.setText(allocatedQty)
        }
        if (scannedItem.mustHaveLot()){
            viewModel.getLotList(orgId,scannedItem.inventorYITEMID,null)
            binding.lotNum.visibility = VISIBLE
        } else {
            binding.lotNum.visibility = GONE
        }
    }



    override fun onClick(v: View?) {
        when(v?.id){
            R.id.info -> moveOrderInfoDialog.show()
            R.id.do_return -> {
                if (isReadyForTransaction()) {
                    val body = ReturnToWarehouseItemsBody(
                        org_id = orgId,
                        transaction_date = viewModel.getTodayDate(),
                        lines = listOf(
                            ReturnToWarehouseLine(
                                linE_NUMBER = scannedItem?.linENUMBER!!,
                                line_id = scannedItem?.linEID!!,
                                locatoR_CODE = selectedLocatorCodeTo!!,
                                qty = scannedItem?.quantity!!,
                                subinventorY_CODE = selectedSubInventoryCodeTo!!
                            )
                        )
                    )
                    viewModel.returnItems(body)
                }
            }
            R.id.show_lines_list_dialog -> linesDialog.show()
            R.id.clear_item -> clearLineData()
        }
    }

    private fun isReadyForTransaction():Boolean{
        var isReady = true
        val issueQty = getEditTextText(binding.allocatedQty)
        if(issueQty.isEmpty()){
            binding.allocatedQty.error = getString(R.string.please_enter_qty)
            isReady = false
        }
        else {
            try{
                issueQty.toDouble()
            } catch (ex:Exception){
                binding.allocatedQty.error = getString(R.string.please_enter_qty)
                isReady = false
            }
        }
        if (selectedSubInventoryCodeTo==null){
            binding.subInventoryTo.error = getString(R.string.please_select_to_sub_inventory)
            isReady = false
        }
        if (selectedLocatorCodeTo==null){
            binding.locatorTo.error = getString(R.string.please_select_to_locator)
            isReady = false
        }
        if (scannedItem?.mustHaveLot()!!){
            if (selectedLot==null){
                binding.lotNum.error = getString(R.string.please_select_lot)
                isReady = false
            }

        }
        return isReady
    }

    enum class SubInvType{
        FROM,To
    }


    override fun onDataScanned(data: String) {
        if (moveOrdersLines.isNotEmpty()) {
            val scannedText = data
            if (getEditTextText(binding.itemCode).isEmpty()) {
                scannedItem = moveOrdersLines.find { it.inventorYITEMCODE == scannedText }
                if (scannedItem != null) {
                    fillItemData(scannedItem!!)
                } else {
                    binding.onScanItemViewsGroup.visibility = GONE
                    binding.itemCode.error = getString(R.string.wrong_item_code)
                }
            } else {
                val locator = locatorsList.find { it.locatorCode == data }
                if (locator!=null){
                    selectedLocatorCodeTo = locator.locatorCode
                    binding.locatorToSpinner.setText(selectedLocatorCodeTo,false)
                }else binding.locatorTo.error = getString(R.string.wrong_locator)
            }
        } else {
            binding.onScanItemViewsGroup.visibility = GONE
            warningDialog(
                requireContext(),
                getString(R.string.please_enter_valid_work_order_number)
            )

        }
    }

    override fun onWorkOrderLineClicked(item: ReturnWorkOrderLine) {
        scannedItem = item
        fillItemData(scannedItem!!)
        linesDialog.dismiss()
    }


}