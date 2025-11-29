package net.gbs.epp_project.Ui.Menus

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.Navigation
import net.gbs.epp_project.Base.BundleKeys
import net.gbs.epp_project.Base.BundleKeys.FACTORY
import net.gbs.epp_project.Base.BundleKeys.FACTORY_ORGANIZATION_ID
import net.gbs.epp_project.Base.BundleKeys.INDIRECT_CHEMICALS
import net.gbs.epp_project.Base.BundleKeys.SOURCE_KEY
import net.gbs.epp_project.Base.BundleKeys.SPARE_PARTS
import net.gbs.epp_project.Base.BundleKeys.SPARE_PARTS_ORGANIZATION_ID
import net.gbs.epp_project.R
import net.gbs.epp_project.Tools.Tools
import net.gbs.epp_project.Tools.Tools.showBackButton
import net.gbs.epp_project.Tools.Tools.showLogOutButton
import net.gbs.epp_project.Tools.Tools.warningDialog
import net.gbs.epp_project.Ui.SplashAndSignIn.SignInFragment.Companion.USER
import net.gbs.epp_project.databinding.FragmentIssueMenuBinding
import java.util.Timer
import kotlin.concurrent.schedule

class IssueMenuFragment : Fragment() {

    private lateinit var binding: FragmentIssueMenuBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIssueMenuBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = Bundle()
        handleAuthority()
        binding.factory.setOnClickListener {
            val userOrganization = USER?.organizations?.find { it.orgId == FACTORY_ORGANIZATION_ID }
            if (userOrganization!=null) {
                bundle.putString(SOURCE_KEY, FACTORY)
                bundle.putInt(BundleKeys.ORGANIZATION_ID_KEY, FACTORY_ORGANIZATION_ID)
                Navigation.findNavController(it)
                    .navigate(R.id.action_issueMenuFragment_to_transactMoveOrderFragment, bundle)
            } else {
                warningDialog(requireContext(),
                    getString(R.string.the_organization_efo_is_not_assigned_to_the_logged_in_user))
            }
        }
        
        binding.spareParts.setOnClickListener {
            val userOrganization = USER?.organizations?.find { it.orgId == SPARE_PARTS_ORGANIZATION_ID }
            if (userOrganization!=null) {
                bundle.putString(SOURCE_KEY, SPARE_PARTS)
                bundle.putInt(BundleKeys.ORGANIZATION_ID_KEY,SPARE_PARTS_ORGANIZATION_ID)
                Navigation.findNavController(it).navigate(R.id.action_issueMenuFragment_to_transactSparePartsWorkOrderFragment,bundle)
            } else {
                warningDialog(requireContext(),
                    getString(R.string.the_organization_esp_is_not_assigned_to_the_logged_in_user))
            }
        }
        binding.indirectChemicals.setOnClickListener {
            val userOrganization = USER?.organizations?.find { it.orgId == SPARE_PARTS_ORGANIZATION_ID }
            if (userOrganization!=null) {
                bundle.putString(SOURCE_KEY, INDIRECT_CHEMICALS)
                bundle.putInt(BundleKeys.ORGANIZATION_ID_KEY,SPARE_PARTS_ORGANIZATION_ID)
                Navigation.findNavController(it).navigate(R.id.action_issueMenuFragment_to_transactSparePartsWorkOrderFragment,bundle)
            } else {
                warningDialog(requireContext(),
                    getString(R.string.the_organization_esp_is_not_assigned_to_the_logged_in_user))
            }
        }

    }

    private fun handleAuthority() {
        binding.factory.isEnabled = USER?.isFactory!!
        binding.spareParts.isEnabled = USER?.isSpareParts!!
        binding.indirectChemicals.isEnabled = USER?.isIndirectChemical!!
    }

    override fun onResume() {
        super.onResume()
        Tools.changeFragmentTitle(getString(R.string.issue_menu), requireActivity())
        showBackButton(requireActivity())
        showLogOutButton(requireActivity())
    }

}