package net.gbs.epp_project.Ui.Gate.RegisterArrival.FormScreens

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.chip.Chip
import net.gbs.epp_project.Base.BaseFragmentWithViewModel
import net.gbs.epp_project.Model.ApiRequestBody.SaveNewVehicleRecordData
import net.gbs.epp_project.Model.Container
import net.gbs.epp_project.Model.Status
import net.gbs.epp_project.R
import net.gbs.epp_project.Tools.Tools.back
import net.gbs.epp_project.Tools.Tools.clearInputLayoutError
import net.gbs.epp_project.Tools.Tools.getEditTextText
import net.gbs.epp_project.Tools.Tools.showErrorAlerter
import net.gbs.epp_project.Tools.Tools.showSuccessAlerter
import net.gbs.epp_project.Tools.Tools.warningDialog
import net.gbs.epp_project.Ui.Gate.RegisterArrival.FormScreens.AddContainerDialog.OnAddContainerButtonClicked
import net.gbs.epp_project.Ui.Gate.RegisterArrival.RegisterArrivalFragment
import net.gbs.epp_project.Ui.Gate.RegisterArrival.RegisterArrivalViewModel
import net.gbs.epp_project.databinding.FragmentVehicleDataBinding

class VehicleDataFragment(private val groupViewModel: RegisterArrivalViewModel) : BaseFragmentWithViewModel<RegisterArrivalViewModel, FragmentVehicleDataBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentVehicleDataBinding
        get() = FragmentVehicleDataBinding::inflate
    private var trailersList = mutableListOf<String>()
    private var containers   = mutableListOf<Container>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
        clearInputLayoutError(binding.plateNo,binding.trailerNo)
        binding.addTrailer.setOnClickListener {
            val trailerNo = getEditTextText(binding.trailerNo)
            if (trailerNo.isNotEmpty()){
                if (!trailersList.contains(trailerNo)) {
                    trailersList.add(trailerNo)
                    groupViewModel.trailers.value = trailersList
                    binding.trailerNo.editText?.setText("")
                }else
                    binding.trailerNo.error = getString(R.string.added_before)
            } else {
                binding.trailerNo.error = getString(R.string.please_enter_trailer_number)
            }
        }
        binding.addContainer.setOnClickListener {
            AddContainerDialog(
                context = requireContext(),
                container = null,
                onAddContainerButtonClicked = { containerAdded,dialog ->
                    val container =containers.find { it.containerNo == containerAdded.containerNo }
                    if (container==null){
                        containers.add(containerAdded)
                        groupViewModel.containers.value = containers
                        dialog.dismiss()
                    } else {
                        warningDialog(requireContext(),getString(R.string.added_before))
                    }
                }
                ).show()
        }
        binding.previousScreen.setOnClickListener {
            groupViewModel.plateNo = getEditTextText(binding.trailerNo)
            (requireParentFragment() as RegisterArrivalFragment).goToPage2()
        }
        binding.save.setOnClickListener {
            val selectedGovernorateId = groupViewModel.selectedGovernorateId
            val selectedSalesAgreementHeaderNumber = groupViewModel.selectedSalesOrderNumber
            val selectedAgreementDetailsId = groupViewModel.selectedAgreementDetailsNoId
            val bookingNo                  = groupViewModel.selectedBookingId
            val driverName                 = groupViewModel.driverName
            val driverPhoneNumber          = groupViewModel.driverPhoneNumber
            val driverNationalId           = groupViewModel.driverNationalId
            val plateNo                    = getEditTextText(binding.plateNo)
            val customerName               = groupViewModel.customerName
            Log.d("VehicleDataFragment", "onViewCreated: salesOrderNumber $selectedSalesAgreementHeaderNumber")
            if (isReadyToSave(plateNo)){
                groupViewModel.saveNewVehicle(
                    SaveNewVehicleRecordData(
                        salesOrderNumber = selectedSalesAgreementHeaderNumber,
                        governorateId = selectedGovernorateId,
                        salesAgrDetailId = selectedAgreementDetailsId,
                        bookingId = bookingNo,
                        driverName = driverName,
                        driverPhone = driverPhoneNumber,
                        driverIdNo = driverNationalId,
                        plateNo = plateNo,
                        listOfTrailerNos = trailersList,
                        listOfContainers = containers,
                        customerName = customerName
                    )
                )
            }
        }
    }

    private fun isReadyToSave(
        plateNo: String
    ): Boolean {
        var isReady = true
        if (plateNo.isEmpty()){
            isReady = false
            binding.plateNo.error = getString(R.string.please_enter_plate_number)
        }
        if(trailersList.isEmpty()){
            isReady = false
            binding.trailerNo.error = getString(R.string.please_enter_trailers_numbers)
        }
        if(containers.isEmpty()){
            isReady = false
            warningDialog(requireContext(), getString(R.string.please_enter_containers_numbers))
        }
        return isReady
    }

    private fun observeData() {
        observePlateNo()
        observeTrailersNos()
        observeContainers()
        observeSavingData()
    }

    private fun observeSavingData() {
        groupViewModel.saveNewVehicleStatus.observe(viewLifecycleOwner) {
            when(it.status){
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> {
                    loadingDialog.dismiss()
                    showSuccessAlerter(it.message,requireActivity())
                    back(requireParentFragment())
                }
                else -> {
                    loadingDialog.dismiss()
                    showErrorAlerter(it.message,requireActivity())
                }
            }
        }
    }

    private fun observeContainers() {
        groupViewModel.containers.observe(viewLifecycleOwner) {
            containers = it
            binding.containers.removeAllViews()
            containers.forEach {
                addNewContainer(it.containerNo)
            }
        }
    }

    private fun addNewContainer(containerNo: String?) {
        val chip = Chip(requireContext()).apply {
            text = containerNo
            isCloseIconVisible = true
            setOnCloseIconClickListener { view
                binding.containers.removeView(it)
                containers.removeIf { it.containerNo == containerNo }
            }
        }
        binding.containers.addView(chip)
    }

    private fun observeTrailersNos() {
        groupViewModel.trailers.observe(viewLifecycleOwner) {
            trailersList = it
            binding.trailers.removeAllViews()
            trailersList.forEach {
                addNewTrailer(it)
            }
        }
    }

    private fun observePlateNo() {
        binding.plateNo.editText?.setText(groupViewModel.plateNo)
    }

    private fun addNewTrailer(trailerNo:String) {
        val chip = Chip(requireContext()).apply {
            text = trailerNo
            isCloseIconVisible = true
            setOnCloseIconClickListener { view
                binding.trailers.removeView(it)
                trailersList.removeIf { it == trailerNo }
            }
        }
        binding.trailers.addView(chip)
    }
}