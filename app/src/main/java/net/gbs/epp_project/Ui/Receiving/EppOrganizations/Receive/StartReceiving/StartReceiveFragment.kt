package net.gbs.epp_project.Ui.Receiving.EppOrganizations.Receive.StartReceiving

import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.navigation.fragment.findNavController

import com.google.gson.Gson

import net.gbs.epp_project.Base.BaseFragmentWithViewModel
import net.gbs.epp_project.Base.BundleKeys.PURCHASE_ORDER_KEY
import net.gbs.epp_project.Model.Organization
import net.gbs.epp_project.Model.PODetailsItem
import net.gbs.epp_project.Model.PoLine
import net.gbs.epp_project.Model.PurchaseOrder
import net.gbs.epp_project.Model.Status
import net.gbs.epp_project.R
import net.gbs.epp_project.Tools.EditTextActionHandler
import net.gbs.epp_project.Tools.Tools.attachButtonsToListener
import net.gbs.epp_project.Tools.Tools.back
import net.gbs.epp_project.Tools.Tools.changeFragmentTitle
import net.gbs.epp_project.Tools.Tools.clearInputLayoutError
import net.gbs.epp_project.Tools.Tools.getEditTextText
import net.gbs.epp_project.Tools.Tools.successDialog
import net.gbs.epp_project.Tools.Tools.warningDialog
import net.gbs.epp_project.databinding.FragmentStartReceiveBinding
import java.lang.NumberFormatException
import java.util.Calendar
import kotlin.Exception
import net.gbs.epp_project.Tools.ZebraScanner
import net.gbs.epp_project.Ui.SplashAndSignIn.SignInFragment.Companion.USER

class StartReceiveFragment : BaseFragmentWithViewModel<StartReceiveViewModel,FragmentStartReceiveBinding>(),View.OnClickListener,
//    Scanner.DataListener,
//    Scanner.StatusListener
    ZebraScanner.OnDataScanned
    ,PoDetailsAdapter.OnPOLineClicked,ReceivedPOItemAdapter.OnPOLineItemRemoved {

    companion object {
        fun newInstance() = StartReceiveFragment()
    }



    private lateinit var purchaseOrder: PurchaseOrder
    private lateinit var barcodeReader : ZebraScanner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        itemsDialog = PoItemsDialog(requireContext(),this)
        purchaseOrder = Gson().fromJson(requireArguments().getString(PURCHASE_ORDER_KEY),PurchaseOrder::class.java)

    }
    private lateinit var itemsDialog: PoItemsDialog
    private var itemsList: List<PODetailsItem> = listOf()
    private lateinit var receivedPOItemAdapter: ReceivedPOItemAdapter
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentStartReceiveBinding
        get() = FragmentStartReceiveBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        barcodeReader = ZebraScanner(requireActivity(),this)
        attachButtonsToListener(this,binding.add, binding.save, binding.poDetails, binding.receivedList,binding.receivedList, binding.itemsList)
        fillHeaderData()
        viewModel.getPoOrganizations(purchaseOrder.poHeaderId!!)
        setUpOrganizationsSpinner()
        observeOrganizations()
        observeItemsList()
//        observeGettingDate()
        clearInputLayoutError(binding.qty,binding.itemCode)
        EditTextActionHandler.OnEnterKeyPressed(binding.itemCode) {
            val itemCode = binding.itemCode.editText?.text.toString().trim()
            binding.itemCode.error = null
            val selectedPoLine = validItemCode(itemCode)
            if (selectedPoLine!=null) {
                binding.itemCode.editText?.setText(selectedPoLine.itemcode)
                binding.qty.editText?.setText(selectedPoLine.remainingQty.toString())
            }

        }
        binding.isNewReceiptNo.setOnCheckedChangeListener{ _, isChecked ->
            if (isChecked){
                viewModel.getNextReceiptNo(selectedOrganization!!.organizationId!!)
            } else{
                binding.receiptNo.editText?.setText("")
                binding.receiptNo.isEnabled = true
                viewModel.getPreviousReceiptNoList(
                    selectedOrganization!!.organizationId!!,
                    purchaseOrder.poHeaderId.toString()
                )
            }
        }
        observeGetNewReceiptNo()
        setUpReceiptNosSpinner()
        observeGetPreviousReceiptNo()
        observeReceivingItems()
        clearInputLayoutError(binding.qty,binding.receiptNo,binding.org,binding.itemCode,binding.transactionDate)
        setUpReceivedLinesRecyclerView()
        binding.dateEditText.setOnClickListener {
           showDatePicker(requireContext())
        }
    }
