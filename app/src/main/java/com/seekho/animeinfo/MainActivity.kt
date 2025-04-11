package com.seekho.animeinfo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.android.volley.Request
import com.android.volley.Request.Method
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.JsonRequest
import com.android.volley.toolbox.Volley
import com.seekho.animeinfo.R
import com.seekho.animeinfo.dashboard.AnimeItem
import com.seekho.animeinfo.dashboard.TopListAdapter
import com.seekho.animeinfo.dashboard.TopListHeaderAdapter
import org.json.JSONObject
import kotlin.math.abs

class MainActivity : AppCompatActivity() {
    private var topListUrl = "https://api.jikan.moe/v4/top/anime"
    private var animeList = ArrayList<AnimeItem>()
    private var headerAnimeList = ArrayList<AnimeItem>()
    private lateinit var topListHeaderAdapter: TopListHeaderAdapter
    private lateinit var topListAdapter: TopListAdapter
    private lateinit var scrollRunnable: Runnable
    private lateinit var headerViewPager: ViewPager2
    private lateinit var recyclerView: RecyclerView
    private lateinit var handler: Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        headerViewPager = findViewById(R.id.header_viewpager)
        recyclerView = findViewById(R.id.recycler_view)
        topListAdapter = TopListAdapter(animeList)
        topListHeaderAdapter = TopListHeaderAdapter(headerAnimeList)
        headerViewPager.adapter = topListHeaderAdapter
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = topListAdapter
        headerViewPager.offscreenPageLimit = 3
        headerViewPager.setPageTransformer { page, position ->
            page.scaleY = .85f + (1- abs(position))*0.15f
        }

        loadData()
    }

    override fun onResume() {
        super.onResume()
        handler = Handler(Looper.getMainLooper())
        scrollRunnable = object: Runnable{
            override fun run() {
                val nextItem = if(headerViewPager.currentItem == headerAnimeList.size -1) 0 else headerViewPager.currentItem+1
                headerViewPager.setCurrentItem(nextItem, true)
                handler.postDelayed(this, 3000)
            }
        }
        scrollRunnable.run()
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(scrollRunnable)
    }
    private fun loadData(){
        val jsonRequest = JsonObjectRequest(Method.GET, topListUrl,null, {
            Log.d("Response", "$it")
            val jsonArray = it.getJSONArray("data")
            for(i in 0 until jsonArray.length()){
                val jsonObject = jsonArray.getJSONObject(i)
                val title = jsonObject.getString("title")
                val imagesObject = jsonObject.getJSONObject("images")
                val imageJpg = imagesObject.getJSONObject("jpg")
                val image = imageJpg.getString("image_url")
                animeList.add(AnimeItem(jsonObject.getInt("mal_id"), title,jsonObject.getString("episodes"), jsonObject.getString("rating"), image))
                if (i < 5)
                    headerAnimeList.add(animeList[i])
            }
            topListHeaderAdapter.notifyDataSetChanged()
            topListAdapter.notifyDataSetChanged()
        },{
            Log.d("Response Error", it.message!!)
        })
        val volleyRequest = Volley.newRequestQueue(this)
        volleyRequest.add(jsonRequest)
    }
}