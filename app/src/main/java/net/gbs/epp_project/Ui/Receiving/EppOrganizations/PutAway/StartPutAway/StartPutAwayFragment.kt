package net.gbs.epp_project.Ui.Receiving.EppOrganizations.PutAway.StartPutAway

import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.content.Context
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
import android.widget.DatePicker

import net.gbs.epp_project.Base.BaseFragmentWithViewModel
import net.gbs.epp_project.Base.BundleKeys.PO_DETAILS_ITEM_2_Key
import net.gbs.epp_project.Base.BundleKeys.PUT_AWAY_REJECT
import net.gbs.epp_project.Model.DeliverLot
import net.gbs.epp_project.Model.Locator
import net.gbs.epp_project.Model.Lot
import net.gbs.epp_project.Model.PODetailsItem2
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
import net.gbs.epp_project.databinding.FragmentStartPutAwayBinding
import java.lang.Exception
import java.util.Calendar
import net.gbs.epp_project.Tools.ZebraScanner
class StartPutAwayFragment : BaseFragmentWithViewModel<StartPutAwayViewModel,FragmentStartPutAwayBinding>(),
//    Scanner.DataListener,
//    Scanner.StatusListener,
    ZebraScanner.OnDataScanned,
    OnClickListener {

    companion object {
        fun newInstance() = StartPutAwayFragment()
    }


    private lateinit var poDetailsItem: PODetailsItem2
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentStartPutAwayBinding
        get() = FragmentStartPutAwayBinding::inflate

    private lateinit var barcodeReader :ZebraScanner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private var isRejected = false
    var itemId : Int? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        barcodeReader = ZebraScanner(requireActivity(),this)
        poDetailsItem = PODetailsItem2.fromJson(arguments?.getString(PO_DETAILS_ITEM_2_Key)!!)
        isRejected = requireArguments().getBoolean(PUT_AWAY_REJECT)

        viewModel.getSubInventoryList(poDetailsItem.shipToOrganizationId!!)
        itemId = poDetailsItem.inventoryItemId


        setUpSubInventorySpinner()
        observeGetSubInventoryList()
        setUpLotSpinner()
        observeLotList()
//        setUpLocatorSpinner()
        observeGettingLocatorList()
//        observeGettingDate()
        fillData()
        binding.itemCode.editText?.requestFocus()
        EditTextActionHandler.OnEnterKeyPressed(binding.itemCode) {
            val scannedItemCode = binding.itemCode.editText?.text.toString().trim()
            if (scannedItemCode == poDetailsItem.itemcode) {
                binding.itemCode.editText?.setText(scannedItemCode)
                binding.subInventory.editText?.requestFocus()
            } else {
                binding.itemCode.editText?.setText("")
                binding.itemCode.error = getString(R.string.please_scan_item_code)
            }
        }
        EditTextActionHandler.OnEnterKeyPressed(binding.locator) {
            val locatorCode = getEditTextText(binding.locator)
            selectedLocator = locatorList.find { it.locatorCode == locatorCode }
            binding.lotSerial.isEnabled = false;
            viewModel.getLotList(poDetailsItem.shipToOrganizationId!!,itemId,selectedSubInventory?.subInventoryCode!!,locatorCode)
            if (selectedLocator!=null) {
                binding.locator.editText?.setText(locatorCode)
            } else {
                binding.locator.error = getString(R.string.wrong_locator)
            }
        }
        attachButtonsToListener(this,binding.putAway,binding.putAwayList)
        checkFocusedField()
        observePutAwayResponse()
        clearInputLayoutError(binding.itemCode,binding.subInventory,binding.locator,binding.transactionDate,binding.lotSerial)
        binding.dateEditText.setOnClickListener {
            showDatePicker(requireContext())
        }

        binding.lotSerial.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                if (getEditTextText(binding.lotSerial).isEmpty())
//                    selectedLot = null
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    private fun observeGettingLocatorList() {
        viewModel.getLocatorListStatus.observe(requireActivity()){
            when(it.status){
                Status.LOADING -> loadingDialog!!.show()
                Status.SUCCESS -> loadingDialog!!.hide()
                else -> {
                    loadingDialog!!.hide()
                    if (!isRejected)
                        warningDialog(requireContext(),"Locators list:\n${it.message}")
                }
            }
        }
        viewModel.getLocatorListLiveData.observe(requireActivity()){
            locatorList = it
            if (locatorList.isNotEmpty()) {
                binding.locator.visibility = VISIBLE
            } else
                binding.locator.visibility = GONE
//            locatorAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,locatorList)
//            binding.locatorSpinner.setAdapter(locatorAdapter)
        }
    }
    private var locatorList: List<Locator> = listOf()
//    private lateinit var locatorAdapter: ArrayAdapter<Locator>
    private var selectedLocator: Locator? = null
//    private fun setUpLocatorSpinner() {
//        locatorAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,locatorList)
//        binding.locatorSpinner.setAdapter(locatorAdapter)
//        binding.locatorSpinner.setOnItemClickListener { adapterView, view, position, l ->
//            selectedLocator = locatorList[position]
//        }
//    }

    private var subInventoryList:List<SubInventory> = listOf()
    private lateinit var subInventoryAdapter: ArrayAdapter<SubInventory>
    private var selectedSubInventory:SubInventory? = null
    private fun setUpSubInventorySpinner() {
        subInventoryAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,subInventoryList)
        binding.subInventorySpinner.setAdapter(subInventoryAdapter)
        binding.subInventorySpinner.setOnItemClickListener { adapterView, view, position, l ->
            selectedSubInventory = subInventoryList[position]
            viewModel.getLocatorList(poDetailsItem.shipToOrganizationId!!,selectedSubInventory?.subInventoryCode!!,poDetailsItem.inventoryItemId!!)
            viewModel.getLotList(poDetailsItem.shipToOrganizationId!!,itemId,selectedSubInventory?.subInventoryCode!!,null)
        }
    }

    private fun observeGetSubInventoryList() {
        viewModel.getSubinventoryListStatus.observe(requireActivity()){
            when(it.status){
                Status.LOADING -> loadingDialog!!.show()
                Status.SUCCESS -> loadingDialog!!.hide()
                else -> {
                    loadingDialog!!.hide()
                    warningDialog(requireContext(),"Subinventories list:\n${it.message}")
                }
            }
        }
        viewModel.getSubinventoryListLiveData.observe(requireActivity()){
            subInventoryList = it
            subInventoryAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,subInventoryList)
            binding.subInventorySpinner.setAdapter(subInventoryAdapter)
        }
    }

    private var lotList:List<Lot> = listOf()
    private lateinit var lotAdapter: ArrayAdapter<Lot>
