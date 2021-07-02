package com.example.listandmap.ui.ui.checklist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.listandmap.R
import com.example.listandmap.dataclass.CheckListResponse
import com.example.listandmap.ui.ui.adapter.BottomListAdapter
import com.example.listandmap.ui.ui.adapter.TopListAdapter
import kotlinx.android.synthetic.main.activity_check_list.*


class CheckListActivity : AppCompatActivity(), CheckListContract.CheckListView,
    CheckListContract.CheckedRowDetails {
    private var checkListPresenter: CheckListPresenter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_list)

        initialize()
    }


    private fun initialize() {
        checkListPresenter = CheckListPresenter(this, this)
        checkListPresenter?.apiCall()
    }


    override fun updateListRecyclerView(checkList: ArrayList<CheckListResponse>) {
        loadCheckRecycler(checkList)
    }


    override fun refreshBottomList(checkList: ArrayList<CheckListResponse>) {
        loadCheckRecycler(checkList)
    }


    override fun refreshTopList(checkList: ArrayList<CheckListResponse>) {
        loadCheckedRecycler(checkList)
    }


    private fun loadCheckRecycler(checkList: ArrayList<CheckListResponse>) {
        recyclerCheckList.setHasFixedSize(true)
        val adapter = BottomListAdapter(this, checkList, this)
        recyclerCheckList.adapter = adapter
    }


    private fun loadCheckedRecycler(checkList: ArrayList<CheckListResponse>) {
        recyclerCheckedList.setHasFixedSize(true)
        val adapter = TopListAdapter(this, checkList, this)
        recyclerCheckedList.adapter = adapter
    }


    override fun onBottomRow(flag: Boolean?, row: CheckListResponse) {
        checkListPresenter?.onBottomRow(flag, row)
    }


    override fun onTopRow(flag: Boolean?, row: CheckListResponse) {
        checkListPresenter?.onTopRow(flag, row)
    }
}