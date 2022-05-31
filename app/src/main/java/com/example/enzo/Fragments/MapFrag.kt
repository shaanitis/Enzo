package com.example.enzo.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.enzo.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Exception


class MapFrag : Fragment() {

private var adLocLatitude:String="11"
    private var adLocLongitude="33"
    lateinit var googleMap:Fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_map, container, false)


        adLocLatitude= arguments?.getString("adLocLatitude").toString()
        adLocLongitude= arguments?.getString("adLocLongitude").toString()


            try {


                val mapFragL: SupportMapFragment =
                    childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
                mapFragL.getMapAsync(object : OnMapReadyCallback {
                    override fun onMapReady(gm: GoogleMap) {
                        val latLng: LatLng =

                            LatLng(adLocLatitude!!.toDouble(), adLocLongitude!!.toDouble())
                        val mOption: MarkerOptions =
                            MarkerOptions().position(latLng).title("Seller is here")
                        gm.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10F))
                        gm.addMarker(mOption)

                    }

                })
            }catch (e:Exception){
                Log.d("err","")
            }






        return view
    }


    }
