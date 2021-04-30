package com.famco.googlemapsop.utils

import android.graphics.Color
import android.os.AsyncTask
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import org.json.JSONObject


class ParserTask(private val mMap:GoogleMap) : AsyncTask<String, Integer, List<List<HashMap<String, String>>>>() {
    override fun doInBackground(vararg p0: String?): List<List<HashMap<String, String>>>? {
        val jObject: JSONObject
        var routes: List<List<HashMap<String, String>>>? = null

        try {
            jObject = JSONObject(p0.get(0))
            val parser = DataParser()
            routes = parser.parse(jObject)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return routes
    }

    override fun onPostExecute(result: List<List<HashMap<String, String>>>) {
        val points:MutableList<LatLng> = ArrayList<LatLng>()
        val lineOptions = PolylineOptions()

        for (element in result) {
            for (j in element.indices) {
                    val point = element[j]
                    val lat = point["lat"]!!.toDouble()
                    val lng = point["lng"]!!.toDouble()
                    val position = LatLng(lat, lng)
                    points.add(position)
                }
            lineOptions.addAll(points)
            lineOptions.width(12f)
            lineOptions.color(Color.RED)
            lineOptions.geodesic(true)
        }

        // Drawing polyline in the Google Map


        // Drawing polyline in the Google Map
        if (points.size != 0) mMap.addPolyline(lineOptions)

    }

    override fun onPreExecute() {
        super.onPreExecute()
    }
}