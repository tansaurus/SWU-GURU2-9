package om.androidbook.medicine4

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.OnMapsSdkInitializedCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import om.androidbook.medicine4.R

class PharmacyFragment : Fragment(), OnMapReadyCallback, OnMapsSdkInitializedCallback {

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var placesClient: PlacesClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        MapsInitializer.initialize(requireContext(), MapsInitializer.Renderer.LEGACY, this)
        val view = inflater.inflate(R.layout.fragment_pharmacy, container, false)
        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)


        return view
    }
    override fun onMapsSdkInitialized(renderer: MapsInitializer.Renderer) {
        when (renderer) {
            MapsInitializer.Renderer.LATEST -> Log.d("MapsDemo", "The latest version of the renderer is used.")
            MapsInitializer.Renderer.LEGACY -> Log.d("MapsDemo", "The legacy version of the renderer is used.")
        }
    }
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        updateLocationUI()
    }

    private fun updateLocationUI() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 위치 권한 요청 로직 추가
            requestLocationPermission()
            return
        }
        googleMap.isMyLocationEnabled = true
        getCurrentLocation()
    }

    private fun getCurrentLocation() {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                moveCameraToLocation(it)
                findPharmaciesNearby(it)
            }
        }
    }

    private fun moveCameraToLocation(location: Location) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 15f))
    }

    private fun findPharmaciesNearby(location: Location) {
        placesClient = Places.createClient(requireContext())

        lifecycleScope.launch {
            try {
                // 필요한 Place 필드를 정의합니다.
                val placeFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
                val currentLatLng = LatLng(location.latitude, location.longitude)

                // 약국을 검색하기 위한 쿼리를 구성합니다.
                val request = FindAutocompletePredictionsRequest.builder()
                    .setLocationBias(RectangularBounds.newInstance(
                        LatLng(currentLatLng.latitude - 0.005, currentLatLng.longitude - 0.005), // 5km 범위
                        LatLng(currentLatLng.latitude + 0.005, currentLatLng.longitude + 0.005)))
                    .setQuery("약국")
                    .setTypeFilter(TypeFilter.ESTABLISHMENT)
                    .setCountries("KR") // 한국 내에서만 검색
                    .build()


                // Places API를 사용하여 검색을 수행합니다.
                val response = placesClient.findAutocompletePredictions(request).await()
                response.autocompletePredictions.forEach { prediction ->
                    val placeId = prediction.placeId
                    val placeRequest = FetchPlaceRequest.builder(placeId, placeFields).build()

                    // 특정 장소에 대한 상세 정보를 가져옵니다.
                    val fetchPlaceResponse = placesClient.fetchPlace(placeRequest).await()
                    val place = fetchPlaceResponse.place

                    // 메인 스레드에서 UI 작업을 수행합니다.
                    withContext(Dispatchers.Main) {
                        addMarkerForPlace(place)
                    }
                }
            } catch (e: Exception) {
                // 오류 처리
            }
        }

    }

    private fun addMarkerForPlace(place: Place) {
        lifecycleScope.launchWhenCreated {
            googleMap.addMarker(
                MarkerOptions()
                    .title(place.name)
                    .position(place.latLng!!)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            )
        }
    }

    private fun requestLocationPermission() {
        // 위치 권한 요청 로직 구현
    }
// 생명주기 메서드들
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}