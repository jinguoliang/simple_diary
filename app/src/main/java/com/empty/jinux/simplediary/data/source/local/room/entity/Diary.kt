package com.empty.jinux.simplediary.data.source.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.empty.jinux.simplediary.data.source.local.room.TABLE_DIARY

/**
 * Created by jingu on 2018/3/2.
 */

@Entity(tableName = TABLE_DIARY)
data class Diary(
        @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Long?,
        @ColumnInfo(name = "title") var title: String = "",
        @ColumnInfo(name = "content_text") var contentText: String = "",
        @ColumnInfo(name = "content_images") var contentImages: String = "",
        @ColumnInfo(name = "content_sounds") var contentSounds: String = "",
        @ColumnInfo(name = "content_videos") var contentVideos: String = "",
        @Embedded(prefix = "weather") var weather: Weather? = null,
        @Embedded(prefix = "emotion") var emotion: Emotion? = null,
        @Embedded(prefix = "location") var location: Location? = null,
        @ColumnInfo(name = "display_time") var createTime: Long = -1,
        @ColumnInfo(name = "create_time") var displayTime: Long = -1,
        @ColumnInfo(name = "last_change_time") var lastChangeTime: Long = -1,
        @ColumnInfo(name = "deleted") var deleted: Boolean = false
)

data class Weather(
        var id: Long?,
        var name: String = "",
        var desc: String = "",
        var icon: String = ""
)

data class Location(
        var id: Long?,
        var latitude: Double = 0.0,
        var longitude: Double = 0.0,
        var address: String = ""
)

data class Emotion(
        var id: Long?,
        var icon: Long
)
