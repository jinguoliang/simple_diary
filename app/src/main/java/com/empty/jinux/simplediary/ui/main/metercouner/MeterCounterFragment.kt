package com.empty.jinux.simplediary.ui.main.metercouner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.empty.jinux.baselibaray.utils.inflate
import com.empty.jinux.baselibaray.view.recycleview.Item
import com.empty.jinux.baselibaray.view.recycleview.ItemController
import com.empty.jinux.baselibaray.view.recycleview.withItems
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.report.Reporter
import com.empty.jinux.simplediary.ui.main.BackPressProcessor
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_meter_counter.*
import javax.inject.Inject

class MeterCounterFragment : DaggerFragment(), MeterCounterContract.View, BackPressProcessor {
    @Inject
    internal lateinit var mPresenter: MeterCounterContract.Presenter

    @Inject
    internal lateinit var mReporter: Reporter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_meter_counter, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupFloatButton()
        mPresenter.start()
    }

    override fun showRecords() {
        recyclerView.withItems {
            add(Card("hello"))
        }
    }

    private fun setupFloatButton() {
        activity?.findViewById<FloatingActionButton>(R.id.fab_add_diary)?.apply {
            show()
            setImageResource(R.drawable.ic_add)
            setOnClickListener {
                addNewRecord()
                mReporter.reportClick("add meter counter record")
            }
        }
    }

    private var recordChangeDialog: AlertDialog? = null

    override fun addNewRecord() {
        val dialog = recordChangeDialog ?: createRecordChangeDialog()
        if (!dialog.isShowing) {
            dialog.show()
        }
    }

    private fun createRecordChangeDialog(): AlertDialog {
        recordChangeDialog = AlertDialog.Builder(context!!).apply {
            setTitle(R.string.add_new_record)
            setView(R.layout.dialog_meter_counter_change_record)
            val view = view!!
            setPositiveButton(R.string.ok) { dialog, _ ->
                val name = view.findViewById<EditText>(R.id.name).text.toString()
                val meterCounterValue = view.findViewById<EditText>(R.id.meterCounterValue).text.toString().toInt()
                val meterCounterUnit = view.findViewById<EditText>(R.id.meterCounterUnit).text.toString()
                mPresenter.addNewRecord(name, meterCounterValue, meterCounterUnit)
                recordChangeDialog?.dismiss()
            }
            setNegativeButton(R.string.cancel) { _, _ ->
                recordChangeDialog?.cancel()
            }
        }.create()
        return recordChangeDialog!!
    }
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
