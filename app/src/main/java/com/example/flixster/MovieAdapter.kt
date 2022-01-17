package com.example.flixster

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

const val MOVIE_EXTRA = "MOVIE_EXTRA"
private const val TAG = "MovieAdapter"
class MovieAdapter(private val context: Context,
                   private val movies: List<Movie>,
                   private val trailers: Map<Int, String>)
    : RecyclerView.Adapter<MovieAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.i(TAG, "onCreateViewHolder")
        val view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.i(TAG, "onBindViewHolder position $position")
        val movie = movies[position]
        holder.bind(movie)
    }

    override fun getItemCount() = movies.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }

        private val ivPoster = itemView.findViewById<ImageView>(R.id.ivPoster)
        private val tvTitle = itemView.findViewById<TextView>(R.id.tvTitle)
        private val tvOverview = itemView.findViewById<TextView>(R.id.tvOverview)

        fun bind(movie: Movie) {
            lateinit var image: String
            val orientation = context.resources.configuration.orientation
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                image = movie.posterImageUrl
            } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                image = movie.backdropImageUrl
            }
            tvTitle.text = movie.title
            tvOverview.text = movie.overview
            Glide.with(context).load(image).into(ivPoster)
        }

        override fun onClick(p0: View?) {
            val movie = movies[adapterPosition]
            Toast.makeText(context, movie.title, Toast.LENGTH_SHORT).show()
            try {
                val i = Intent(context, InfoActivity::class.java)
                i.putExtra(MOVIE_EXTRA, movie)
                i.putExtra("trailer", trailers[movies[adapterPosition].movieId])
                context.startActivity(i)
            } catch (noActivity: ActivityNotFoundException) {
                noActivity.printStackTrace()
            }

        }
    }
}