//    private var selectedLot:Lot? = null
    private fun setUpLotSpinner() {
        lotAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,lotList)
        binding.lotSerialSpinner.setAdapter(lotAdapter)
        binding.lotSerialSpinner.setOnItemClickListener { adapterView, view, position, l ->
//            selectedLot = lotList[position]
        }
    }

    private fun observeLotList() {
        viewModel.getLotListStatus.observe(requireActivity()){
            when(it.status){
                Status.LOADING -> loadingDialog!!.show()
                Status.SUCCESS -> loadingDialog!!.hide()
                else -> {
                    loadingDialog!!.hide()
//                    warningDialog(requireContext(),it.message)
                    binding.lotSerial.visibility = GONE
                    binding.lotSerial.isEnabled = false
                }
            }
        }
        viewModel.getLotListLiveData.observe(requireActivity()){
            if (it.isEmpty()){
                binding.lotSerial.visibility = GONE
                binding.lotSerial.isEnabled = false
            } else {
                binding.lotSerial.visibility = VISIBLE
                binding.lotSerial.isEnabled = true
            }
            lotList = it
            lotAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,lotList)
            binding.lotSerialSpinner.setAdapter(lotAdapter)
        }
    }



    private fun observePutAwayResponse() {
        viewModel.putAwayStatus.observe(requireActivity()){
            when(it.status){
                Status.LOADING -> loadingDialog!!.show()
                Status.SUCCESS -> {
                    loadingDialog!!.hide()
                    showSuccessAlerter(it.message,requireActivity())
                    back(this)
                }
                else -> {
                    loadingDialog!!.hide()
                    warningDialog(requireContext(),it.message)
                }
            }
        }
    }

    private var itemCodeFocused = false
    private var subinventoryFocused = false
    private var locatorFocused = false
    private fun checkFocusedField() {
        binding.itemCode.editText?.setOnFocusChangeListener { view, b ->  itemCodeFocused = b}
        binding.subInventory.editText?.setOnFocusChangeListener { view, b ->  subinventoryFocused = b}
        binding.locator.editText?.setOnFocusChangeListener { view, b ->  locatorFocused = b}
    }

    private fun fillData() {
        binding.vendor.text = poDetailsItem.supplier
        binding.poNumber.text = poDetailsItem.pono
        binding.receiptNumber.text = poDetailsItem.receiptno
        binding.date.text = poDetailsItem.receiptdate?.substring(0,10)
        binding.itemCode.editText?.setText(poDetailsItem.itemcode)
        binding.itemDescription.text = poDetailsItem.itemdesc


        binding.poQty.text = poDetailsItem.poLineQty.toString()
        if (!isRejected) {
            binding.qty.editText?.setText(poDetailsItem.itemqtyAccepted.toString())
            binding.putAwayQty.text = poDetailsItem.itemqtyAccepted.toString()
            binding.locator.visibility = VISIBLE
            if (poDetailsItem.mustHaveLot()){
                binding.lotSerial.visibility = VISIBLE
            } else {
                binding.lotSerial.visibility = GONE
            }
        } else {
            binding.qty.editText?.setText(poDetailsItem.itemqtyRejected.toString())
            binding.putAwayQty.text = poDetailsItem.itemqtyRejected.toString()
            binding.locator.visibility = GONE
            binding.lotSerial.visibility = GONE
        }

    }


    override fun onClick(v: View?) {
        when (v?.id){
            R.id.put_away ->{
                if(readyToSave()){
                    try {
//                        Log.d(TAG, "onClick: ${selectedLot}")
//                        val lotNum = if (selectedLot==null) getEditTextText(binding.lotSerial) else selectedLot?.lotName
                        var locatorCode =
                            if (getEditTextText(binding.locator).isEmpty())
                                null
                            else
                                getEditTextText(binding.locator)
                        viewModel.PutAwayMaterial(
                            poHeaderId = poDetailsItem.poHeaderId!!,
                            poLineId = poDetailsItem.poLineId!!,
                            locator_code = locatorCode,
                            subinventory_code = selectedSubInventory?.subInventoryCode!!,
                            shipToOrganizationId = poDetailsItem.shipToOrganizationId!!,
                            receiptNo = poDetailsItem.receiptno!!,
                            transactionDate = viewModel.getTodayDate(),
                            acceptedQty = poDetailsItem.itemqtyAccepted!!,
                            lot_num = getEditTextText(binding.lotSerial),
                            isRejected = isRejected,
                            isFullControl = poDetailsItem.mustHaveLot()
                        )
                    } catch (ex:Exception){
                        warningDialog(requireContext(),getString(R.string.error_in_saving_data))
                        Log.d(TAG, "onClick: ${ex.message}")
                    }
                }

            }
        }
    }

    private fun readyToSave(): Boolean {
        val itemCode = binding.itemCode.editText?.text.toString().trim()
        val subInventory = binding.subInventory.editText?.text.toString().trim()
        val locator      = binding.locator.editText?.text.toString().trim()
        val lot          = binding.lotSerial.editText?.text.toString().trim()
        var isReady = true
        selectedDate = binding.transactionDate.editText?.text.toString()
        if(!locatorList.isEmpty()) {
            val locatorData = locatorList.find { it.locatorCode == locator }
            if (locatorData == null) {
                binding.locator.error = getString(R.string.wrong_locator)
                isReady = false
            }
        }
        if (selectedDate!!.isEmpty()){
            binding.transactionDate.error = getString(R.string.please_select_date)
            isReady = false
        }

        if (itemCode.isEmpty()){
            binding.itemCode.error = getString(R.string.please_scan_item_code)
            isReady =  false
        }
        if (subInventory.isEmpty()){
            binding.subInventory.error = getString(R.string.please_scan_subinventory_code)
            isReady =  false
        }
        if (!isRejected) {
            if (selectedLocator == null) {
                binding.locator.error = getString(R.string.please_scan_locator_code)
                isReady = false
            }
            if (poDetailsItem.mustHaveLot()){
//                if (selectedLot==null){
//                    binding.lotSerial.error = getString(R.string.please_select_or_enter_lot)
//                    isReady = false
//                } else {
                    if(getEditTextText(binding.lotSerial).isEmpty()){
                        binding.lotSerial.error = getString(R.string.please_select_or_enter_lot)
                        isReady = false
                    }
//                }
            }
        }

        return isReady
    }

    override fun onResume() {
        super.onResume()
        if (isRejected){
            Tools.changeFragmentTitle(getString(R.string.start_rejection), requireActivity())
        } else {
            Tools.changeFragmentTitle(getString(R.string.start_put_away), requireActivity())
        }

//        viewModel.getTodayDate()
        binding.transactionDate.editText?.setText(viewModel.getDisplayTodayDate())
        barcodeReader.onResume()
    }

    override fun onPause() {
        super.onPause()
        barcodeReader.onPause()
    }
    var selectedDate : String? = null
    fun showDatePicker(context: Context) {
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
//            val scannedCode = barcodeReader.onData(p0)
////            if (itemCodeFocused) {
////                if (scannedCode == poDetailsItem.itemcode) {
////                    binding.itemCode.editText?.setText(scannedCode)
////                    binding.subInventory.editText?.requestFocus()
////                } else {
////                    binding.itemCode.editText?.setText("")
////                    binding.itemCode.error = getString(R.string.wrong_item_code)
////                }
////            } else {
////                if (locatorFocused) {
//            if (selectedSubInventory!=null) {
//                selectedLocator = locatorList.find { it.locatorCode == scannedCode }
//                if (selectedLocator != null) {
//                    binding.locator.editText?.setText(scannedCode)
//                } else {
//                    binding.locator.error = getString(R.string.wrong_locator)
//                }
//            } else
//                binding.subInventory.error = getString(R.string.please_scan_sub_inventory_first)
//            barcodeReader.restartReadData()
//        }
//    }

//    override fun onStatus(p0: StatusData?) {
//        barcodeReader.onStatus(p0)
//    }

    override fun onDataScanned(data: String) {
        val scannedCode = data
//            if (itemCodeFocused) {
//                if (scannedCode == poDetailsItem.itemcode) {
//                    binding.itemCode.editText?.setText(scannedCode)
//                    binding.subInventory.editText?.requestFocus()
//                } else {
//                    binding.itemCode.editText?.setText("")
//                    binding.itemCode.error = getString(R.string.wrong_item_code)
//                }
//            } else {
//                if (locatorFocused) {
        if (selectedSubInventory!=null) {
            selectedLocator = locatorList.find { it.locatorCode == scannedCode }
            if (selectedLocator != null) {
//                if (!binding.locator.editText?.isFocused!!)
                    binding.locator.editText?.setText(scannedCode)
//                binding.locator.editText?.clearFocus()
            } else {
                binding.locator.error = getString(R.string.wrong_locator)
            }
        } else
            binding.subInventory.error = getString(R.string.please_scan_sub_inventory_first)
    }
}