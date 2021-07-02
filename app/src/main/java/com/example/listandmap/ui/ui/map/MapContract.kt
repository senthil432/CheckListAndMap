package com.example.listandmap.ui.ui.map

import com.example.listandmap.dataclass.MapDirectionRequest
import com.example.listandmap.dataclass.MapDirectionResponse

class MapContract {

    interface MapModel {
        fun getMapDirections(mapPresenter: MapPresenter, request: MapDirectionRequest)
    }

    interface MapView {
        fun drawLineAndMarker(response: MapDirectionResponse)
    }

    interface MapPresenter {
        fun apiCallDirection(request: MapDirectionRequest)
        fun loadDirectionResponse(response: MapDirectionResponse)
    }

}