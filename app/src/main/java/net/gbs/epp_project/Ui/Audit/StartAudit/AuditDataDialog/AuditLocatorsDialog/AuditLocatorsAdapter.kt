package net.gbs.epp_project.Ui.Audit.StartAudit.AuditDataDialog.AuditLocatorsDialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import net.gbs.epp_project.Model.AuditLocator
import net.gbs.epp_project.Model.AuditOrderSubinventory
import net.gbs.epp_project.R
import net.gbs.epp_project.databinding.AuditLocatorItemBinding

class AuditLocatorsAdapter(private val context: Context)
    : RecyclerView.Adapter<AuditLocatorsAdapter.AuditLocatorsViewHolder>(), Filterable {

    private var originalList: List<AuditOrderSubinventory> = listOf()
    private var filteredList: MutableList<AuditOrderSubinventory> = mutableListOf()

    var auditOrderList: List<AuditOrderSubinventory> = listOf()
        set(value) {
            field = value

            originalList = value
            filteredList = value.sortedBy { it.locatorCode!!.lowercase() }.toMutableList()

            notifyDataSetChanged()
        }

    inner class AuditLocatorsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = AuditLocatorItemBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuditLocatorsViewHolder {
        val binding = AuditLocatorItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AuditLocatorsViewHolder(binding.root)
    }

    override fun getItemCount(): Int = filteredList.size

    override fun onBindViewHolder(holder: AuditLocatorsViewHolder, position: Int) {
        val item = filteredList[position]

        holder.binding.locatorCode.text = item.locatorCode

        if (item.isFullyScannedLocator) {
            holder.binding.locatorCode.setTextColor(context.getColor(R.color.green))
        } else {
            holder.binding.locatorCode.setTextColor(context.getColor(R.color.black))
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
                        it.locatorCode!!.lowercase().contains(search)
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

    // لو عايز تعمل Sort يدويًا في أي وقت
    fun sortAlphabetically() {
        filteredList.sortBy { it.locatorCode!!.lowercase() }
        notifyDataSetChanged()
    }
}
