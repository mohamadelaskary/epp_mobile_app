package net.gbs.epp_project.Ui.Gate.CheckIn.CheckIn

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
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
import net.gbs.epp_project.Tools.Tools.showBackButton
import net.gbs.epp_project.Tools.Tools.showSuccessAlerter
import net.gbs.epp_project.Tools.Tools.warningDialog
import net.gbs.epp_project.Ui.Gate.ConfirmArrival.TrucksList.ConfirmArrivalTruckListFragment.Companion.TRUCK_KEY
import net.gbs.epp_project.databinding.FragmentContainerCheckInBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class CheckInFragment : BaseFragmentWithViewModel<CheckInViewModel, FragmentContainerCheckInBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentContainerCheckInBinding
        get() = FragmentContainerCheckInBinding::inflate
    private lateinit var vehicle: Vehicle
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vehicle = Vehicle.fromJson(requireArguments().getString(TRUCK_KEY)!!)
        fillData()
        observeMobileApprove()
        binding.confirm.setOnClickListener {

            if (isReadyToSave(sendingDateTime)){
                viewModel.checkIn(vehicle.id!!,sendingDateTime)
            }
        }
        binding.selectDateTime.setOnClickListener {
            showSpinnerDatePicker(requireContext())
        }
    }

    private fun isReadyToSave(
        arrivalDateTime: String
    ): Boolean {
        var isReady = true
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
            salesOrderNumber.text   = vehicle.salesOrderNumber
            agreementDate.text      = vehicle.receivingDate
            customerName.text       = vehicle.customerName
            itemCode.text           = vehicle.itemCode
            plateNo.text            = vehicle.plateNo
            containers.text         = vehicle.listOfContainers[0]
            driverName.text         = vehicle.driverName
            nationalIdNumber.text   = vehicle.driverIdNo
            securityNumver.text     = vehicle.securityNumber

        }
    }

    override fun onResume() {
        super.onResume()
        changeFragmentTitle(getString(R.string.check_in),requireActivity() as MainActivity)
        showBackButton(requireActivity())
    }
    var displayDateTime = ""
    var sendingDateTime = ""
    fun showSpinnerDatePicker(context: Context) {
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            context,
            R.style.SpinnerDialogTransparent, // استخدم الستايل اللي عملناه
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
            R.style.SpinnerDialogTransparent, // استخدم الستايل
            { _, hour, minute ->
                println("Selected time: $hour:$minute")
                val selectedDateTime = LocalDateTime.of(year, month + 1, dayOfMonth, hour, minute)

                // العرض للمستخدم (مثلاً 15/09/2025 16:14)
                val displayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.getDefault())
                displayDateTime = selectedDateTime.format(displayFormatter)

                // الإرسال للسيرفر (ISO 8601 UTC: 2025-09-15T16:14:00.000Z)
                val sendingFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                sendingDateTime = selectedDateTime.format(sendingFormatter)

                // حط النتيجة في الـ TextInputEditText
                binding.checkInDateTime.editText?.setText(displayDateTime)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )

        timePickerDialog.show()
    }

}