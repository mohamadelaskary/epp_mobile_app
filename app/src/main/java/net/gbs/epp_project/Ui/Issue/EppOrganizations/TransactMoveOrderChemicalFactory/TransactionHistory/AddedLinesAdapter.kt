package net.gbs.epp_project.Ui.Issue.EppOrganizations.TransactMoveOrderChemicalFactory.TransactionHistory

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.transition.Visibility
import net.gbs.epp_project.Model.TransactMultiLine
import net.gbs.epp_project.Model.Vehicle
import net.gbs.epp_project.Ui.Gate.ConfirmArrival.TrucksList.TrucksAdapter
import net.gbs.epp_project.Ui.Gate.ConfirmArrival.TrucksList.TrucksAdapter.TrucksViewHolder
import net.gbs.epp_project.Ui.Issue.EppOrganizations.TransactMoveOrderChemicalFactory.TransactionHistory.AddedLinesAdapter.AddedLinesViewHolder
import net.gbs.epp_project.databinding.FactoryIssueTransactAddedLinesItemLayoutBinding
import net.gbs.epp_project.databinding.VehicleItemLayoutBinding

class AddedLinesAdapter(val onRemoveAddedLineButtonClicked: OnRemoveAddedLineButtonClicked,val linesList: List<TransactMultiLine>): Adapter<AddedLinesViewHolder>() {
    inner class AddedLinesViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        val binding = FactoryIssueTransactAddedLinesItemLayoutBinding.bind(itemView)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddedLinesViewHolder {
        val binding = FactoryIssueTransactAddedLinesItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AddedLinesViewHolder(binding.root)
    }

    override fun onBindViewHolder(
        holder: AddedLinesViewHolder,
        position: Int
    ) {
        val line = linesList[position]
        with(holder.binding){
            if (line.froMLOCATORCode.isNullOrEmpty()) {
                locatorCode.visibility = GONE
            } else {
                locatorCode.text = line.froMLOCATORCode
                locatorCode.visibility = VISIBLE
            }
            issueQty.text    = line.quantity.toString()
            if (line.lots!![0].lotName.isNullOrEmpty()) {
                lotsQtys.visibility = GONE
            } else {
                lotsQtys.text = line.lots.toString().substring(1, line.lots.toString().length - 1)
                lotsQtys.visibility = VISIBLE
            }
            clearItem.setOnClickListener {
                onRemoveAddedLineButtonClicked.onRemoveAddedLineButtonClicked(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return linesList.size
    }

    interface OnRemoveAddedLineButtonClicked {
        fun onRemoveAddedLineButtonClicked(position: Int)
    }

}