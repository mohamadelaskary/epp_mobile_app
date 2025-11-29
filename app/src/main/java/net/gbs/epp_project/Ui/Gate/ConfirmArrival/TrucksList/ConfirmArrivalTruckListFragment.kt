package net.gbs.epp_project.Ui.Gate.ConfirmArrival.TrucksList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import net.gbs.epp_project.Base.BaseFragmentWithViewModel
import net.gbs.epp_project.MainActivity.MainActivity
import net.gbs.epp_project.Model.Status
import net.gbs.epp_project.Model.Vehicle
import net.gbs.epp_project.R
import net.gbs.epp_project.Tools.Tools.changeFragmentTitle
import net.gbs.epp_project.Tools.Tools.showBackButton
import net.gbs.epp_project.databinding.FragmentTruckListBinding

class ConfirmArrivalTruckListFragment : BaseFragmentWithViewModel<TruckListViewModel, FragmentTruckListBinding>(),
    TrucksAdapter.OnTruckItemClicked {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTruckListBinding
        get() = FragmentTruckListBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpTrucksListRecyclerView()
        observeGettingTrucksList()
    }

    private fun observeGettingTrucksList() {
        viewModel.viewArrivalVehiclesStatus.observe(requireActivity()){
            when(it.status){
                Status.LOADING ->{
                    loadingDialog.show()
                    adapter.trucksList = listOf()
                    binding.errorMessage.visibility = GONE
                }
                Status.SUCCESS -> loadingDialog.hide()
                else -> {
                    loadingDialog.hide()
                    adapter.trucksList = listOf()
                    binding.errorMessage.visibility = VISIBLE
                    binding.errorMessage.text = it.message
                }
            }
        }
        viewModel.viewArrivalVehicles.observe(requireActivity()) {
            if(it.isNotEmpty()){
                binding.errorMessage.visibility = GONE
            } else {
                binding.errorMessage.visibility = VISIBLE
                binding.errorMessage.text = getString(R.string.no_vehicles_found)
            }
            adapter.trucksList = it
        }
    }

    private lateinit var adapter: TrucksAdapter
    private fun setUpTrucksListRecyclerView() {
        adapter = TrucksAdapter(this)
        binding.trucksList.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        changeFragmentTitle(getString(R.string.confirm_arrival_truck_list),requireActivity() as MainActivity)
        showBackButton(requireActivity())
        viewModel.viewArrivalVehicles()
    }
    companion object {
        const val TRUCK_KEY = "truck_key"
    }
    override fun onTruckItemClicked(truck: Vehicle) {
        val bundle = Bundle()
        bundle.putString(TRUCK_KEY, Vehicle.toJson(truck))
        findNavController().navigate(R.id.action_confirmArrivalTruckListFragment_to_confirmArrivalFragment,bundle)
    }
}