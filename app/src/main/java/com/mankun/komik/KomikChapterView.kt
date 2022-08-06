package com.mankun.komik

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.mankun.KomikIndoScrape
import com.mankun.R

class KomikChapterView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_komik_chapter_view)

        val chapterUrl = intent.extras!!.getString("CHAPTER_URL")
        val chapterNum = intent.extras!!.getString("CHAPTER_NUM")

        val komikChapterNum: MaterialToolbar = findViewById(R.id.top_toolbar)
        val komikChapterImgList: RecyclerView = findViewById(R.id.chapter_img_list)

        komikChapterNum.title = "Chapter $chapterNum"
        komikChapterImgList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val komikIndoScrape = KomikIndoScrape(this)
        komikIndoScrape.GetChapterImg(chapterUrl!!).execute()
    }
}