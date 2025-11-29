package net.gbs.epp_project.Ui.Issue.EppOrganizations.SpareParts

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.VISIBLE
import net.gbs.epp_project.Model.LotQty
import net.gbs.epp_project.Model.MoveOrderLine
import net.gbs.epp_project.Model.TransactMultiLine
import net.gbs.epp_project.databinding.MoveOrderLineItemLayoutBinding
import net.gbs.epp_project.databinding.TransactAddedLineLayoutBinding
import java.util.ArrayList

class AddedLinesAdapter(val lines:List<TransactMultiLine>, val onLineItemDeleteButtonClicked: OnLineItemDeleteButtonClicked) :
    Adapter<AddedLinesAdapter.AddedLinesViewHolder>() {
    inner class AddedLinesViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        val binding = TransactAddedLineLayoutBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddedLinesViewHolder {
        val binding = TransactAddedLineLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AddedLinesViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return lines.size
    }

    override fun onBindViewHolder(holder: AddedLinesViewHolder, position: Int) {
        val line = lines[position]
        with(holder.binding){
            itemCode.text = line.inventorYITEMCODE
            subInventoryFrom.text = line.froMSUBINVENTORYCODE
            locatorFrom.text = line.froMLOCATORCode
            qty.text = line.quantity.toString()
            removeItem.setOnClickListener {
                onLineItemDeleteButtonClicked.onLineItemDeleteButtonClicked(line.lineId!!,position)
            }
            if (line.mustHaveLot()){
                lotGroup.visibility = VISIBLE
                lots.text = qtysToString(line.lots)
            } else {
                lotGroup.visibility = GONE
            }
        }
    }

    interface OnLineItemDeleteButtonClicked {
        fun onLineItemDeleteButtonClicked(lineId: Int?,position: Int)
    }

    private fun qtysToString(lots: List<LotQty>?): String {
        var lotsText = ""
        lots?.forEachIndexed { index, lotQty ->
            if (index==0)
                lotsText += lotQty.toString()
            else
                lotsText += ", $lotQty"
        }
        return lotsText
    }
}