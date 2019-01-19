package com.empty.jinux.simplediary.test_utils

import org.mockito.Mockito

/**
 * Created by jingu on 2018/3/3.
 */

fun <T> any(): T {
    Mockito.any<T>()
    return uninitialized()
}
private fun <T> uninitialized(): T = null as T