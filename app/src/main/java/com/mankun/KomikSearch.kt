package com.mankun

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar

class KomikSearch : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_komik_search)

        initTopToolbar()

        val komikSearch: EditText = findViewById(R.id.search_text)
        val komikSearchButton: Button = findViewById(R.id.search_button)
        val komikSearchResultList: RecyclerView = findViewById(R.id.komik_list)

        komikSearchResultList.layoutManager = GridLayoutManager(this, 2)

        komikSearchButton.setOnClickListener {
            val komikSearchResult = KomikIndoScrape(this)
            komikSearchResult.GetKomikSearchResult(komikSearch.text.toString()).execute()
        }
    }

    private fun initTopToolbar() {
        val topToolbar: MaterialToolbar = findViewById(R.id.top_toolbar)
        topToolbar.inflateMenu(R.menu.komik_search_list_menu)

        topToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.komik_latest -> {
                    startActivity(Intent(this, KomikLatestList::class.java))
                    finish()
                }
            }
            true
        }
    }
}