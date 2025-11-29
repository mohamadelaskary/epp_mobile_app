package net.gbs.epp_project.Ui.Audit.StartAudit.AuditDataDialog.AuditItemsDialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import net.gbs.epp_project.Model.AuditOrderSubinventory
import net.gbs.epp_project.R
import net.gbs.epp_project.databinding.AuditItemItemBinding
import net.gbs.epp_project.databinding.AuditLocatorItemBinding

class AuditItemsAdapter(private val context:Context):Adapter<AuditItemsAdapter.AuditItemsViewHolder>() {
    var locatorsList:List<AuditOrderSubinventory> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    inner class AuditItemsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = AuditItemItemBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuditItemsViewHolder {
        val binding= AuditItemItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AuditItemsViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return locatorsList.size
    }

    override fun onBindViewHolder(holder: AuditItemsViewHolder, position: Int) {
        val item = locatorsList[position]
        with(holder.binding) {
            itemCode.text = locatorsList[position].itemCode

            if (item.countingQty!=null&&item.countingQty!=0.0){
                itemCode.setTextColor(context.getColor(R.color.green))
                countedQty.setTextColor(context.getColor(R.color.green))
                countedQty.text = locatorsList[position].countingQty.toString()
            } else {
                itemCode.setTextColor(context.getColor(R.color.black))
                countedQty.setTextColor(context.getColor(R.color.black))
                countedQty.text = "0"
            }
        }
    }
}