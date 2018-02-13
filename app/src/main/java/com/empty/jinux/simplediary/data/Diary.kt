/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.empty.jinux.simplediary.data

import com.google.common.base.Strings
import com.google.common.base.Objects
import java.util.*

/**
 * Immutable model class for a Diary.
 */
data class Diary
/**
 * Use this constructor to specify a completed Diary if the Diary already has an id (copy of
 * another Diary).
 *
 * @param title       title of the task
 * @param description description of the task
 * @param id          id of the task
 * @param completed   true if the task is completed, false if it's active
 */
constructor(val title: String, val description: String,
                          val id: String = UUID.randomUUID().toString(), val isCompleted: Boolean = false) {

    val titleForList: String
        get() = if (!Strings.isNullOrEmpty(title)) {
            title
        } else {
            description
        }

    val isActive: Boolean
        get() = !isCompleted

    val isEmpty: Boolean
        get() = Strings.isNullOrEmpty(title) && Strings.isNullOrEmpty(description)

    /**
     * Use this constructor to create a new completed Diary.
     *
     * @param title       title of the task
     * @param description description of the task
     * @param completed   true if the task is completed, false if it's active
     */
    constructor(title: String, description: String, completed: Boolean) : this(title, description, UUID.randomUUID().toString(), completed) {}

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val task = o as Diary?
        return Objects.equal(id, task!!.id) &&
                Objects.equal(title, task.title) &&
                Objects.equal(description, task.description)
    }

    override fun hashCode(): Int {
        return Objects.hashCode(id, title, description)
    }

    override fun toString(): String {
        return "Diary with title " + title!!
    }
}
/**
 * Use this constructor to create a new active Diary.
 *
 * @param title       title of the task
 * @param description description of the task
 */
/**
 * Use this constructor to create an active Diary if the Diary already has an id (copy of another
 * Diary).
 *
 * @param title       title of the task
 * @param description description of the task
 * @param id          id of the task
 */
