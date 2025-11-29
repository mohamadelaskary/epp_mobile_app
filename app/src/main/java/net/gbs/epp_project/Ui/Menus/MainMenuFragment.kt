package net.gbs.epp_project.Ui.Menus

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.Navigation
import net.gbs.epp_project.R
import net.gbs.epp_project.Tools.Tools
import net.gbs.epp_project.Tools.Tools.showLogOutButton
import net.gbs.epp_project.Ui.SplashAndSignIn.SignInFragment.Companion.USER
import net.gbs.epp_project.databinding.FragmentMainMenuBinding
import java.util.Timer
import kotlin.concurrent.schedule
import androidx.navigation.findNavController

class MainMenuFragment : Fragment() {

    private lateinit var binding: FragmentMainMenuBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainMenuBinding.inflate(inflater,container,false)
        return binding.root
    }
    var doubleBackToExitPressedOnce = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleAuthority()
        binding.gateModule.setOnClickListener {
            it.findNavController().navigate(R.id.action_mainMenuFragment_to_gate_nav_graph)
        }
        binding.audit.setOnClickListener {
            it.findNavController().navigate(R.id.action_mainMenuFragment_to_auditMenuFragment)
        }
        binding.issue.setOnClickListener {
            it.findNavController().navigate(R.id.action_mainMenuFragment_to_issueMenuFragment)
        }

        binding.receiving.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_mainMenuFragment_to_receiving_nav_graph)
        }
        binding.returnButton.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_mainMenuFragment_to_return_nav_graph)
        }
        binding.transfer.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_mainMenuFragment_to_transfer_nav_graph)
        }
        binding.itemInfo.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_mainMenuFragment_to_itemInfoFragment)
        }
        binding.containersReceiving.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_mainMenuFragment_to_customerNameSearchFragment)
        }
        binding.finalProducts.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_mainMenuFragment_to_eppOrganizationsFinalProductMenuFragment)
        }
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                if (doubleBackToExitPressedOnce) {
                    requireActivity().finish()
                    return
                }
                doubleBackToExitPressedOnce = true
                Toast.makeText(requireContext(),
                    getString(R.string.click_back_again_to_exit), Toast.LENGTH_SHORT).show()
                Timer().schedule(2000) {
                    doubleBackToExitPressedOnce = false
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, onBackPressedCallback
        )
    }

    private fun handleAuthority() {
        binding.issue.isEnabled = USER?.isFactory!!||USER?.isSpareParts!!||USER?.isIndirectChemical!!
        binding.receiving.isEnabled = USER?.isReceive!!||USER?.isInspection!!||USER?.isDeliver!!||USER?.isDeliverRejected!!||USER?.isItemPos!!
        binding.returnButton.isEnabled = USER?.isReturnToVendor!!||USER?.isReturnToWarehouse!!
        binding.transfer.isEnabled = USER?.isTransfer!!
        binding.audit.isEnabled = USER?.isPhysicalInventory!!|| USER?.isCycleCount!!
        binding.itemInfo.isEnabled = USER?.isItemInfo!!
        binding.finalProducts.isEnabled = USER?.isIssueFinalProduct!!|| USER?.isReceiveFinalProduct!!||USER?.isItemInfoFinalProduct!!
    }


    override fun onResume() {
        super.onResume()
        Tools.changeFragmentTitle(getString(R.string.main_menu), requireActivity())
        Tools.hideBackButton(requireActivity())
        showLogOutButton(requireActivity())
    }

}