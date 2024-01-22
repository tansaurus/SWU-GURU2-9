package om.androidbook.medicine4

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var recentMedicinesRecyclerView: RecyclerView
    private lateinit var searchHistoryRecyclerView: RecyclerView
    private lateinit var recentMedicinesAdapter: MedicineAdapter
    private lateinit var searchHistoryAdapter: MedicineAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medicinelist)

        // DBHelper 초기화
        dbHelper = DBHelper(this, "DRUG_INFO.db", null, 1)

        // RecyclerView 및 Adapter 초기화
        recentMedicinesRecyclerView = findViewById(R.id.mediclinelistView)
        searchHistoryRecyclerView = findViewById(R.id.serchView)
        recentMedicinesAdapter = MedicineAdapter(mutableListOf()) { medicine ->
            // 여기에 삭제 로직 구현
        }
        searchHistoryAdapter = MedicineAdapter(mutableListOf()) { medicine ->
            // 여기에 삭제 로직 구현
        }

        // RecyclerView 설정
        recentMedicinesRecyclerView.layoutManager = LinearLayoutManager(this)
        recentMedicinesRecyclerView.adapter = recentMedicinesAdapter

        searchHistoryRecyclerView.layoutManager = LinearLayoutManager(this)
        searchHistoryRecyclerView.adapter = searchHistoryAdapter

        // 데이터 로딩
        loadRecentMedicines()
        loadSearchHistory()
    }

    private fun loadRecentMedicines() {
        val recentMedicines = dbHelper.getRecentMedicines()
        recentMedicinesAdapter.clear() // clear 메서드로 수정
        recentMedicinesAdapter.addAll(recentMedicines) // addAll 메서드로 수정
    }

    private fun loadSearchHistory() {
        val searchHistory = dbHelper.getSearchHistory()
        searchHistoryAdapter.clear() // clear 메서드로 수정
        searchHistoryAdapter.addAll(searchHistory) // addAll 메서드로 수정
    }
}
