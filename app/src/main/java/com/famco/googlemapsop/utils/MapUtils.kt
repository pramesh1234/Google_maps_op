package com.famco.googlemapsop.utils

import com.google.android.gms.maps.model.LatLng
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class MapUtils {
    companion object {
         fun getDirectionsUrl(origin: LatLng, dest: LatLng): String? {

            val str_origin = "origin=" + origin.latitude + "," + origin.longitude
            val str_dest = "destination=" + dest.latitude + "," + dest.longitude
            val mode = "mode=driving"

            val parameters = "$str_origin&$str_dest&$mode"

            val output = "json"

            return "https://maps.googleapis.com/maps/api/directions/$output?$parameters&key=AIzaSyCPwiCxW76nUJ_QyUoMdNfgnwY0VoiPxqA"
        }

        @Throws(IOException::class)
         fun downloadUrl(strUrl: String): String {
            var data = ""
            var iStream: InputStream? = null
            var urlConnection: HttpURLConnection? = null
            try {
                val url = URL(strUrl)
                urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.connect()
                iStream = urlConnection.getInputStream()
                val br = BufferedReader(InputStreamReader(iStream))
                val sb = StringBuffer()
                var line: String? = ""
                while (br.readLine().also { line = it } != null) {
                    sb.append(line)
                }
                data = sb.toString()
                br.close()
            } catch (e: Exception) {

            } finally {
                iStream?.close()
                urlConnection?.disconnect()
            }
            return data
        }
    }
}