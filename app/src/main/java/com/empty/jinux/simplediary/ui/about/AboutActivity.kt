package com.empty.jinux.simplediary.ui.about

import android.widget.ImageView
import android.widget.TextView

import com.empty.jinux.simplediary.BuildConfig
import com.empty.jinux.simplediary.R

import me.drakeet.multitype.Items
import me.drakeet.support.about.AbsAboutActivity
import me.drakeet.support.about.Card
import me.drakeet.support.about.Category
import me.drakeet.support.about.Contributor
import me.drakeet.support.about.License

class AboutActivity : AbsAboutActivity() {

    override fun onCreateHeader(icon: ImageView, slogan: TextView, version: TextView) {
        icon.setImageResource(R.drawable.ic_done)
        slogan.text = "About Page By drakeet"
        version.text = "v" + BuildConfig.VERSION_NAME
    }


    override fun onItemsCreated(items: Items) {
        items.add(Category("介绍与帮助"))
        items.add(Card(getString(R.string.about_intro)))

        items.add(Category("Developers"))
        items.add(Contributor(R.drawable.ic_add, "drakeet", "Developer & designer", "http://weibo.com/drak11t"))
        items.add(Contributor(R.drawable.ic_add, "黑猫酱", "Developer", "https://drakeet.me"))
        items.add(Contributor(R.drawable.ic_add, "小艾大人", "Developer"))

        items.add(Category("Open Source Licenses"))
        items.add(License("MultiType", "drakeet", License.APACHE_2, "https://github.com/drakeet/MultiType"))
        items.add(License("about-page", "drakeet", License.APACHE_2, "https://github.com/drakeet/about-page"))
    }
}