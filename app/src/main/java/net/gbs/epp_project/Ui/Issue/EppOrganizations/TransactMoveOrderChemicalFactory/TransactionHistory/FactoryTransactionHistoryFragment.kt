package net.gbs.epp_project.Ui.Issue.EppOrganizations.TransactMoveOrderChemicalFactory.TransactionHistory

import android.content.ContentValues.TAG
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import net.gbs.epp_project.Base.BaseFragmentWithViewModel
import net.gbs.epp_project.Base.BundleKeys.MOVE_ORDER_LINE_KEY
import net.gbs.epp_project.Base.BundleKeys.MOVE_ORDER_NUMBER_KEY
import net.gbs.epp_project.Base.BundleKeys.ORGANIZATION_ID_KEY
import net.gbs.epp_project.Base.BundleKeys.SOURCE_KEY
import net.gbs.epp_project.Base.BundleKeys.SUB_INVENTORY_FROM_CODE
import net.gbs.epp_project.Model.ApiRequestBody.TransactMultiItemsBody
import net.gbs.epp_project.Model.Locator
import net.gbs.epp_project.Model.Lot
import net.gbs.epp_project.Model.LotQty
import net.gbs.epp_project.Model.MoveOrderLine
import net.gbs.epp_project.Model.Status
import net.gbs.epp_project.Model.SubInventory
import net.gbs.epp_project.Model.TransactMultiLine
import net.gbs.epp_project.R
import net.gbs.epp_project.Tools.Tools
import net.gbs.epp_project.Tools.Tools.back
import net.gbs.epp_project.Tools.Tools.clearInputLayoutError
import net.gbs.epp_project.Tools.Tools.getEditTextText
import net.gbs.epp_project.Tools.Tools.showSuccessAlerter
import net.gbs.epp_project.Tools.Tools.warningDialog
import net.gbs.epp_project.Ui.Issue.EppOrganizations.TransactMoveOrderChemicalFactory.LocatorLotQtyAdapter
import net.gbs.epp_project.Ui.Issue.EppOrganizations.TransactMoveOrderChemicalFactory.TransactionHistory.AddLotsBottomSheet.AddLotsBottomSheet
import net.gbs.epp_project.databinding.FragmentFactoryTransactionHistoryBinding

class FactoryTransactionHistoryFragment : BaseFragmentWithViewModel<TransactionHistoryViewModel, FragmentFactoryTransactionHistoryBinding>() {

