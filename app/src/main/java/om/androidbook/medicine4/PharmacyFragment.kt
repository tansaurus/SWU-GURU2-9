package om.androidbook.medicine4

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
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
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class PharmacyFragment : Fragment(), OnMapReadyCallback,
    OnMapsSdkInitializedCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var placesClient: PlacesClient
    private lateinit var pharmacyInfoLayout: ConstraintLayout
    private lateinit var pharmacyNameTextView: TextView
    private lateinit var pharmacyAddressTextView: TextView
    private lateinit var pharmacyCallTextView: TextView
    private var lastClickedMarker: Marker? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        MapsInitializer.initialize(requireContext(), MapsInitializer.Renderer.LEGACY, this)
        val view = inflater.inflate(R.layout.fragment_pharmacy, container, false)
        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        pharmacyInfoLayout = view.findViewById<ConstraintLayout>(R.id.pharmacyInfoLayout)
        pharmacyNameTextView = view.findViewById<TextView>(R.id.pharmacyNameTextView)
        pharmacyAddressTextView = view.findViewById<TextView>(R.id.pharmacyAddressTextView)
        pharmacyCallTextView = view.findViewById<TextView>(R.id.pharmacyCallTextView)

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
        googleMap.setOnMarkerClickListener(this) // 마커 클릭 리스너 설정
        placesClient = Places.createClient(requireContext()) // placesClient 초기화
        updateLocationUI()

        // 맵 클릭 시 이벤트
        map.setOnMapClickListener {
            // 약국 정보 Layout invisible
            pharmacyInfoLayout.visibility = View.GONE
        }

        // 맵 드래그 시 이벤트
        map.setOnCameraMoveListener {
            // 클릭한 마커가 있을 경우
            if (lastClickedMarker != null ) {
                // 약국 정보 Layout invisible
                googleMap.setOnCameraIdleListener {
                    pharmacyInfoLayout.visibility = View.GONE
                }
                pharmacyInfoLayout.visibility = View.GONE
                lastClickedMarker = null
            }

            // 약국 정보 Layout invisible
            pharmacyInfoLayout.visibility = View.GONE
        }
    }

    // 마커 클릭 시 이벤트
    override fun onMarkerClick(marker: Marker): Boolean {
        // 마커 위치
        val clickedMarkerPosition = marker.position
        // 마커의 위치로 카메라 이동
        val zoomLevel = 16.0f // 줌 레벨
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(clickedMarkerPosition, zoomLevel), 500, null)

        // 클릭된 마커의 Place ID
        val placeId = marker.tag as? String

        // Place ID가 있다면 약국의 상세 정보 가져오기
        if (placeId != null) {
            googleMap.setOnCameraIdleListener {
                fetchPlaceDetails(placeId)
                // 마지막으로 클릭한 마커 저장
                lastClickedMarker = marker
            }
        }

        return true
    }

    // 약국의 상세 정보를 가져오는 메소드
    private fun fetchPlaceDetails(placeId: String) {
        lifecycleScope.launch {
            try {
                // Place 필드 정의
                val placeFields = listOf(Place.Field.ID, Place.Field.NAME,
                                            Place.Field.ADDRESS, Place.Field.PHONE_NUMBER)

                // 상세 정보 가져오기
                val placeRequest = FetchPlaceRequest.builder(placeId, placeFields).build()
                val fetchPlaceResponse: FetchPlaceResponse = placesClient.fetchPlace(placeRequest).await()

                // UI 작업
                withContext(Dispatchers.Main) {
                    val place = fetchPlaceResponse.place

                    // 약국 정보 Layout visible
                    pharmacyInfoLayout.visibility = View.VISIBLE

                    // 약국 이름
                    pharmacyNameTextView.text = place.name

                    // 약국 상세 주소
                    pharmacyAddressTextView.text = place.address

                    // 약국 전화번호
                    pharmacyCallTextView.text = place.phoneNumber
                }
            } catch (e: Exception) {
                // 오류 처리
                // 토스트 메세지 출력
                Toast.makeText(requireContext(), "약국 정보를 불러올 수 없습니다.", Toast.LENGTH_LONG).show()
            }
        }
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
                val oneKmInDegrees = 0.009
                // 약국을 검색하기 위한 쿼리를 구성합니다.
                val request = FindAutocompletePredictionsRequest.builder()
                    .setLocationBias(RectangularBounds.newInstance(
                        LatLng(currentLatLng.latitude - oneKmInDegrees, currentLatLng.longitude - oneKmInDegrees), // 1km 범위
                        LatLng(currentLatLng.latitude + oneKmInDegrees, currentLatLng.longitude + oneKmInDegrees)))
                    .setQuery("약국")
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
            val marker = googleMap.addMarker(
                MarkerOptions()
                    .title(place.name)
                    .position(place.latLng!!)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            )
            // 마커의 태그로 placeId 설정
            if (marker != null) {
                marker.tag = place.id
            }
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