package net.gbs.epp_project.Ui.Audit.AuditList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import net.gbs.epp_project.Base.BaseFragmentWithViewModel
import net.gbs.epp_project.Model.AuditOrder
import net.gbs.epp_project.Model.NavigationKeys.AUDIT_ORDER_KEY
import net.gbs.epp_project.Model.Status
import net.gbs.epp_project.R
import net.gbs.epp_project.Tools.Tools
import net.gbs.epp_project.databinding.FragmentAuditListBinding

class AuditListFragment : BaseFragmentWithViewModel<AuditListViewModel,FragmentAuditListBinding>(),AuditOrderAdapter.OnAuditOrderItemClicked {

    companion object {
        fun newInstance() = AuditListFragment()
    }


    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentAuditListBinding
        get() = FragmentAuditListBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeGettingAuditOrdersList()
        setUpRecyclerView()
    }
    private lateinit var auditOrderAdapter: AuditOrderAdapter
    private fun setUpRecyclerView() {
        auditOrderAdapter = AuditOrderAdapter(this)
        binding.auditOrdersList.adapter = auditOrderAdapter
    }
    private var auditOrdersList:List<AuditOrder> = listOf()
    private fun observeGettingAuditOrdersList() {
        viewModel.getAuditOrdersListStatus.observe(requireActivity()){
            when(it.status){
                Status.LOADING -> {
                    loadingDialog!!.show()
                    binding.noData.visibility = GONE
                }
                Status.SUCCESS -> loadingDialog!!.dismiss()
                else -> {
                    loadingDialog!!.dismiss()
                    binding.noData.visibility = VISIBLE
                    binding.noData.text = it.message
                }
            }
        }
        viewModel.getAuditOrdersListLiveData.observe(requireActivity()){
            if (it.isNotEmpty()){
                binding.noData.visibility = GONE
                binding.auditOrdersList.visibility = VISIBLE
            } else {
                binding.noData.visibility = VISIBLE
                binding.noData.text = getString(R.string.no_orders)
            }
            auditOrdersList = it
            auditOrderAdapter.auditOrders = auditOrdersList
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getOrdersList()
        Tools.changeFragmentTitle(getString(R.string.audit_list), requireActivity())
    }





    override fun OnOrderItemClicked(position: Int) {
        val bundle = Bundle()
        bundle.putString(AUDIT_ORDER_KEY,AuditOrder.toJson(auditOrdersList[position]))
        findNavController().navigate(R.id.action_auditListFragment_to_startAuditFragment,bundle)
    }
}