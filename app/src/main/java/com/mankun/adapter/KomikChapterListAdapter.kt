package com.mankun.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.mankun.komik.KomikChapterView
import com.mankun.R
import org.json.JSONArray
import org.json.JSONObject

class KomikChapterListAdapter: RecyclerView.Adapter<KomikChapterListAdapter.ViewHolder> {
    private var chapterJson = JSONArray()
    private var context: Context?

    constructor(chapterJson: JSONArray, context: Context) {
        this.chapterJson = chapterJson
        this.context = context
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(chapter: JSONObject) {
            chapterNum.text = chapter.getString("num")
            chapterReleaseTime.text = chapter.getString("release_time")
            chapterCard.setOnClickListener {
                val intent = Intent(itemView.context, KomikChapterView::class.java)
                intent.putExtra("CHAPTER_URL", chapter.getString("url"))
                intent.putExtra("CHAPTER_NUM", chapter.getString("num"))
                itemView.context.startActivity(intent)
            }
        }

        private val chapterNum: TextView = itemView.findViewById(R.id.chapter_num_text)
        private val chapterReleaseTime: TextView = itemView.findViewById(R.id.chapter_release_time_text)
        private val chapterCard: CardView = itemView.findViewById(R.id.chapter_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_komik_chapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(chapterJson.get(position) as JSONObject)
    }

    override fun getItemCount(): Int {
        return chapterJson.length()
    }
}