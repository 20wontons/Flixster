package com.example.flixster

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerView
import jp.wasabeef.glide.transformations.BlurTransformation
import okhttp3.Headers

private const val YOUTUBE_API_KEY = "AIzaSyAgY_UzUg1pmE-lj6BdJQT-3afbUbOLx9o"
private const val TRAILERS_URL = "https://api.themoviedb.org/3/movie/%d/trailers?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed"
private const val TAG = "InfoActivity"

class InfoActivity : YouTubeBaseActivity() {

    private lateinit var infoBackground: ImageView
    private lateinit var infoPlayer: YouTubePlayerView
    private lateinit var infoTitle: TextView
    private lateinit var infoReleaseDate: TextView
    private lateinit var infoRatingBar: RatingBar
    private lateinit var infoOverview:  TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        infoBackground = findViewById<ImageView>(R.id.infoBackground)
        infoPlayer = findViewById<YouTubePlayerView>(R.id.infoPlayer)
        infoTitle = findViewById<TextView>(R.id.infoTitle)
        infoReleaseDate = findViewById<TextView>(R.id.infoReleaseDate)
        infoRatingBar = findViewById<RatingBar>(R.id.infoRatingBar)
        infoOverview = findViewById<TextView>(R.id.infoOverview)

        val movie = intent.getParcelableExtra<Movie>(MOVIE_EXTRA) as Movie
        Log.i(TAG, "Movie is $movie")

        var image: String? = ""
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            image = movie.posterImageUrl
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            image = movie.backdropImageUrl
        }
        Glide.with(this).load(image).transform(BlurTransformation()).into(infoBackground)
//        findViewById<ImageView>(R.id.infoPlayButton).setOnClickListener {
//            Log.i(TAG, "User clicked on play button")
//            try {
//                val i = Intent(this@InfoActivity, TrailerActivity::class.java)
//                i.putExtra("trailer", trailer)
//                startActivity(i)
//            } catch (noActivity: ActivityNotFoundException) {
//                noActivity.printStackTrace()
//            }
//        }

        infoTitle.text = movie.title
        infoReleaseDate.text = "Release Date: ${movie.releaseDate}"
        infoRatingBar.rating = movie.rating.toFloat()
        infoOverview.text = movie.overview

        val client = AsyncHttpClient()
        client.get(TRAILERS_URL.format(movie.movieId), object: JsonHttpResponseHandler() {
            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                Log.e(TAG, "onFailure $statusCode")
            }

            override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                Log.i(TAG, "onSuccess")
                val results = json.jsonObject.getJSONArray("youtube")
                if (results.length() == 0) {
                    Log.w(TAG, "No movie trailers found")
                    return
                }
                for (j in 0 until results.length()) {
                    val movieTrailerJson = results.getJSONObject(j)
                    if (movieTrailerJson.getString("name").contains("Official Trailer")) {
                        Log.i(TAG, "Movie official trailer found!")
                        val youtubeKey = movieTrailerJson.getString("source")
                        initializeYoutube(youtubeKey, movie.rating)
                        return
                    }
                }
                for (j in 0 until results.length()) {
                    val movieTrailerJson = results.getJSONObject(j)
                    if (movieTrailerJson.getString("name").contains("Trailer")) {
                        Log.i(TAG, "Movie trailer found!")
                        val youtubeKey = movieTrailerJson.getString("source")
                        initializeYoutube(youtubeKey, movie.rating)
                        return
                    }
                }
                Log.w(TAG, "No movie official trailers found")
                val movieTrailerJson = results.getJSONObject(0)
                val youtubeKey = movieTrailerJson.getString("source")
                initializeYoutube(youtubeKey, movie.rating)
            }

        })


    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            android.R.id.home -> {
//                supportFinishAfterTransition()
//                return true
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }

    private fun initializeYoutube(youtubeKey: String, rating: Double) {
        infoPlayer.initialize(YOUTUBE_API_KEY, object: YouTubePlayer.OnInitializedListener {
            override fun onInitializationSuccess(
                provider: YouTubePlayer.Provider?,
                player: YouTubePlayer?,
                p2: Boolean
            ) {
                Log.i(TAG, "onInitializationSuccess")
                if (rating > 7.0) {
                    player?.loadVideo(youtubeKey)
                } else {
                    player?.cueVideo(youtubeKey)
                }
            }

            override fun onInitializationFailure(
                provider: YouTubePlayer.Provider?,
                p1: YouTubeInitializationResult?
            ) {
                Log.i(TAG, "onInitializationFailure")
            }

        })
    }
}