    companion object {
        fun newInstance() = FactoryTransactionHistoryFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentFactoryTransactionHistoryBinding
        get() = FragmentFactoryTransactionHistoryBinding::inflate

    private lateinit var moveOrderLine: MoveOrderLine
    private var moveOrderNumber : String = ""
    private var orgId : Int = -1
    private var source: String? = null
    private var subInventoryFrom : SubInventory? = null
    private lateinit var addLotsBottomSheet: AddLotsBottomSheet
    private lateinit var transactMultiLine : TransactMultiLine
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        moveOrderLine = MoveOrderLine.fromJson(arguments?.getString(MOVE_ORDER_LINE_KEY)!!)
        moveOrderNumber = arguments?.getString(MOVE_ORDER_NUMBER_KEY)!!
        orgId           = arguments?.getInt(ORGANIZATION_ID_KEY)!!
        source          = arguments?.getString(SOURCE_KEY)
        subInventoryFrom= SubInventory.fromJson(arguments?.getString(SUB_INVENTORY_FROM_CODE)!!)
        viewModel.getLocatorsListByItemId(orgId,subInventoryFrom?.subInventoryCode!!,moveOrderLine.inventorYITEMID!!)
        addLotsBottomSheet = AddLotsBottomSheet(context = requireContext(), onSaveButtonClicked = object :
            AddLotsBottomSheet.OnSaveButtonClicked{
            override fun onSaveButtonClicked(lotQtyList: List<LotQty>) {
                val transactMultiLine = TransactMultiLine(
                    lineId = moveOrderLine.linEID,
                    linENUMBER = moveOrderLine.linENUMBER,
                    lots = ArrayList(lotQtyList),
                    quantity = getEditTextText(binding.qtyIssuedFromLocator).toDouble(),
                    froMLOCATORCode = selectedLocator?.locatorCode
                )
                addLotsBottomSheet.dismiss()
//                addedLines.add(transactMultiLine)
//                locatorLotQtyAdapter.notifyItemInserted(addedLines.size)
                binding.locatorsSpinner.setText("",false)
                selectedLocator = null
                binding.qtyIssuedFromLocator.editText?.setText("")
                binding.remainingQty.text = calculateRemainingQty().toString()

            }

        })
//        locatorFrom     = Locator.fromJson(arguments?.getString(LOCATOR_FROM_CODE_KEY)!!)
//        when(source) {
//            FACTORY, RECEIVE_FINAL_PRODUCT, ISSUE_FINAL_PRODUCT -> binding.moveOrderNumberLabel.text = getString(R.string.move_order_number)
//            INDIRECT_CHEMICALS     -> binding.moveOrderNumberLabel.text = getString(R.string.work_order_number)
//        }
        binding.moveOrderNumberLabel.text = getString(R.string.move_order_number)
        fillMoveOrderLineData()
        clearInputLayoutError(binding.locators,binding.qtyIssuedFromLocator)


        setUpLocatorsSpinner()
        setUpLotSpinner()
        setUpLocatorLotQtysRecyclerView()

        binding.save.setOnClickListener {
//            if (addedLines.isNotEmpty()){
                if (calculateRemainingQty()==0.0) {
                    viewModel.transactMultiItems(
                        TransactMultiItemsBody(
                            orgId = orgId,
                            lines = listOf(
                                TransactMultiLine(
                                lineId = moveOrderLine.linEID,
                                linENUMBER = moveOrderLine.linENUMBER,
                                lots = locatorLotQtys
                                )
                            ),
                            isFinalProduct = false
                        )
                    )
                } else {
                    warningDialog(requireContext(),
                        getString(R.string.please_add_all_quantity_to_lines))
                }
//            } else {
//                warningDialog(requireContext(), getString(R.string.please_add_quantity_to_lines))
//            }
        }

        binding.add.setOnClickListener {
            val issueQty = getEditTextText(binding.qtyIssuedFromLocator)
            if (calculateRemainingQty()>0){
                if (isReadyToAddLine(issueQty)){
//                    if (locatorsList.isNotEmpty()) {
//                        if (selectedLocator!=null) {
//                            val lotsList = lotList.filter { it.locatoRID == selectedLocator?.locatorId!! }
//                            if (lotsList.isNotEmpty()) {
//                                addLotsBottomSheet.lotList = lotsList
//                                addLotsBottomSheet.allocatedQty = issueQtyFromLocator.toDouble()
//                                addLotsBottomSheet.locatorFrom = selectedLocator
//                                addLotsBottomSheet.show()
//                            } else {
//                                addedLines.add(
//                                    TransactMultiLine(
//                                        lineId = moveOrderLine.linEID,
//                                        linENUMBER = moveOrderLine.linENUMBER,
//                                        quantity = getEditTextText(binding.qtyIssuedFromLocator).toDouble(),
//                                        froMLOCATORCode = selectedLocator?.locatorCode,
//                                        lots = listOf(
//                                            LotQty(
//                                                locatorId = selectedLocator?.locatorId.toString()
//                                            )
//                                        )
//                                    )
//                                )
//                                locatorLotQtyAdapter.notifyItemInserted(addedLines.size)
//                            }
//                        } else {
//                            binding.locators.error = getString(R.string.please_select_locator)
//                        }
//                    } else {
//                        if (lotList.isNotEmpty()) {
//                            addLotsBottomSheet.lotList = lotList
//                            addLotsBottomSheet.allocatedQty = issueQtyFromLocator.toDouble()
//                            addLotsBottomSheet.locatorFrom = null
//                            addLotsBottomSheet.show()
//                        }
//                    }
                    locatorLotQtys.add(
                        LotQty(
                            locatorId = selectedLocator?.locatorId.toString(),
                            locatorFromCode = selectedLocator?.locatorCode,
                            lotName = selectedLot?.lotName,
                            qty = issueQty.toDouble()
                        )
                    )
                    locatorLotQtyAdapter.notifyItemInserted(locatorLotQtys.size)
                    binding.lotNumberSpinner.setText("",false)
                    selectedLot = null
                    val remainingQty = calculateRemainingQty()
                    binding.remainingQty.text = remainingQty.toString()
                    binding.qtyIssuedFromLocator.editText?.setText(remainingQty.toString())
                }
            } else {
                warningDialog(requireContext(),
                    getString(R.string.all_quantity_is_already_added))
            }
        }
        observeLiveData()
    }
    private var selectedLot:Lot? = null
    private fun setUpLotSpinner() {
        binding.lotNumberSpinner.setOnItemClickListener{ _, _, selectedPosition, _ ->
            selectedLot = lotList[selectedPosition]
        }
    }

