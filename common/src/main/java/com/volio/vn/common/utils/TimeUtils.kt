package com.volio.vn.common.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

object TimeUtils {
    @SuppressLint("SimpleDateFormat")
    fun formatTime(time: Long): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        return formatter.format(time)
    }

    @SuppressLint("SimpleDateFormat")
    fun formatTimeHHmm(time: Long): String {
        val formatter = SimpleDateFormat("HH:mm")
        return formatter.format(time)
    }
    @SuppressLint("SimpleDateFormat")
    fun formatTimeDayLockScreen(time: Long): String {
        val formatter = SimpleDateFormat("E, dd MMM")
        return formatter.format(time)
    }
    fun formatTimeToDate(time: Long): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        return formatter.format(time)
    }

    fun formatTimeNameSlideShow(time: Long): String {
        val formatter = SimpleDateFormat("yyyyMMdd")
        return formatter.format(time)
    }

    fun millisToHour(ms: Long): String {
        val hour = TimeUnit.MILLISECONDS.toHours(ms)
        if (hour < 10)
            return "0$hour"
        return hour.toString()
    }

    fun millisToMinutes(ms: Long): String {
        val min = TimeUnit.MILLISECONDS.toMinutes(ms)
        if (min < 10)
            return "0$min"
        return min.toString()
    }

    fun millisToSeconds(ms: Long): String {
        val sec = TimeUnit.MILLISECONDS.toSeconds(ms)
        if (sec < 10)
            return "0$sec"
        return sec.toString()
    }

    fun millisToTick(ms: Long): String {
        val tick = TimeUnit.MILLISECONDS.toMillis(ms) % 1000 / 100
//        if (tick < 10)
//            return "0$tick"
        return tick.toString()
    }

    fun formatMillisToTimeProgress(ms: Long): String {
        val time = ms / 1000
        val h = time / 3600
        val m = (time - h * 3600) / 60
        val s = time - h * 3600 - m * 60
        return if (h > 0) {
            String.format("%01d:%02d:%02d", h, m, s)
        } else {
            String.format("%01d:%02d", m, s)
        }
    }

    fun getTextByMsNew(ms: Long): String {
        val time = ms / 1000
        val h = time / 3600
        val m = (time - h * 3600) / 60
        val s = time - h * 3600 - m * 60
        return if (h > 0) {
            String.format("%02d:%02d:%02d", h, m, s)
        } else {
            String.format("%02d:%02d", m, s)
        }
    }

    fun getTextByMsCut(ms: Long): String {
        val time = ms / 1000
        val h = time / 3600
        val m = (time - h * 3600) / 60
        val s = time - h * 3600 - m * 60
        val ms = ms % 1000
        return String.format("%02d:%02d:%02d", h, m, s) + "." + ms

    }

}