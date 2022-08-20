package co.example.uidomaindatamultimodule.ui.home
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import co.example.common.adapter.DataBoundListAdapter
import co.example.common.constant.AppConstant
import co.example.entity.room.BusStoppageEntity
import co.example.uidomaindatamultimodule.R
import co.example.uidomaindatamultimodule.databinding.ListItemStoppageBinding

class BusStoppageAdapter (private val callBack:(busStoppageEntity: BusStoppageEntity) -> Unit):
    DataBoundListAdapter<BusStoppageEntity, ListItemStoppageBinding>(

        diffCallback = object : DiffUtil.ItemCallback<BusStoppageEntity>() {
            override fun areItemsTheSame(oldItem: BusStoppageEntity, newItem: BusStoppageEntity): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: BusStoppageEntity, newItem: BusStoppageEntity): Boolean {
                return oldItem == newItem
            }

        }
    ) {
    private var isClickable:Boolean = true
    private lateinit var cardType: String
    private var isStudentFareActive = false

    override fun createBinding(parent: ViewGroup): ListItemStoppageBinding =
        ListItemStoppageBinding.inflate(LayoutInflater.from(parent.context),parent,false)


    /**
     * We need to show our item in three different ways
     * for example:
     *    if our card data Type is only stoppage then in our item only stoppage will be shown
     *    if our card data Type is only amount then in our item only amount will be shown
     *    and if our card data type is both then stoppage and amount will be shown
     * */

    override fun bind(binding: ListItemStoppageBinding, item: BusStoppageEntity, position: Int) {
        var fare = item.fare
        var updatedBusStoppageEntity = item
        if (isStudentFareActive){
            fare = item.studentFare
            updatedBusStoppageEntity = item.copy(isStudentFareType = true)
        }

        when(cardType){
            AppConstant.cardTypeBoth -> {
                binding.stoppageNameTv.text = item.name
                binding.stoppageFareTv.text = binding.root.context.getString(R.string.fare,fare)
            }
            AppConstant.cardTypeStoppage -> {
                binding.stoppageFareTv.isVisible = false
                binding.stoppageNameTv.text = item.name
            }
            AppConstant.cardTypeAmount -> {
                binding.stoppageNameTv.isVisible = false
                binding.stoppageFareTv.text = binding.root.context.getString(R.string.fare,fare)
            }
        }

        binding.root.setOnClickListener {
            if (isClickable) callBack.invoke(updatedBusStoppageEntity)
        }

    }

    fun setClickable(isClickable:Boolean){
        this.isClickable = isClickable
    }

    fun setStudentFare(isStudentFareActive: Boolean) {
        this.isStudentFareActive = isStudentFareActive
    }
    fun setCardDataType (cardType: String){
        this.cardType = cardType
    }

}