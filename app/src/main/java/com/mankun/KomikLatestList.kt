package com.mankun

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar

class KomikLatestList : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_komik_latest_list)

        initTopToolbar()

        val komikList: RecyclerView = findViewById(R.id.komik_list)
        komikList.layoutManager = GridLayoutManager(this, 2)

        val komikIndoScrape = KomikIndoScrape(this)
        komikIndoScrape.GetLatestList().execute()
    }

    private fun initTopToolbar() {
        val topToolbar: MaterialToolbar = findViewById(R.id.top_toolbar)
        topToolbar.inflateMenu(R.menu.komik_latest_list_menu)

        topToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.komik_search -> {
                    startActivity(Intent(this, KomikSearch::class.java))
                    finish()
                }
            }
            true
        }
    }
}