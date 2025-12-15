package net.gbs.epp_project.Ui.Audit.StartAudit.AuditDataDialog.AuditItemsDialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import net.gbs.epp_project.Model.AuditOrderSubinventory
import net.gbs.epp_project.R
import net.gbs.epp_project.databinding.AuditItemItemBinding
import net.gbs.epp_project.databinding.AuditLocatorItemBinding

class AuditItemsAdapter(private val context: Context)
    : RecyclerView.Adapter<AuditItemsAdapter.AuditItemsViewHolder>(), Filterable {

    private var originalList: List<AuditOrderSubinventory> = listOf()
    private var filteredList: MutableList<AuditOrderSubinventory> = mutableListOf()

    var locatorsList: List<AuditOrderSubinventory> = listOf()
        set(value) {
            field = value

            originalList = value
            filteredList = value.sortedBy { it.itemCode!!.lowercase() }.toMutableList()

            notifyDataSetChanged()
        }

    inner class AuditItemsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = AuditItemItemBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuditItemsViewHolder {
        val binding = AuditItemItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AuditItemsViewHolder(binding.root)
    }

    override fun getItemCount(): Int = filteredList.size

    override fun onBindViewHolder(holder: AuditItemsViewHolder, position: Int) {
        val item = filteredList[position]

        with(holder.binding) {

            itemCode.text = item.itemCode

            if (item.countingQty != null && item.countingQty != 0.0) {
                itemCode.setTextColor(context.getColor(R.color.green))
                countedQty.setTextColor(context.getColor(R.color.green))
                countedQty.text = item.countingQty.toString()
            } else {
                itemCode.setTextColor(context.getColor(R.color.black))
                countedQty.setTextColor(context.getColor(R.color.black))
                countedQty.text = "0"
            }
        }
    }

    // -----------------------
    //       FILTER LOGIC
    // -----------------------
    override fun getFilter(): Filter {
        return object : Filter() {

            override fun performFiltering(query: CharSequence?): FilterResults {
                val search = query?.toString()?.lowercase()?.trim() ?: ""

                val resultList = if (search.isEmpty()) {
                    originalList
                } else {
                    originalList.filter {
                        it.itemCode!!.lowercase().contains(search)
                    }
                }

                return FilterResults().apply {
                    values = resultList
                }
            }

            override fun publishResults(query: CharSequence?, results: FilterResults?) {
                filteredList =
                    (results?.values as? List<AuditOrderSubinventory>)?.toMutableList()
                        ?: mutableListOf()

                notifyDataSetChanged()
            }
        }
    }

    // Sort manually if needed
    fun sortAlphabetically() {
        filteredList.sortBy { it.itemCode!!.lowercase() }
        notifyDataSetChanged()
    }
}
