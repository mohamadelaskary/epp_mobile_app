package net.gbs.epp_project.Ui.Gate.RegisterArrival.FormScreens

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import net.gbs.epp_project.Model.Container
import net.gbs.epp_project.R
import net.gbs.epp_project.Tools.Tools.clearInputLayoutError
import net.gbs.epp_project.Tools.Tools.containsOnlyDigits
import net.gbs.epp_project.Tools.Tools.getEditTextText
import net.gbs.epp_project.databinding.AddContainerDialogLayoutBinding

class AddContainerDialog(context: Context,private val container: Container?,private val onAddContainerButtonClicked: OnAddContainerButtonClicked):Dialog(context) {
    private lateinit var binding: AddContainerDialogLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddContainerDialogLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        clearInputLayoutError(binding.containerNo,binding.tareWeight,binding.grossWeight,binding.lotNo)
        if (container!=null)
            fillDate()
        binding.close.setOnClickListener {
            dismiss()
        }
        binding.addContainer.setOnClickListener {
            val containerNo = getEditTextText(binding.containerNo)
            val grossWeight = getEditTextText(binding.grossWeight)
            val lotNo = getEditTextText(binding.lotNo)
            val tareWeight = getEditTextText(binding.tareWeight)
            if(isReadyToAdd(containerNo,grossWeight,lotNo,tareWeight)){
                onAddContainerButtonClicked.onAddContainerButtonClicked(Container(
                    containerNo = containerNo,
                    grossWeight = grossWeight.toInt(),
                    lotNo = lotNo,
                    tareWeight = tareWeight.toInt()
                ),this)
            }
        }
    }

    private fun fillDate() {
        binding.containerNo.editText?.setText(container?.containerNo)
        binding.grossWeight.editText?.setText(container?.grossWeight.toString())
        binding.lotNo.editText?.setText(container?.lotNo)
        binding.tareWeight.editText?.setText(container?.tareWeight.toString())
        binding.containerNo.isEnabled = false
        binding.grossWeight.isEnabled = false
        binding.lotNo.isEnabled = false
        binding.tareWeight.isEnabled = false
        binding.addContainer.isEnabled = false

    }

    private fun isReadyToAdd(
        containerNo: String,
        grossWeight: String,
        lotNo: String,
        tareWeight: String
    ): Boolean {
        var isReady = true
        if (containerNo.isEmpty()){
            isReady = false
            binding.containerNo.error = context.getString(R.string.please_enter_container_number)
        }
        if (grossWeight.isEmpty()){
            isReady = false
            binding.grossWeight.error = context.getString(R.string.please_enter_gross_weight)
        }
        if (lotNo.isEmpty()){
            isReady = false
            binding.lotNo.error = context.getString(R.string.please_enter_lot_number)
        }
        if (tareWeight.isEmpty()){
            isReady = false
            binding.tareWeight.error = context.getString(R.string.please_enter_tare_weight)
        }
//        if (!containsOnlyDigits(grossWeight)){
//            isReady = false
//            binding.grossWeight.error = context.getString(R.string.please_enter_valid_gross_weight)
//        }
//        if (!containsOnlyDigits(tareWeight)){
//            isReady = false
//            binding.tareWeight.error = context.getString(R.string.please_enter_valid_tare_weight)
//        }
        return isReady
    }

    fun interface OnAddContainerButtonClicked{
        fun onAddContainerButtonClicked(container: Container,dialog: DialogInterface)
    }
}