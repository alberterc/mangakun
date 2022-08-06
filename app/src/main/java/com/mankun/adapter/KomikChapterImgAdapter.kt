package com.mankun.adapter

import android.content.Context
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.mankun.R
import com.squareup.picasso.Picasso

class KomikChapterImgAdapter: RecyclerView.Adapter<KomikChapterImgAdapter.ViewHolder> {
    private var context: Context?
    private var chapterImgList: List<String>

    constructor(chapterImgList: List<String>, context: Context) {
        this.context = context
        this.chapterImgList = chapterImgList
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(chapterImgUrl: String) {
            when (itemView.context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    Picasso.get()
                        .load(chapterImgUrl)
                        .placeholder(R.drawable.placeholder_background_dark)
                        .into(chapterImg)
                }

                Configuration.UI_MODE_NIGHT_NO -> {
                    Picasso.get()
                        .load(chapterImgUrl)
                        .placeholder(R.drawable.placeholder_background_light)
                        .into(chapterImg)
                }
            }
        }

        private val chapterImg: ImageView = itemView.findViewById(R.id.chapter_img)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_chapter_img, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(chapterImgList[position])
    }

    override fun getItemCount(): Int {
        return chapterImgList.size
    }
}