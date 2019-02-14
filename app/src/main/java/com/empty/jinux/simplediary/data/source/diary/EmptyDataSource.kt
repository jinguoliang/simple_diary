package com.empty.jinux.simplediary.data.source.diary

/**
 * Created by jingu on 2018/3/15.
 */
class EmptyDataSource : DiariesDataSource {
    override fun deleteDiaryAsync(diaryId: Long, callback: DiariesDataSource.OnCallback<Boolean>) {
        callback.onResult(true)
    }

    override fun getDiaries(callback: DiariesDataSource.LoadDiariesCallback) {
        callback.onDiariesLoaded(arrayListOf())
    }

    override fun getDiary(diaryId: Long, callback: DiariesDataSource.GetDiaryCallback) {
        callback.onDataNotAvailable()
    }

    override fun save(diary: Diary, callback: DiariesDataSource.OnCallback<Long>) {
    }

    override fun refreshDiaries() {
    }

    override fun deleteAllDiaries() {
    }

    override fun deleteDiary(diaryId: Long) {
    }
}