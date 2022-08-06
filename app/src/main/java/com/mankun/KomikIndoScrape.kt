package com.mankun

import android.app.Activity
import android.os.AsyncTask
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter.Indenter
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mankun.adapter.KomikChapterImgAdapter
import com.mankun.adapter.KomikChapterListAdapter
import com.mankun.adapter.KomikLatestListAdapter
import com.mankun.adapter.KomikSearchResultListAdapter
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.*
import java.lang.ref.WeakReference


@Suppress("DEPRECATION")
class KomikIndoScrape(context: Activity) {
    private val baseUrl = "https://komikindo.id/"
    private var activityReference = WeakReference(context)

    // Setup a pretty printer with an indenter (indenter has 4 spaces in this case)
    // found from stackoverflow
    private val indent: Indenter = DefaultIndenter("    ", DefaultIndenter.SYS_LF)


    private fun writeJsonFile(list: MutableList<Map<String, Any>>, fileName: String) {
        val printer = DefaultPrettyPrinter()
        printer.indentObjectsWith(indent)
        printer.indentArraysWith(indent)
        // create a new json array
        val jsonArray = jacksonObjectMapper().writer(printer).writeValueAsString(list)

        // create a new file
        val file = File(activityReference.get()!!.applicationContext.filesDir, fileName)
        // write into the file with the json array
        try {
            val fileWriter = FileWriter(file)
            val bufferedWriter = BufferedWriter(fileWriter)
            bufferedWriter.write(jsonArray.toString())
            bufferedWriter.close()
        } catch (ignored: IOException) {}
    }

    private fun readJsonFile(fileName: String): JSONArray? {
        // get file reference
        val file = File(activityReference.get()!!.applicationContext.filesDir, fileName)

        // read file with the json array
        try {
            val fileReader = FileReader(file)
            val bufferedReader = BufferedReader(fileReader)
            val stringBuilder = StringBuilder()
            var line = bufferedReader.readLine()
            while (line != null) {
                stringBuilder.append(line).append("\n")
                line = bufferedReader.readLine()
            }
            bufferedReader.close()

            // get title
            val jsonString = stringBuilder.toString()
            return JSONArray(jsonString)
        }
        catch (ignored: JSONException) {}
        catch (ignored: IOException) {}

        return null
    }

    inner class GetLatestList : AsyncTask<Void, Void, Void>() {
        private val url = baseUrl + "komik-terbaru/"
        private val sep = "."
        private val komikLatestList = mutableListOf<Map<String, Any>>()
        private val fileName = "komik_latest_list.json"

        override fun doInBackground(vararg params: Void?): Void? {
            try {
                val document: Document = Jsoup.connect(url).get()
                val komiks = document.select("div.animepost")
                for (komik in komiks) {
                    val komikThumbnail = komik.select("img")[0].absUrl("src")
                    val komikTitle = komik.select("div.tt > h4")[0].text().trim()
                    val komikDetailsUrl = komik.select("a")[0].absUrl("href")
                    val komikChapterNum = komik.select("div.lsch > a")[0].text().substringAfter(sep).trim()
                    val komikChapterRelease = komik.select("div.lsch > span.datech")[0].text().trim()

                    val komikItem: Map<String, String> = mapOf(
                        "title" to komikTitle,
                        "url" to komikDetailsUrl,
                        "thumbnail"  to komikThumbnail,
                        "chapter_num" to komikChapterNum,
                        "chapter_release" to komikChapterRelease
                    )
                    komikLatestList.add(komikItem)
                }
                writeJsonFile(komikLatestList, fileName)
            } catch (ignored: IOException) {}

            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            val komikList: RecyclerView = activityReference.get()!!.findViewById(R.id.komik_list)
            val jsonArray = readJsonFile(fileName)

            komikList.adapter = KomikLatestListAdapter(jsonArray!!, activityReference.get()!!.applicationContext)
        }
    }

