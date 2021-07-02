package com.example.listandmap.ui.ui.home

import android.content.Context
import android.content.Intent

class HomePresenter : HomeContract.HomePresenter {

    override fun intentToClass(context: Context, className: Class<*>) {
        Intent(context, className).also {
            context.startActivity(it)
        }
    }
}