package com.mankun.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.mankun.komik.KomikDetails
import com.mankun.R
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject

class KomikSearchResultListAdapter : RecyclerView.Adapter<KomikSearchResultListAdapter.ViewHolder> {
    private var jsonArray = JSONArray()
    private var context: Context?

    constructor(jsonArray: JSONArray, context: Context) {
        this.jsonArray = jsonArray
        this.context = context
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(komik: JSONObject) {
            Picasso.get()
                .load(komik.getString("thumbnail"))
                .into(komikThumbnail)
            komikTitle.text = komik.getString("title")
            komikRating.text = komik.getString("rating")
            komikItem.setOnClickListener {
                val intent = Intent(itemView.context, KomikDetails::class.java)
                intent.putExtra("KOMIK_URL", komik.getString("url"))
                itemView.context.startActivity(intent)
            }
        }

        private val komikTitle: TextView = itemView.findViewById(R.id.komik_title)
        private val komikThumbnail: ImageView = itemView.findViewById(R.id.komik_thumbnail)
        private val komikRating: TextView = itemView.findViewById(R.id.komik_rating)
        private val komikItem: CardView = itemView.findViewById(R.id.komik_search_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_komik_search, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(jsonArray.get(position) as JSONObject)
    }

    override fun getItemCount(): Int {
        return jsonArray.length()
    }
}