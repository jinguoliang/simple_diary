package com.empty.jinux.simplediary.data.source.local.room.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.empty.jinux.simplediary.data.source.local.room.*

/**
 * Created by jingu on 2018/3/2.
 */

@Entity(tableName = TABLE_DIARY)
data class Diary(
        @PrimaryKey(autoGenerate = true) @ColumnInfo(name = COLUMN_ID) var id: Int?,
        @ColumnInfo(name = COLUMN_TITLE) var title: String = "",
        @ColumnInfo(name = COLUMN_CONTENT_TEXT) var contentText: String = "",
        @ColumnInfo(name = COLUMN_CONTENT_IMAGES) var contentImages: String = "",
        @ColumnInfo(name = COLUMN_CONTENT_SOUNDS) var contentSounds: String = "",
        @ColumnInfo(name = COLUMN_CONTENT_VIDEOS) var contentVideos: String = "",
        @ColumnInfo(name = COLUMN_WEATHER_ID) var weatherID: Long = -1,
        @ColumnInfo(name = COLUMN_EMOTION_ID) var emotionID: Long = -1,
        @ColumnInfo(name = COLUMN_LOCATION_LAN) var locationLatitude: Double = 0.0,
        @ColumnInfo(name = COLUMN_LOCATION_LON) var locationLongitude: Double = 0.0,
        @ColumnInfo(name = COLUMN_CREATE_TIME) var createTime: Long = -1,
        @ColumnInfo(name = COLUMN_DISPLAY_TIME) var displayTime: Long = -1,
        @ColumnInfo(name = COLUMN_LAST_CHANGE_TIME) var lastChangeTime: Long = -1
) {
}