package co.example.dateutil

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateTimeParser {

    fun getCurrentDeviceDateTime(format:String):String{
        val c = Calendar.getInstance().time
        val df = SimpleDateFormat(format, Locale.US)
        return df.format(c)
    }

    fun convertReadableDateTime(givenDateTime:String?,dateFormat:String,outputFormat:String):String{
        var formatDate = ""
        var sf = SimpleDateFormat(dateFormat, Locale.US)
        try {
            givenDateTime?.let {
                val parseDate = sf.parse(it)
                sf = SimpleDateFormat(outputFormat, Locale.US)
                formatDate = sf.format(parseDate!!)
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return formatDate
    }



}
