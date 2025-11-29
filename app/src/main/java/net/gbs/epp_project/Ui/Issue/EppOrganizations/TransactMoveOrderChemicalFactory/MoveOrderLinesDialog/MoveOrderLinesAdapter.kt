package net.gbs.epp_project.Ui.Issue.EppOrganizations.TransactMoveOrderChemicalFactory.MoveOrderLinesDialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import net.gbs.epp_project.Model.MoveOrderLine
import net.gbs.epp_project.R
import net.gbs.epp_project.databinding.MoveOrderLineItemLayoutBinding

class MoveOrderLinesAdapter(val orderLineItemClicked: OnMoveOrderLineItemClicked,val context:Context):Adapter<MoveOrderLinesAdapter.MoveOrderLinesViewHolder>() {
    var linesList :List<MoveOrderLine> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class MoveOrderLinesViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        val binding = MoveOrderLineItemLayoutBinding.bind(itemView)
    }
    interface OnMoveOrderLineItemClicked {
        fun onMoveOrderLineClicked(item:MoveOrderLine)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoveOrderLinesViewHolder {
        val binding = MoveOrderLineItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MoveOrderLinesViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return linesList.size
    }

    override fun onBindViewHolder(holder: MoveOrderLinesViewHolder, position: Int) {
        val line = linesList[position]
        with(holder.binding){
            itemCode.text = line.inventorYITEMCODE
            itemDescription.text = line.inventorYITEMDESC
            allocatedQty.text = line.allocatedQUANTITY.toString()
            qty.text = line.quantity.toString()
            locatorCode.text = line.froMLOCATORCode
            if (line.isAlreadyAdded)
                background.setCardBackgroundColor(context.resources.getColor(R.color.grey))
            else
                background.setCardBackgroundColor(context.resources.getColor(R.color.white))
        }

        holder.itemView.setOnClickListener {
            if (!line.isAlreadyAdded)
                orderLineItemClicked.onMoveOrderLineClicked(line)
        }
    }
}