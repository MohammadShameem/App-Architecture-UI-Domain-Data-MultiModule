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
        try {
            if (SunmiPrintHelper.instance.showPrinterStatus(application)) {
                val currentDeviceDate = BanglaConverterUtil.convertMonthNumberToBengali(DateTimeParser.getCurrentDeviceDateTime(DateTimeFormat.outputDMY))
                val currentDeviceTime = BanglaConverterUtil.convertNumberToBengaliNumber(DateTimeParser.getCurrentDeviceDateTime(DateTimeFormat.outputHMSA))
                var countImageIndex = 0
                val direction = sharedPrefHelper.getInt(SpKey.selectedDirection)
                ticketFormatList.forEach{ ticket ->
                    if (ticket.is_center != null){
                        if (ticket.is_center!!) SunmiPrintHelper.instance.setAlign(1)
                        else SunmiPrintHelper.instance.setAlign(0)
                    }
                    when (ticket.type) {
                        AppConstant.companyName -> {
                            SunmiPrintHelper.instance.printText("${ticket.name}\n", ticket.font_size.toFloat(),
                                isBold = ticket.is_bold, isUnderLine = false)
                        }
                        AppConstant.ticketSerial -> {
                            SunmiPrintHelper.instance.printText("${ticket.leading_text}${BanglaConverterUtil.
                            convertNumberToBengaliNumber(ticketSerial.toString())}\n", ticket.font_size.toFloat(),
                                isBold = ticket.is_bold, isUnderLine = false)
                        }
                        AppConstant.routeName -> {
                             val route =  if(direction == 0) ticket.down_route_name else ticket.up_route_name
                            SunmiPrintHelper.instance.printText("${ticket.leading_text}$route\n",
                                 size = ticket.font_size.toFloat(),isBold = ticket.is_bold,isUnderLine = false)
                        }
                        AppConstant.ticketCount -> {
                            SunmiPrintHelper.instance.printText("${ticket.leading_text}${ticketQuantity}\n", ticket.font_size.toFloat(),
                                isBold = ticket.is_bold, isUnderLine = false)
                        }
                        AppConstant.text -> {
                            SunmiPrintHelper.instance.printText("${ticket.text}\n", ticket.font_size.toFloat(),
                                isBold = ticket.is_bold, isUnderLine = false)
                        }
                        AppConstant.direction -> {
                            val directionText= if(direction==0) AppConstant.down else AppConstant.up
                            SunmiPrintHelper.instance.printText("${ticket.leading_text}${directionText}\n", ticket.font_size.toFloat(),
                                isBold = ticket.is_bold, isUnderLine = false)
                        }
                        AppConstant.countermanName -> {
                            SunmiPrintHelper.instance.printText("" +
                                    "${ticket.leading_text}${sharedPrefHelper.getString(SpKey.userName)}\n", ticket.font_size.toFloat(),
                                isBold = ticket.is_bold, isUnderLine = false)
                        }
                        AppConstant.complainNumber -> {
                            SunmiPrintHelper.instance.printText("${ticket.leading_text+ticket.text}\n", ticket.font_size.toFloat(),
                                isBold = ticket.is_bold, isUnderLine = false)
                        }
                        AppConstant.date -> {
                            SunmiPrintHelper.instance.printText("${ticket.leading_text+currentDeviceDate}\n", ticket.font_size.toFloat(),
                                isBold = ticket.is_bold, isUnderLine = false)
                        }

                        AppConstant.passengerNumber -> {
                            if (passengerPhone.isNotEmpty()){
                                SunmiPrintHelper.instance.printText("${ticket.leading_text+passengerPhone}\n", ticket.font_size.toFloat(),
                                    isBold = ticket.is_bold, isUnderLine = false)
                            }
                        }
                        AppConstant.time -> {
                            SunmiPrintHelper.instance.printText("${ticket.leading_text+currentDeviceTime}\n", ticket.font_size.toFloat(),
                                isBold = ticket.is_bold, isUnderLine = false)
                        }
                        AppConstant.dateTime -> {
                            SunmiPrintHelper.instance.printText("${ticket.leading_text}$currentDeviceDate $currentDeviceTime\n",
                                ticket.font_size.toFloat(), isBold = ticket.is_bold, isUnderLine = false)
                        }
                        AppConstant.toStoppageName -> {
                            SunmiPrintHelper.instance.printText("${ticket.leading_text}${busStoppageEntity.name}\n",
                                    ticket.font_size.toFloat(), isBold = ticket.is_bold, isUnderLine = false)
                        }
                        AppConstant.stoppageName -> {
                            SunmiPrintHelper.instance.printText("${ticket.leading_text}${sharedPrefHelper.getString(SpKey.selectedBusStoppageName)}\n",
                                    ticket.font_size.toFloat(), isBold = ticket.is_bold, isUnderLine = false)
                        }
                        AppConstant.fare -> {
                            if (busStoppageEntity.isStudentFareType){
                                SunmiPrintHelper.instance.printText(
                                    "${ticket.leading_student_fare_text}${BanglaConverterUtil.convertNumberToBengaliNumber("$ticketFare")}${ticket.trailing_text}\n",
                                    ticket.font_size.toFloat(), isBold = ticket.is_bold, isUnderLine = false)
                            }else{
                                SunmiPrintHelper.instance.printText(
                                    "${ticket.leading_text}${BanglaConverterUtil.convertNumberToBengaliNumber("$ticketFare")}${ticket.trailing_text}\n",
                                    ticket.font_size.toFloat(), isBold = ticket.is_bold, isUnderLine = false)
                            }
                        }

                        AppConstant.image->{
                            if(bitmapList.isNotEmpty()){
                                SunmiPrintHelper.instance.printBitmap(bitmapList[countImageIndex])
                                SunmiPrintHelper.instance.printText("\n" ,10f,isBold = true, isUnderLine = false)
                                countImageIndex++
                            }
                        }

                        AppConstant.qrCode ->{
                            var qrCodeData = ""
                            ticket.text.split(",").forEach { textQrCodeData->
                                when(textQrCodeData) {
                                    AppConstant.qrSerial -> qrCodeData += ticketSerial
                                    AppConstant.qrName -> qrCodeData += ",${busStoppageEntity.name},"
                                    AppConstant.qrDate -> qrCodeData += "$currentDeviceDate,"
                                    AppConstant.qrTime -> qrCodeData += "$currentDeviceTime,"
                                }
                            }
                            SunmiPrintHelper.instance.printQr(qrCodeData,ticket.qrcode_size,1)
                            SunmiPrintHelper.instance.printText("\n" ,10f,isBold = true, isUnderLine = false)
                        }
                    }
                }
                SunmiPrintHelper.instance.feedPaper()
                ticketPrintSuccessCallback.invoke()
            }

        }catch (e:Exception){
            ticketPrintErrorCallback.invoke()
        }
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