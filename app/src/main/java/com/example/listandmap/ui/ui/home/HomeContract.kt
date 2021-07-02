package com.example.listandmap.ui.ui.home

import android.content.Context

class HomeContract {
    interface HomePresenter {
        fun intentToClass(context: Context, className: Class<*>)
    }
}