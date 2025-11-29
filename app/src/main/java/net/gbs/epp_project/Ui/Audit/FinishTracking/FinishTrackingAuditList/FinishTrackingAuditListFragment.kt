package net.gbs.epp_project.Ui.Audit.FinishTracking.FinishTrackingAuditList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import net.gbs.epp_project.Base.BaseFragmentWithViewModel
import net.gbs.epp_project.Model.AuditOrder
import net.gbs.epp_project.Model.NavigationKeys.AUDIT_ORDER_KEY
import net.gbs.epp_project.Model.Status
import net.gbs.epp_project.R
import net.gbs.epp_project.Tools.Tools
import net.gbs.epp_project.databinding.FragmentFinishTrackingAuditListBinding

class FinishTrackingAuditListFragment : BaseFragmentWithViewModel<FinishTrackingAuditListViewModel,FragmentFinishTrackingAuditListBinding>(),FinishTrackingAuditListAdapter.OnFinishTrackingOrderItemClicked {

    companion object {
        fun newInstance() = FinishTrackingAuditListFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentFinishTrackingAuditListBinding
        get() = FragmentFinishTrackingAuditListBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        observeGettingAuditOrdersList()
    }
    private lateinit var adapter: FinishTrackingAuditListAdapter
    private fun setUpRecyclerView() {
        adapter = FinishTrackingAuditListAdapter(this)
        binding.auditOrdersList.adapter = adapter
    }

    private var auditOrdersList:List<AuditOrder> = listOf()
    private fun observeGettingAuditOrdersList() {
        viewModel.getAuditOrdersListStatus.observe(requireActivity()){
            when(it.status){
                Status.LOADING -> {
                    loadingDialog!!.show()
                    binding.noData.visibility = View.GONE
                }
                Status.SUCCESS -> loadingDialog!!.dismiss()
                else -> {
                    loadingDialog!!.dismiss()
                    binding.noData.visibility = View.VISIBLE
                    binding.noData.text = it.message
                }
            }
        }
        viewModel.getAuditOrdersListLiveData.observe(requireActivity()){
            if (it.isNotEmpty()){
                binding.noData.visibility = View.GONE
                binding.auditOrdersList.visibility = View.VISIBLE
            } else {
                binding.noData.visibility = View.VISIBLE
                binding.noData.text = getString(R.string.no_orders)
            }
            auditOrdersList = it
            adapter.auditOrders = auditOrdersList
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getOrdersList()
        Tools.changeFragmentTitle(getString(R.string.finish_audit), requireActivity())
    }

    override fun OnItemClicked(position: Int) {
        val bundle = Bundle()
        bundle.putString(AUDIT_ORDER_KEY,AuditOrder.toJson(auditOrder = auditOrdersList[position]))
        findNavController().navigate(R.id.action_finishTrackingAuditListFragment_to_startFinishTrackingFragment,bundle)
    }

}