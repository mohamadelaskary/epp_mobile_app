package net.gbs.epp_project.Ui.Gate.RegisterArrival

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.gbs.epp_project.Base.BaseFragmentWithViewModel
import net.gbs.epp_project.MainActivity.MainActivity
import net.gbs.epp_project.R
import net.gbs.epp_project.Tools.Tools.changeFragmentTitle
import net.gbs.epp_project.Tools.Tools.changeTitle
import net.gbs.epp_project.Tools.Tools.showBackButton
import net.gbs.epp_project.databinding.FragmentRegisterArrivalBinding

class RegisterArrivalFragment : BaseFragmentWithViewModel<RegisterArrivalViewModel, FragmentRegisterArrivalBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentRegisterArrivalBinding
        get() = FragmentRegisterArrivalBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewPager()

    }
    private lateinit var viewPagerAdapter: FormViewPagerAdapter
    private fun setUpViewPager() {
        viewPagerAdapter = FormViewPagerAdapter(this,viewModel)
        binding.formsViewPager.adapter = viewPagerAdapter
    }

    fun goToPage1(){
        binding.formsViewPager.setCurrentItem(0, true)
    }
    fun goToPage2(){
        binding.formsViewPager.setCurrentItem(1, true)
    }
    fun goToPage3(){
        binding.formsViewPager.setCurrentItem(2, true)
    }


    override fun onResume() {
        super.onResume()
        Log.d("RegisterArrivalFragment", "onResume: ${getString(R.string.register_arrival)}")
        changeFragmentTitle(getString(R.string.register_arrival),requireActivity() as MainActivity)
        showBackButton(requireActivity())
    }


}