package net.gbs.epp_project.Ui.Gate.ConfirmArrival.ConfirmArrival

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.gbs.epp_project.Base.BaseFragmentWithViewModel
import net.gbs.epp_project.MainActivity.MainActivity
import net.gbs.epp_project.Model.Status
import net.gbs.epp_project.Model.Vehicle
import net.gbs.epp_project.R
import net.gbs.epp_project.Tools.Tools.back
import net.gbs.epp_project.Tools.Tools.changeFragmentTitle
import net.gbs.epp_project.Tools.Tools.containsOnlyDigits
import net.gbs.epp_project.Tools.Tools.getEditTextText
import net.gbs.epp_project.Tools.Tools.showBackButton
import net.gbs.epp_project.Tools.Tools.showSuccessAlerter
import net.gbs.epp_project.Tools.Tools.warningDialog
import net.gbs.epp_project.Ui.Gate.ConfirmArrival.TrucksList.ConfirmArrivalTruckListFragment.Companion.TRUCK_KEY
import net.gbs.epp_project.databinding.FragmentConfirmArrivalBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class ConfirmArrivalFragment : BaseFragmentWithViewModel<ConfirmArrivalViewModel, FragmentConfirmArrivalBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentConfirmArrivalBinding
        get() = FragmentConfirmArrivalBinding::inflate
    private lateinit var vehicle: Vehicle
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vehicle = Vehicle.fromJson(requireArguments().getString(TRUCK_KEY)!!)
        fillData()
        observeMobileApprove()
        binding.confirm.setOnClickListener {
            val plateNo = getEditTextText(binding.plateNo)
            val container = getEditTextText(binding.container)
            val driverNationalId = getEditTextText(binding.driverNationalId)
            val securityNumber   = getEditTextText(binding.securityNumber)
            val arrivalDateTime  = getEditTextText(binding.arrivalDateTime)
            if (isReadyToSave(plateNo,container,driverNationalId,securityNumber,arrivalDateTime)){
                viewModel.mobileApprove(vehicle.id!!,plateNo,container,driverNationalId,securityNumber,sendingDateTime)
            }
        }
        binding.selectDateTime.setOnClickListener {
            showSpinnerDatePicker(requireContext())
        }
    }

    private fun isReadyToSave(
        plateNo: String,
        container: String,
        driverNationalId: String,
        securityNumber: String,
        arrivalDateTime: String
    ): Boolean {
        var isReady = true
        if (plateNo.isEmpty()){
            binding.plateNo.error = getString(R.string.please_enter_plate_number)
            isReady = false
        }
        if (container.isEmpty()){
            binding.container.error = getString(R.string.please_enter_container_number)
            isReady = false
        }
        if (driverNationalId.isEmpty()){
            binding.driverNationalId.error = getString(R.string.please_enter_driver_national_id)
            isReady = false
        } else {
            if (!containsOnlyDigits(driverNationalId)){
                binding.driverNationalId.error = getString(R.string.national_id_must_contains_only_digits)
                isReady = false
            }
            if (driverNationalId.length>14 || driverNationalId.length<14){
                binding.driverNationalId.error = getString(R.string.national_id_contains_14_digits)
                isReady = false
            }
        }
        if (securityNumber.isEmpty()){
            isReady = false
            binding.securityNumber.error = getString(R.string.please_enter_security_number)
        }
        if (arrivalDateTime.isEmpty()){
            isReady = false
            binding
        }
        return isReady
    }

    private fun observeMobileApprove() {
        viewModel.mobileApproveStatus.observe(requireActivity()) {
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

    private fun fillData() {
        with(binding){
            salesOrderNumber.text = vehicle.salesOrderNumber
            agreementDate.text    = vehicle.receivingDate
            customerName.text     = vehicle.customerName
            itemCode.text         = vehicle.itemCode
            plateNo.editText?.setText(vehicle.plateNo)
            container.editText?.setText(vehicle.listOfContainers[0])
            driverName.text       = vehicle.driverName
            phone.text            = vehicle.driverPhone
            driverNationalId.editText?.setText(vehicle.driverIdNo)
        }
    }

    override fun onResume() {
        super.onResume()
        changeFragmentTitle(getString(R.string.confirm_arrival),requireActivity() as MainActivity)
        showBackButton(requireActivity())
    }
    var displayDateTime = ""
    var sendingDateTime = ""

    fun showSpinnerDatePicker(context: Context) {
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            context,
            R.style.SpinnerDialogTransparent, // استخدم الثيم اللي فوق
            { _, year, month, dayOfMonth ->
                println("Selected date: $dayOfMonth/${month + 1}/$year")
                showSpinnerTimePicker(context, year, month, dayOfMonth)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }

    fun showSpinnerTimePicker(context: Context, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()

        val timePickerDialog = TimePickerDialog(
            context,
            R.style.SpinnerDialogTransparent, // استخدم الثيم اللي فوق
            { _, hour, minute ->
                println("Selected time: $hour:$minute")

                val selectedDateTime = LocalDateTime.of(year, month + 1, dayOfMonth, hour, minute)

                // العرض للمستخدم
                val displayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.getDefault())
                displayDateTime = selectedDateTime.format(displayFormatter)

                // الإرسال للسيرفر (ISO 8601 UTC)
                val sendingFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                sendingDateTime = selectedDateTime.format(sendingFormatter)

                // عرض في TextInputEditText
                binding.arrivalDateTime.editText?.setText(displayDateTime)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )

        timePickerDialog.show()
    }

}