package com.empty.jinux.simplediary

import com.empty.jinux.simplediary.data.source.DiariesDataSource
import com.empty.jinux.simplediary.location.LocationManager
import com.empty.jinux.simplediary.ui.diarydetail.DiaryDetailContract
import com.empty.jinux.simplediary.ui.diarydetail.presenter.DiaryDetailPresenter
import com.empty.jinux.simplediary.utils.any
import com.empty.jinux.simplediary.weather.WeatherManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.*
import org.mockito.Mockito.verify

/**
 * Created by jingu on 2018/3/3.
 */
class DiaryDetailPresenterTest {

    @Mock
    lateinit var mDiaryRepository: DiariesDataSource
    @Mock
    lateinit var mView: DiaryDetailContract.View
    @Mock
    lateinit var mLocationManager: LocationManager
    @Mock
    lateinit var mWeatherManager: WeatherManager

    @Captor
    lateinit var mGetDiaryCallbackCaptor: ArgumentCaptor<out DiariesDataSource.GetDiaryCallback>

    lateinit var mPresenter: DiaryDetailPresenter

    @Before
    fun setupPresenter() {
        MockitoAnnotations.initMocks(this)

        mPresenter = DiaryDetailPresenter(
                mDiaryRepository,
                mView,
                mLocationManager,
                mWeatherManager
        )
        mPresenter.setWordCountToday(8)
    }

    @Test
    fun testStart_new() {
        mPresenter.start()
        assertTrue(mPresenter.isNewDiary)
    }

    @Test
    fun testStart_view() {
        mPresenter.setDiaryId(23L)
        mPresenter.setWordCountToday(8)
        mPresenter.start()
        val idArg = ArgumentCaptor.forClass(Long::class.java)
        verify(mDiaryRepository).getDiary(idArg.capture(), any())
        assertEquals(idArg.value, 23L)
    }
}