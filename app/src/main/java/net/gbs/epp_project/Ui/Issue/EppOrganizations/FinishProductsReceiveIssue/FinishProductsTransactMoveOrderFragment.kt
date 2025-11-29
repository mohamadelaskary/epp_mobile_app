package net.gbs.epp_project.Ui.Issue.EppOrganizations.FinishProductsReceiveIssue

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

import net.gbs.epp_project.Base.BaseFragmentWithViewModel
import net.gbs.epp_project.Base.BundleKeys.FACTORY
import net.gbs.epp_project.Base.BundleKeys.FINAL_PRODUCT
import net.gbs.epp_project.Base.BundleKeys.ISSUE_FINAL_PRODUCT
import net.gbs.epp_project.Base.BundleKeys.MOVE_ORDER_LINE_KEY
import net.gbs.epp_project.Base.BundleKeys.MOVE_ORDER_NUMBER_KEY
import net.gbs.epp_project.Base.BundleKeys.ORGANIZATION_ID_KEY
import net.gbs.epp_project.Base.BundleKeys.RECEIVE_FINAL_PRODUCT
import net.gbs.epp_project.Base.BundleKeys.SOURCE_KEY
import net.gbs.epp_project.Model.ApiRequestBody.AllocateItemsBody
import net.gbs.epp_project.Model.ApiRequestBody.TransactItemsBody
import net.gbs.epp_project.Model.Line
import net.gbs.epp_project.Model.MoveOrder
import net.gbs.epp_project.Model.MoveOrderLine
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
import net.gbs.epp_project.Ui.Issue.EppOrganizations.IssueReports.IssueOrderReport.IssueOrderReportDialog
import net.gbs.epp_project.Ui.Issue.EppOrganizations.IssueReports.OnHandForIssueOrderReport.OnHandIssueOrderReportDialog
import net.gbs.epp_project.Ui.Issue.EppOrganizations.TransactMoveOrderChemicalFactory.MoveOrderInfoDialog
import net.gbs.epp_project.Ui.Issue.EppOrganizations.TransactMoveOrderChemicalFactory.MoveOrderLinesDialog.MoveOrderLinesAdapter
import net.gbs.epp_project.Ui.Issue.EppOrganizations.TransactMoveOrderChemicalFactory.MoveOrderLinesDialog.MoveOrderLinesDialog
import net.gbs.epp_project.Ui.Issue.EppOrganizations.TransactMoveOrderChemicalFactory.TransactMoveOrderViewModel
import net.gbs.epp_project.databinding.FragmentTransactFinishProductsMoveOrderBinding
import net.gbs.epp_project.Tools.ZebraScanner
class FinishProductsTransactMoveOrderFragment : BaseFragmentWithViewModel<TransactMoveOrderViewModel, FragmentTransactFinishProductsMoveOrderBinding>(),MoveOrderInfoDialog.OnInfoDialogButtonsClicked,
//    DataListener,StatusListener,
    ZebraScanner.OnDataScanned,
    OnClickListener, MoveOrderLinesAdapter.OnMoveOrderLineItemClicked {

    companion object {
        fun newInstance() = FinishProductsTransactMoveOrderFragment()
    }


    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTransactFinishProductsMoveOrderBinding
        get() = FragmentTransactFinishProductsMoveOrderBinding::inflate

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
        setUpMoveOrdersNumbersSpinner()
        observeGettingMoveOrdersList()
        observeGettingIssueOrderLists()
        viewModel.getSubInvertoryList(orgId)
        when (source) {
            FACTORY -> {
                binding.moveOrderNumber.hint = getString(R.string.move_order_number)
            }

            RECEIVE_FINAL_PRODUCT, ISSUE_FINAL_PRODUCT -> {
                binding.moveOrderNumber.hint = getString(R.string.sales_order_number)
                binding.transact.visibility = GONE
            }

        }




        Tools.changeFragmentTitle(source!!, requireActivity())
        clearInputLayoutError(binding.moveOrderNumber, binding.subInventoryTo, binding.itemCode)
        attachButtonsToListener(
            this,
            binding.info,
            binding.allocate,
            binding.transact,
            binding.lotSerial,
            binding.showLinesListDialog,
            binding.showLinesListDialog,
            binding.clearItem!!
        )

        observeGettingMoveOrderLines()
        observeGettingSubInventoryList()
        observeAllocatingTransactingItems()
        observeGettingLotList()
        setUpSubInventorySpinner()
        setUpReportsDialogs()
        setUpMoveOrdersNumbersSpinner()

        EditTextActionHandler.OnEnterKeyPressed(binding.itemCode) {
            val itemCode = getEditTextText(binding.itemCode)
            if (moveOrdersLines.isNotEmpty()) {
                scannedItem = moveOrdersLines.find { it.inventorYITEMCODE == itemCode }
                if (scannedItem != null) {
                    viewModel.getLotList(orgId,scannedItem?.inventorYITEMID,scannedItem?.froMSUBINVENTORYCODE!!)
                } else {
                    binding.onScanItemViewsGroup.visibility = GONE
                    binding.itemCode.editText?.setText("")
                    binding.itemCode.error = getString(R.string.wrong_item_code)
                    binding.lotSerial.visibility = GONE

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
                    Tools.warningDialog(requireContext(), it.message)
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
            if (moveOrdersList.isNotEmpty()) {
                viewModel.getIssueOrderLists(selectedMoveOrder?.moveOrderRequestNumber!!, orgId)
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
                Status.LOADING -> loadingDialog!!.show()
                Status.SUCCESS -> loadingDialog!!.hide()
                else -> {
                    warningDialog(requireContext(), it.message)
                    loadingDialog!!.hide()
                }
            }
        }
        viewModel.getMoveOrdersListLiveData.observe(requireActivity()) {
            moveOrdersList = it
            moveOrdersAdapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, moveOrdersList)
            binding.moveOrderNumberSpinner.setAdapter(moveOrdersAdapter)
            if (moveOrdersList.isNotEmpty()) {
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
            viewModel.getMoveOrderLines(selectedMoveOrder?.moveOrderHeaderId!!, orgId)
            binding.itemCode.editText?.setText("")
            binding.onScanItemViewsGroup.visibility = GONE
            binding.lotSerial.visibility = GONE

    }

    private fun observeGettingLotList() {
        viewModel.getLotListStatus.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> loadingDialog!!.show()
                Status.SUCCESS -> loadingDialog!!.hide()
                else -> {
                    loadingDialog!!.hide()
//                    warningDialog(requireContext(), it.message)
                    binding.onScanItemViewsGroup.visibility = VISIBLE
                    fillItemData(scannedItem!!)
                    binding.lotSerial.visibility = GONE
                }
            }
        }
        viewModel.getLotListLiveData.observe(requireActivity()) {
            if (it.isNotEmpty()) {
                binding.lotSerial.visibility = VISIBLE
            } else {
                binding.lotSerial.visibility = GONE
            }
            binding.onScanItemViewsGroup.visibility = VISIBLE
            fillItemData(scannedItem!!)
        }
    }

    private fun observeAllocatingTransactingItems() {
        viewModel.allocateItemsStatus.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> loadingDialog!!.show()
                Status.SUCCESS -> {
                    loadingDialog!!.hide()
                    clearLineData()
                    try {
                        viewModel.getIssueOrderLists(
                            selectedMoveOrder?.moveOrderRequestNumber!!,
                            orgId
                        )
                    } catch (ex:Exception){
                        warningDialog(requireContext(),ex.message!!)
                    }
                    viewModel.getMoveOrderLines(selectedMoveOrder?.moveOrderHeaderId!!, orgId)
//                    back(this)
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
        binding.lotSerial.visibility = GONE
        binding.itemCode.editText?.setText("")
        binding.allocatedQty.editText?.setText("")
        binding.subInventoryFromSpinner.setText("", false)
        binding.subInventoryToSpinner.setText("", false)
        scannedItem = null
        selectedSubInventoryCodeFrom = null
        selectedSubInventoryCodeTo = null
        binding.transact.visibility = GONE
    }


    private var subInventoryList = listOf<SubInventory>()
    private lateinit var subInventoryToAdapter: ArrayAdapter<SubInventory>
    private lateinit var subInventoryFromAdapter: ArrayAdapter<SubInventory>
    private var selectedSubInventoryCodeFrom: String? = null
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
            selectedSubInventoryCodeFrom = subInventoryList[selectedIndex].subInventoryCode
            viewModel.getLocatorsList(orgId, selectedSubInventoryCodeFrom!!,scannedItem?.inventorYITEMID!!)
            subInvType = SubInvType.FROM
        }
        binding.subInventoryToSpinner.setOnItemClickListener { _, _, selectedIndex, _ ->
            selectedSubInventoryCodeTo = subInventoryList[selectedIndex].subInventoryCode
//            viewModel.getLocatorsList(orgId, selectedSubInventoryCodeTo!!)
            subInvType = SubInvType.To
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
            Log.d(TAG, "MOVE_ORDERS_LINESobserveGettingMoveOrderLines: ")
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
        if (source == ISSUE_FINAL_PRODUCT || source == RECEIVE_FINAL_PRODUCT) {
            binding.transact.visibility = GONE
        } else {
            binding.transact.visibility = VISIBLE
        }
        if (scannedItem.froMSUBINVENTORYCODE?.isNotEmpty()!!) {
            binding.subInventoryFromSpinner.setText(scannedItem.froMSUBINVENTORYCODE, false)
            binding.subInventoryFrom.isEnabled = false
            selectedSubInventoryCodeFrom = scannedItem.froMSUBINVENTORYCODE
            viewModel.getLocatorsList(orgId, scannedItem.froMSUBINVENTORYCODE!!,scannedItem.inventorYITEMID!!)
        }
        if (scannedItem.tOSUBINVENTORYCODE?.isNotEmpty()!!) {
            binding.subInventoryToSpinner.setText(scannedItem.tOSUBINVENTORYCODE, false)
            selectedSubInventoryCodeTo = scannedItem.tOSUBINVENTORYCODE
            binding.subInventoryTo.isEnabled = false
        }


        val allocatedQty = scannedItem.quantity.toString()
        binding.allocatedQty.editText?.setText(allocatedQty)

        if(scannedItem.mustHaveLot()){
            binding.transact.visibility = GONE
            binding.lotSerial.visibility = VISIBLE
        }else{
            binding.transact.visibility = VISIBLE
            binding.lotSerial.visibility = GONE
        }

    }


//        if (source.equals(INDIRECT_CHEMICALS)){
////            binding.issueTypeGroup.visibility = GONE
////            binding.allocateGroup.visibility = GONE
//            binding.transact.visibility = VISIBLE
//            binding.lotSerial.visibility = VISIBLE
//        } else {
//            binding.issueTypeGroup.visibility = VISIBLE
//            binding.transact.visibility = GONE
//            binding.lotSerial.visibility = GONE
//        }

//
//    override fun onStatus(statusData: StatusData) {
//       barcodeReader.onStatus(statusData)
//    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.info -> moveOrderInfoDialog.show()
            R.id.allocate -> {
                if (selectedMoveOrder?.moveOrderType == "3"){
                    if (isReadyForShipping()){
                        val line = Line(
                            lineId = scannedItem?.linEID,
                            lineNumber = scannedItem?.linENUMBER,
                            fromSubInventoryCode = selectedSubInventoryCodeFrom,
                            fromLocatorCode = null,
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
                            fromSubInventoryCode = selectedSubInventoryCodeFrom,
                            fromLocatorCode = null,
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
                val body = TransactItemsBody(
                    orgId = orgId,
                    lineId = scannedItem?.linEID,
                    lineNumber = scannedItem?.linENUMBER,
                    transaction_date = viewModel.getTodayDate()
                )
                viewModel.transactItems(body)
                Log.d(TAG, "onClick: Transact")
            }
            R.id.lot_serial -> {
                val bundle = Bundle()
                bundle.putString(MOVE_ORDER_NUMBER_KEY,selectedMoveOrder?.moveOrderRequestNumber!!)
                bundle.putString(MOVE_ORDER_LINE_KEY,MoveOrderLine.toJson(scannedItem!!))
                bundle.putString(SOURCE_KEY,source)
                bundle.putInt(ORGANIZATION_ID_KEY,orgId)
                Navigation.findNavController(requireView()).navigate(R.id.action_finishProductsTransactMoveOrderFragment_to_transactionHistoryFragment,bundle)
                Log.d(TAG, "onClick: lot_serial")
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

        return isReady
    }
    private fun isReadyForTransaction():Boolean{
        var isReady = true
        if (selectedSubInventoryCodeFrom==null){
            binding.subInventoryFrom.error = getString(R.string.please_select_from_sub_inventory)
            isReady = false
        }


        if (selectedSubInventoryCodeTo==null){
            binding.subInventoryTo.error = getString(R.string.please_select_to_sub_inventory)
            isReady = false
        }

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
        viewModel.getLotList(orgId,item.inventorYITEMID,item.froMSUBINVENTORYCODE!!)
        linesDialog.dismiss()
    }

    override fun onDataScanned(data: String) {
        if (moveOrdersLines.isNotEmpty()) {
            val scannedText = data
            scannedItem = moveOrdersLines.find { it.inventorYITEMCODE == scannedText }
            if (scannedItem != null) {
                viewModel.getLotList(orgId,scannedItem?.inventorYITEMID,scannedItem?.froMSUBINVENTORYCODE!!)
            } else {
                binding.onScanItemViewsGroup.visibility = GONE
                binding.itemCode.editText?.setText("")
                binding.itemCode.error = getString(R.string.wrong_item_code)
                binding.lotSerial.visibility = GONE

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