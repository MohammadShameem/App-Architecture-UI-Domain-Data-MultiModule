package co.example.uidomaindatamultimodule.ui.home

import android.os.Bundle
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import co.example.common.base.BaseBottomSheetDialogFragment
import co.example.common.constant.AppConstant
import co.example.uidomaindatamultimodule.databinding.DialogMulitpleTicketPrintBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MultipleTicketDialogFragment(
    private val numberOfTicketCallBack: (numberOfTicket: Int) -> Unit,
    private val cancelCallback:()-> Unit
) : BaseBottomSheetDialogFragment<DialogMulitpleTicketPrintBinding>() {

    override fun viewBindingLayout(): DialogMulitpleTicketPrintBinding =
        DialogMulitpleTicketPrintBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, co.example.assets.R.style.DialogStyleWindowSoftInputMode)
    }

    override fun initializeView(savedInstanceState: Bundle?) {

        binding.ticketNumberEt.doAfterTextChanged {
            binding.ticketNumberTextInputL.error = null
        }

        binding.printBtn.setOnClickListener {
            if (binding.ticketNumberEt.text?.isEmpty() == true)
                binding.ticketNumberTextInputL.error = getString(co.example.assets.R.string.please_give_valid_input)
            else if (binding.ticketNumberEt.text.toString().toInt()<=1)
                binding.ticketNumberTextInputL.error = getString(co.example.assets.R.string.msg_lowest_ticket_count)
            else if (binding.ticketNumberEt.text.toString().toInt() > AppConstant.maxTicketPrintNumber)
                binding.ticketNumberTextInputL.error = getString(co.example.assets.R.string.allowed_ticket_count)
            else {
                numberOfTicketCallBack.invoke(binding.ticketNumberEt.text.toString().toInt())
                dismiss()
            }
        }
        binding.cancelBtn.setOnClickListener {
            cancelCallback.invoke()
            dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelCallback.invoke()
    }

}