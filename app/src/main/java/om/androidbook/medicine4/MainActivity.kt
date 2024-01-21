package om.androidbook.medicine4

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "KakaoAK f501f9cd2c5a00368858a7569f862fc2"  // REST API 키
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    private lateinit var mapView: MapView
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnSearch: Button
    private lateinit var btnPrevPage: Button
    private lateinit var btnNextPage: Button
    private lateinit var etSearchField: EditText
    private lateinit var tvPageNumber: TextView
    private val listItems = arrayListOf<ListLayout>()   // 리사이클러 뷰 아이템
    private val listAdapter = ListAdapter(listItems)    // 리사이클러 뷰 어댑터
    private var pageNumber = 1      // 검색 페이지 번호
    private var keyword = ""        // 검색 키워드
    private var uLongitude: Double = -1.0
    private var uLatitude: Double = -1.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // 리사이클러 뷰
        mapView = findViewById(R.id.mapView)
        recyclerView = findViewById(R.id.rv_list)
        btnSearch = findViewById(R.id.btn_search)
        btnPrevPage = findViewById(R.id.btn_prevPage)
        btnNextPage = findViewById(R.id.btn_nextPage)
        etSearchField = findViewById(R.id.et_search_field)
        tvPageNumber = findViewById(R.id.tv_pageNumber)

        // 리사이클러 뷰 설정
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = listAdapter

        // 리스트 아이템 클릭 시 해당 위치로 이동
        listAdapter.setItemClickListener(object: ListAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                val mapPoint = MapPoint.mapPointWithGeoCoord(listItems[position].y, listItems[position].x)
                mapView.setMapCenterPointAndZoomLevel(mapPoint, 1, true)
            }
        })

        // 검색 버튼
        btnSearch.setOnClickListener {
            keyword = etSearchField.text.toString()
            pageNumber = 1
            searchKeyword(keyword, pageNumber)
        }

        // 이전 페이지 버튼
        btnPrevPage.setOnClickListener {
            if (pageNumber > 1) {
                pageNumber--
                tvPageNumber.text = pageNumber.toString()
                searchKeyword(keyword, pageNumber)
            }
        }

        // 다음 페이지 버튼
        btnNextPage.setOnClickListener {
            pageNumber++
            tvPageNumber.text = pageNumber.toString()
            searchKeyword(keyword, pageNumber)
        }

        checkLocationPermission()
    }

    // 키워드 검색 함수
    private fun searchKeyword(keyword: String, page: Int) {
        if (uLatitude == -1.0 || uLongitude == -1.0) {
            Toast.makeText(this, "위치 정보를 불러오는 중입니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KakaoAPI::class.java)
        val call = api.getSearchKeyword(API_KEY, keyword, page, 5000, uLongitude, uLatitude)

        // API 서버에 요청
        call.enqueue(object: Callback<ResultSearchKeyword> {
            // onResponse 및 onFailure 구현
            override fun onResponse(call: Call<ResultSearchKeyword>, response: Response<ResultSearchKeyword>) {
                // 통신 성공
                addItemsAndMarkers(response.body())
            }

            override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                // 통신 실패
                Log.w("LocalSearch", "통신 실패: ${t.message}")
            }
        })
    }


    // 검색 결과 처리 함수
    private fun addItemsAndMarkers(searchResult: ResultSearchKeyword?) {
        if (!searchResult?.documents.isNullOrEmpty()) {
            listItems.clear()
            mapView.removeAllPOIItems() // 기존의 마커들을 제거

            for (document in searchResult!!.documents) {
                val item = ListLayout(
                    document.place_name,
                    document.road_address_name,
                    document.address_name,
                    document.x.toDouble(),
                    document.y.toDouble()
                )
                listItems.add(item)

                // 각 위치에 마커 추가
                val marker = MapPOIItem().apply {
                    itemName = document.place_name
                    mapPoint = MapPoint.mapPointWithGeoCoord(document.y.toDouble(), document.x.toDouble())
                    markerType = MapPOIItem.MarkerType.BluePin // 마커 타입 설정
                    selectedMarkerType = MapPOIItem.MarkerType.RedPin
                }
                mapView.addPOIItem(marker) // 지도에 마커 추가
            }

            listAdapter.notifyDataSetChanged()

            btnNextPage.isEnabled = !searchResult.meta.is_end
            btnPrevPage.isEnabled = pageNumber != 1
        } else {
            Toast.makeText(this, "검색 결과가 없습니다", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startTracking() {
        mapView.currentLocationTrackingMode =
            MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
        val lm: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val userNowLocation: Location? = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val locationListener = LocationListener { location ->
            // 현재 위치 업데이트
            uLatitude = location.latitude
            uLongitude = location.longitude
        }
        val uLatitude = userNowLocation?.latitude
        val uLongitude = userNowLocation?.longitude
        val uNowPosition = MapPoint.mapPointWithGeoCoord(uLatitude!!, uLongitude!!)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, locationListener)
        } else {
            Toast.makeText(this, "위치 권한이 필요합니다", Toast.LENGTH_SHORT).show()
        }

        val marker = MapPOIItem()
        marker.itemName = "현 위치"
        marker.mapPoint = uNowPosition
        marker.markerType = MapPOIItem.MarkerType.BluePin
        marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin
        mapView.addPOIItem(marker)
    }



    // 위치추적 중지
    private fun stopTracking() {
        mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTracking()
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            startTracking()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    startTracking()
                } else {
                    Toast.makeText(this, "위치 권한이 필요합니다", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}