//    private var fullTodaysDate = ""
//    private fun observeGettingDate() {
//        viewModel.getDateStatus.observe(requireActivity()){
//            when(it.status){
//                Status.LOADING  -> {
//                    loadingDialog!!.show()
//                    binding.dateEditText.isEnabled = false
//                }
//                Status.SUCCESS ->{
//                    loadingDialog!!.hide()
//                    binding.dateEditText.isEnabled = false
//                }
//                else -> {
//                    loadingDialog!!.hide()
//                    binding.dateEditText.isEnabled = true
//                }
//            }
//        }
//        viewModel.getDateLiveData.observe(requireActivity()){
//            fullTodaysDate = it
//            binding.dateEditText.setText(it.substring(0,10))
//        }
//    }

    private fun observeReceivingItems() {
        viewModel.itemReceivingResultStatus.observe(requireActivity()){
            when(it.status){
                Status.LOADING -> loadingDialog!!.show()
                Status.SUCCESS -> {
                    loadingDialog!!.hide()
                    successDialog(requireContext(),"Saved successfully\nReceipt number:${binding.receiptNo.editText?.text.toString()}")
//                    clearOrganizationData()
                    back(this)
                }
                else -> {
                    loadingDialog!!.hide()
                    warningDialog(requireContext(),it.message)
                    Log.e(TAG, "observeReceivingItems: ${it.message}", )
//                    successDialog(requireContext(),"Saved successfully\nReceipt number:${binding.receiptNo.editText?.text.toString()}")
//                    clearOrganizationData()
                }
            }
        }
    }

    private fun clearOrganizationData() {
        binding.organizationSpinner.setText("",false)
        binding.itemDataGroup.visibility = GONE
        binding.qty.editText?.setText("")
        binding.receiptNo.editText?.setText("")
        binding.itemCode.editText?.setText("")
        itemsDialog.itemsList = listOf()
        poLines = mutableListOf()
        itemsList = listOf()
        receivedLines = mutableListOf()
        receivedPOItemAdapter = ReceivedPOItemAdapter(receivedLines,this)
        binding.receivedPoLines.adapter = receivedPOItemAdapter
        binding.isNewReceiptNo.isChecked = false
    }

    private var receiptList:List<String> = listOf()
    private lateinit var receiptNoListAdapter:ArrayAdapter<String>
    private fun setUpReceiptNosSpinner() {
        receiptNoListAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,receiptList)
        binding.receiptNumbers.setAdapter(receiptNoListAdapter)
        binding.receiptNo.editText?.setOnFocusChangeListener { _, isFocused ->
            if (!isFocused){
                val text = binding.receiptNo.editText?.text.toString().trim()
                if (!receiptList.contains(text)){
                    binding.receiptNo.editText?.setText("")
                    binding.receiptNo.error = getString(R.string.please_enter_valid_receipt_no)
                }
            }
        }
    }

    private fun observeGetPreviousReceiptNo() {
        viewModel.getPreviousReceiptNoStatus.observe(requireActivity()){
            when(it.status){
                Status.LOADING -> loadingDialog!!.show()
                Status.SUCCESS -> loadingDialog!!.hide()
                else -> {
                    loadingDialog!!.hide()
                    warningDialog(requireContext(),it.message)
                    Log.e(TAG, "observeGetPreviousReceiptNo: ${it.message}", )
                }
            }
        }
        viewModel.getPreviousReceiptNoLiveData.observe(requireActivity()){
            receiptList = it
            receiptNoListAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,receiptList)
            binding.receiptNumbers.setAdapter(receiptNoListAdapter)
        }
    }

    private fun observeGetNewReceiptNo() {
        viewModel.getNextReceiptNoLiveData.observe(requireActivity()){
            binding.receiptNo.editText?.setText(it)
            binding.receiptNo.isEnabled = false
        }
        viewModel.getNextReceiptNoStatus.observe(requireActivity()){
            when(it.status){
                Status.LOADING -> loadingDialog!!.show()
                Status.SUCCESS -> loadingDialog!!.hide()
                else -> {
                    loadingDialog!!.hide()
                    warningDialog(requireContext(),it.message)
                    Log.e(TAG, "observeGetNewReceiptNo: ${it.message}", )
                }
            }
        }
    }

    private fun observeOrganizations() {
        viewModel.getPoOrganizationsStatus.observe(requireActivity()){
            when(it.status){
                Status.LOADING -> loadingDialog!!.show()
                Status.SUCCESS -> loadingDialog!!.hide()
                Status.ERROR -> {
                    loadingDialog!!.hide()
                    binding.org.error = it.message
                }
                else -> {
                    loadingDialog!!.hide()
                    warningDialog(requireContext(),it.message)
                    Log.e(TAG, "observeOrganizations: ${it.message}", )
                }
            }
        }
        viewModel.getPoOrganizationsLiveData.observe(requireActivity()){
            poOrganizations = it
            organizationsAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,poOrganizations)
            binding.organizationSpinner.setAdapter(organizationsAdapter)
        }
    }

    private var receivedLines: MutableList<PODetailsItem> = mutableListOf()
    private fun setUpReceivedLinesRecyclerView() {
        receivedPOItemAdapter = ReceivedPOItemAdapter(receivedLines,this)
        binding.receivedPoLines.adapter = receivedPOItemAdapter
    }
    private lateinit var organizationsAdapter: ArrayAdapter<Organization>
    private var poOrganizations = listOf<Organization>()
    private var selectedOrganization: Organization? = null
    private fun setUpOrganizationsSpinner() {
        organizationsAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,poOrganizations)
        binding.organizationSpinner.setAdapter(organizationsAdapter)
        binding.organizationSpinner.setOnItemClickListener{ _: AdapterView<*>, _: View, position: Int, _: Long ->


            val userOrganization = USER?.organizations?.find {
                Log.d(TAG, "setUpOrganizationsSpinner: PoOrganization${poOrganizations[position].organizationId}")
                Log.d(TAG, "setUpOrganizationsSpinner: userOrganizations${it.orgId}")
                it.orgId==poOrganizations[position].organizationId
            }
            if (userOrganization!=null) {
                selectedOrganization = poOrganizations[position]
                viewModel.getPurchaseOrderDetailsList(
                    selectedOrganization!!.organizationId!!,
                    purchaseOrder.poHeaderId.toString()
                )
                binding.isNewReceiptNo.isChecked = true
            } else {
                warningDialog(requireContext(),getString(R.string.this_user_isn_t_authorized_to_select_that_organization))
                Log.e(TAG, "setUpOrganizationsSpinner: ${getString(R.string.this_user_isn_t_authorized_to_select_that_organization)}", )
            }
        }
    }

    private fun observeItemsList() {
        viewModel.getPurchaseOrderDetailsListStatus.observe(viewLifecycleOwner){
            when(it.status){
                Status.LOADING -> loadingDialog!!.show()
                Status.SUCCESS -> loadingDialog!!.hide()
                else -> {
                    loadingDialog!!.hide()
                    warningDialog(requireContext(),it.message)
                    Log.e(TAG, "observeItemsList: ${it.message}", )
                }
            }
        }
        viewModel.getPurchaseOrderDetailsListLiveData.observe(viewLifecycleOwner){
            binding.itemDataGroup.visibility = VISIBLE
            itemsList = it
            itemsDialog.itemsList = it
        }
    }

    private fun fillHeaderData() {
        binding.poHeader.vendor.text = purchaseOrder.supplier
        binding.poHeader.poNumber.text = purchaseOrder.pono.toString()
        binding.poHeader.poCreatorName.text = purchaseOrder.poCreatedUser

        binding.poHeader.poType.text = purchaseOrder.potype
//        if (purchaseOrder.receiptNo!=null){
//            binding.receiptNo.isEnabled = false
//            binding.receiptNo.editText?.setText(purchaseOrder.receiptNo)
//        } else {
//            binding.receiptNo.isEnabled = true
//            binding.receiptNo.editText?.setText("")
//        }
    }


    private var poLines:MutableList<PoLine> = mutableListOf()
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.save -> {
                try {
                    if (poLines.isNotEmpty()) {
                        if (isReadyToSave())
                            viewModel.ItemsReceiving(
                                poHeaderId = selectedPoLine?.poHeaderId!!,
                                poLines,
                                transactionDate = viewModel.getTodayDate()
                            )
                    } else {
                        warningDialog(
                            requireContext(),
                            getString(R.string.enter_receiving_data_first)
                        )
                        Log.e(TAG, "onClick: "+getString(R.string.enter_receiving_data_first), )
                    }
                } catch (ex:Exception){
                    warningDialog(requireContext(),"po_header_id = ${selectedPoLine?.poHeaderId!!},\ntransactionDate = $selectedDate,\n receivedListSize = ${poLines.size}")
                    Log.e(TAG, "onClick: ", ex)
                }
            }
            R.id.po_details -> {
                enableItemClick = false
                itemsDialog.show()
            }
            R.id.items_list -> {
                enableItemClick = true
                itemsDialog.show()
            }
            R.id.received_list -> {
                val bundle = Bundle()
                bundle.putString(PURCHASE_ORDER_KEY,PurchaseOrder.toJson(purchaseOrder))
                findNavController().navigate(R.id.action_startReceiveFragment_to_itemInfoFragment,bundle)
            }
            R.id.add -> {
                val receivedQty = getEditTextText(binding.qty)
                try {
                    if (receivedQty.toDouble()<=selectedPoLine?.remainingQty!!){
                        handleDataFound(receivedQty)
                    } else binding.qty.error = getString(R.string.please_enter_valid_receive_qty)
                } catch (ex:NumberFormatException){
                    binding.qty.error = getString(R.string.please_enter_valid_receive_qty)
                }

            }
        }
    }

    private fun isReadyToSave(): Boolean {
        var isReady = true
        val receiptNo   = binding.receiptNo.editText?.text.toString().trim()
        selectedDate = binding.transactionDate.editText?.text.toString()
        if (selectedDate!!.isEmpty()){
            binding.transactionDate.error = getString(R.string.please_select_date)
            isReady = false
        }

        if (receiptNo.isEmpty()){
            binding.receiptNo.error = getString(R.string.please_enter_receipt_no)
            isReady = false
        }
        if (receivedLines.isEmpty()){
            warningDialog(requireContext(), getString(R.string.please_add_items_to_be_received))
            Log.e(TAG, "isReadyToSave: "+getString(R.string.please_add_items_to_be_received), )
            isReady = false
        }
        return  isReady
    }

    override fun onResume() {
        super.onResume()
        changeFragmentTitle(getString(R.string.start_receive), requireActivity())
        refillSelectedOrganizationData()
//        viewModel.getTodayDate()
        binding.transactionDate.editText?.setText(viewModel.getDisplayTodayDate())
        barcodeReader.onResume()
    }

    private fun refillSelectedOrganizationData() {
        val orgText = getEditTextText(binding.org)
        if (orgText.isNotEmpty()) {
            val orgCode = orgText.substring(0,3)
            selectedOrganization = poOrganizations.find { it.organizationCode==orgCode }
            viewModel.getPurchaseOrderDetailsList(
                selectedOrganization!!.organizationId!!,
                purchaseOrder.poHeaderId.toString()
            )
            binding.isNewReceiptNo.isChecked = true
        }
    }

    override fun onPause() {
        super.onPause()
        barcodeReader.onPause()
    }



    private fun handleDataFound(receivedQty:String) {
        selectedPoLine?.let {
            it.currentReceivedQty = receivedQty.toDouble()
            it.isAdded = true
            receivedLines.add(it)
            receivedPOItemAdapter.notifyDataSetChanged()
            val poLine = PoLine(
                poLineId = it.poLineId!!,
                quantityReceived = receivedQty.toDouble(),
                shipToOrganizationId = it.shipToOrganizationId!!,
                shipToLocationId = it.shipToLocationId!!,
                receiptNum = binding.receiptNo.editText?.text.toString().trim()
            )
            poLines.add(poLine)
            binding.itemCode.editText?.setText("")
            binding.qty.editText?.setText("")
        }
    }


    private fun validItemCode(itemCode: String): PODetailsItem? {
        var selectedPoLine :PODetailsItem? = null
        val itemInItemsList = itemsList.find { it.itemcode == itemCode }
        val itemInReceivedList = receivedLines.find { it.itemcode == itemCode }
        if (itemInItemsList!=null){
            if (itemInReceivedList==null){
                if (itemInItemsList.remainingQty>0){
                        selectedPoLine = itemInItemsList
                } else {
                        selectedPoLine= null
                        binding.itemCode.error = getString(R.string.scanned_item_is_fully_received)
                }
            } else {
                binding.itemCode.error = getString(R.string.added_before)
            }
        }  else {
            selectedPoLine= null
            binding.itemCode.editText?.setText("")
            binding.qty.editText?.setText("")
            binding.itemCode.error = getString(R.string.wrong_item_code)
        }
//        for (position in itemsList.indices){
//            Log.d(TAG, "validItemCode listItemCode: ${itemsList[position].itemcode}")
//            Log.d(TAG, "validItemCode selectedItemCode: $itemCode")
//            if (itemsList[position].itemcode == itemCode){
//                if (itemsList[position].remainingQty>0) {
//                    if (itemsList[position].remainingQty>=receivedQty.toInt()) {
//                        if (!addedBefore(itemsList[position].itemcode)) {
//                            selectedPoLine = itemsList[position]
//                            isValid = true
//                            break
//                        } else {
//                            binding.itemCode.error = getString(R.string.added_before)
//                            isValid = false
//                        }
//                    } else {
//                        binding.qty.error = getString(R.string.received_qty_must_be_less_or_equal_remaining_qty)
//                        isValid = false
//                    }
//                } else {
//                    selectedPoLine= null
//                    binding.itemCode.error = getString(R.string.scanned_item_is_fully_received)
//                    isValid = false
//                }
//            } else {
//                selectedPoLine= null
//                binding.itemCode.error = getString(R.string.wrong_item_code)
//                isValid = false
//            }
//        }
        return selectedPoLine
    }

    private fun addedBefore(itemCode: String?): Boolean {
        var addedBefore = false
        for (po in receivedLines){
            if (po.itemcode==itemCode){
                addedBefore = true
                break
            } else {
                addedBefore = false
            }
        }
        return addedBefore
    }




    private var enableItemClick = true
    private var selectedPoLine : PODetailsItem? = null
    override fun onPOLineClicked(po: PODetailsItem) {
        if (enableItemClick) {
            selectedPoLine = po
            itemsDialog.dismiss()
            binding.itemCode.error = null
            if (selectedPoLine?.remainingQty!!>0) {
                binding.itemCode.editText?.setText(selectedPoLine!!.itemcode)
                binding.qty.editText?.setText(selectedPoLine!!.remainingQty.toString())
            } else {
                selectedPoLine= null
                binding.itemCode.error = getString(R.string.scanned_item_is_fully_received)
            }
        }
    }


    override fun onPOLineItemRemoved(position: Int) {
        receivedLines.removeAt(position)
        poLines.removeAt(position)
        receivedPOItemAdapter.notifyItemRemoved(position)
        itemsList[position].isAdded = false
        itemsDialog.itemsList = itemsList
    }
    private var selectedDate : String? = null
    private fun showDatePicker(context: Context) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            context,
            { view: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                val yearString = year.toString().substring(2,4)
                val monthString = month+1
                if (monthString>10){
                    selectedDate = if (dayOfMonth>10)
                        "$year-$monthString-$dayOfMonth"
                    else
                        "$year-$monthString-0$dayOfMonth"
                } else {
                    selectedDate = if (dayOfMonth>10)
                        "$year-0$monthString-$dayOfMonth"
                    else
                        "$year-0$monthString-0$dayOfMonth"
                }
                binding.dateEditText.setText(selectedDate)
            }, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH]
        )
        datePickerDialog.show()
    }
    
//    override fun onData(p0: ScanDataCollection?) {
//        activity?.runOnUiThread {
//            val itemCode = barcodeReader.onData(p0)
//            selectedPoLine = validItemCode(itemCode)
//            if (selectedPoLine!=null) {
//                binding.itemCode.editText?.setText(selectedPoLine?.itemcode)
//                binding.qty.editText?.setText(selectedPoLine?.remainingQty.toString())
//            }
//            barcodeReader.restartReadData()
//        }
//    }
//
//    override fun onStatus(p0: StatusData?) {
//        barcodeReader.onStatus(p0)
//    }

    override fun onDataScanned(data: String) {
        val itemCode = data
        selectedPoLine = validItemCode(itemCode)
        if (selectedPoLine!=null) {
            if (!binding.itemCode.editText?.isFocused!!)
                binding.itemCode.editText?.setText(selectedPoLine?.itemcode)
            binding.itemCode.editText?.clearFocus()
            binding.qty.editText?.setText(selectedPoLine?.remainingQty.toString())
        }
    }


}