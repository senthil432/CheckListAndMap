package com.example.listandmap.ui.ui.map

import android.content.Context
import com.example.listandmap.R
import com.example.listandmap.dataclass.MapDirectionRequest
import com.example.listandmap.restapi.ApiClient
import com.example.listandmap.restapi.ApiServiceInterface
import com.example.listandmap.utility.Util

class MapModel(private val context: Context) : MapContract.MapModel {

    private var api: ApiServiceInterface? = null

    init {
        api = ApiClient().callApiMapService()
    }


    override fun getMapDirections(
        mapPresenter: MapContract.MapPresenter,
        request: MapDirectionRequest
    ) {

        if (Util.networkCheck(context)) {

          Util.main {

              val response = api?.getDirection(request.origin, request.destination, request.key)!!

              with(response) {

                  if (isSuccessful && body() != null) {

                      mapPresenter.loadDirectionResponse(body()!!)

                  } else {

                      Util.toast(
                          context,
                          response.message() ?: context.getString(R.string.try_again)
                      )

                  }

              }

          }

        } else {

            Util.toast(context, context.getString(R.string.no_internet))

        }

    }
}