package co.example.uidomaindatamultimodule.ui.home

import android.os.Bundle
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import co.example.common.base.BaseBottomSheetDialogFragment
import co.example.uidomaindatamultimodule.databinding.DialogSpecialFareBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SpecialFareDialogFragment(
    private val specialFareCallBack: (specialFare: Int, phoneNumber: String) -> Unit,
    private val cancelCallback:()->Unit
) : BaseBottomSheetDialogFragment<DialogSpecialFareBinding>() {

    override fun viewBindingLayout(): DialogSpecialFareBinding =
        DialogSpecialFareBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, co.example.assets.R.style.DialogStyleWindowSoftInputMode)
    }

    override fun initializeView(savedInstanceState: Bundle?) {
        binding.mobileNumberEt.doAfterTextChanged {
            binding.specialFareNumberTextInputL.error = null
        }
        binding.specialFareEt.doAfterTextChanged {
            binding.specialFareTextInputL.error = null
        }

        binding.printBtn.setOnClickListener {
            val amount = binding.specialFareEt.text.toString().toIntOrNull() ?: 0
            val phoneNumber = binding.mobileNumberEt.text.toString()

            if (amount < 0) {
                binding.specialFareTextInputL.error = getString(co.example.assets.R.string.empty_field)
            } else if (phoneNumber.isEmpty() || phoneNumber.length < 8) {
                binding.specialFareNumberTextInputL.error = getString(co.example.assets.R.string.enter_phone_number)
            } else {
                val phoneNo = binding.mobileNoSP.selectedItem.toString() + binding.mobileNumberEt.text.toString()
                val newFare = binding.specialFareEt.text.toString().toInt()
                specialFareCallBack.invoke(newFare,phoneNo)
                dismiss()
            }
        }
        binding.cancelTV.setOnClickListener {
            cancelCallback()
            dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelCallback()
    }
}