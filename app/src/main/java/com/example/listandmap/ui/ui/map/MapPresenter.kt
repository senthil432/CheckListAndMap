package com.example.listandmap.ui.ui.map

import android.content.Context
import com.example.listandmap.dataclass.MapDirectionRequest
import com.example.listandmap.dataclass.MapDirectionResponse

class MapPresenter(
    context: Context,
    mapView: MapContract.MapView
): MapContract.MapPresenter {

    private var view: MapContract.MapView = mapView
    private var model: MapContract.MapModel = MapModel(context)

    override fun apiCallDirection(request: MapDirectionRequest) {
       model.getMapDirections(this, request)
    }

    override fun loadDirectionResponse(response: MapDirectionResponse) {
        view.drawLineAndMarker(response)
    }
}