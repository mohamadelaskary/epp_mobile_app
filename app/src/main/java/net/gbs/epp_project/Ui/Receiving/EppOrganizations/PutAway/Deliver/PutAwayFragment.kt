package net.gbs.epp_project.Ui.Receiving.EppOrganizations.PutAway.Deliver

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.navigation.findNavController
import net.gbs.epp_project.Base.BaseFragmentWithViewModel
import net.gbs.epp_project.Base.BundleKeys.PO_DETAILS_ITEM_2_Key
import net.gbs.epp_project.Base.BundleKeys.PUT_AWAY_REJECT
import net.gbs.epp_project.Model.PODetailsItem2
import net.gbs.epp_project.Model.Status
import net.gbs.epp_project.R
import net.gbs.epp_project.Tools.Tools
import net.gbs.epp_project.Tools.Tools.warningDialog
import net.gbs.epp_project.Ui.SplashAndSignIn.SignInFragment.Companion.USER
import net.gbs.epp_project.databinding.FragmentPutAwayBinding

class PutAwayFragment : BaseFragmentWithViewModel<PutAwayViewModel,FragmentPutAwayBinding>(),View.OnClickListener,
    PurchaseOrdersPutAwayAdapter.PutAwayItemClick {

    companion object {
        fun newInstance() = PutAwayFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPutAwayBinding
        get() = FragmentPutAwayBinding::inflate


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        observeGettingPOs()
        Tools.attachButtonsToListener(this,binding.search)
        if (viewModel.poNum!=null||viewModel.receiptNo!=null){
            binding.poNumber.editText?.setText(viewModel.poNum)
            binding.receiptNo.editText?.setText(viewModel.receiptNo)
            viewModel.getPurchaseOrderReceiptNoList(viewModel.poNum!!,viewModel.receiptNo!!)
        }
    }
    private fun observeGettingPOs() {
        viewModel.poDetailsItemsStatus.observe(requireActivity()){
            when(it.status){
                Status.LOADING ->{
                    loadingDialog!!.show()
                    binding.errorMessage.visibility = View.GONE
                }
                Status.SUCCESS ->{
                    loadingDialog!!.hide()
                }
                else -> {
                    loadingDialog!!.hide()
                    binding.errorMessage.visibility = View.VISIBLE
                    binding.errorMessage.text = it.message
                }
            }
        }
        viewModel.poDetailsItemsLiveData.observe(requireActivity()) { it ->
            val itemList = mutableListOf<PODetailsItem2>()
            it.forEach {
                    if (it.itemqtyAccepted!=0.0) {
                        if(it.isinspected.toBoolean() && !it.isdelivered.toBoolean())
                            itemList.add(it)
                    }
            }
            if (itemList.isNotEmpty()) {
                poAdapter.poList = itemList
            } else {
                binding.errorMessage.visibility = VISIBLE
                binding.errorMessage.text       =
                    getString(R.string.no_accepted_quantity_to_be_delivered)
                poAdapter.poList = itemList
            }
        }
    }
    private lateinit var poAdapter: PurchaseOrdersPutAwayAdapter
    private fun setUpRecyclerView() {
        poAdapter = PurchaseOrdersPutAwayAdapter(this)
        binding.poList.adapter = poAdapter
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.search ->{

                val poNum           = binding.poNumber.editText?.text.toString().trim()
                val receiptNum      = binding.receiptNo.editText?.text.toString().trim()
                if (poNum.isNotEmpty()||receiptNum.isNotEmpty()){
                    viewModel.getPurchaseOrderReceiptNoList(poNum,receiptNum)
                } else {
                    if (poNum.isEmpty())
                        binding.poNumber.error = getString(R.string.please_enter_po_number)
                    if (receiptNum.isEmpty())
                        binding.receiptNo.error = getString(R.string.please_enter_receipt_no)
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
            Tools.changeFragmentTitle(getString(R.string.put_away), requireActivity())
    }

    val bundle = Bundle()
    override fun putAwayItemClicked(poDetailsItem2: PODetailsItem2) {
        val userOrganization = USER?.organizations?.find { it.orgId == poDetailsItem2.shipToOrganizationId }
        if (userOrganization!=null) {
            bundle.putString(PO_DETAILS_ITEM_2_Key, PODetailsItem2.toJson(poDetailsItem2))
            bundle.putBoolean(PUT_AWAY_REJECT, false)
            view?.findNavController()
                ?.navigate(R.id.action_putAwayFragment_to_startPutAwayFragment, bundle)
        } else {
            warningDialog(requireContext(),
                getString(R.string.this_user_isn_t_authorized_to_deliver_purchase_order_with_this_organization))
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        val poNum = binding.poNumber.editText?.text.toString().trim()
        val receiptNo = binding.receiptNo.editText?.text.toString().trim()
        viewModel.poNum = poNum
        viewModel.receiptNo = receiptNo
    }

}