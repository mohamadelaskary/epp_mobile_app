package net.gbs.epp_project.Ui.Issue.EppOrganizations.SpareParts

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import net.gbs.epp_project.Base.BundleKeys.ADD_LOT_QTY_S_T_LINE
import net.gbs.epp_project.Base.BundleKeys.INDIRECT_CHEMICALS
import net.gbs.epp_project.Base.BundleKeys.ISSUE_FINAL_PRODUCT
import net.gbs.epp_project.Base.BundleKeys.RECEIVE_FINAL_PRODUCT
import net.gbs.epp_project.Base.BundleKeys.SPARE_PARTS
import net.gbs.epp_project.Model.ApiRequestBody.TransactItemsBody
import net.gbs.epp_project.Model.Lot
import net.gbs.epp_project.Model.LotQty
import net.gbs.epp_project.Model.TransactMultiLine
import net.gbs.epp_project.R
import net.gbs.epp_project.Tools.Tools.getEditTextText
import net.gbs.epp_project.Tools.Tools.warningDialog
import net.gbs.epp_project.Ui.Issue.EppOrganizations.TransactMoveOrderChemicalFactory.TransactionHistory.LotQtyAdapter
import net.gbs.epp_project.databinding.AddLotsBottomSheetLayoutBinding

class AddLotsBottomSheet(context: Context,  var allocatedQty:Double=0.0, private val onSaveButtonClicked: OnSaveButtonClicked):BottomSheetDialog(context) {
    var lotList: List<Lot> = listOf()
        set(value) {
            field = value
        }
    private lateinit var binding: AddLotsBottomSheetLayoutBinding
    private var isDataSaved = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddLotsBottomSheetLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpLotQtyRecyclerView()

        binding.remainingQty.text = remainingQty.toString()
        binding.lotNumberSpinner.setOnItemClickListener { adapterView, view, i, l ->
            selectedLot = lotList[i]
        }
        binding.add.setOnClickListener {
            val enteredQty = getEditTextText(binding.lotQty)
            if (selectedLot!=null) {
                if (validQty(enteredQty)) {
                    lotQtyList.add(
                        LotQty(
                            lotName = selectedLot?.lotName,
                            qty = enteredQty.toDouble()
                        )
                    )
                    lotQtyAdapter.notifyDataSetChanged()
                    remainingQty -= enteredQty.toDouble()
                    binding.remainingQty.text = remainingQty.toString()
                    clearLotData()
                }
            }else binding.lotNumber.error = context.getString(R.string.please_select_lot)
        }

        binding.save.setOnClickListener {
            if (lotQtyList.isNotEmpty()){
                if (remainingQty==0.0) {
                    val lotQtys = lotQtyList
                    onSaveButtonClicked.onSaveButtonClicked(lotQtys)
//                    clearFields()
                } else {
                    warningDialog(context,
                        context.getString(R.string.please_add_all_quantity_to_lots))
                }
            } else {
                binding.lotNumber.error = context.getString(R.string.please_select_lot)
            }
        }
    }

    private fun clearFields() {
        binding.lotNumberSpinner.setText("",false)
        binding.lotQty.editText?.setText("")
        lotQtyList.clear()
        lotQtyAdapter.notifyDataSetChanged()
    }

    private var lotQtyList = mutableListOf<LotQty>()
    private var selectedLot: Lot? = null
    private lateinit var lotQtyAdapter: LotQtyAdapter
    private fun setUpLotQtyRecyclerView() {
        lotQtyAdapter = LotQtyAdapter(lotQtyList,object: LotQtyAdapter.OnLotQtyRemoveItemButtonClicked{
            override fun onLotQtyRemoveItemButtonClicked(position: Int) {
                remainingQty +=lotQtyList[position].qty!!
                binding.remainingQty.text = remainingQty.toString()
                binding.lotQty.editText?.setText(remainingQty.toString())
                selectedLot = null
                lotQtyList.removeAt(position)
                lotQtyAdapter.notifyDataSetChanged()
            }
        })
        binding.lotQtyList.adapter = lotQtyAdapter
    }

    private var remainingQty = 0.0
    private fun validQty(qtyText: String): Boolean {
        var valid = true
        if (qtyText.isEmpty()){
            valid = false
            binding.lotQty.error = context.getString(R.string.please_enter_qty)
        } else {
            try {
                if(qtyText.toDouble()> remainingQty){
                    valid = false
                    binding.lotQty.error =
                        context.getString(R.string.lot_quantity_must_be_more_than_or_equal_to)+remainingQty

                }
                if (qtyText.toDouble()<=0){
                    valid = false
                    binding.lotQty.error =
                        context.getString(R.string.lot_quantity_must_not_be_equal_to_zero)
                }
//                if (qtyText.toDouble()>selectedLot?.transactioNQUANTITY!!){
//                    valid = false
//                    binding.lotQty.error =
//                        getString(R.string.lot_quantity_must_not_be_more_than_selected_lot_quantity)
//                }
            } catch (ex:Exception){
                valid = false
                binding.lotQty.error = context.getString(R.string.please_enter_valid_qty)
            }
        }
        return valid
    }

    private fun clearLotData() {
        binding.lotQty.editText?.setText("")
        binding.lotNumberSpinner.setText("",false)
        binding.remainingQty.text = remainingQty.toString()
        binding.lotQty.editText?.setText(remainingQty.toString())
    }


    private lateinit var lotsAdapter:ArrayAdapter<Lot>
    private fun setUpLotsSpinner(lotQtyList: List<Lot>) {
        lotsAdapter = ArrayAdapter(context,android.R.layout.simple_list_item_1,lotQtyList)
        binding.lotNumberSpinner.setAdapter(lotsAdapter)
    }

    interface OnSaveButtonClicked{
        fun onSaveButtonClicked(lotQtyList:List<LotQty>)
    }

    override fun onStart() {
        super.onStart()
        remainingQty = allocatedQty - totalAddedQty(lotQtyList)
        binding.remainingQty.text = remainingQty.toString()
        binding.lotQty.editText?.setText(remainingQty.toString())
        setUpLotsSpinner(lotList)
    }

    private fun totalAddedQty(lotQtyList: MutableList<LotQty>): Double {
        var total = 0.0
        lotQtyList.forEach{
            total += it.qty!!
        }
        return total
    }
}