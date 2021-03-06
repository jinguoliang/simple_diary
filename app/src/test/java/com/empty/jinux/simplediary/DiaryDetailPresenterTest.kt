package com.empty.jinux.simplediary

import com.empty.jinux.simplediary.data.source.DiariesDataSource
import com.empty.jinux.simplediary.location.LocationManager
import com.empty.jinux.simplediary.ui.diarydetail.DiaryDetailContract
import com.empty.jinux.simplediary.ui.diarydetail.presenter.DiaryDetailPresenter
import com.empty.jinux.simplediary.utils.any
import com.empty.jinux.simplediary.weather.WeatherManager
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.*
import org.mockito.Mockito.atLeastOnce
import org.mockito.Mockito.verify

/**
 * Created by jingu on 2018/3/3.
 */
class DiaryDetailPresenterTest {

    @Mock
    lateinit var mDiaryReposity: DiariesDataSource
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
                mDiaryReposity,
                mView,
                mLocationManager,
                mWeatherManager
        )
    }

    @Test
    fun testStart_new() {
        Mockito.`when`(mLocationManager.getLastLocation(any())).thenAnswer {
            mLocationManager.getCurrentAddress(any())
        }
        mPresenter.start()

//        verify(mWeatherManager).getCurrentWeather(Matchers.anyDouble(),
//                Matchers.anyDouble(), any())
        verify(mLocationManager, atLeastOnce()).getCurrentAddress(any())
    }

    @Test
    fun testStart_view() {
        mPresenter.setDiaryId(23L)
        mPresenter.start()
        val idArg = ArgumentCaptor.forClass(Long::class.java)
        verify(mDiaryReposity).getDiary(idArg.capture(), any())
        assertEquals(idArg.value, 23L)

//        verify(mView).showWeather(Matchers.anyString(),
//                Matchers.anyString())
//        verify(mView).showLocation(Matchers.anyString())
    }
}