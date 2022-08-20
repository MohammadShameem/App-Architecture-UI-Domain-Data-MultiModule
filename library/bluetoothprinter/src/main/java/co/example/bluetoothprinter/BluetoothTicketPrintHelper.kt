package co.example.bluetoothprinter

import android.graphics.Bitmap
import co.example.common.constant.AppConstant
import co.example.dateutil.DateTimeFormat
import co.example.dateutil.DateTimeParser
import co.example.entity.home.AmountWiseDistributionEntity
import co.example.entity.room.BusStoppageEntity
import co.example.entity.room.TicketFormatEntity
import co.example.sharedpref.SharedPrefHelper
import co.example.sharedpref.SpKey

class BluetoothTicketPrintHelper(
    private val bluetoothThreadPrinterHelper: BluetoothThreadPrinterHelper,
    private val sharedPrefHelper: SharedPrefHelper
) {

    fun printTicket(
        busStoppageEntity: BusStoppageEntity,
        ticketFormatList: List<TicketFormatEntity>,
        ticketFare: Int,
        ticketSerial:Int,
        ticketQuantity:Int,
        passengerNumber: String,
        bitmapList:List<Bitmap> = listOf(),
        ticketPrintErrorCallback:()->Unit,
        ticketPrintSuccessCallback:()->Unit
    ) {

    }

    fun printReport(ticketCount:Int,
        ticketAmount:Int,
        ticketFormatList: List<TicketFormatEntity>,
        amountWiseDistribution: List<AmountWiseDistributionEntity>
    ){

    }

    private fun getCompanyNameTicketFormat(
        ticketFormatList: List<TicketFormatEntity>
    ): String{
        if (ticketFormatList.isNotEmpty()) {
            ticketFormatList.forEach {
                if (it.type == AppConstant.companyName){
                    return it.name
                }
            }
        }
        return  sharedPrefHelper.getString(SpKey.companyName)
    }


}