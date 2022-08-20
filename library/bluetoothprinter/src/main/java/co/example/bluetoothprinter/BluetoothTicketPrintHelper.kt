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
        try {
            if (bluetoothThreadPrinterHelper.isBluetoothEnable()){
                val currentDeviceDate = DateTimeParser.getCurrentDeviceDateTime(DateTimeFormat.outputDMY)
                val currentDeviceTime = DateTimeParser.getCurrentDeviceDateTime(DateTimeFormat.outputHMSA)
                var countImageIndex = 0
                val direction = sharedPrefHelper.getInt(SpKey.selectedDirection)

                ticketFormatList.forEach{ ticket ->
                    if (ticket.is_center != null){
                        if (ticket.is_center!!) bluetoothThreadPrinterHelper.setAlignment(Alignment.CENTER)
                        else bluetoothThreadPrinterHelper.setAlignment(Alignment.LEFT)
                    }
                    when (ticket.type) {
                        AppConstant.companyName -> bluetoothThreadPrinterHelper.printEnglishText("${ticket.name}\n",
                            TextSize.valueOf(ticket.bluetooth_font_size), isBold = ticket.is_bold)

                        AppConstant.countermanName -> bluetoothThreadPrinterHelper.printEnglishText(
                            "${ticket.leading_text}${sharedPrefHelper.getString(SpKey.userName)}\n",
                            TextSize.valueOf(ticket.bluetooth_font_size), isBold = ticket.is_bold)

                        AppConstant.ticketSerial -> bluetoothThreadPrinterHelper.printEnglishText("${ticket.leading_text}$ticketSerial\n",
                            TextSize.valueOf(ticket.bluetooth_font_size), isBold = ticket.is_bold)

                        AppConstant.ticketCount -> bluetoothThreadPrinterHelper.printEnglishText("${ticket.leading_text}$ticketQuantity\n",
                            TextSize.valueOf(ticket.bluetooth_font_size), isBold = ticket.is_bold)

                        AppConstant.routeName -> {
                            val route =  if(direction == 0) ticket.down_route_name else ticket.up_route_name
                            bluetoothThreadPrinterHelper.printEnglishText("${ticket.leading_text}$route\n",
                                TextSize.valueOf(ticket.bluetooth_font_size), isBold = ticket.is_bold)
                        }

                        AppConstant.direction -> {
                            val directionText= if(direction==0) AppConstant.down else AppConstant.up
                            bluetoothThreadPrinterHelper.printEnglishText(
                                "${ticket.leading_text}${directionText}\n", TextSize.valueOf(ticket.bluetooth_font_size), isBold = ticket.is_bold)
                        }

                        AppConstant.text -> bluetoothThreadPrinterHelper.printEnglishText("${ticket.text}\n",
                            TextSize.valueOf(ticket.bluetooth_font_size), isBold = ticket.is_bold)

                        AppConstant.complainNumber -> bluetoothThreadPrinterHelper.printEnglishText("${ticket.leading_text+ticket.text}\n",
                            TextSize.valueOf(ticket.bluetooth_font_size), isBold = ticket.is_bold)

                        AppConstant.date -> bluetoothThreadPrinterHelper.printEnglishText("${ticket.leading_text+currentDeviceDate}\n",
                            TextSize.valueOf(ticket.bluetooth_font_size), isBold = ticket.is_bold)

                        AppConstant.time -> bluetoothThreadPrinterHelper.printEnglishText("${ticket.leading_text+currentDeviceTime}\n",
                            TextSize.valueOf(ticket.bluetooth_font_size), isBold = ticket.is_bold)

                        AppConstant.dateTime -> bluetoothThreadPrinterHelper.printEnglishText("${ticket.leading_text}$currentDeviceDate $currentDeviceTime\n",
                            TextSize.valueOf(ticket.bluetooth_font_size), isBold = ticket.is_bold)

                        AppConstant.passengerNumber -> if (passengerNumber.isNotEmpty()){ bluetoothThreadPrinterHelper.printEnglishText(
                            "${ticket.leading_text}$passengerNumber\n", TextSize.valueOf(ticket.bluetooth_font_size), isBold = ticket.is_bold) }

                        AppConstant.toStoppageName -> bluetoothThreadPrinterHelper.printEnglishText("${ticket.leading_text}${busStoppageEntity.name}\n",
                                    TextSize.valueOf(ticket.bluetooth_font_size), isBold = ticket.is_bold)

                        AppConstant.stoppageName -> bluetoothThreadPrinterHelper.printEnglishText(
                            "${ticket.leading_text}${sharedPrefHelper.getString(SpKey.selectedBusStoppageName)}\n",
                                TextSize.valueOf(ticket.bluetooth_font_size), isBold = ticket.is_bold)

                        AppConstant.fare -> {
                            if (busStoppageEntity.isStudentFareType) {
                                bluetoothThreadPrinterHelper.printEnglishText("${ticket.leading_student_fare_text}${"$ticketFare"}${ticket.trailing_text}\n",
                                    TextSize.valueOf(ticket.bluetooth_font_size), isBold = ticket.is_bold)
                            }
                            else bluetoothThreadPrinterHelper.printEnglishText("${ticket.leading_text}${"$ticketFare"}${ticket.trailing_text}\n",
                                TextSize.valueOf(ticket.bluetooth_font_size), isBold = ticket.is_bold)
                        }

                        AppConstant.image->{
                            if(bitmapList.isNotEmpty()){
                                bluetoothThreadPrinterHelper.printImage(bitmapList[countImageIndex])
                                bluetoothThreadPrinterHelper.printEnglishText("\n" , TextSize.NORMAL)
                                countImageIndex++
                            }
                        }

                        AppConstant.qrCode ->{
                            var qrCodeData = ""
                            ticket.text.split(",").forEach { textQrCodeData->
                                when(textQrCodeData){
                                    AppConstant.qrSerial -> qrCodeData += ticketSerial
                                    AppConstant.qrName -> qrCodeData += ",${busStoppageEntity.name},"
                                    AppConstant.qrDate -> qrCodeData += "$currentDeviceDate,"
                                    AppConstant.qrTime -> qrCodeData += "$currentDeviceTime,"
                                }
                            }
                            bluetoothThreadPrinterHelper.printQRCode(qrCodeData,ticket.qrcode_size, AppConstant.qrcode_1)

                        }
                    }
                }
                bluetoothThreadPrinterHelper.printEnglishText("\n\n\n" ,TextSize.NORMAL)
                ticketPrintSuccessCallback.invoke()

            }
        }catch (e:Exception){
           ticketPrintErrorCallback.invoke()
        }
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