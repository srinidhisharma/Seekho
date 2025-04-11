package com.seekho.animeinfo.dashboard

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.seekho.animeinfo.R
import com.seekho.animeinfo.content.DetailInfoActivity

data class AnimeItem(val animeId: Int, val title: String, val episodeCount: String, val rating: String, val image: String)
class TopListHeaderAdapter(private val animeList: ArrayList<AnimeItem>): RecyclerView.Adapter<TopListHeaderAdapter.ViewHolder>()  {

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val title: TextView = itemView.findViewById(R.id.title)
        val episodeCount: TextView = itemView.findViewById(R.id.episode_count)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.custom_view_list_info_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val animeModal = animeList[position]
        val episodes = "${animeModal.episodeCount} Episodes"
        Glide.with(context).load(animeModal.image).into(holder.imageView)
        holder.title.text = animeModal.title
        holder.episodeCount.text = episodes
        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailInfoActivity::class.java)
            intent.putExtra("AnimeId", animeModal.animeId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return animeList.size
    }
}