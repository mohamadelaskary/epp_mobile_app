package net.gbs.epp_project.Ui.Receiving.EppOrganizations.Inspection.StartInspection

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import net.gbs.epp_project.Base.BaseFragmentWithViewModel
import net.gbs.epp_project.Base.BundleKeys.PO_DETAILS_ITEM_2_Key
import net.gbs.epp_project.Model.PODetailsItem2
import net.gbs.epp_project.Model.Status
import net.gbs.epp_project.R
import net.gbs.epp_project.Tools.Tools
import net.gbs.epp_project.Tools.Tools.back
import net.gbs.epp_project.Tools.Tools.changeFragmentTitle
import net.gbs.epp_project.Tools.Tools.clearInputLayoutError
import net.gbs.epp_project.Tools.Tools.showSuccessAlerter
import net.gbs.epp_project.Tools.Tools.warningDialog
import net.gbs.epp_project.databinding.FragmentStartInspectionBinding
import java.util.Calendar

class StartInspectionFragment : BaseFragmentWithViewModel<StartInspectionViewModel,FragmentStartInspectionBinding>(),View.OnClickListener {

    companion object {
        fun newInstance() = StartInspectionFragment()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    private lateinit var poDetailsItem2: PODetailsItem2
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentStartInspectionBinding
        get() = FragmentStartInspectionBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        poDetailsItem2 = PODetailsItem2.fromJson(arguments?.getString(PO_DETAILS_ITEM_2_Key)!!)
        fillPOData()
        observeInspection()
        handleAcceptedQtyTextChange()
//        observeGettingDate()
        clearInputLayoutError(binding.acceptedQty,binding.itemCode,binding.rejectedQty,binding.transactionDate)
        Tools.attachButtonsToListener(this,binding.save,binding.poDetails)
        binding.dateEditText.setOnClickListener {
            showDatePicker(requireContext())
        }
    }
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
//            binding.dateEditText.setText(it.substring(0,10))
//        }
//    }
    private fun observeInspection() {
        viewModel.inspectStatus.observe(requireActivity()){
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

    private fun handleAcceptedQtyTextChange() {
        binding.acceptedQty.editText?.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.acceptedQty.error = null
                if (binding.acceptedQty.editText?.text.toString().isNotEmpty()) {
                    val acceptedQty = binding.acceptedQty.editText?.text.toString().toDouble()
                    if (acceptedQty <= poDetailsItem2.itemqtyReceived!!) {
                        val rejectedQty = poDetailsItem2.itemqtyReceived!! - acceptedQty
                        binding.rejectedQty.editText?.setText(rejectedQty.toString())
                    } else {
                        binding.rejectedQty.editText?.setText("0.0")
                        binding.acceptedQty.error =
                            getString(R.string.accepted_qty_must_be_less_or_equal_to_received_qty)
                    }
                } else {
                    binding.acceptedQty.editText?.setText("0.0")
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
    }

    private fun fillPOData() {
        binding.poNumber.text = poDetailsItem2.pono.toString()
        binding.vendor.text = poDetailsItem2.supplier.toString()
        binding.date.text = poDetailsItem2.receiptdate.toString().substring(0,10)
        binding.itemCode.editText?.setText(poDetailsItem2.itemcode.toString())
        binding.itemDescription.text = poDetailsItem2.itemdesc.toString()
        binding.receiptNumber.text = poDetailsItem2.receiptno.toString()
        binding.receivedQty.text = poDetailsItem2.itemqtyReceived.toString()
        binding.poQty.text = poDetailsItem2.poLineQty.toString()
        binding.acceptedQty.editText?.setText(poDetailsItem2.itemqtyReceived.toString())
        binding.rejectedQty.editText?.setText("0.0")
    }


    override fun onClick(v: View?) {
        when(v?.id){
            R.id.save ->{
                val itemCode = binding.itemCode.editText?.text.toString().trim()
                val acceptedQty = binding.acceptedQty.editText?.text.toString().trim()
                val selectedDate = binding.transactionDate.editText?.text.toString().trim()
                if (selectedDate.isNotEmpty()) {
                    if (itemCode.isNotEmpty()) {
                        if (acceptedQty.isNotEmpty()) {
//                            if (Tools.containsOnlyDigits(acceptedQty)) {
                                viewModel.InspectMaterial(
                                    poHeaderId = poDetailsItem2.poHeaderId!!,
                                    poLineId = poDetailsItem2.poLineId!!,
                                    receiptNo = poDetailsItem2.receiptno!!,
                                    shipToOrganizationId = poDetailsItem2.shipToOrganizationId!!,
                                    acceptedQty = acceptedQty.toDouble(),
                                    transactionDate = viewModel.getTodayDate(),
                                )
//                            } else
//                                binding.acceptedQty.error =
//                                    getString(R.string.please_enter_valid_accepted_qty)
                        } else {
                            binding.acceptedQty.error =
                                getString(R.string.please_enter_accepted_qty)
                        }
                    } else {
                        binding.itemCode.error = getString(R.string.please_scan_item_code)
                    }
                }   else  binding.transactionDate.error = getString(R.string.please_select_date)
            }
//            R.id.po_details -> {
//                v.findNavController().navigate(R.id.action_startInspectionFragment_to_PODetailsFragment)
//            }
        }
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

    override fun onResume() {
        super.onResume()
        changeFragmentTitle(getString(R.string.start_inspection),requireActivity())
        binding.transactionDate.editText?.setText(viewModel.getDisplayTodayDate())
    }

}