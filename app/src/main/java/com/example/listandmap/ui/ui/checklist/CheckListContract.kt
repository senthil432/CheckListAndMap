package com.example.listandmap.ui.ui.checklist

import com.example.listandmap.dataclass.CheckListResponse

class CheckListContract {

    interface CheckListModel {
        fun getListFromApi(presenter: CheckListPresenter)
    }

    interface CheckListView {
        fun updateListRecyclerView(checkList: ArrayList<CheckListResponse>)
        fun refreshBottomList(checkList: ArrayList<CheckListResponse>)
        fun refreshTopList(checkList: ArrayList<CheckListResponse>)
    }

    interface CheckListPresenter {
        fun apiCall()
        fun loadApiList(list: ArrayList<CheckListResponse>)
        fun onBottomRow(flag: Boolean?, row: CheckListResponse)
        fun onTopRow(flag: Boolean?, row: CheckListResponse)
    }


    interface CheckedRowDetails {
        fun onBottomRow(flag: Boolean?, row: CheckListResponse)
        fun onTopRow(flag: Boolean?, row: CheckListResponse)
    }
}