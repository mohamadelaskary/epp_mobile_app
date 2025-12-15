package net.gbs.epp_project.Ui.Audit.StartAudit

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import net.gbs.epp_project.Base.BaseFragmentWithViewModel
import net.gbs.epp_project.Model.AuditOrder
import net.gbs.epp_project.Model.AuditOrderSubinventory
import net.gbs.epp_project.Model.NavigationKeys.AUDIT_ORDER_KEY
import net.gbs.epp_project.Model.Status
import net.gbs.epp_project.R
import net.gbs.epp_project.Tools.EditTextActionHandler
import net.gbs.epp_project.Tools.Tools
import net.gbs.epp_project.Tools.ZebraScanner
import net.gbs.epp_project.Ui.Audit.StartAudit.AuditDataDialog.AuditItemsDialog.AuditItemsDialog
import net.gbs.epp_project.Ui.Audit.StartAudit.AuditDataDialog.AuditLocatorsDialog.AuditLocatorsDialog
import net.gbs.epp_project.Ui.SplashAndSignIn.SignInFragment.Companion.isAllowChangeQuantity
import net.gbs.epp_project.Ui.SplashAndSignIn.SignInFragment.Companion.manualEnter
import net.gbs.epp_project.databinding.FragmentStartAuditBinding

class StartAuditFragment :
    BaseFragmentWithViewModel<StartAuditViewModel, FragmentStartAuditBinding>(),
    View.OnClickListener,
    ZebraScanner.OnDataScanned {

    override val bindingInflater =
        { inflater: LayoutInflater, container: ViewGroup?, attach: Boolean ->
            FragmentStartAuditBinding.inflate(inflater, container, attach)
        }

    private lateinit var barcodeReader: ZebraScanner
    private lateinit var auditOrder: AuditOrder

    private var selectedSubInventory: AuditOrderSubinventory? = null
    private var selectedSubInventoryList = mutableListOf<AuditOrderSubinventory>()
    private var locatorsForSubinventory = listOf<AuditOrderSubinventory>()
    private var itemsList = listOf<AuditOrderSubinventory>()

    private var scannedQty = 0
    private var isItemSaved = false
    private var autoSave = false
    private var firstOpen = true

    private lateinit var locatorsDialog: AuditLocatorsDialog
    private lateinit var itemsDialog: AuditItemsDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        barcodeReader = ZebraScanner(requireActivity(), this)

        locatorsDialog = AuditLocatorsDialog(requireContext())
        itemsDialog = AuditItemsDialog(requireContext())

        Tools.attachButtonsToListener(
            this,
            binding.itemInfo,
            binding.locatorsListInfo,
            binding.save,
            binding.auditList,
            binding.finishAudit,
            binding.clearLocatorCode
        )

        observeSavingData()
        observeFinishTracking()
        handleManualAuthority()
        watchItemCodeText()
        setUpAutoSaveToggleButton()
    }

    override fun onResume() {
        super.onResume()

        val auditJson = arguments?.getString(AUDIT_ORDER_KEY)
        if (auditJson.isNullOrEmpty()) {
            Tools.warningDialog(requireContext(), "Audit data not found")
            findNavController().popBackStack()
            return
        }

        auditOrder = AuditOrder.fromJson(auditJson)
        barcodeReader.onResume()

        fillAuditOrderData()
        fillSubInventorySpinner()

        binding.autoSave.isChecked = viewModel.autoSave ?: true
        Tools.changeFragmentTitle(getString(R.string.start_audit), requireActivity())
    }

    override fun onPause() {
        super.onPause()
        barcodeReader.onPause()
        viewModel.autoSave = autoSave
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.locatorCode = Tools.getEditTextText(binding.locatorCode)
        if (!isItemSaved) {
            viewModel.subinventory = selectedSubInventory
            viewModel.scannedQty = Tools.getEditTextText(binding.scannedQty)
        }
    }

    override fun onDataScanned(data: String) {
        if (!::auditOrder.isInitialized || data.isBlank()) return

        if (Tools.getEditTextText(binding.locatorCode).isEmpty()) {

            if (binding.subInventory.editText?.text.isNullOrEmpty()) {
                binding.locatorCode.error =
                    getString(R.string.please_select_sub_inventory_first)
                return
            }

            val sub = selectedSubInventoryList.find { it.locatorCode == data }
            if (sub == null) {
                binding.locatorCode.error =
                    getString(R.string.wrong_locator_code_or_this_locator_not_assigned_to_that_user)
                return
            }

            binding.locatorCode.editText?.setText(data)
            itemsList = auditOrder.getItemsForLocatorCodeAndSubInventory(
                sub.subInventoryCode ?: return,
                sub.locatorCode ?: return
            )

        } else {

            val sub = selectedSubInventoryList.find {
                it.locatorCode == Tools.getEditTextText(binding.locatorCode) &&
                        it.itemCode == data
            }

            if (sub == null) {
                Tools.warningDialog(
                    requireContext(),
                    getString(R.string.item_code_is_wrong_or_not_match_with_subinventory_and_locator)
                )
                return
            }

            selectedSubInventory = sub
            fillItemData()
        }
    }

    private fun fillItemData() {
        val sub = selectedSubInventory ?: return

        scannedQty++
        binding.itemDataGroup.visibility = VISIBLE
        binding.itemCode.editText?.setText(sub.itemCode)
        binding.scannedQty.editText?.setText(scannedQty.toString())
        binding.itemDesc.text = sub.itemDescription
        binding.orgDesc.text = sub.orgCode
        binding.uom.text = sub.uom
        isItemSaved = false
    }

    private fun fillSubInventorySpinner() {
        val list = auditOrder.editedSubInventoriesList()
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, list)

        binding.subInventorySpinner.setAdapter(adapter)
        binding.subInventorySpinner.setOnItemClickListener { _, _, position, _ ->
            selectedSubInventoryList.clear()
            val selected = list[position]
            auditOrder.subInventories
                .filter { it.subInventoryId == selected.subInventoryId }
                .forEach { selectedSubInventoryList.add(it) }

            binding.locatorCode.editText?.setText("")
            binding.itemCode.editText?.setText("")
        }
    }

    private fun observeSavingData() {
        viewModel.getSavingDataStatus.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> {
                    loadingDialog.dismiss()
                    Tools.showSuccessAlerter(it.message, requireActivity())
                    isItemSaved = true
                    binding.save.isEnabled = false
                }

                else -> {
                    loadingDialog.dismiss()
                    Tools.warningDialog(requireContext(), it.message)
                }
            }
        }
    }

    private fun observeFinishTracking() {
        viewModel.finishTrackingStatus.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> {
                    loadingDialog.dismiss()
                    Tools.showSuccessAlerter(it.message, requireActivity())
                }

                else -> {
                    loadingDialog.dismiss()
                    Tools.warningDialog(requireContext(), it.message)
                }
            }
        }
    }

    private fun watchItemCodeText() {
        binding.itemCode.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (s.isNullOrEmpty()) {
                    selectedSubInventory = null
                    scannedQty = 0
                    binding.itemDataGroup.visibility = GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setUpAutoSaveToggleButton() {
        binding.autoSave.setOnCheckedChangeListener { _, checked ->
            autoSave = checked || !isAllowChangeQuantity
            binding.autoSave.isChecked = autoSave
        }
    }

    private fun handleManualAuthority() {
        binding.locatorCode.isEnabled = manualEnter
        binding.itemCode.isEnabled = manualEnter
        binding.scannedQty.isEnabled = isAllowChangeQuantity
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.save -> {
                val sub = selectedSubInventory ?: return
                val qty = Tools.getEditTextText(binding.scannedQty).toDoubleOrNull() ?: return

                viewModel.saveData(
                    qty = qty,
                    headerId = auditOrder.physicalInventoryHeaderId ?: return,
                    itemCode = sub.itemCode ?: return,
                    locatorCode = sub.locatorCode ?: return,
                    subInventoryCode = sub.subInventoryCode ?: return,
                    orgCode = sub.orgCode ?: return
                )
            }

            R.id.audit_list -> {
                val bundle = Bundle()
                bundle.putString(AUDIT_ORDER_KEY, AuditOrder.toJson(auditOrder))
                findNavController().navigate(
                    R.id.action_startAuditFragment_to_auditedListFragment,
                    bundle
                )
            }
        }
    }

    private fun fillAuditOrderData() {
        binding.orderNo.text = auditOrder.orderDesc
        binding.orderDate.text = auditOrder.orderStartDate?.take(10)
        scannedQty = 0
    }
}
