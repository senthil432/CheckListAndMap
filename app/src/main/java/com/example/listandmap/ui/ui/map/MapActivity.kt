package com.example.listandmap.ui.ui.map

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.example.listandmap.R
import com.example.listandmap.dataclass.MapDirectionRequest
import com.example.listandmap.dataclass.MapDirectionResponse
import com.example.listandmap.utility.Util
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.maps.android.PolyUtil
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.btm_sheet_direction.*
import java.io.IOException
import java.util.*


class MapActivity : FragmentActivity(), MapContract.MapView, OnMapReadyCallback {
    private var mapPresenter: MapPresenter? = null
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var currentLatLong: LatLng
    private var address = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        initializeMap()
        initClickEvents()
    }


    @SuppressLint("VisibleForTests")
    private fun initializeMap() {
        mapPresenter = MapPresenter(this, this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        getCurrentLocation()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }


    private fun initClickEvents() {
        btnGetDirections.setOnClickListener {
            openBottomSheet()
        }
    }


    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap) {
        googleMap = map ?: return
        googleMap.isMyLocationEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = true
        googleMap.uiSettings.isZoomControlsEnabled = true
    }


    private fun getCurrentLocation() {
        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = (10 * 1000).toLong()
        locationRequest.fastestInterval = 5000

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)
        val locationSettingsRequest = builder.build()

        val result =
            LocationServices.getSettingsClient(this).checkLocationSettings(locationSettingsRequest)

        result.addOnCompleteListener { task ->
            try {
                val response = task.getResult(ApiException::class.java)
                if (response?.locationSettingsStates!!.isLocationPresent) {
                    getLastLocation()
                }
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val resolvable = exception as ResolvableApiException
                        resolvable.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)
                    } catch (e: IntentSender.SendIntentException) {
                    } catch (e: ClassCastException) {
                    }

                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> { }
                }
            }
        }
    }


    @SuppressLint("MissingPermission")
    private fun getLastLocation() {

        fusedLocationProviderClient.lastLocation

            .addOnSuccessListener { task: Location? ->

                task?.let { it: Location ->

                    val gcd = Geocoder(this, Locale.getDefault())
                    val addresses: List<Address>
                    try {
                        addresses = gcd.getFromLocation(
                            task.latitude,
                            task.longitude,
                            1
                        )
                        if (addresses.isNotEmpty()) {
                            address = addresses[0].getAddressLine(0)
                        }

                        currentLatLong = LatLng(task.latitude, task.longitude)

                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    val icon = BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource(
                            this.resources,
                            R.drawable.ic_marker
                        )
                    )
                    googleMap.addMarker(
                        MarkerOptions()
                            .position(LatLng(task.latitude, task.longitude))
                            .title("Current Location")
                            .snippet(address)
                            .icon(icon)
                    )

                    val cameraPosition = CameraPosition.Builder()
                        .target(LatLng(task.latitude, task.longitude))
                        .zoom(17f)
                        .build()
                    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

                } ?: kotlin.run {
                    getCurrentLocation()
                }
            }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> {
                if (resultCode == Activity.RESULT_OK) {
                    getCurrentLocation()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    // For Directions
    private fun openBottomSheet() {
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(R.layout.btm_sheet_direction)
        dialog.setCancelable(true)

        dialog.edFrom.setText(address)

        dialog.btnFindRoute.setOnClickListener {
            when {
                dialog.edTo.text.isNullOrEmpty() -> Util.toast(this, "Enter To Destination")
                else -> {
                    val origin =
                        currentLatLong.latitude.toString() + "," + currentLatLong.longitude.toString()
                    initGetDirection(origin, dialog.edTo.text.toString())
                }
            }
        }

        dialog.btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


    private fun initGetDirection(origin: String, destination: String) {
        val request = MapDirectionRequest(origin, destination, getString(R.string.api_key))
        mapPresenter?.apiCallDirection(request)
    }


    override fun drawLineAndMarker(response: MapDirectionResponse) {
        if (!response.routes.isNullOrEmpty()) {
            drawPolyline(response)
            setMarker(response)
        } else {
            Util.toast(this, getString(R.string.try_again))
        }
    }


    private fun drawPolyline(response: MapDirectionResponse) {
        val shape = response.routes?.get(0)?.overviewPolyline?.points

        val polyline = PolylineOptions()
            .addAll(PolyUtil.decode(shape))
            .width(8f)
            .color(Color.BLUE)

        googleMap.addPolyline(polyline)
    }


    private fun setMarker(response: MapDirectionResponse) {

        if (!response.routes?.get(0)?.legs?.isNullOrEmpty()!!) {

            val res = response.routes?.get(0)?.legs?.get(0)

            val startMarkerPoint = LatLng(res?.startLocation?.lat!!, res.startLocation?.lng!!)
            val startMarker = MarkerOptions()
                .position(startMarkerPoint)

            val endMarkerPoint = LatLng(res.endLocation?.lat!!, res.endLocation?.lng!!)
            val endMarker = MarkerOptions()
                .position(endMarkerPoint)

            googleMap.addMarker(startMarker)
            googleMap.addMarker(endMarker)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(endMarkerPoint, 11.6f))

        }

    }


    companion object {
        const val REQUEST_CHECK_SETTINGS = 123
    }
}