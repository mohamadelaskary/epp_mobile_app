package net.gbs.epp_project.Ui.Audit.StartAudit.AuditDataDialog.AuditLocatorsDialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import net.gbs.epp_project.Model.AuditLocator
import net.gbs.epp_project.Model.AuditOrderSubinventory
import net.gbs.epp_project.databinding.AuditLocatorsDialogBinding

class AuditLocatorsDialog(private val context: Context):Dialog(context) {
    var auditLocator:List<AuditOrderSubinventory> = listOf()
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
                locatorsAdapter.filter.filter(p0)
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }
    private lateinit var locatorsAdapter: AuditLocatorsAdapter
    private fun setUpRecyclerView() {
        locatorsAdapter = AuditLocatorsAdapter(context)
        binding.locatorsList.adapter = locatorsAdapter
    }

    override fun onStart() {
        super.onStart()
        locatorsAdapter.auditOrderList = auditLocator
    }
}