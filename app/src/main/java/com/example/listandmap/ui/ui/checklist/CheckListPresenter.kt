package com.example.listandmap.ui.ui.checklist

import android.content.Context
import com.example.listandmap.dataclass.CheckListResponse

class CheckListPresenter(
    context: Context,
    checkListView: CheckListContract.CheckListView
) : CheckListContract.CheckListPresenter {

    private var view: CheckListContract.CheckListView = checkListView
    private var model: CheckListContract.CheckListModel? = CheckListModel(context)


    override fun apiCall() {
        model?.getListFromApi(this)
    }


    override fun loadApiList(list: ArrayList<CheckListResponse>) {
        view.updateListRecyclerView(list)
        bottomList = list
    }


    companion object {
        var topList: ArrayList<CheckListResponse>? = ArrayList()
        var bottomList: ArrayList<CheckListResponse>? = ArrayList()
    }


    override fun onBottomRow(flag: Boolean?, row: CheckListResponse) {
        if (flag!!) {

            if (!bottomList.isNullOrEmpty()) {

                val it = bottomList?.iterator()
                while (it!!.hasNext()) {
                    if (it.next().id == row.id) {
                        it.remove()
                        break
                    }
                }

                view.refreshBottomList(bottomList!!)
            }

            topList?.add(0, row)
            view.refreshTopList(topList!!)
        }
    }


    override fun onTopRow(flag: Boolean?, row: CheckListResponse) {
        if (flag!!) {

            if (!topList.isNullOrEmpty()) {

                val it = topList?.iterator()
                while (it!!.hasNext()) {
                    if (it.next().id == row.id) {
                        it.remove()
                        break
                    }
                }

                view.refreshTopList(topList!!)
            }

            bottomList?.add(0, row)
            view.refreshBottomList(bottomList!!)
        }
    }
}