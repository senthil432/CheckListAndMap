package com.example.listandmap.ui.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.listandmap.R
import com.example.listandmap.dataclass.CheckListResponse
import com.example.listandmap.ui.ui.checklist.CheckListContract
import kotlinx.android.synthetic.main.check_list_item_view.view.*

class BottomListAdapter(
    private val context: Context,
    private val checkList: ArrayList<CheckListResponse>?,
    private val rowClicked: CheckListContract.CheckedRowDetails
) : RecyclerView.Adapter<BottomListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.check_list_item_view, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBindListData(checkList!![position])

        holder.itemView.apply {
            cbTitle.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    rowClicked.onBottomRow(true, checkList[position])
                }
            }
        }
    }


    override fun getItemCount(): Int = checkList?.size ?: 0


    override fun getItemId(position: Int): Long = position.toLong()


    override fun getItemViewType(position: Int): Int = position


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun onBindListData(list: CheckListResponse) {

            itemView.apply {

                cbTitle.text = list.title

            }

        }

    }
}