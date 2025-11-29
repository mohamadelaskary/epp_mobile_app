package net.gbs.epp_project.Ui.Issue.EppOrganizations.TransactMoveOrderChemicalFactory

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.OnClickListener
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import net.gbs.epp_project.Base.BaseFragmentWithViewModel
import net.gbs.epp_project.Base.BundleKeys.FACTORY
import net.gbs.epp_project.Base.BundleKeys.FINAL_PRODUCT
import net.gbs.epp_project.Base.BundleKeys.ISSUE_FINAL_PRODUCT
import net.gbs.epp_project.Base.BundleKeys.MOVE_ORDER_LINE_KEY
import net.gbs.epp_project.Base.BundleKeys.MOVE_ORDER_NUMBER_KEY
import net.gbs.epp_project.Base.BundleKeys.ORGANIZATION_ID_KEY
import net.gbs.epp_project.Base.BundleKeys.RECEIVE_FINAL_PRODUCT
import net.gbs.epp_project.Base.BundleKeys.SOURCE_KEY
import net.gbs.epp_project.Base.BundleKeys.SUB_INVENTORY_FROM_CODE
import net.gbs.epp_project.Model.ApiRequestBody.AllocateItemsBody
import net.gbs.epp_project.Model.ApiRequestBody.TransactItemsBody
import net.gbs.epp_project.Model.Line
import net.gbs.epp_project.Model.Locator
import net.gbs.epp_project.Model.MoveOrder
import net.gbs.epp_project.Model.MoveOrderLine
import net.gbs.epp_project.Model.OnHandItemForAllocate
import net.gbs.epp_project.Model.Status
import net.gbs.epp_project.Model.SubInventory
import net.gbs.epp_project.R
import net.gbs.epp_project.Tools.EditTextActionHandler
import net.gbs.epp_project.Tools.Tools
import net.gbs.epp_project.Tools.Tools.attachButtonsToListener
import net.gbs.epp_project.Tools.Tools.back
import net.gbs.epp_project.Tools.Tools.clearInputLayoutError
import net.gbs.epp_project.Tools.Tools.getEditTextText
import net.gbs.epp_project.Tools.Tools.showSuccessAlerter
import net.gbs.epp_project.Tools.Tools.warningDialog
import net.gbs.epp_project.Tools.ZebraScanner
import net.gbs.epp_project.Ui.Issue.EppOrganizations.IssueReports.IssueOrderReport.IssueOrderReportDialog
import net.gbs.epp_project.Ui.Issue.EppOrganizations.IssueReports.OnHandForIssueOrderReport.OnHandIssueOrderReportDialog
import net.gbs.epp_project.Ui.Issue.EppOrganizations.TransactMoveOrderChemicalFactory.MoveOrderLinesDialog.MoveOrderLinesAdapter
import net.gbs.epp_project.Ui.Issue.EppOrganizations.TransactMoveOrderChemicalFactory.MoveOrderLinesDialog.MoveOrderLinesDialog
import net.gbs.epp_project.databinding.FragmentTransactMoveOrderBinding

