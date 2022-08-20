package co.example.sunmibanglaprinter

import android.app.Application
import android.graphics.Bitmap
import co.example.common.constant.AppConstant
import co.example.dateutil.DateTimeFormat
import co.example.dateutil.DateTimeParser
import co.example.entity.home.AmountWiseDistributionEntity
import co.example.entity.room.BusStoppageEntity
import co.example.entity.room.TicketFormatEntity
import co.example.sharedpref.SharedPrefHelper
import co.example.sharedpref.SpKey

class SunmiTicketPrintHelper(
    private val application: Application,
    private val sharedPrefHelper:SharedPrefHelper
) {

    fun printTicket(
        busStoppageEntity: BusStoppageEntity,
        ticketFormatList: List<TicketFormatEntity>,
        ticketFare: Int,
        ticketSerial:Int,
        ticketQuantity:Int,
        passengerPhone:String,
        bitmapList:List<Bitmap> = listOf(),
        ticketPrintErrorCallback:()->Unit,
        ticketPrintSuccessCallback:()->Unit
    ) {

    }



    fun printReport(
        ticketCount:Int,
        ticketAmount:Int,
        amountWiseDistribution: List<AmountWiseDistributionEntity>,
        ticketFormatList: List<TicketFormatEntity>
    ){
    }

    private fun getCompanyNameTicketFormat(
        ticketFormatList: List<TicketFormatEntity>
    ): TicketFormatEntity{
        if (ticketFormatList.isNotEmpty()) {
            ticketFormatList.forEach {
                if (it.type == AppConstant.companyName){
                    return it
                }
            }
        }
        return  TicketFormatEntity(type =AppConstant.companyName, is_bold = true,
            font_size = 25, is_center = false,
            name = sharedPrefHelper.getString(SpKey.companyName) )
    }

}