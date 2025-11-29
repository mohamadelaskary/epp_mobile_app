package net.gbs.epp_project.Ui.Gate.RegisterArrival.FormScreens

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.gbs.epp_project.Base.BaseFragmentWithViewModel
import net.gbs.epp_project.R
import net.gbs.epp_project.Tools.Tools.clearInputLayoutError
import net.gbs.epp_project.Tools.Tools.containsOnlyDigits
import net.gbs.epp_project.Tools.Tools.getEditTextText
import net.gbs.epp_project.Ui.Gate.RegisterArrival.RegisterArrivalFragment
import net.gbs.epp_project.Ui.Gate.RegisterArrival.RegisterArrivalViewModel
import net.gbs.epp_project.databinding.FragmentDriverDataBinding

class DriverDataFragment(private val groupViewModel: RegisterArrivalViewModel) : BaseFragmentWithViewModel<RegisterArrivalViewModel, FragmentDriverDataBinding>() {


    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentDriverDataBinding
        get() = FragmentDriverDataBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clearInputLayoutError(binding.driverName,binding.driverPhoneNumber,binding.driverNationalId)
        binding.nextScreen.setOnClickListener {
            val driverName = getEditTextText(binding.driverName)
            val driverPhoneNumber = getEditTextText(binding.driverPhoneNumber)
            val driverNationalId  = getEditTextText(binding.driverNationalId)
            if (isReadyToNext(driverName,driverPhoneNumber,driverNationalId)){
                groupViewModel.driverName = driverName
                groupViewModel.driverPhoneNumber = driverPhoneNumber
                groupViewModel.driverNationalId = driverNationalId
                (requireParentFragment() as RegisterArrivalFragment).goToPage3()
            }
        }

        binding.previousScreen.setOnClickListener {
            (requireParentFragment() as RegisterArrivalFragment).goToPage1()
        }
    }

    fun isReadyToNext(driverName:String, driverPhoneNumber:String,driverNationalId :String): Boolean{
        var isReady = true
        if (driverName.isEmpty()){
            isReady = false
            binding.driverName.error = getString(R.string.please_enter_driver_name)
        }
        if (driverPhoneNumber.isEmpty()){
            isReady = false
            binding.driverPhoneNumber.error = getString(R.string.please_enter_driver_phone_number)
        }
        if (driverNationalId.isEmpty()){
            isReady = false
            binding.driverNationalId.error = getString(R.string.please_enter_driver_national_id)
        }
        if (driverPhoneNumber.length!=11){
            isReady = false
            binding.driverPhoneNumber.error = getString(R.string.phone_number_must_be_11_numbers)
        }
        if (!containsOnlyDigits(driverPhoneNumber)){
            isReady = false
            binding.driverPhoneNumber.error =
                getString(R.string.phone_number_must_contain_only_numbers)
        }
        if (driverNationalId.length!=14){
            isReady = false
            binding.driverNationalId.error = getString(R.string.national_id_contains_14_digits)
        }
        if (!containsOnlyDigits(driverNationalId)){
            isReady = false
            binding.driverNationalId.error =
                getString(R.string.national_id_must_contains_only_digits)
        }
        return isReady
    }
}