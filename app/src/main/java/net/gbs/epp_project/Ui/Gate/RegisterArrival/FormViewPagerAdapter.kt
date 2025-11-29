package net.gbs.epp_project.Ui.Gate.RegisterArrival

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import net.gbs.epp_project.Ui.Gate.RegisterArrival.FormScreens.DriverDataFragment
import net.gbs.epp_project.Ui.Gate.RegisterArrival.FormScreens.OrderDataFragment
import net.gbs.epp_project.Ui.Gate.RegisterArrival.FormScreens.VehicleDataFragment

class FormViewPagerAdapter(fragment: Fragment,private val viewModel: RegisterArrivalViewModel) : FragmentStateAdapter(fragment) {
    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> OrderDataFragment(viewModel)
            1 -> DriverDataFragment(viewModel)
            2 -> VehicleDataFragment(viewModel)
            else -> throw IllegalStateException()
        }
    }

    override fun getItemCount(): Int = 3
}