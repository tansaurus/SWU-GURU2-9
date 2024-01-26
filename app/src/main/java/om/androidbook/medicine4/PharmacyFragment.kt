import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
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
import com.google.android.gms.maps.MapsInitializer.Renderer
import com.google.android.gms.maps.OnMapsSdkInitializedCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import om.androidbook.medicine4.R

class PharmacyFragment : Fragment(), OnMapReadyCallback, OnMapsSdkInitializedCallback {

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var placesClient: PlacesClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 레이아웃에 mapView 구성 필요
        val view = inflater.inflate(R.layout.fragment_pharmacy, container, false)
        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        // Google Maps SDK 초기화 및 렌더러 설정
        MapsInitializer.initialize(requireContext(), Renderer.LEGACY, this)

        return view
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        updateLocationUI()
    }

    override fun onMapsSdkInitialized(renderer: Renderer) {
        when (renderer) {
            Renderer.LATEST -> Log.d("MapsDemo", "The latest version of the renderer is used.")
            Renderer.LEGACY -> Log.d("MapsDemo", "The legacy version of the renderer is used.")
        }
    }

    private fun updateLocationUI() {
        try {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // 권한 요청 로직 필요
                return
            }
            googleMap.isMyLocationEnabled = true

            // 사용자 현재 위치 가져오기
            val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    // 현재 위치로 지도 이동
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 15f))

                    // 약국 찾기 함수 호출
                    findPharmaciesNearby(location)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun findPharmaciesNearby(location: Location) {
        // PlacesClient 초기화
        placesClient = Places.createClient(requireContext())

        // CoroutineScope 사용하여 백그라운드 작업 실행
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 현재 위치의 LatLng 객체 생성
                val currentLatLng = LatLng(location.latitude, location.longitude)

                // 약국을 검색하기 위한 쿼리 생성
                val placeFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
                val request = FindAutocompletePredictionsRequest.builder()
                    .setLocationBias(
                        RectangularBounds.newInstance(
                            LatLng(currentLatLng.latitude - 0.05, currentLatLng.longitude - 0.05), // 5km 범위
                            LatLng(currentLatLng.latitude + 0.05, currentLatLng.longitude + 0.05)
                        )
                    )
                    .setQuery("pharmacy")
                    .build()

                // 검색 수행
                val response = placesClient.findAutocompletePredictions(request).await()
                for (prediction in response.autocompletePredictions) {
                    val placeId = prediction.placeId

                    val placeRequest = FetchPlaceRequest.builder(placeId, placeFields).build()
                    val fetchPlaceResponse = placesClient.fetchPlace(placeRequest).await()
                    val place = fetchPlaceResponse.place

                    CoroutineScope(Dispatchers.Main).launch {
                        googleMap.addMarker(
                            MarkerOptions()
                                .title(place.name)
                                .position(place.latLng!!)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // 여기서 오류 처리를 할 수 있습니다.
            }
        }
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