    private fun calculateRemainingQty():Double {
        var remainingQty = moveOrderLine.quantity!!
        for (locatorLotQty in locatorLotQtys){
            remainingQty -= locatorLotQty.qty!!
            Log.d(TAG, "calculateRemainingQty: ${locatorLotQty.qty}")
        }
        Log.d(TAG, "calculateRemainingQty: $remainingQty")
        return remainingQty
    }

    private fun isReadyToAddLine(issueQty: String): Boolean {
        var isReady = true
        if (locatorsList.isNotEmpty()&&selectedLocator==null){
            binding.locators.error = getString(R.string.please_select_from_locator)
            isReady = false
        }
        if (lotList.isNotEmpty()&&selectedLot==null){
            binding.locators.error = getString(R.string.please_select_from_locator)
            isReady = false
        }
        if (issueQty.isEmpty()){
            binding.qtyIssuedFromLocator.error = getString(R.string.please_enter_qty)
            isReady = false
        } else {
            try{
                val remainingQty = calculateRemainingQty()
                if (issueQty.toDouble()<=0){
                    binding.qtyIssuedFromLocator.error =
                        getString(R.string.quantity_must_be_bigger_than_0)
                    isReady = false
                }
                if (issueQty.toDouble()>remainingQty){
                    binding.qtyIssuedFromLocator.error = getString(R.string.quantity_must_be_less_than_or_equal_to)+remainingQty
                    isReady = false
                }
            } catch (_: NumberFormatException){
                binding.qtyIssuedFromLocator.error = getString(R.string.please_enter_valid_qty)
                isReady = false
            }
        }
        return isReady
    }

    private fun observeLiveData() {
        observeSavingTransacting()
        observeGettingLotList()
        observeGettingLocatorsList()
    }

    private fun observeGettingLocatorsList() {
        viewModel.getLocatorsListStatus.observe(viewLifecycleOwner) {
            when(it.status){
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> loadingDialog.dismiss()
                else -> {
                    loadingDialog.dismiss()
//                    warningDialog(requireContext(),"Locators List:${it.message}")
                }
            }
        }
        viewModel.getLocatorsListLiveData.observe(viewLifecycleOwner) {
            locatorsList = it
            binding.locators.visibility = if (locatorsList.isNotEmpty()) VISIBLE else GONE
            locatorsAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,locatorsList)
            binding.locatorsSpinner.setAdapter(locatorsAdapter)
        }

    }


    private val locatorLotQtys : MutableList<LotQty> = mutableListOf()
    private lateinit var locatorLotQtyAdapter: LocatorLotQtyAdapter
    private fun setUpLocatorLotQtysRecyclerView() {
        locatorLotQtyAdapter = LocatorLotQtyAdapter(onRemoveAddedLineButtonClicked = object : LocatorLotQtyAdapter.OnRemoveAddedLineButtonClicked {
            override fun onRemoveAddedLineButtonClicked(position: Int) {
                locatorLotQtys.removeAt(position)
                locatorLotQtyAdapter.notifyItemRemoved(position)
                val remainingQty = calculateRemainingQty().toString()
                binding.remainingQty.text = remainingQty
                binding.qtyIssuedFromLocator.editText?.setText(remainingQty)

            }
        }, locatorLotQtyList = locatorLotQtys)
        binding.locatorLotQtyList.adapter = locatorLotQtyAdapter
    }

    private fun observeSavingTransacting() {
        viewModel.transactItemsStatus.observe(requireActivity()){
            when(it.status){
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> {
                    loadingDialog.hide()
                    showSuccessAlerter(it.message,requireActivity())
                    back(this)
                }
                else -> {
                    loadingDialog.hide()
                    warningDialog(requireContext(),it.message)
                }
            }
        }
    }

    private var lotList = listOf<Lot>()
    private var locatorsList = listOf<Locator>()
    private lateinit var locatorsAdapter: ArrayAdapter<Locator>
    private var selectedLocator: Locator? = null
    private fun setUpLocatorsSpinner() {
        binding.locatorsSpinner.setOnItemClickListener { _, _, selectedPosition, _ ->
            selectedLocator = locatorsList[selectedPosition]
            viewModel.getLotList(orgId,moveOrderLine.inventorYITEMID,subInventoryFrom?.subInventoryCode,selectedLocator?.locatorCode)
        }
    }
