package net.gbs.epp_project.Ui.Audit.FinishTracking.StartFinishing

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ArrayAdapter
import net.gbs.epp_project.Base.BaseFragmentWithViewModel
import net.gbs.epp_project.Model.AuditOrder
import net.gbs.epp_project.Model.AuditOrderItemWithLocation
import net.gbs.epp_project.Model.NavigationKeys.AUDIT_ORDER_KEY
import net.gbs.epp_project.Model.Status
import net.gbs.epp_project.R
import net.gbs.epp_project.Tools.Tools
import net.gbs.epp_project.Tools.Tools.changeFragmentTitle
import net.gbs.epp_project.Tools.Tools.showSuccessAlerter
import net.gbs.epp_project.Tools.Tools.warningDialog
import net.gbs.epp_project.databinding.FragmentStartFinishTrackingBinding

class StartFinishTrackingFragment : BaseFragmentWithViewModel<StartFinishTrackingViewModel,FragmentStartFinishTrackingBinding>(),OnClickListener {

    companion object {
        fun newInstance() = StartFinishTrackingFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentStartFinishTrackingBinding
        get() = FragmentStartFinishTrackingBinding::inflate

    private lateinit var auditOrder: AuditOrder
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auditOrder = AuditOrder.fromJson(arguments?.getString(AUDIT_ORDER_KEY)!!)
        setUpSubInventoriesSpinner()
        Tools.attachButtonsToListener(this, binding.finishAudit)

        fillData()
        observeFinishTracking()
    }

    private fun observeFinishTracking() {
        viewModel.finishTrackingStatus.observe(requireActivity()){
            when(it.status){
                Status.LOADING -> loadingDialog!!.show()
                Status.SUCCESS -> {
                    loadingDialog!!.dismiss()
                    showSuccessAlerter(it.message,requireActivity())
                }
                else -> {
                    warningDialog(requireContext(),it.message)
                    loadingDialog!!.dismiss()
                }
            }
        }
    }

    private var subInventories : List<AuditOrderItemWithLocation> = listOf()
    private fun fillData() {
        with(binding){
            orderNo.text = auditOrder.orderNo
            orderDate.text = auditOrder.orderStartDate?.substring(0,10)
            subInventories = auditOrder.itemsWithLocation
            val subInventoriesAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,subInventories)
            subInventorySpinner.setAdapter(subInventoriesAdapter)
        }
    }
    private var selectedSubInventory : AuditOrderItemWithLocation? = null
    private fun setUpSubInventoriesSpinner() {
        binding.subInventorySpinner.setOnItemClickListener { adapterView, view, position, l ->
            selectedSubInventory = subInventories[position]
         }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.finish_audit -> {
                if (selectedSubInventory!=null){
                    showConfirmAlerterDialog(getString(R.string.are_you_sure_to_finish_tracking_in_subinventory)+selectedSubInventory?.subInventoryCode+" ?")
                } else {
                    binding.subInventory.error = getString(R.string.please_select_sub_inventory)
                }
            }
        }
    }
    private fun showConfirmAlerterDialog(message:String) {
        val alerterDialog = AlertDialog.Builder(requireContext())
        alerterDialog.setMessage(message)
            .setPositiveButton(getString(R.string.finish)) { dialogInterface, i ->
                viewModel.finishTracking(
                    auditOrder.physicalInventoryHeaderId!!,
                    selectedSubInventory?.subInventoryCode!!
                )
            }.setNegativeButton(getString(R.string.cancel)) { dialogInterface, i ->
                dialogInterface.dismiss()
            }.create().show()
    }

    override fun onResume() {
        super.onResume()
        changeFragmentTitle(getString(R.string.start_finishing_audit),requireActivity())
    }
}