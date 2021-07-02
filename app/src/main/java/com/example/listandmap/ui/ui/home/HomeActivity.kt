package com.example.listandmap.ui.ui.home

import android.content.IntentSender
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.listandmap.R
import com.example.listandmap.ui.ui.checklist.CheckListActivity
import com.example.listandmap.ui.ui.map.MapActivity
import com.example.listandmap.utility.Util
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener


class HomeActivity : AppCompatActivity() {
    private var homePresenter: HomePresenter? = null
    private var btnList: Button? = null
    private var btnMap: Button? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initialize()
    }


    private fun initialize() {
        homePresenter = HomePresenter()

        btnList = findViewById(R.id.btnList)
        btnMap = findViewById(R.id.btnMap)

        handleClickListener()
    }


    private fun handleClickListener() {

        btnList?.setOnClickListener {

            if (Util.networkCheck(this))
                homePresenter?.intentToClass(this, CheckListActivity::class.java)
            else
                Util.toast(this, getString(R.string.no_internet))

        }

        btnMap?.setOnClickListener {
            runtimeLocationPermission()
        }
    }


    private fun runtimeLocationPermission() {
        Dexter.withContext(this)
            .withPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    enableDeviceLocation()
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    // Do something like intent to app permission page.
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }
            })
            .check()
    }


    private fun enableDeviceLocation() {
        val mGoogleApiClient = GoogleApiClient.Builder(this)
            .addApi(LocationServices.API).build()
        mGoogleApiClient.connect()

        val locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval((30 * 1000).toLong())
            .setFastestInterval((5 * 1000).toLong())

        val locationSettingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true) // important!
            .build()

        val result = LocationServices.SettingsApi
            .checkLocationSettings(mGoogleApiClient, locationSettingsRequest)

        result.setResultCallback { result ->
            val status = result.status
            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS -> {
                    homePresenter?.intentToClass(this, MapActivity::class.java)
                }
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                    try {
                        status.startResolutionForResult(this, 10)
                    } catch (e: IntentSender.SendIntentException) {
                    }
                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    Util.toast(this, "Enable location to proceed!")
                }
            }

            mGoogleApiClient.disconnect()
        }
    }
}