//    private fun validQty(qtyText: String): Boolean {
//        var valid = true
//        if (qtyText.isEmpty()){
//            valid = false
//            binding.lotQty.error = getString(R.string.please_enter_qty)
//        } else {
//            try {
//                if(qtyText.toDouble()> remainingQty){
//                    valid = false
//                    binding.lotQty.error =
//                        getString(R.string.lot_quantity_must_be_more_than_or_equal_to)+remainingQty
//
//                }
//                if (qtyText.toDouble()<=0){
//                    valid = false
//                    binding.lotQty.error =
//                        getString(R.string.lot_quantity_must_not_be_equal_to_zero)
//                    clearLotData()
//                }
////                if (qtyText.toDouble()>selectedLot?.transactioNQUANTITY!!){
////                    valid = false
////                    binding.lotQty.error =
////                        getString(R.string.lot_quantity_must_not_be_more_than_selected_lot_quantity)
////                }
//            } catch (ex:Exception){
//                valid = false
//                binding.lotQty.error = getString(R.string.please_enter_valid_qty)
//            }
//        }
//        return valid
//    }

    private fun observeGettingLotList() {
        viewModel.getLotListStatus.observe(requireActivity()){
            when(it.status){
                Status.LOADING ->{
                    loadingDialog.show()
                }
                Status.SUCCESS -> {
                    loadingDialog.hide()
                }
                else -> {
                    loadingDialog.hide()
//                    warningDialog(requireContext(),it.message)
                }
            }
        }
        viewModel.getLotListLiveData.observe(requireActivity()){
            lotList = it
//            addLotsBottomSheet.lotList
            val lotAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,lotList)
            binding.lotNumberSpinner.setAdapter(lotAdapter)
            if (lotList.isNotEmpty()){
                binding.lotNumber.visibility = VISIBLE
            } else {
                binding.lotNumber.visibility = GONE
            }
        }
    }

    private fun fillMoveOrderLineData() {
        binding.moveOrderNumber.text = moveOrderNumber.toString()
        binding.lineNumber.text      = moveOrderLine.linENUMBER.toString()
        binding.itemDescription.text = moveOrderLine.inventorYITEMDESC
        binding.allocatedQty.text = calculateRemainingQty().toString()
        binding.subInventoryFrom.text  = subInventoryFrom?.subinventorydescription
//        binding.locator.text           = locatorFrom?.locatorCode!!
        val remainingQty = calculateRemainingQty().toString()
        binding.remainingQty.text = remainingQty
        binding.qtyIssuedFromLocator.editText?.setText(remainingQty)
    }

    override fun onResume() {
        super.onResume()
        Tools.changeFragmentTitle(getString(R.string.lot_serial), requireActivity())
        viewModel.getLotList(orgId,moveOrderLine.inventorYITEMID,subInventoryFrom?.subInventoryCode,null)
    }
}