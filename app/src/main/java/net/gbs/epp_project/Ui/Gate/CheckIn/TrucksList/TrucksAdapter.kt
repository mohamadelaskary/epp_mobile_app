package net.gbs.epp_project.Ui.Gate.CheckIn.TrucksList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import net.gbs.epp_project.Model.Vehicle
import net.gbs.epp_project.Ui.ContainersReceiving.CustomerDataSearch.Truck
import net.gbs.epp_project.databinding.TransactAddedLineLayoutBinding
import net.gbs.epp_project.databinding.TruckItemLayoutBinding
import net.gbs.epp_project.databinding.VehicleItemLayoutBinding

class TrucksAdapter(val onTruckItemClicked: OnTruckItemClicked) : Adapter<TrucksAdapter.TrucksViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrucksViewHolder {
        val binding = VehicleItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TrucksViewHolder(binding.root)
    }

    var trucksList:List<Vehicle> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onBindViewHolder(
        holder: TrucksViewHolder,
        position: Int
    ) {
        val vehicle = trucksList[position]
        with(holder.binding){
            salesOrderNumber.text = vehicle.salesOrderNumber
            customerName.text     = vehicle.customerName
            plateNo.text          = vehicle.plateNo
            containers.text       = vehicle.listOfContainers[0]
            item.setOnClickListener {
                onTruckItemClicked.onTruckItemClicked(vehicle)
            }
        }
    }

    override fun getItemCount(): Int {
        return trucksList.size
    }

    inner class TrucksViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        val binding = VehicleItemLayoutBinding.bind(itemView)
    }

    interface OnTruckItemClicked {
        fun onTruckItemClicked(truck: Vehicle)
    }
}