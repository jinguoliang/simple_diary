package com.empty.jinux.simplediary.intent

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.empty.jinux.simplediary.R

/**
 * Created by jingu on 2018/2/28.
 *
 *
 */

fun helpTranslate(context: Context) =
        buildViewIntent(context.getString(R.string.translateURL))

fun openDocument() = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
    addCategory(Intent.CATEGORY_OPENABLE)
    type = "*/*"
}

fun rateApp(context: Context) =
        buildViewIntent(context.getString(R.string.playStoreURL, context.packageName))

fun sendFeedback(context: Context) =
        buildSendToIntent(context.getString(R.string.feedbackURL))

fun shareApp(context: Context) = buildSendIntent(context.getString(R.string.share_app_content_fmt,
        context.getString(R.string.playStoreHttpsURL, context.packageName)))

fun shareContentIntent(context: Context, content: String) = buildSendIntent(content)

private fun buildSendIntent(content: String) = Intent().apply {
    action = Intent.ACTION_SEND
    type = "text/plain"
    putExtra(Intent.EXTRA_TEXT, content)
}

private fun buildSendToIntent(url: String) = Intent().apply {
    action = Intent.ACTION_SENDTO
    data = Uri.parse(url)
}

private fun buildViewIntent(url: String) = Intent().apply {
    action = Intent.ACTION_VIEW
    data = Uri.parse(url)
}