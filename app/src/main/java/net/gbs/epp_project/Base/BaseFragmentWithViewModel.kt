package net.gbs.epp_project.Base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.viewbinding.ViewBinding
import net.gbs.epp_project.Tools.LoadingDialog
import java.lang.reflect.ParameterizedType
import androidx.navigation.findNavController

abstract class BaseFragmentWithViewModel<VM : AndroidViewModel, VB : ViewBinding> : Fragment() {
    lateinit var loadingDialog: LoadingDialog
    lateinit var viewModel: VM
    lateinit var binding: VB
    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = bindingInflater(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingDialog = LoadingDialog(requireContext())
        val parameterizedType = javaClass.genericSuperclass as? ParameterizedType

        // now get first actual class, which is the class of VM (ProfileVM in this case)
        @Suppress("UNCHECKED_CAST")
        val vmClass = parameterizedType?.actualTypeArguments?.getOrNull(0) as? Class<VM>?
        if (vmClass != null)
            viewModel = ViewModelProvider(
                this,
                BaseViewModelFactory(requireActivity().application!!,requireActivity())
            )[vmClass]
        else
            Log.i("BaseFragment", "could not find VM class for $this")
    }
}