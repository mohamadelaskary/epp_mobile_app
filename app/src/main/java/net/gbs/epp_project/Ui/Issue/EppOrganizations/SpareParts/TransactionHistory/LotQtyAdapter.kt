package net.gbs.epp_project.Ui.Issue.EppOrganizations.SpareParts.TransactionHistory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import net.gbs.epp_project.Model.LotQty
import net.gbs.epp_project.databinding.IssueOrderLayoutBinding
import net.gbs.epp_project.databinding.LotQtyItemLayoutBinding

class LotQtyAdapter(val lotQtyList: List<LotQty>,val onLotQtyRemoveItemButtonClicked: OnLotQtyRemoveItemButtonClicked): Adapter<LotQtyAdapter.LotQtyViewHolder>() {
    inner class LotQtyViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        val binding = LotQtyItemLayoutBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LotQtyViewHolder {
        val binding = LotQtyItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return LotQtyViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return lotQtyList.size
    }

    override fun onBindViewHolder(holder: LotQtyViewHolder, position: Int) {
        val lotQty = lotQtyList[position]
        with(holder.binding){
            qty.text = lotQty.qty.toString()
            lotDesc.text = lotQty.lotName
            remove.setOnClickListener {
                onLotQtyRemoveItemButtonClicked.onLotQtyRemoveItemButtonClicked(position)
            }
        }
    }
    interface OnLotQtyRemoveItemButtonClicked {
        fun onLotQtyRemoveItemButtonClicked(position: Int)
    }
}