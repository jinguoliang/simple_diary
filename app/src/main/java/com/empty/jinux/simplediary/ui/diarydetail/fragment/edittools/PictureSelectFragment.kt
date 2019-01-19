package com.empty.jinux.simplediary.ui.diarydetail.fragment.edittools

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.empty.jinux.baselibaray.utils.inflate
import com.empty.jinux.baselibaray.view.recycleview.Item
import com.empty.jinux.baselibaray.view.recycleview.ItemController
import com.empty.jinux.baselibaray.view.recycleview.withItems
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.ui.diarydetail.fragment.DiaryDetailFragment
import com.empty.jinux.simplediary.ui.diarydetail.fragment.MFragment
import com.empty.jinux.simplediary.util.ImageUtil
import kotlinx.android.synthetic.main.fragment_select_picture.*
import java.io.File

class PictureSelectFragment : MFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_select_picture, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recyclerView.withItems {
            ImageUtil.getLatestPhoto(activity!!)?.apply {
                add(PictureItem(Uri.fromFile(File(second)), object: PictureItem.OnClickListener {
                    override fun onClick(uri: Uri) {
                        mParentFragment.insertPicture(uri)
                    }
                }))
            }
            add(AddPictureItem(View.OnClickListener { v ->
                mParentFragment.startActivityForResult(Intent(Intent.ACTION_GET_CONTENT).also {
                    it.type = "image/*"
                    it.addCategory(Intent.CATEGORY_DEFAULT)
                }, DiaryDetailFragment.REQUEST_SELECT_PICTURE)
            }))
        }
    }
}

class PictureItem(val uri: Uri, val onClickListener: OnClickListener) : Item {
    override val controller = object : ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            return Holder(parent.inflate(R.layout.item_picture, false))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as Holder
            item as PictureItem

            holder.pic.setImageURI(item.uri)
            holder.itemView.setOnClickListener { onClickListener.onClick(item.uri) }
        }
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pic: ImageView = itemView.findViewById(R.id.pic)
    }

    interface OnClickListener {
        fun onClick(uri: Uri)
    }

}

class AddPictureItem(val onClickListener: View.OnClickListener) : Item {
    override val controller = object : ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            return Holder(parent.inflate(R.layout.item_add_picture, false))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as Holder

            holder.itemView.setOnClickListener(onClickListener)
        }
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)

}