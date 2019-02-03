package com.empty.jinux.simplediary.ui.main.metercouner

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.empty.jinux.baselibaray.utils.inflate
import com.empty.jinux.baselibaray.view.recycleview.Item
import com.empty.jinux.baselibaray.view.recycleview.ItemController
import com.empty.jinux.baselibaray.view.recycleview.withItems
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.data.metercounter.MeterCounter
import com.empty.jinux.simplediary.report.Reporter
import com.empty.jinux.simplediary.ui.main.BackPressProcessor
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.android.support.DaggerFragment
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fragment_meter_counter.*
import kotlinx.android.synthetic.main.item_metercounter.*
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

    override fun showRecords(data: List<MeterCounter>) {
        recyclerView.withItems {
            data.forEach {
                add(MeterCounterCard(it, object : MeterCounterCard.Listenter {
                    override fun onIncrease(item: MeterCounter) {
                        mPresenter.updateRecord(item.copy(records = item.records.run { listOf(this.get(0) + 1) }))

                    }

                    override fun onDecrease(item: MeterCounter) {
                        mPresenter.updateRecord(item.copy(records = item.records.run { listOf(this.get(0) - 1) }))
                    }

                    override fun onEdit(item: MeterCounter) {
                        changeRecord(item) {
                            mPresenter.updateRecord(it)
                        }
                    }
                }))
            }
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

    private var recordChangeDialog: MeterCounterEditDialog? = null

    override fun addNewRecord() {
        changeRecord(null) {
            mPresenter.addNewRecord(it)
        }
    }

    private fun changeRecord(meterCounter: MeterCounter?, onConfirm: (meterCounter: MeterCounter) -> Unit) {
        val dialog = recordChangeDialog
                ?: MeterCounterEditDialog(context!!, meterCounter, onConfirm)
        if (!dialog.isShowing()) {
            dialog.show()
        }
    }
}

data class MeterCounterCard(val meterCounter: MeterCounter,
                            val listener: Listenter) : Item {
    companion object Controller : ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            return ViewHolder(parent.inflate(R.layout.item_metercounter, false))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            item as MeterCounterCard
            holder as ViewHolder
            holder.title.text = item.meterCounter.name
            holder.value.text = item.meterCounter.records.sumBy { it }.toString()
            holder.unit.text = item.meterCounter.unit
            holder.increase.setOnClickListener {
                item.listener.onIncrease(item.meterCounter)
            }
            holder.decrease.setOnClickListener {
                item.listener.onDecrease(item.meterCounter)
            }
            holder.edit.setOnClickListener {
                item.listener.onEdit(item.meterCounter)
            }
        }

    }

    class ViewHolder(override val containerView: View)
        : RecyclerView.ViewHolder(containerView), LayoutContainer

    override val controller: ItemController = Controller

    interface Listenter {
        fun onIncrease(item: MeterCounter)
        fun onDecrease(item: MeterCounter)
        fun onEdit(item: MeterCounter)
    }
}

class MeterCounterEditDialog(
        val context: Context,
        meterCounter: MeterCounter?,
        val end: (meterCounter: MeterCounter) -> Unit
) {
    private var dialog: AlertDialog

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_meter_counter_change_record, null)
        val name = view.findViewById<EditText>(R.id.name)
        val meterCounterValue = view.findViewById<EditText>(R.id.meterCounterValue)
        val meterCounterUnit = view.findViewById<EditText>(R.id.meterCounterUnit)
        meterCounter?.also {
            name.setText(it.name)
            meterCounterUnit.setText(it.unit)
            meterCounterValue.setText(it.records.sumBy { it }.toString())
        }
        dialog = AlertDialog.Builder(context).apply {
            setTitle(R.string.add_new_record)
            setView(view)
            setPositiveButton(R.string.ok) { dialog, _ ->
                end(MeterCounter(meterCounter?.id ?: -1,
                        name.text.toString(),
                        meterCounterUnit.text.toString(),
                        listOf(meterCounterValue.text.toString().toInt())
                ))
                dialog.dismiss()
            }
            setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.cancel()
            }
        }.create()
    }

    fun show() {
        dialog.show()
    }

    fun isShowing(): Boolean {
        return dialog.isShowing
    }
}
