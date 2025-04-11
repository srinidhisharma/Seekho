package com.seekho.animeinfo.content

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.media3.exoplayer.ExoPlayer
import com.android.volley.Request.Method
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.seekho.animeinfo.R


class DetailInfoActivity : AppCompatActivity(), View.OnClickListener {
    private var detailUrl = "https://api.jikan.moe/v4/anime/"
    private var id = ""
    private lateinit var synopsisText: TextView
    private lateinit var genreText: TextView
    private lateinit var episodeText: TextView
    private lateinit var ratingText: TextView
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var playerView: YouTubePlayerView
    private lateinit var thumbnailImage: ImageView
    private lateinit var title: TextView
    private lateinit var backBtn: ImageButton

    data class Anime(val title: String, val synopsis: String, val genres: List<String>, val episodes: Int, val rating: Double, val trailerUrl: String?,val posterUrl: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_info)
        id = intent.getIntExtra("AnimeId", 0).toString()
        synopsisText = findViewById(R.id.synopsis_page)
        genreText = findViewById(R.id.genre_page)
        episodeText = findViewById(R.id.episodes_page)
        ratingText = findViewById(R.id.rating_page)
        playerView = findViewById(R.id.playerView)
        thumbnailImage = findViewById(R.id.thumbnailImage)
        title = findViewById(R.id.title)
        backBtn = findViewById(R.id.back)
        backBtn.setOnClickListener(this)
        lifecycle.addObserver(playerView)
        loadData()
    }

    private fun loadData(){
        val jsonRequest = JsonObjectRequest(Method.GET, detailUrl+"$id", null,{
            val jsonObject = it.getJSONObject("data")
            val trailerObj = jsonObject.getJSONObject("trailer")
            val trailer = trailerObj.getString("youtube_id")
            val imageList = trailerObj.getJSONObject("images")
            val titleName = jsonObject.getString("title")
            val synopsis = jsonObject.getString("synopsis")
            val posterUrl = imageList.getString("image_url")
            val genreList = jsonObject.getJSONArray("genres")
            var genre = ""
            for (i in 0 until genreList.length()){
                val obj = genreList.getJSONObject(i)
                genre = if(genre.isEmpty()) obj.getString("name") else ", ${obj.getString("name")}"
            }
            val rating = jsonObject.getString("rating")
            val episodes = "${jsonObject.getInt("episodes")} Episodes"
            synopsisText.text = synopsis
            genreText.text = genre
            episodeText.text = episodes
            ratingText.text = rating
            title.text = titleName
            if(trailer.isNotEmpty()){
                playerView.addYouTubePlayerListener(object :
                    AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        youTubePlayer.loadVideo(trailer, 0f)
                    }
                })
            }else{
                playerView.isVisible = false
                thumbnailImage.isVisible = true
                Glide.with(this).load(posterUrl).into(thumbnailImage)
            }
        },{

        })
        val volleyRequest = Volley.newRequestQueue(this)
        volleyRequest.add(jsonRequest)
    }

    override fun onClick(v: View?) {
        if(v == backBtn){
            finish()
        }
    }
}