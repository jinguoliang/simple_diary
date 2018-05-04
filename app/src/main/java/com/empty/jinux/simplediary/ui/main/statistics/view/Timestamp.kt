/*
 * Copyright (C) 2015-2017 Álinson Santos Xavier <isoron@gmail.com>
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

package com.empty.jinux.simplediary.ui.main.statistics.view

import java.util.*
import java.util.Calendar.DAY_OF_WEEK

class Timestamp(val unixTime: Long) {

    val weekday: Int
        get() = toCalendar().get(DAY_OF_WEEK) % 7

    init {
        if (unixTime < 0 || unixTime % DAY_LENGTH != 0L)
            throw IllegalArgumentException(
                    "Invalid unix time: $unixTime")
    }

    constructor(cal: GregorianCalendar) : this(cal.timeInMillis) {}

    /**
     * Returns -1 if this timestamp is older than the given timestamp, 1 if this
     * timestamp is newer, or zero if they are equal.
     */
    fun compare(other: Timestamp): Int {
        return java.lang.Long.signum(this.unixTime - other.unixTime)
    }


    operator fun minus(days: Int): Timestamp {
        return plus(-days)
    }

    operator fun plus(days: Int): Timestamp {
        return Timestamp(unixTime + DAY_LENGTH * days)
    }

    /**
     * Returns the number of days between this timestamp and the given one. If
     * the other timestamp equals this one, returns zero. If the other timestamp
     * is older than this one, returns a negative number.
     */
    fun daysUntil(other: Timestamp): Int {
        return ((other.unixTime - this.unixTime) / DAY_LENGTH).toInt()
    }

    fun isNewerThan(other: Timestamp): Boolean {
        return compare(other) > 0
    }

    fun isOlderThan(other: Timestamp): Boolean {
        return compare(other) < 0
    }


    fun toJavaDate(): Date {
        return Date(unixTime)
    }

    fun toCalendar(): GregorianCalendar {
        val day = GregorianCalendar(TimeZone.getTimeZone("GMT"))
        day.timeInMillis = unixTime
        return day
    }

    override fun toString(): String {
        return "unixTime $unixTime"
    }

    companion object {

        val DAY_LENGTH: Long = 86400000

        val ZERO = Timestamp(0)

        /**
         * Given two timestamps, returns whichever timestamp is the oldest one.
         */
        fun oldest(first: Timestamp, second: Timestamp): Timestamp {
            return if (first.unixTime < second.unixTime) first else second
        }
    }
}
