package com.example.listandmap.ui.ui.checklist

import android.content.Context
import com.example.listandmap.R
import com.example.listandmap.restapi.ApiClient
import com.example.listandmap.restapi.ApiServiceInterface
import com.example.listandmap.utility.Util

class CheckListModel(private val context: Context) : CheckListContract.CheckListModel {

    private var api: ApiServiceInterface? = null


    init {
        api = ApiClient().callApiService()
    }


    override fun getListFromApi(presenter: CheckListContract.CheckListPresenter) {

        if (Util.networkCheck(context)) {

           Util.main {

               val response = api?.getCheckList()!!

               with(response) {

                   if (isSuccessful && body() != null) {

                       presenter.loadApiList(body()!!)

                   } else {

                       Util.toast(context, response.message() ?: context.getString(R.string.try_again))

                   }

               }

           }

        } else {
            Util.toast(context, context.getString(R.string.no_internet))
        }
    }
}