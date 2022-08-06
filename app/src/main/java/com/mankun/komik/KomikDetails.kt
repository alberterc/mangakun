package com.mankun.komik

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mankun.KomikIndoScrape
import com.mankun.R

class KomikDetails : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_komik_details)

        val komikUrl = intent.extras!!.getString("KOMIK_URL")

        val komikChapterList: RecyclerView = findViewById(R.id.chapter_list)
        komikChapterList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val komikIndoScrape = KomikIndoScrape(this)
        komikIndoScrape.GetKomikDetails(komikUrl!!).execute()
    }
}