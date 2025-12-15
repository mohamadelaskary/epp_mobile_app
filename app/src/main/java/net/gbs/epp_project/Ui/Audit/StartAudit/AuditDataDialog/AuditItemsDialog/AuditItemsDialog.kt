package net.gbs.epp_project.Ui.Audit.StartAudit.AuditDataDialog.AuditItemsDialog

import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import net.gbs.epp_project.Model.AuditOrderSubinventory
import net.gbs.epp_project.databinding.AuditLocatorsDialogBinding

class AuditItemsDialog(private val context: Context):Dialog(context) {
    var itemsList:List<AuditOrderSubinventory> = listOf()
    private lateinit var binding:AuditLocatorsDialogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AuditLocatorsDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpRecyclerView()
        binding.close.setOnClickListener {
            dismiss()
        }
        binding.search.editText?.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {

            }

            override fun onTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {
                itemsAdapter.filter.filter(p0)
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }
    private lateinit var itemsAdapter: AuditItemsAdapter
    private fun setUpRecyclerView() {
        itemsAdapter = AuditItemsAdapter(context)
        binding.locatorsList.adapter = itemsAdapter
    }

    override fun onStart() {
        super.onStart()
        itemsAdapter.locatorsList = itemsList
        itemsList.forEach {
            Log.d(TAG, "observeSavingData: ${it.itemCode}     ${it.countingQty}")
        }
    }
}