class TransactMoveOrderFragment : BaseFragmentWithViewModel<TransactMoveOrderViewModel, FragmentTransactMoveOrderBinding>(),MoveOrderInfoDialog.OnInfoDialogButtonsClicked,
//    DataListener,StatusListener,
    ZebraScanner.OnDataScanned,
    OnClickListener, MoveOrderLinesAdapter.OnMoveOrderLineItemClicked{

    companion object {
        fun newInstance() = TransactMoveOrderFragment()
    }


    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTransactMoveOrderBinding
        get() = FragmentTransactMoveOrderBinding::inflate

    private lateinit var barcodeReader :ZebraScanner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private var orgId: Int = -1
    private var source: String? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        barcodeReader = ZebraScanner(requireActivity(),this)
        orgId = arguments?.getInt(ORGANIZATION_ID_KEY)!!
        source = arguments?.getString(SOURCE_KEY)

        observeGettingMoveOrdersList()
        observeGettingIssueOrderLists()

        viewModel.getSubInvertoryList(orgId)
        setUpMoveOrdersNumbersSpinner()
        setUpReportsDialogs()
        when (source) {
            FACTORY -> {
                binding.moveOrderNumber.hint = getString(R.string.move_order_number)
            }

            RECEIVE_FINAL_PRODUCT, ISSUE_FINAL_PRODUCT -> {
                binding.moveOrderNumber.hint = getString(R.string.sales_order_number)
            }

        }



        Tools.changeFragmentTitle(source!!, requireActivity())

        clearInputLayoutError(binding.subInventoryFrom,binding.moveOrderNumber, binding.subInventoryTo, binding.itemCode)
        attachButtonsToListener(
            this,
            binding.info,
            binding.allocate,
            binding.transact,
            binding.lotSerial,
            binding.showLinesListDialog,
            binding.showLinesListDialog,

        )

        setUpReportsDialogs()
//        observeGettingMoveOrder()
        observeGettingMoveOrderLines()
        observeGettingSubInventoryList()
        observeGettingLocatorsList()
        observeAllocatingTransactingItems()
        observeGettingLotList()
        observeSubinventoryOnHand()
        setUpSubInventorySpinner()
        setUpLocatorsSpinner()
        observeGettingLocatorItemsDetails()


        EditTextActionHandler.OnEnterKeyPressed(binding.itemCode) {
            val itemCode = getEditTextText(binding.itemCode)
            if (moveOrdersLines.isNotEmpty()) {
                scannedItem = moveOrdersLines.find { it.inventorYITEMCODE == itemCode }
                if (scannedItem == null) {
                    binding.onScanItemViewsGroup.visibility = GONE
                    binding.itemCode.editText?.setText("")
                    binding.itemCode.error = getString(R.string.wrong_item_code)
                    binding.lotSerial.visibility = GONE
                } else {
                    fillItemData(scannedItem!!)
                }
            } else {
                binding.onScanItemViewsGroup.visibility = GONE
                when (source) {
                    FACTORY -> warningDialog(
                        requireContext(),
                        getString(R.string.please_enter_valid_move_order_number)
                    )

                    FINAL_PRODUCT, RECEIVE_FINAL_PRODUCT, ISSUE_FINAL_PRODUCT -> warningDialog(
                        requireContext(),
                        getString(R.string.please_enter_valid_sales_order_number)
                    )

                }
            }
        }
    }
    private fun observeSubinventoryOnHand() {
       viewModel.getSubInventoryOnHandStatus.observe(viewLifecycleOwner) {
           when(it.status){
               Status.LOADING -> loadingDialog.show()
               Status.SUCCESS -> loadingDialog.dismiss()
               else -> {
                   loadingDialog.dismiss()
                   binding.subInventoryFromSpinner.setText("",false)
                   binding.subInventoryFrom.error =
                       getString(R.string.the_selected_sub_inventory_doesn_t_contain_enough_quantity_to_issue)
                   selectedLocatorFrom = null
                   binding.subInventoryFrom.editText?.isEnabled = true
               }
           }
       }
        viewModel.getSubInventoryOnHandLiveData.observe(viewLifecycleOwner) {
            if (it[0].onhand!! <= scannedItem?.quantity!!){
                binding.onHandQty.text = it[0].onhand!!.toString()
                binding.subInventoryFromSpinner.setText("",false)
                binding.subInventoryFrom.error =
                    getString(R.string.the_selected_sub_inventory_doesn_t_contain_enough_quantity_to_issue)
                selectedLocatorFrom = null
                binding.subInventoryFrom.isEnabled = true
            } else {
                selectedSubInventoryCodeFrom = SubInventory(orgId,it[0].subinventory,it[0].subinventoryDesc)
                binding.subInventoryFromSpinner.setText(selectedSubInventoryCodeFrom?.subInventoryCode,false)
                binding.onHandQty.text = it[0].onhand.toString()
                binding.subInventoryFrom.error = null
                binding.subInventoryFrom.isEnabled = false
            }
        }
    }

    private fun calculateOnHandQty(allocates: List<OnHandItemForAllocate>): Double {
        var onHandQty = 0.0
        for (allocate in allocates){
            onHandQty+=allocate.onhand!!
        }
        return onHandQty
    }


    private lateinit var ordersItemsDialog: IssueOrderReportDialog
    private lateinit var onHandItemsDialog: OnHandIssueOrderReportDialog
    private lateinit var moveOrderInfoDialog: MoveOrderInfoDialog
    private lateinit var linesDialog: MoveOrderLinesDialog
    private fun setUpReportsDialogs() {
        ordersItemsDialog   = IssueOrderReportDialog(requireContext())
        onHandItemsDialog   = OnHandIssueOrderReportDialog(requireContext())
        moveOrderInfoDialog = MoveOrderInfoDialog(requireContext(), this)
        linesDialog         = MoveOrderLinesDialog(requireContext(),this)
    }

    private fun observeGettingIssueOrderLists() {
        viewModel.getIssueOrdersListStatus.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> {
                    binding.issueOrdersListsLoading?.show()
                    binding.info.visibility = GONE
                }
                Status.SUCCESS -> {
                    binding.issueOrdersListsLoading?.hide()
                    binding.info.visibility = VISIBLE
                }
                else -> {
                    binding.issueOrdersListsLoading?.hide()
                    warningDialog(requireContext(), it.message)
                    binding.info.visibility = GONE
                }
            }
        }
        viewModel.getIssueOrdersListLiveData.observe(requireActivity()) {
            ordersItemsDialog.items = it.getMoveOrderLinesList
            onHandItemsDialog.items = it.getOnHandList
            binding.info.isEnabled = true
        }
    }

    private var moveOrdersList = listOf<MoveOrder>()
    private lateinit var moveOrdersAdapter: ArrayAdapter<MoveOrder>
    private fun setUpMoveOrdersNumbersSpinner() {
        binding.moveOrderNumberSpinner.setOnItemClickListener { _, _, position, _ ->
            selectedMoveOrder = moveOrdersList.find { it.moveOrderRequestNumber== getEditTextText(binding.moveOrderNumber) }
            binding.info.isEnabled = false
            if (moveOrdersList.isNotEmpty()){
                viewModel.getIssueOrderLists(selectedMoveOrder?.moveOrderRequestNumber!!, orgId)
                Log.d(TAG, "getMoveOrderLinesSetUpMoveOrdersNumbersSpinner: ")
                viewModel.getMoveOrderLines(selectedMoveOrder?.moveOrderHeaderId!!, orgId)
            }
            binding.transactionDate?.editText?.setText(viewModel.getDisplayTodayDate())
            binding.itemCode.editText?.setText("")
            binding.onScanItemViewsGroup.visibility = GONE
            binding.lotSerial.visibility = GONE
            binding.moveOrderNumber.editText?.clearFocus()
        }
    }

    private fun observeGettingMoveOrdersList() {
        viewModel.getMoveOrdersListStatus.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> loadingDialog.hide()
                else -> {
                    warningDialog(requireContext(), it.message)
                    loadingDialog.hide()
                }
            }
        }
        viewModel.getMoveOrdersListLiveData.observe(requireActivity()) {
            moveOrdersList = it
            moveOrdersAdapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, moveOrdersList)
            binding.moveOrderNumberSpinner.setAdapter(moveOrdersAdapter)
            if (moveOrdersList.isNotEmpty()) {
                Log.d(TAG, "getMoveOrderLinesObserveGettingMoveOrdersList: $selectedMoveOrder")
                if (selectedMoveOrder != null) {
                    val moveOrder =
                        moveOrdersList.find { it.moveOrderRequestNumber == selectedMoveOrder?.moveOrderRequestNumber }
                    if (moveOrder != null) {
                        refillMoveOrderData()
                    } else {
                        binding.moveOrderNumberSpinner.setText("",false)
                    }
                }
            } else {
                back(this)
            }
        }
    }

    private fun refillMoveOrderData() {
            binding.info.isEnabled = false
            viewModel.getIssueOrderLists(selectedMoveOrder?.moveOrderRequestNumber!!, orgId)
        Log.d(TAG, "getMoveOrderLinesRefillMoveOrderData: ")
            viewModel.getMoveOrderLines(selectedMoveOrder?.moveOrderHeaderId!!, orgId)
            binding.transactionDate?.editText?.setText(viewModel.getDisplayTodayDate())
            binding.itemCode.editText?.setText("")
            binding.onScanItemViewsGroup.visibility = GONE
            binding.lotSerial.visibility = GONE
        }

    private fun observeGettingLotList() {
        viewModel.getLotListStatus.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> loadingDialog.hide()
                else -> {
                    loadingDialog.hide()
//                    warningDialog(requireContext(), it.message)
                    binding.onScanItemViewsGroup.visibility = VISIBLE
                    binding.lotSerial.visibility = GONE
                }
            }
        }
        viewModel.getLotListLiveData.observe(requireActivity()) {
            val lotInSelectedLocator = it.filter{it.locatoRID==selectedLocatorFrom?.locatorId}
            if (lotInSelectedLocator.isNotEmpty()) {
                binding.lotSerial.visibility = VISIBLE
            } else {
                binding.lotSerial.visibility = GONE
            }
            binding.onScanItemViewsGroup.visibility = VISIBLE

        }
    }

    private fun observeAllocatingTransactingItems() {
        viewModel.allocateItemsStatus.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> loadingDialog!!.show()
                Status.SUCCESS -> {
//                    if (scannedItem?.mustHaveLot()!!){
                        val bundle = Bundle()
                        bundle.putString(
                            MOVE_ORDER_NUMBER_KEY,
                            selectedMoveOrder?.moveOrderRequestNumber!!
                        )
                        bundle.putString(MOVE_ORDER_LINE_KEY, MoveOrderLine.toJson(scannedItem!!))
                        bundle.putString(SOURCE_KEY, source)
//                    bundle.putString(LOCATOR_FROM_CODE_KEY, Locator.toJson(selectedLocatorFrom!!))
                        bundle.putString(SUB_INVENTORY_FROM_CODE, SubInventory.toJson(selectedSubInventoryCodeFrom!!))
                        bundle.putInt(ORGANIZATION_ID_KEY, orgId)
                        requireView().findNavController().navigate(
                            R.id.action_transactMoveOrderFragment_to_transactionHistoryFragment,
                            bundle
                        )

//                    } else {
//                        clearLineData()
//                        try {
//                            viewModel.getIssueOrderLists(
//                                selectedMoveOrder?.moveOrderRequestNumber!!,
//                                orgId
//                            )
//                        } catch (ex: Exception) {
//                            warningDialog(requireContext(), ex.message!!)
//                        }
//                        Log.d(TAG, "getMoveOrderLinesObserveAllocatingTransactingItems: ")
//                        viewModel.getMoveOrderLines(selectedMoveOrder?.moveOrderHeaderId!!, orgId)
////                    back(this)
//                        showSuccessAlerter(it.message, requireActivity())
//                    }
                    loadingDialog.hide()
                }

                else -> {
                    loadingDialog.hide()
                    warningDialog(requireContext(), it.message)
                }
            }
        }
    }

    private fun clearLineData() {
        binding.onScanItemViewsGroup.visibility = GONE
        binding.lotSerial.visibility = GONE
        binding.itemCode.editText?.setText("")
        binding.allocatedQty.editText?.setText("")
        binding.subInventoryFromSpinner.setText("", false)
        binding.subInventoryToSpinner.setText("", false)
        binding.locatorFromSpinner.setText("", false)
        scannedItem = null
        selectedSubInventoryCodeFrom = null
        selectedLocatorFrom = null
        selectedSubInventoryCodeTo = null
        binding.transact.visibility = GONE
    }


    private var subInventoryList = listOf<SubInventory>()
    private lateinit var subInventoryToAdapter: ArrayAdapter<SubInventory>
    private lateinit var subInventoryFromAdapter: ArrayAdapter<SubInventory>
    private var selectedSubInventoryCodeFrom: SubInventory? = null
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
                subInventoryFromAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    subInventoryList
                )
                binding.subInventoryFromSpinner.setAdapter(subInventoryFromAdapter)
                subInventoryToAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    subInventoryList
                )
                binding.subInventoryToSpinner.setAdapter(subInventoryToAdapter)
            } else {
                binding.subInventoryTo.error = getString(R.string.no_sub_inventories_for_this_org_id)
                binding.subInventoryFrom.error = getString(R.string.no_sub_inventories_for_this_org_id)
            }
        }
    }

    private fun setUpSubInventorySpinner() {
        binding.subInventoryFromSpinner.setOnItemClickListener { _, _, selectedIndex, _ ->
            selectedSubInventoryCodeFrom = subInventoryList[selectedIndex]
            viewModel.getSubInventoryOnHand(orgId, selectedSubInventoryCodeFrom?.subInventoryCode!!,scannedItem?.inventorYITEMCODE!!)
            subInvType = SubInvType.FROM
        }
        binding.subInventoryToSpinner.setOnItemClickListener { _, _, selectedIndex, _ ->
            selectedSubInventoryCodeTo = subInventoryList[selectedIndex].subInventoryCode
//            viewModel.getLocatorsList(orgId, selectedSubInventoryCodeTo!!)
            subInvType = SubInvType.To
        }
    }


    private var locatorsList = listOf<Locator>()
    private lateinit var locatorsFromAdapter: ArrayAdapter<Locator>
    private var selectedLocatorFrom: Locator? = null

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
                locatorsFromAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    locatorsList
                )
                binding.locatorFromSpinner.setAdapter(locatorsFromAdapter)

            } else {
                binding.locatorFrom.error = getString(R.string.no_locators_for_this_sub_inventory)
            }
        }
    }

    private fun setUpLocatorsSpinner() {
        binding.locatorFromSpinner.setOnItemClickListener { _, _, selectedIndex, _ ->
            selectedLocatorFrom = locatorsList[selectedIndex]
            viewModel.getOnHandLocatorDetails(orgId,scannedItem?.inventorYITEMCODE!!,selectedLocatorFrom?.locatorCode!!)
        }
    }

    private var moveOrdersLines = listOf<MoveOrderLine>()
    private fun observeGettingMoveOrderLines() {
        viewModel.getMoveOrderLinesStatus.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> loadingDialog!!.show()
                Status.SUCCESS -> loadingDialog!!.hide()
                else -> {
                    loadingDialog!!.hide()
//                    back(this)
                    warningDialog(requireContext(), it.message)
                }
            }
        }
        viewModel.getMoveOrderLinesLiveData.observe(requireActivity()) {
            if (it.isNotEmpty()) {
                moveOrdersLines = it
                linesDialog.linesList = moveOrdersLines
                binding.dataGroup.visibility = VISIBLE
            } else {
                when (source) {
                    FACTORY -> warningDialog(
                        requireContext(),
                        getString(R.string.no_lines_for_this_move_order)
                    )

                    RECEIVE_FINAL_PRODUCT, ISSUE_FINAL_PRODUCT -> warningDialog(
                        requireContext(),
                        getString(R.string.no_lines_for_this_sales_order)
                    )
                }
            }
        }
    }


    private var selectedMoveOrder: MoveOrder? = null


    override fun onResume() {
        super.onResume()
        when(source){
            FACTORY, ISSUE_FINAL_PRODUCT -> Tools.changeFragmentTitle(getString(R.string.start_issue), requireActivity())
            RECEIVE_FINAL_PRODUCT        -> Tools.changeFragmentTitle(getString(R.string.start_receiving), requireActivity())
        }

        Tools.showBackButton(requireActivity())
        when(source){
            FACTORY        -> viewModel.getMoveOrdersList_Factory(orgId)
            ISSUE_FINAL_PRODUCT,RECEIVE_FINAL_PRODUCT  -> viewModel.getMoveOrdersList_FinishProduct(orgId)
        }
        barcodeReader.onResume()


        if (viewModel.moveOrder!=null){
            selectedMoveOrder = viewModel.moveOrder
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroy: ")
        if (selectedMoveOrder!=null){
            viewModel.moveOrder = selectedMoveOrder
        }
    }

    override fun onPause() {
        super.onPause()
        barcodeReader.onPause()
    }

    private var scannedItem: MoveOrderLine? = null
    private fun fillItemData(scannedItem: MoveOrderLine) {
        if (getEditTextText(binding.itemCode).isEmpty())
            binding.itemCode.editText?.setText(scannedItem.inventorYITEMCODE)
        binding.itemDesc.text = scannedItem.inventorYITEMDESC
        binding.onHandQty.text = scannedItem.onHANDQUANTITY.toString()
        binding.onScanItemViewsGroup.visibility = VISIBLE

        if (scannedItem.froMSUBINVENTORYCODE?.isNotEmpty()!!) {
//            binding.subInventoryFromSpinner.setText(scannedItem.froMSUBINVENTORYCODE, false)
          //  binding.subInventoryFrom.isEnabled = false
//            selectedSubInventoryCodeFrom = SubInventory(orgId, scannedItem.froMSUBINVENTORYCODE)
//            viewModel.getLocatorsListByItemId(orgId, scannedItem.froMSUBINVENTORYCODE!!,scannedItem.inventorYITEMID!!)
            viewModel.getSubInventoryOnHand(orgId,scannedItem.froMSUBINVENTORYCODE!!,scannedItem.inventorYITEMCODE!!)
        }
        binding.subInventoryToSpinner.setText(scannedItem.tOSUBINVENTORYCODE, false)
        if (scannedItem.tOSUBINVENTORYCODE?.isNotEmpty()!!) {
            selectedSubInventoryCodeTo = scannedItem.tOSUBINVENTORYCODE
            binding.subInventoryTo.isEnabled = false
        } else
            binding.subInventoryTo.isEnabled = true
//        if(scannedItem.allocatedQUANTITY!=0.0){
//            binding.locatorFromSpinner.setText(scannedItem.froMLOCATORCode, false)
            binding.allocatedQty.editText?.setText(scannedItem.quantity.toString())
//        }
//        if (scannedItem.froMLOCATORCode!!.isNotEmpty()) {
//            selectedLocatorCodeFrom = scannedItem.froMLOCATORCode.toString()
//            binding.locatorFromSpinner.setText(scannedItem.froMLOCATORCode, false)
//            viewModel.getOnHandLocatorDetails(orgId,scannedItem.inventorYITEMCODE!!,scannedItem.froMLOCATORCode!!)
//        }

        //
//        if(scannedItem.mustHaveLot()){
////            binding.transact.visibility = GONE
////            binding.lotSerial.visibility = VISIBLE
//            binding.allocate.text = getString(R.string.allocate)
//        }else{
////            binding.transact.visibility = VISIBLE
////            binding.lotSerial.visibility = GONE
//            binding.allocate.text = getString(R.string.allocate_transact)
//        }
        if (scannedItem.allocatedQUANTITY==scannedItem.quantity){
            binding.transact.visibility = VISIBLE
            binding.allocate.visibility = GONE
        } else {
            binding.transact.visibility = GONE
            binding.allocate.visibility = VISIBLE
        }
    }

    private fun observeGettingLocatorItemsDetails() {
        viewModel.getLocatorDetailsListStatus.observe(viewLifecycleOwner) {
            when(it.status){
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> loadingDialog.hide()
                else -> {
                    loadingDialog.show()
                    warningDialog(requireContext(), getString(R.string.locator_quantity, it.message))
                }
            }
        }
        viewModel.getLocatorDetailsListLiveData.observe (viewLifecycleOwner){
            if(it.isNotEmpty()){
                val locatorItem = it[0]
                binding.allocatedQty.editText?.setText(
                    locatorItem.availableQty.coerceAtMost(
                        scannedItem?.remainingQty!!
                    ).toString())
                viewModel.getLotList(orgId,scannedItem?.inventorYITEMID,scannedItem?.froMSUBINVENTORYCODE!!)
            } else {
                warningDialog(requireContext(),
                    getString(R.string.this_item_is_not_located_in_selected_locator))
            }
        }
    }



    override fun onClick(v: View?) {
        when(v?.id){
            R.id.info -> moveOrderInfoDialog.show()
            R.id.allocate -> {
                if (selectedMoveOrder?.moveOrderType == "3"){
                    if (isReadyForShipping()){
                        val line = Line(
                            lineId = scannedItem?.linEID,
                            lineNumber = scannedItem?.linENUMBER,
                            fromSubInventoryCode = selectedSubInventoryCodeFrom?.subInventoryCode,
                            fromLocatorCode = selectedLocatorFrom?.locatorCode,
                        )
                        val body = AllocateItemsBody(
                            orgId = orgId,
                            lines = arrayListOf(line),
                            transaction_date = viewModel.getTodayDate()
                        )
                        viewModel.allocateItems(body)
                    }
                } else {
                    if (isReadyForTransaction()){
                        val line = Line(
                            lineId = scannedItem?.linEID,
                            lineNumber = scannedItem?.linENUMBER,
                            fromSubInventoryCode = selectedSubInventoryCodeFrom?.subInventoryCode,
                            fromLocatorCode = selectedLocatorFrom?.locatorCode,
                            toSubInventoryCode = selectedSubInventoryCodeTo,
                        )
                        val body = AllocateItemsBody(
                            orgId = orgId,
                            lines = arrayListOf(line),
                            transaction_date = viewModel.getTodayDate()
                        )
                        viewModel.allocateItems(body)
                    }
                }
            }
            R.id.transact -> {
                val bundle = Bundle()
                bundle.putString(
                    MOVE_ORDER_NUMBER_KEY,
                    selectedMoveOrder?.moveOrderRequestNumber!!
                )
                bundle.putString(MOVE_ORDER_LINE_KEY, MoveOrderLine.toJson(scannedItem!!))
                bundle.putString(SOURCE_KEY, source)
//                    bundle.putString(LOCATOR_FROM_CODE_KEY, Locator.toJson(selectedLocatorFrom!!))
                bundle.putString(SUB_INVENTORY_FROM_CODE, SubInventory.toJson(selectedSubInventoryCodeFrom!!))
                bundle.putInt(ORGANIZATION_ID_KEY, orgId)
                requireView().findNavController().navigate(
                    R.id.action_transactMoveOrderFragment_to_transactionHistoryFragment,
                    bundle
                )
            }
            R.id.lot_serial -> {
                Log.d(TAG, "onClick: remainingQuantity${scannedItem?.remainingQty}")
                if (scannedItem?.remainingQty==0.0) {
                    val bundle = Bundle()
                    bundle.putString(
                        MOVE_ORDER_NUMBER_KEY,
                        selectedMoveOrder?.moveOrderRequestNumber!!
                    )
                    Log.d(TAG, "onClick: ${SubInventory.toJson(selectedSubInventoryCodeFrom!!)}")
                    bundle.putString(SUB_INVENTORY_FROM_CODE, SubInventory.toJson(selectedSubInventoryCodeFrom!!))
                    bundle.putString(MOVE_ORDER_LINE_KEY, MoveOrderLine.toJson(scannedItem!!))
                    bundle.putString(SOURCE_KEY, source)
//                    bundle.putString(LOCATOR_FROM_CODE_KEY, Locator.toJson(selectedLocatorFrom!!))
                    bundle.putInt(ORGANIZATION_ID_KEY, orgId)
                    Navigation.findNavController(requireView()).navigate(
                        R.id.action_transactMoveOrderFragment_to_transactionHistoryFragment,
                        bundle
                    )
                } else {
                    warningDialog(requireContext(),
                        getString(R.string.please_allocate_all_quantity_first))
                }
            }
            R.id.show_lines_list_dialog -> linesDialog.show()
            R.id.clear_item -> clearLineData()
        }
    }
    
    private fun isReadyForShipping():Boolean{
        var isReady = true
        if (selectedSubInventoryCodeFrom==null){
            binding.subInventoryFrom.error = getString(R.string.please_select_from_sub_inventory)
            isReady = false
        }
//        if (locatorsList.isNotEmpty()) {
//            if (selectedLocatorFrom == null) {
//                binding.locatorFrom.error = getString(R.string.please_select_from_locator)
//                isReady = false
//            }
//        }
//        else {
//            if (!containsOnlyDigits(issueQty)){
//                binding.issueQty.error = getString(R.string.please_enter_valid_qty)
//                isReady = false
//            }
//        }
        return isReady
    }
    private fun isReadyForTransaction():Boolean{
        var isReady = true
        if (selectedSubInventoryCodeFrom==null){
            binding.subInventoryFrom.error = getString(R.string.please_select_from_sub_inventory)
            isReady = false
        }
//        if (locatorsList.isNotEmpty()) {
//            if (selectedLocatorFrom == null) {
//                binding.locatorFrom.error = getString(R.string.please_select_from_locator)
//                isReady = false
//            }
//        }
//        else {
//            if (!containsOnlyDigits(issueQty)){
//                binding.issueQty.error = getString(R.string.please_enter_valid_qty)
//                isReady = false
//            }
//        }
        if (selectedSubInventoryCodeTo==null){
            binding.subInventoryTo.error = getString(R.string.please_select_to_sub_inventory)
            isReady = false
        }
//        if (source!= FINAL_PRODUCT) {
//            if (selectedLocatorCodeFrom == null) {
//                binding.locatorFrom.error = getString(R.string.please_select_from_locator)
//                isReady = false
//            }
//        }
        return isReady
    }

    enum class SubInvType{
        FROM,To
    }

    override fun OnOrderItemsButtonClicked() {
        moveOrderInfoDialog.dismiss()
        ordersItemsDialog.show()
    }

    override fun OnOrderOnHandButtonClicked() {
        moveOrderInfoDialog.dismiss()
        onHandItemsDialog.show()
    }

    override fun onMoveOrderLineClicked(item: MoveOrderLine) {
        scannedItem = item
        fillItemData(scannedItem!!)
        linesDialog.dismiss()
    }

    override fun onDataScanned(data: String) {
        if (moveOrdersLines.isNotEmpty()) {
            val scannedText = data
            scannedItem = moveOrdersLines.find { it.inventorYITEMCODE == scannedText }
            if (scannedItem == null) {
                binding.onScanItemViewsGroup.visibility = GONE
                binding.itemCode.editText?.setText("")
                binding.itemCode.error = getString(R.string.wrong_item_code)
                binding.lotSerial.visibility = GONE
            } else {
                fillItemData(scannedItem!!)
            }
        } else {
            binding.onScanItemViewsGroup.visibility = GONE
            when (source) {
                FACTORY -> warningDialog(
                    requireContext(),
                    getString(R.string.please_enter_valid_move_order_number)
                )

                RECEIVE_FINAL_PRODUCT, ISSUE_FINAL_PRODUCT -> warningDialog(
                    requireContext(),
                    getString(R.string.please_enter_valid_sales_order_number)
                )

            }
        }
    }




}