    inner class GetKomikDetails(private val url: String) : AsyncTask<Void, Void, Void>() {
        private val komikDetails = mutableListOf<Map<String, Any>>()
        private val fileName = "komik_details.json"

        override fun doInBackground(vararg params: Void?): Void? {
            try {
                val document: Document = Jsoup.connect(url).get()
                val komikDetailSoup = document.select("div.infoanime")[0]

                val komikTitle = komikDetailSoup.select("h1.entry-title")[0].text().substringAfter("Komik").trim()
                val komikDescription = komikDetailSoup.select("div.shortcsc")[1].text().trim()
                val komikThumbnail = komikDetailSoup.select("div.thumb > img")[0].absUrl("src")
                val komikMoreDetails = komikDetailSoup.select("div.infox > div.spe > span")
                var komikState = ""
                var komikAltTitles = ""
                var komikAuthor = ""
                var komikIllustrator = ""
                var komikGraphics = ""
                var komikTheme = ""
                var komikType = ""
                var komikReaderCount = ""

                for (eachDetail in komikMoreDetails) {
                    val sep = ":"
                    when (eachDetail.select("b")[0].text().trim()) {
                        "Status:" -> {
                            komikState = eachDetail.text().substringAfter(sep).trim()
                        }
                        "Judul Alternatif:" -> {
                            komikAltTitles = eachDetail.text().substringAfter(sep).trim()
                        }
                        "Pengarang:" -> {
                            komikAuthor = eachDetail.text().substringAfter(sep).trim()
                        }
                        "Ilustrator:" -> {
                            komikIllustrator = eachDetail.text().substringAfter(sep).trim()
                        }
                        "Grafis:" -> {
                            komikGraphics = eachDetail.text().substringAfter(sep).trim()
                        }
                        "Tema:" -> {
                            komikTheme = eachDetail.text().substringAfter(sep).trim()
                        }
                        "Jenis Komik:" -> {
                            komikType = eachDetail.text().substringAfter(sep).trim()
                        }
                        "Jumlah Pembaca:" -> {
                            komikReaderCount = eachDetail.text().substringAfter(sep).trim()
                        }
                    }
                }

                // get komik genres
                val genres = komikDetailSoup.select("div.genre-info > a")
                val komikGenres = mutableListOf<String>()
                for (genre in genres) {
                    komikGenres.add(genre.text().trim())
                }

                // get komik synopsis
                val komikSynopsis = document.select("div.entry-content > p")[0].text().trim()

                // get komik chapters
                val chapters = document.select("div#chapter_list > ul > li")
                val komikChapters = mutableListOf<Map<String, String>>()
                for (chapter in chapters) {
                    val chapterDetail = mapOf(
                        "num" to chapter.select("a > chapter")[0].text().trim(),
                        "release_time" to chapter.select("span.dt > a")[0].text().trim(),
                        "url" to chapter.select("a")[0].absUrl("href")
                    )

                    komikChapters.add(chapterDetail)
                }

                // create a map for komik details
                val komikItem = mapOf(
                    "title" to komikTitle,
                    "description" to komikDescription,
                    "thumbnail" to komikThumbnail,
                    "alt_titles" to komikAltTitles,
                    "state" to komikState,
                    "author" to komikAuthor,
                    "illustrator" to komikIllustrator,
                    "graphics" to komikGraphics,
                    "theme" to komikTheme,
                    "type" to komikType,
                    "genre" to komikGenres,
                    "reader_count" to komikReaderCount,
                    "synopsis" to komikSynopsis,
                    "chapters" to komikChapters
                )
                komikDetails.add(komikItem)

                writeJsonFile(komikDetails, fileName)
            } catch (ignored: IOException) {}

            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            val jsonObject = readJsonFile(fileName)!!.get(0) as JSONObject
            val chapterJson = jsonObject.getJSONArray("chapters")

            val komikThumbnail: ImageView = activityReference.get()!!.findViewById(R.id.komik_thumbnail)
            val komikTitle: TextView = activityReference.get()!!.findViewById(R.id.komik_title)
            val komikType: TextView = activityReference.get()!!.findViewById(R.id.komik_type_text)
            val komikState: TextView = activityReference.get()!!.findViewById(R.id.komik_state_text)
            val komikAuthor: TextView = activityReference.get()!!.findViewById(R.id.komik_author_text)
            val komikIllustrator: TextView = activityReference.get()!!.findViewById(R.id.komik_illustrator_text)
            val komikReaderCount: TextView = activityReference.get()!!.findViewById(R.id.komik_reader_count_text)
            val komikSynopsis: TextView = activityReference.get()!!.findViewById(R.id.komik_synopsis_text)
            val komikChapterList: RecyclerView = activityReference.get()!!.findViewById(R.id.chapter_list)

            Picasso.get()
                .load(jsonObject.getString("thumbnail"))
                .into(komikThumbnail)
            komikTitle.text = jsonObject.getString("title")
            komikType.text = jsonObject.getString("type")
            komikState.text = jsonObject.getString("state")
            komikAuthor.text = jsonObject.getString("author")
            komikIllustrator.text = jsonObject.getString("illustrator")
            komikReaderCount.text = jsonObject.getString("reader_count")
            komikSynopsis.text = jsonObject.getString("synopsis")
            komikChapterList.adapter = KomikChapterListAdapter(chapterJson, activityReference.get()!!.applicationContext)
        }
    }

    inner class GetChapterImg(private var url: String) : AsyncTask<Void, Void, Void>() {
        private val komikChapter = mutableListOf<String>()

        override fun doInBackground(vararg params: Void?): Void? {
            try {
                val document: Document = Jsoup.connect(url).get()
                val chapterImagesSoup = document.select("div#chimg-auh")[0]
                val chapterImages = chapterImagesSoup.select("img")
                for (image in chapterImages) {
                    val chapterImage = image.absUrl("src")
                    komikChapter.add(chapterImage)
                }

            } catch (ignored: IOException) {}

            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            val chapterImageList: RecyclerView = activityReference.get()!!.findViewById(R.id.chapter_img_list)
            chapterImageList.adapter = KomikChapterImgAdapter(komikChapter, activityReference.get()!!.applicationContext)
        }
    }

    inner class GetKomikSearchResult(searchStr: String) : AsyncTask<Void, Void, Void>() {
        private val komikSearchResultList = mutableListOf<Map<String, Any>>()
        private val url = "$baseUrl?s=$searchStr"
        private val fileName = "komik_search_list.json"

        override fun doInBackground(vararg params: Void?): Void? {
            try {
                val document: Document = Jsoup.connect(url).get()
                val komiks = document.select("div.animepost")
                for (komik in komiks) {
                    val komikThumbnail = komik.select("img")[0].absUrl("src")
                    val komikTitle = komik.select("div.tt > h4")[0].text().trim()
                    val komikDetailsUrl = komik.select("a")[0].absUrl("href")
                    val komikRating = komik.select("div.rating > i")[0].text().trim()

                    val komikItem: Map<String, String> = mapOf(
                        "title" to komikTitle,
                        "url" to komikDetailsUrl,
                        "thumbnail"  to komikThumbnail,
                        "rating" to komikRating
                    )
                    komikSearchResultList.add(komikItem)
                }
                writeJsonFile(komikSearchResultList, fileName)
            } catch (ignored: IOException) {}

            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            val komikSearchResultList: RecyclerView = activityReference.get()!!.findViewById(R.id.komik_list)
            val jsonArray = readJsonFile(fileName)

            komikSearchResultList.adapter = KomikSearchResultListAdapter(jsonArray!!, activityReference.get()!!.applicationContext)
        }

    }

}