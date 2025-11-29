package net.gbs.epp_project.Ui.Return.ReturnToVendor.AddItemScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import net.gbs.epp_project.Base.BaseFragmentWithViewModel
import net.gbs.epp_project.Base.BundleKeys.PO_ITEMS_LIST_KEY
import net.gbs.epp_project.Base.BundleKeys.PO_LINE_KEY
import net.gbs.epp_project.Model.Lot
import net.gbs.epp_project.Model.LotQty
import net.gbs.epp_project.Model.POItem
import net.gbs.epp_project.Model.POLineReturn
import net.gbs.epp_project.Model.Status
import net.gbs.epp_project.R
import net.gbs.epp_project.Tools.Tools
import net.gbs.epp_project.Tools.Tools.attachButtonsToListener
import net.gbs.epp_project.Tools.Tools.getEditTextText
import net.gbs.epp_project.Tools.Tools.warningDialog
import net.gbs.epp_project.Tools.ZebraScanner
import net.gbs.epp_project.Ui.Issue.EppOrganizations.TransactMoveOrderChemicalFactory.TransactionHistory.LotQtyAdapter
import net.gbs.epp_project.Ui.Return.ReturnToVendor.ItemsDialog.ReturnToVendorItemsDialog
import net.gbs.epp_project.Ui.Return.ReturnToVendor.ItemsDialog.ReturnToVendorItemsDialogAdapter
import net.gbs.epp_project.databinding.FragmentAddItemScreenBinding

class AddItemScreenFragment : BaseFragmentWithViewModel<AddItemScreenViewModel,FragmentAddItemScreenBinding>(),LotQtyAdapter.OnLotQtyRemoveItemButtonClicked,ReturnToVendorItemsDialogAdapter.OnItemSelected,ZebraScanner.OnDataScanned, OnClickListener {

    companion object {
        fun newInstance() = AddItemScreenFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentAddItemScreenBinding
        get() = FragmentAddItemScreenBinding::inflate
    private lateinit var barcodeReader:ZebraScanner
    private var itemsList:List<POItem> = listOf()
    private lateinit var itemsDialog: ReturnToVendorItemsDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        barcodeReader = ZebraScanner(requireActivity(),this)
        itemsList = requireArguments().getParcelableArrayList(PO_ITEMS_LIST_KEY)!!
        itemsDialog = ReturnToVendorItemsDialog(requireContext(),this)
        itemsDialog.itemsList = itemsList
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeGettingLotList()
        setUpLotSpinner()
        setUpLotQtyRecyclerView()
        attachButtonsToListener(this,binding.save)
        binding.showLinesListDialog.setOnClickListener {
            itemsDialog.show()
        }
    }

    private lateinit var lotQtyAdapter: LotQtyAdapter
    private fun setUpLotQtyRecyclerView() {
        lotQtyAdapter = LotQtyAdapter(lotQtyList,this)
        binding.lotQtyList.adapter = lotQtyAdapter
    }

    private var lotList : List<Lot> = listOf()
    private lateinit var lotAdapter : ArrayAdapter<Lot>
    private var lotQtyList  = ArrayList<LotQty>()
    private fun setUpLotSpinner() {
        binding.lotNumberSpinner.setOnItemClickListener { adapterView, view, selectedPosition, l ->
            if (getEditTextText(binding.lotQty).isNotEmpty()){
                try {
                    val lotQty = LotQty(
                        lotName = lotList[selectedPosition].lotName,
                        qty = getEditTextText(binding.lotQty).toDouble()
                    )
                    lotQtyList.add(lotQty)
                    lotQtyAdapter.notifyItemInserted(lotQtyList.size-1)
                } catch (ex:Exception){
                    binding.lotQty.error = getString(R.string.please_enter_valid_qty)
                }
            } else {
                binding.lotQty.error = getString(R.string.please_enter_qty)
            }
        }
    }

    private fun observeGettingLotList() {
        viewModel.getLotListStatus.observe(requireActivity()){
            when(it.status){
                Status.LOADING -> loadingDialog!!.show()
                Status.SUCCESS -> loadingDialog!!.hide()
                else -> {
                    loadingDialog!!.hide()
                }
            }
        }
        viewModel.getLotListLiveData.observe(requireActivity()){
            lotAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,lotList)
            binding.lotNumberSpinner.setAdapter(lotAdapter)
        }
    }
    var scannedItemsList = mutableListOf<POItem>()
    override fun onDataScanned(data: String) {
        scannedItemsList = itemsList.filter { it.itemcode == data }.toMutableList()
        itemsDialog.itemsList = scannedItemsList
        itemsDialog.show()
    }

    override fun onResume() {
        super.onResume()
        barcodeReader.onResume()
        Tools.changeFragmentTitle(getString(R.string.add_item),requireActivity())

    }

    override fun onPause() {
        super.onPause()
        barcodeReader.onPause()
    }
    private var scannedItem :POItem? = null
//    override fun onItemSelected(item: POItem) {
//        if (!item.isSelected) {
//            itemsDialog.hide()
//            scannedItem = item
//            fillItemData()
//            viewModel.getLotList(
//                scannedItem?.shipToOrganizationId!!,
//                scannedItem?.inventorYITEMID,
//                null
//            )
//        } else {
//
//        }
//
//    }



    private fun fillItemData() {
        binding.itemDataGroup.visibility = VISIBLE
        binding.itemDesc.text = scannedItem?.itemdesc
    }

    override fun onLotQtyRemoveItemButtonClicked(position: Int) {
        lotQtyList.removeAt(position)
        lotQtyAdapter.notifyItemRemoved(position)
    }

    override fun onClick(v: View?) {
        when (v?.id){
            R.id.save -> {
                if (isReadyToAdd()){
                    val poLineReturn = POLineReturn(
                        transactioNID = scannedItem?.transactioNID,
                        transactioNTYPE = scannedItem?.transactioNTYPE,
                        shipToOrganizationId = scannedItem?.shipToOrganizationId,
                        receiptNum = scannedItem?.receiptno,
                        poLineId =  scannedItem?.poLineId,
                        itemDescription = scannedItem?.itemdesc,
                        lots = lotQtyList,
                        quantityReturned = calculateReturnQty()
                    )
                    findNavController().previousBackStackEntry?.savedStateHandle?.set(PO_LINE_KEY, poLineReturn)
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun calculateReturnQty(): Double {
        var sum = 0.0
        lotQtyList.forEach {
            sum += it.qty!!
        }
        return sum
    }

    private fun isReadyToAdd(): Boolean {
        var isReady = true
        if (scannedItem==null){
            isReady = false
            binding.itemCode.error = getString(R.string.please_scan_or_enter_item_code)
        }
        if (lotList.isNotEmpty()){
            if (lotQtyList.isEmpty()){
                isReady = false
                warningDialog(requireContext(),getString(R.string.please_select_lot))
            }
        }
        return isReady
    }

    override fun onItemSelected(item: POItem, position: Int) {
        TODO("Not yet implemented")
    }
}