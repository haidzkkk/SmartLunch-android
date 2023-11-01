package com.fpoly.smartlunch.ultis

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
object StringUltis {
    val dateIso8601Format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    val dateIso8601Format2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    val dateTimeFormat = SimpleDateFormat("HH:mm:ss")
    val dateTimeHourFormat = SimpleDateFormat("HH:mm")
    val dateTimeDayFormat = SimpleDateFormat("hh:mm aa")
    val dateTimeDateFormat = SimpleDateFormat("HH:mm EEE dd/MM")
    val dateDayFormat = SimpleDateFormat("EEE dd/MM/yy")
    val dateDay2Format = SimpleDateFormat("EEE dd/MM")
    val dateFormat = SimpleDateFormat("dd/MM/yyyy")
    val dateMonthFormat = SimpleDateFormat("MMM yyyy")
    val dateDayTimeFormat = SimpleDateFormat("EEE dd/MM/yy\nHH:mm:ss")
}

fun String.convertToStringFormat(inputDateFormat: SimpleDateFormat, outputDateFormat: SimpleDateFormat): String =
    try {
        val date = inputDateFormat.parse(this)
        date?.let {
            outputDateFormat.format(it)
        } ?: this
    } catch (e: Exception) {
        this
    }
fun String.convertToMillisFormat(inputDateFormat: SimpleDateFormat): Long =
    try {
        val date = inputDateFormat.parse(this)
        date?.let {
            date.time
        } ?: 0L
    } catch (e: Exception) {
        0L
    }
fun String.convertToDateFormat(inputDateFormat: SimpleDateFormat): Date? =
    try {
        val date = inputDateFormat.parse(this)
        date.let {
            date
        }
    } catch (e: Exception) {
        null
    }
fun Date.convertDateToStringFormat(outputDateFormat: SimpleDateFormat): String = outputDateFormat.format(this)
fun Date.compareWithString(strDate: String ,inputDateFormat: SimpleDateFormat) : Boolean {
    var date1 = StringUltis.dateFormat.parse(StringUltis.dateFormat.format(this))
    var date2 = StringUltis.dateFormat.parse(StringUltis.dateFormat.format(inputDateFormat.parse(strDate)!!))
    return date1.time == date2.time;
}
fun Long.convertLongToStringFormat(outputDateFormat: SimpleDateFormat): String = outputDateFormat.format(this)
fun Long.convertLongToDate(): Date = Date(this)


