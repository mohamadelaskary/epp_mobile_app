package net.gbs.epp_project.Ui.Menus

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import net.gbs.epp_project.MainActivity.MainActivity
import net.gbs.epp_project.R
import net.gbs.epp_project.Tools.Tools.changeFragmentTitle
import net.gbs.epp_project.Tools.Tools.changeTitle
import net.gbs.epp_project.Tools.Tools.showBackButton
import net.gbs.epp_project.databinding.FragmentGateMenuBinding

class GateMenuFragment : Fragment() {


    private lateinit var binding: FragmentGateMenuBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGateMenuBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.confirmArrival.setOnClickListener {
            it.findNavController().navigate(R.id.action_gateMenuFragment2_to_confirmArrivalTruckListFragment)
        }
        binding.registerArrival.setOnClickListener{
            it.findNavController().navigate(R.id.action_gateMenuFragment2_to_checkInTruckListFragment)
        }
        binding.checkIn.setOnClickListener {
            it.findNavController().navigate(R.id.action_gateMenuFragment2_to_checkInTruckListFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        changeFragmentTitle(
            getString(R.string.gate_menu),
            requireActivity() as MainActivity
        )
        showBackButton(requireActivity())
    }
}