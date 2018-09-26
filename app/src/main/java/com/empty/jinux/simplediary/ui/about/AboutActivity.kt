package com.empty.jinux.simplediary.ui.about

import androidx.annotation.DrawableRes
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import com.empty.jinux.baselibaray.utils.inflate
import com.empty.jinux.baselibaray.view.recycleview.Item
import com.empty.jinux.baselibaray.view.recycleview.ItemController
import com.empty.jinux.simplediary.BuildConfig
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.intent.buildViewIntent
import com.empty.jinux.simplediary.report.Reporter
import com.empty.jinux.simplediary.ui.LockHelper
import org.jetbrains.anko.dimen
import javax.inject.Inject

class AboutActivity : DaggerAppCompatAboutActivity() {

    @Inject
    lateinit var lockHelper: LockHelper

    @Inject
    lateinit var mReporter: Reporter

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
        slogan.text = getString(R.string.app_slogan)
        slogan.updatePadding(top = dimen(R.dimen.about_page_slogin_padding_top))
        version.text = getString(R.string.app_version_fmt, BuildConfig.VERSION_NAME)
    }


    override fun onItemsCreated(items: MutableList<Item>) {
        items.add(Category(getString(R.string.about_app_intro_hint)))
        items.add(Card(getString(R.string.about_intro)))
//
        items.add(Category("Developers"))
        items.add(Contributor(R.drawable.jinux_head, "Jinux", "Developer & designer", "http://weibo.com/jinux111"))
//
        items.add(Category("Open Source Licenses"))
        items.add(License("Dagger2", "Google", License.APACHE_2, "https://github.com/google/dagger"))
        val square = "square"
        items.add(License("Picasso", square, License.APACHE_2, "https://github.com/square/picasso"))
        items.add(License("Retrofit", square, License.APACHE_2, "https://github.com/square/retrofit"))
        items.add(License("OkHttp", square, License.APACHE_2, "https://github.com/square/okhttp"))

        items.add(License("Anko", "IDEA", License.APACHE_2, "https://github.com/Kotlin/anko"))
    }
}

class Category(val title: String) : Item {
    companion object Controller : ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            return ViewHolder(parent.inflate(R.layout.layout_about_category, false))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            item as Category
            holder as ViewHolder
            holder.titleView.text = item.title
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleView = itemView.findViewById<TextView>(R.id.title)
    }

    override val controller: ItemController = Controller
}

class Card(val title: String) : Item {
    companion object Controller : ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            return ViewHolder(parent.inflate(R.layout.layout_about_card, false))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            item as Card
            holder as ViewHolder
            holder.content.text = item.title
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val content = itemView.findViewById<TextView>(R.id.content)
    }

    override val controller: ItemController = Controller
}

class Contributor(@param:DrawableRes val head: Int,
                  val name: String,
                  val title: String,
                  val hostUrl: String) : Item {
    companion object Controller : ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            return ViewHolder(parent.inflate(R.layout.layout_about_contributor, false))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            item as Contributor
            holder as ViewHolder
            holder.head.setImageResource(item.head)
            holder.name.text = item.name
            holder.title.text = item.title
            holder.itemView.setOnClickListener {
                holder.itemView.context.startActivity(buildViewIntent(item.hostUrl))
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val head = itemView.findViewById<ImageView>(R.id.head)
        val name = itemView.findViewById<TextView>(R.id.name)
        val title = itemView.findViewById<TextView>(R.id.title)
    }

    override val controller: ItemController = Controller
}

class License(val title: String,
              val autor: String,
              val license: String,
              val host: String) : Item {
    companion object Controller : ItemController {
        const val APACHE_2 = ""

        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            return ViewHolder(parent.inflate(R.layout.layout_about_license, false))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            item as License
            holder as ViewHolder
            holder.title.text = item.title
            holder.author.text = item.autor
            holder.license.text = item.license
            holder.host.text = item.host
            holder.itemView.setOnClickListener {
                holder.itemView.context.startActivity(buildViewIntent(item.host))
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val author = itemView.findViewById<TextView>(R.id.author)
        val host = itemView.findViewById<TextView>(R.id.host)
        val license = itemView.findViewById<TextView>(R.id.license)
        val title = itemView.findViewById<TextView>(R.id.title)
    }

    override val controller: ItemController = Controller
}