/*
 * Copyright (C) 2016 Álinson Santos Xavier <isoron@gmail.com>
 *
 * This file is part of Loop Habit Tracker.
 *
 * Loop Habit Tracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Loop Habit Tracker is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.empty.jinux.simplediary.util

import com.empty.jinux.simplediary.ui.main.statistics.view.Timestamp
import java.util.*
import java.util.Calendar.*

object DateUtils {

    private var fixedLocalTime: Long? = null

    private var fixedTimeZone: TimeZone? = null

    private var fixedLocale: Locale? = null

    /**
     * Time of the day when the new day starts.
     */
    val NEW_DAY_OFFSET = 3

    /**
     * Number of milliseconds in one day.
     */
    val DAY_LENGTH = (24 * 60 * 60 * 1000).toLong()

    /**
     * Number of milliseconds in one hour.
     */
    val HOUR_LENGTH = (60 * 60 * 1000).toLong()

    val localTime: Long
        get() {
            if (fixedLocalTime != null) return fixedLocalTime!!

            val tz = timezone
            val now = Date().time
            return now + tz.getOffset(now)
        }

    /**
     * @return array with week days numbers starting according to locale
     * settings, e.g. [2,3,4,5,6,7,1] in Europe
     */
    val localeWeekdayList: Array<Int>
        get() {
            val dayNumbers = arrayOf<Int>()
            val calendar = GregorianCalendar()
            calendar.set(DAY_OF_WEEK, calendar.firstDayOfWeek)
            for (i in dayNumbers.indices) {
                dayNumbers[i] = calendar.get(DAY_OF_WEEK)
                calendar.add(DAY_OF_MONTH, 1)
            }
            return dayNumbers
        }

    val longDayNames: Array<String>
        get() = getDayNames(GregorianCalendar.LONG)

    val shortDayNames: Array<String>
        get() = getDayNames(SHORT)

    val today: Timestamp
        get() = Timestamp(startOfToday)

    val startOfToday: Long
        get() = getStartOfDay(localTime - NEW_DAY_OFFSET * HOUR_LENGTH)

    val startOfTodayCalendar: GregorianCalendar
        get() = getCalendar(startOfToday)

    private val timezone: TimeZone
        get() = fixedTimeZone ?: TimeZone.getDefault()

    private val locale: Locale
        get() = fixedLocale ?: Locale.getDefault()

    fun applyTimezone(localTimestamp: Long): Long {
        val tz = timezone
        return localTimestamp - tz.getOffset(localTimestamp - tz.getOffset(localTimestamp))
    }

    fun formatHeaderDate(day: GregorianCalendar): String {
        val locale = locale
        val dayOfMonth = Integer.toString(day.get(DAY_OF_MONTH))
        val dayOfWeek = day.getDisplayName(DAY_OF_WEEK, SHORT, locale)
        return dayOfWeek + "\n" + dayOfMonth
    }

    private fun getCalendar(timestamp: Long): GregorianCalendar {
        val day = GregorianCalendar(TimeZone.getTimeZone("GMT"), locale)
        day.timeInMillis = timestamp
        return day
    }

    private fun getDayNames(format: Int): Array<String> {
        val wdays = arrayOf<String>()

        val day = GregorianCalendar()
        day.set(DAY_OF_WEEK, Calendar.SATURDAY)

        for (i in wdays.indices) {
            wdays[i] = day.getDisplayName(DAY_OF_WEEK, format, locale)
            day.add(DAY_OF_MONTH, 1)
        }

        return wdays
    }

    /**
     * @return array with weekday names starting according to locale settings,
     * e.g. [Mo,Di,Mi,Do,Fr,Sa,So] in Germany
     */
    fun getLocaleDayNames(format: Int): Array<String> {
        val days = arrayOf<String>()

        val calendar = GregorianCalendar()
        calendar.set(DAY_OF_WEEK, calendar.firstDayOfWeek)
        for (i in days.indices) {
            days[i] = calendar.getDisplayName(DAY_OF_WEEK, format,
                    locale)
            calendar.add(DAY_OF_MONTH, 1)
        }

        return days
    }

    fun getStartOfDay(timestamp: Long): Long {
        return timestamp / DAY_LENGTH * DAY_LENGTH
    }

    fun millisecondsUntilTomorrow(): Long {
        return startOfToday + DAY_LENGTH - (localTime - NEW_DAY_OFFSET * HOUR_LENGTH)
    }

    fun setFixedTimeZone(tz: TimeZone) {
        fixedTimeZone = tz
    }

    fun removeTimezone(timestamp: Long): Long {
        val tz = timezone
        return timestamp + tz.getOffset(timestamp)
    }

    fun setFixedLocalTime(timestamp: Long?) {
        fixedLocalTime = timestamp
    }

    fun setFixedLocale(locale: Locale) {
        fixedLocale = locale
    }

    fun truncate(field: TruncateField, timestamp: Long): Long {
        val cal = DateUtils.getCalendar(timestamp)

        when (field) {
            DateUtils.TruncateField.MONTH -> {
                cal.set(DAY_OF_MONTH, 1)
                return cal.timeInMillis
            }

            DateUtils.TruncateField.WEEK_NUMBER -> {
                val firstWeekday = cal.firstDayOfWeek
                val weekday = cal.get(DAY_OF_WEEK)
                var delta = weekday - firstWeekday
                if (delta < 0) delta += 7
                cal.add(Calendar.DAY_OF_YEAR, -delta)
                return cal.timeInMillis
            }

            DateUtils.TruncateField.QUARTER -> {
                val quarter = cal.get(Calendar.MONTH) / 3
                cal.set(DAY_OF_MONTH, 1)
                cal.set(Calendar.MONTH, quarter * 3)
                return cal.timeInMillis
            }

            DateUtils.TruncateField.YEAR -> {
                cal.set(Calendar.MONTH, Calendar.JANUARY)
                cal.set(DAY_OF_MONTH, 1)
                return cal.timeInMillis
            }

            else -> throw IllegalArgumentException()
        }
    }

    fun getUpcomingTimeInMillis(hour: Int, minute: Int): Long {
        val calendar = DateUtils.startOfTodayCalendar
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        var time: Long = calendar.timeInMillis

        if (DateUtils.localTime > time)
            time += DateUtils.DAY_LENGTH

        return applyTimezone(time)
    }

    enum class TruncateField {
        MONTH, WEEK_NUMBER, YEAR, QUARTER
    }
}
