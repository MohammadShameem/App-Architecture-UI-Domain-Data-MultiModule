package co.example.common.extfun

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog

import java.util.*


fun Activity.showCurrentDatePickerDialogWithDateLimit(maxDay:Int,callback:(pickDate:String)->Unit ){
    val fromCalender = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(this, { _, year, month, day ->
        callback.invoke("$year-${String.format("%02d", month+1)}-${String.format("%02d", day)}")
    }, fromCalender.get(Calendar.YEAR),  fromCalender.get(Calendar.MONTH), fromCalender.get(Calendar.DAY_OF_MONTH))
    val datePicker = datePickerDialog.datePicker
    datePicker.minDate = fromCalender.timeInMillis

    val toCalender = Calendar.getInstance()
    toCalender.set(fromCalender.get(Calendar.YEAR),fromCalender.get(Calendar.MONTH),fromCalender.get(Calendar.DAY_OF_MONTH)+maxDay)
    datePicker.maxDate = toCalender.timeInMillis
    datePickerDialog.show()

}

fun Activity.showDatePickerDialog(year:Int,month:Int,day:Int,callback:(pickDate:String)->Unit){
    val calendar = Calendar.getInstance()
    calendar.set(year,month,day)
    DatePickerDialog(this, { _, selectedYear, monthOfYear, dayOfMonth ->
            callback.invoke("$selectedYear-${String.format("%02d", monthOfYear+1)}-${String.format("%02d", dayOfMonth)}")
        }, calendar.get(Calendar.YEAR),  calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
}

fun Activity.showTimePickerDialog(callback:(pickTime:String)->Unit){
    val cal = Calendar.getInstance()
    TimePickerDialog(this, {_, hour, minute ->
        callback.invoke("$hour:$minute:00")
    }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()
}

