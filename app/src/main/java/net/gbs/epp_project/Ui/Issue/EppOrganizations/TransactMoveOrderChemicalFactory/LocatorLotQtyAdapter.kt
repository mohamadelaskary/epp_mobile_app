package net.gbs.epp_project.Ui.Issue.EppOrganizations.TransactMoveOrderChemicalFactory

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import net.gbs.epp_project.Model.LotQty
import net.gbs.epp_project.Ui.Issue.EppOrganizations.TransactMoveOrderChemicalFactory.LocatorLotQtyAdapter.LocatorLotQtyViewHolder
import net.gbs.epp_project.databinding.FactoryIssueTransactAddedLinesItemLayoutBinding

class LocatorLotQtyAdapter(val onRemoveAddedLineButtonClicked: OnRemoveAddedLineButtonClicked, val locatorLotQtyList: List<LotQty>): Adapter<LocatorLotQtyViewHolder>() {
    inner class LocatorLotQtyViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        val binding = FactoryIssueTransactAddedLinesItemLayoutBinding.bind(itemView)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LocatorLotQtyViewHolder {
        val binding = FactoryIssueTransactAddedLinesItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return LocatorLotQtyViewHolder(binding.root)
    }

    override fun onBindViewHolder(
        holder: LocatorLotQtyViewHolder,
        position: Int
    ) {
        val locatorLotQty = locatorLotQtyList[position]
        with(holder.binding){
            if (locatorLotQty.locatorId.isNullOrEmpty()) {
                locatorCode.visibility = GONE
            } else {
                locatorCode.text = locatorLotQty.locatorFromCode
                locatorCode.visibility = VISIBLE
            }
            issueQty.text    = locatorLotQty.qty.toString()
            if (locatorLotQty.lotName.isNullOrEmpty()) {
                lotsQtys.visibility = GONE
            } else {
                lotsQtys.text = locatorLotQty.toString()
                lotsQtys.visibility = VISIBLE
            }
            clearItem.setOnClickListener {
                onRemoveAddedLineButtonClicked.onRemoveAddedLineButtonClicked(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return locatorLotQtyList.size
    }

    interface OnRemoveAddedLineButtonClicked {
        fun onRemoveAddedLineButtonClicked(position: Int)
    }

}