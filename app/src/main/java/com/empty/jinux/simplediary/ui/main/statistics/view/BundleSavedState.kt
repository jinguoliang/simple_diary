/*
 * Copyright (C) 2017 Álinson Santos Xavier <isoron@gmail.com>
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

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.v4.os.ParcelableCompat
import android.support.v4.os.ParcelableCompatCreatorCallbacks

class BundleSavedState : android.support.v4.view.AbsSavedState {

    val bundle: Bundle

    constructor(superState: Parcelable, bundle: Bundle) : super(superState) {
        this.bundle = bundle
    }

    constructor(source: Parcel, loader: ClassLoader) : super(source, loader) {
        this.bundle = source.readBundle(loader)
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        out.writeBundle(bundle)
    }

    companion object {
        val CREATOR: Parcelable.Creator<BundleSavedState> = ParcelableCompat.newCreator(
                object : ParcelableCompatCreatorCallbacks<BundleSavedState> {
                    override fun createFromParcel(source: Parcel,
                                                  loader: ClassLoader): BundleSavedState {
                        return BundleSavedState(source, loader)
                    }

                    override fun newArray(size: Int): Array<BundleSavedState> {
                        return arrayOf()
                    }
                })
    }
}