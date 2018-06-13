package com.empty.jinux.simplediary.ui.about

import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.core.view.updatePadding
import com.empty.jinux.simplediary.BuildConfig
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.ui.LockHelper
import org.jetbrains.anko.dimen
import javax.inject.Inject

class AboutActivity : DaggerAppCompatAboutActivity() {

    @Inject
    lateinit var lockHelper: LockHelper

    override fun onStart() {
        super.onStart()
        lockHelper.onStart(this)
    }

    override fun onStop() {
        super.onStop()
        lockHelper.onStop()
    }

    override fun onCreateHeader(icon: ImageView, slogan: TextView, version: TextView) {
        icon.setImageResource(R.drawable.ic_app_launcher)
        slogan.text = getString(R.string.app_name)
        slogan.updatePadding(top = dimen(R.dimen.about_page_slogin_padding_top))
        version.text = getString(R.string.app_version_fmt, BuildConfig.VERSION_NAME)
    }


    override fun onItemsCreated(items: MutableList<Any>) {
//        items.add(Category(getString(R.string.about_app_intro_hint)))
//        items.add(Card(getString(R.string.about_intro)))
//
//        items.add(Category("Developers"))
//        items.add(Contributor(R.drawable.jinux_head, "Jinux", "Developer & designer", "git@github.com:google/guava.githttp://weibo.com/jinux111"))
//
//        items.add(Category("Open Source Licenses"))
//        items.add(License("Dagger2", "Google", License.APACHE_2, "https://github.com/google/dagger"))
//        val square = "square"
//        items.add(License("Picasso", square, License.APACHE_2, "https://github.com/square/picasso"))
//        items.add(License("Retrofit", square, License.APACHE_2, "https://github.com/square/retrofit"))
//        items.add(License("OkHttp", square, License.APACHE_2, "https://github.com/square/okhttp"))
//
//        items.add(License("Anko", "IDEA", License.APACHE_2, "https://github.com/Kotlin/anko"))
    }
}