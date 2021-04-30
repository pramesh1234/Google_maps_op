package com.famco.googlemapsop.mapScreen

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.famco.googlemapsop.R
import com.famco.googlemapsop.utils.MapUtils
import com.famco.googlemapsop.utils.ParserTask
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import java.util.*

private const val TAG = "MapsActivity"
var mMap: GoogleMap? = null

class MapsActivity : AppCompatActivity() {
    lateinit var mapFragment: SupportMapFragment
    lateinit var client: FusedLocationProviderClient
    private val REQUEST_LOCATION_PERMISSION = 1
    lateinit var myLocationLatLng: LatLng
    var destinationIsMarked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        client = LocationServices.getFusedLocationProviderClient(this)
        mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        enableMyLocation()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )

        } else {

            getCurrentLocation()
        }
    }


    private fun getCurrentLocation() {
        val task: Task<Location> = client.lastLocation
        task.addOnSuccessListener(OnSuccessListener { location ->

            if (location != null) {

                val zoomLevel = 15f
                mapFragment.getMapAsync {
                    val latLng = LatLng(location.latitude, location.longitude)
                    it.addMarker(MarkerOptions().position(latLng).title("Me"))
                    it.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel))
                    it.isMyLocationEnabled = true
                    myLocationLatLng = latLng
                    mMap = it
                    setMapLongClick(it)
                }
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            }
        }
    }

    private fun setMapLongClick(map: GoogleMap) {
        map.setOnMapLongClickListener { latLng ->
            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                latLng.latitude,
                latLng.longitude
            )
            map.addMarker(
                MarkerOptions()
                    .position(latLng).title("destination")
            )
            val url = MapUtils.getDirectionsUrl(myLocationLatLng, latLng).toString()
            Log.e(TAG, "setMapLongClick: $url")
            val downloadTask = DownloadTask()
            downloadTask.execute(url)

            destinationIsMarked = true
        }
    }

    class DownloadTask : AsyncTask<String, Unit, String>() {
        override fun doInBackground(vararg p0: String?): String {
            var data: String? = ""

            try {
                data = p0[0]?.let { MapUtils.downloadUrl(it) }
            } catch (e: Exception) {

            }
            return data!!
        }

        override fun onPostExecute(result: String?) {
            mMap?.let { ParserTask(it) }?.execute(result)
        }
    }
}

