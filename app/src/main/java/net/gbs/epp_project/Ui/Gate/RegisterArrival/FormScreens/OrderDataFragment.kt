package net.gbs.epp_project.Ui.Gate.RegisterArrival.FormScreens

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import net.gbs.epp_project.Base.BaseFragmentWithViewModel
import net.gbs.epp_project.Model.FeBooking
import net.gbs.epp_project.Model.Governorate
import net.gbs.epp_project.Model.SalesAgreementDetails
import net.gbs.epp_project.Model.SalesAgreementHeader
import net.gbs.epp_project.Model.Status
import net.gbs.epp_project.R
import net.gbs.epp_project.Tools.Tools
import net.gbs.epp_project.Tools.Tools.getEditTextText
import net.gbs.epp_project.Ui.Gate.RegisterArrival.RegisterArrivalFragment
import net.gbs.epp_project.Ui.Gate.RegisterArrival.RegisterArrivalViewModel
import net.gbs.epp_project.databinding.FragmentOrderDataBinding

class OrderDataFragment(private val groupViewModel: RegisterArrivalViewModel) : BaseFragmentWithViewModel<RegisterArrivalViewModel, FragmentOrderDataBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentOrderDataBinding
        get() = FragmentOrderDataBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getSpinnersDate()
        observeGettingSpinnersData()
        handleSpinnersOnItemClicked()
        binding.nextScreen.setOnClickListener {
            val customerName = getEditTextText(binding.customerName)
            if (isReadyToNext(customerName = customerName)){
                groupViewModel.selectedGovernorateId = selectedGovernorateId
                groupViewModel.selectedBookingId = selectedBookingId
                groupViewModel.selectedSalesOrderNumber = selectedSalesOrderNumber
                groupViewModel.selectedAgreementDetailsNoId = selectedAgreementDetailsNumberId
                groupViewModel.customerName = customerName
                Log.d("OrderDataFragment", "onViewCreated: salesOrderNumber $selectedSalesOrderNumber")
                (requireParentFragment() as RegisterArrivalFragment).goToPage2()
            }
        }
    }

    fun isReadyToNext(customerName:String): Boolean{
        var isReady = true
        if (selectedGovernorateId == null){
            isReady = false
            binding.governorate.error = getString(R.string.please_select_governorate)
        }
        if (selectedBookingId == null){
            isReady = false
            binding.governorate.error = getString(R.string.please_select_booking_number)
        }
        if (selectedSalesOrderNumber == null){
            isReady = false
            binding.governorate.error = getString(R.string.please_select_sales_order_number)
        }
        if (selectedAgreementDetailsNumberId == null){
            isReady = false
            binding.governorate.error = getString(R.string.please_select_agreement_details_number)
        }
        if (customerName.isEmpty()){
            isReady = false
            binding.governorate.error = getString(R.string.please_select_agreement_details_number)
        }
        return isReady
    }

    private fun handleSpinnersOnItemClicked() {
        handleOnGovernorateItemClicked()
        handleOnBookingNumberItemClicked()
        handleOnSalesOrderItemClicked()
        handleOnAgreementDetailsNumberItemClicked()
    }
    private var selectedAgreementDetailsNumberId : Int? = null
    private fun handleOnAgreementDetailsNumberItemClicked() {
        binding.salesAgreementNoDetailsSpinner.setOnItemClickListener { _,_,position,_ ->
            selectedAgreementDetailsNumberId = agreementDetailsList[position].salesAgrDetailId
        }
    }

    private var selectedSalesOrderNumber : String? = null
    private fun handleOnSalesOrderItemClicked() {
        binding.salesAgreementNoSpinner.setOnItemClickListener { _,_,position,_ ->
            selectedSalesOrderNumber = salesOrdersList[position].salesAgrNumber
            groupViewModel.getAgreementDsList(salesOrdersList[position].salesAgrHeaderId!!)
        }

    }

    private var selectedBookingId :Int? = null
    private fun handleOnBookingNumberItemClicked() {
        binding.bookingNoSpinner.setOnItemClickListener { _,_,position,_ ->
            selectedBookingId = bookingNumbersList[position].bookingId
        }
    }

    private var selectedGovernorateId:Int? = null
    private fun handleOnGovernorateItemClicked() {
        binding.governorateSpinner.setOnItemClickListener { _, _, position, _ ->
            selectedGovernorateId = governoratesList[position].id
        }
    }

    private fun observeGettingSpinnersData() {
        observeGettingGovernoratesList()
        observeGettingBookingNosList()
        observeGettingSalesOrdersNosList()
        observeGettingAgreementDetailsNoList()
    }
    private var agreementDetailsList = listOf<SalesAgreementDetails>()
    private fun observeGettingAgreementDetailsNoList() {
        groupViewModel.getAgreementDsListStatus.observe(viewLifecycleOwner) {
            when(it.status){
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> loadingDialog.hide()
                else -> {
                    loadingDialog.hide()
                    Tools.warningDialog(
                        requireContext(),
                        getString(R.string.agreement_details_numbers) + it.message
                    )
                }
            }
        }
        groupViewModel.getAgreementDsList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()){
                binding.salesAgreementNoDetailsSpinner.setAdapter(
                    ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_list_item_1,
                        it
                    )
                )
            } else {
                Tools.warningDialog(
                    requireContext(),
                    getString(R.string.agreement_details_numbers) + "\n" + getString(
                        R.string.no_data_found_please_contact_system_administrator
                    )
                )
            }
            agreementDetailsList = it
        }
    }
    private var salesOrdersList = listOf<SalesAgreementHeader>()
    private fun observeGettingSalesOrdersNosList() {
        groupViewModel.getAgreementHsListStatus.observe(viewLifecycleOwner) {
            when(it.status){
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> loadingDialog.hide()
                else -> {
                    loadingDialog.hide()
                    Tools.warningDialog(
                        requireContext(),
                        getString(R.string.sales_orders_numbers) + it.message
                    )
                }
            }
        }
        groupViewModel.getAgreementHsList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()){
                binding.salesAgreementNoSpinner.setAdapter(
                    ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_list_item_1,
                        it
                    )
                )
            } else {
                Tools.warningDialog(
                    requireContext(), getString(R.string.sales_orders_numbers) + "\n" + getString(
                        R.string.no_data_found_please_contact_system_administrator
                    )
                )
            }
            salesOrdersList = it
        }
    }
    private var bookingNumbersList = listOf<FeBooking>()
    private fun observeGettingBookingNosList() {
        groupViewModel.getFeBookingsListStatus.observe(viewLifecycleOwner) {
            when(it.status){
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> loadingDialog.hide()
                else -> {
                    loadingDialog.hide()
                    Tools.warningDialog(
                        requireContext(),
                        getString(R.string.booking_numbers) + it.message
                    )
                }
            }
        }
        groupViewModel.getFeBookingsList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()){
                binding.bookingNoSpinner.setAdapter(
                    ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_list_item_1,
                        it
                    )
                )
            } else {
                Tools.warningDialog(
                    requireContext(), getString(R.string.booking_numbers) + "\n" + getString(
                        R.string.no_data_found_please_contact_system_administrator
                    )
                )
            }
            bookingNumbersList = it
        }
    }
    private var governoratesList = listOf<Governorate>()
    private fun observeGettingGovernoratesList() {
        groupViewModel.getGovernoratesListStatus.observe(viewLifecycleOwner) {
            when(it.status){
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> loadingDialog.hide()
                else -> {
                    loadingDialog.hide()
                    Tools.warningDialog(
                        requireContext(),
                        getString(R.string.governorates_list) + it.message
                    )
                }
            }
        }
        groupViewModel.getGovernoratesList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()){
                val governoratesAdapter =
                    ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, it)
                binding.governorateSpinner.setAdapter(governoratesAdapter)
            } else {
                Tools.warningDialog(
                    requireContext(), getString(R.string.governorates_list) + "\n" + getString(
                        R.string.no_data_found_please_contact_system_administrator
                    )
                )
            }
            governoratesList = it
        }
    }

    private fun getSpinnersDate() {
        groupViewModel.getGovernoratesList()
        groupViewModel.getFeBookingsList()
        groupViewModel.getAgreementHsList()
    }
}