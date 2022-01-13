package com.example.flixster

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONException

private const val TAG = "MainActivity"
private const val NOW_PLAYING_URL = "https://api.themoviedb.org/3/movie/now_playing?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed"
private const val TRAILER_URL_1 = "https://api.themoviedb.org/3/movie/" // movie_id
private const val TRAILER_URL_2 = "/trailers?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed"
class MainActivity : AppCompatActivity() {
    private val movies = mutableListOf<Movie>()
    private val trailers = mutableMapOf<Int, String>()
    private lateinit var rvMovies: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rvMovies = findViewById<RecyclerView>(R.id.rvMovies)

        val onClickListener = object : MovieAdapter.OnClickListener {
            override fun onItemClicked(position: Int) {
                Log.i(TAG,"Item Clicked: $position")
                try {
                    val i = Intent(this@MainActivity, InfoActivity::class.java)
                    i.putExtra("backdrop", movies[position].backdropImageUrl)
                    i.putExtra("poster", movies[position].posterImageUrl)
                    i.putExtra("trailer", trailers[movies[position].movieId])
                    i.putExtra("title", movies[position].title)
                    i.putExtra("releaseDate", movies[position].releaseDate)
                    i.putExtra("rating", movies[position].rating)
                    i.putExtra("overview", movies[position].overview)
                    i.putExtra("position", position)
                    i.putExtra("code", 400)
                    startActivity(i)
                } catch (noActivity: ActivityNotFoundException) {
                    noActivity.printStackTrace()
                }
            }
        }

        val movieAdapter = MovieAdapter(this, movies, onClickListener)
        rvMovies.adapter = movieAdapter
        rvMovies.layoutManager = LinearLayoutManager(this)

        val client = AsyncHttpClient()
        client.get(NOW_PLAYING_URL, object: JsonHttpResponseHandler() {
            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                Log.e(TAG, "onFailure $statusCode")
            }

            override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                Log.i(TAG, "onSuccess: JSON data $json")
                try {
                    val movieJsonArray = json.jsonObject.getJSONArray("results")
                    movies.addAll(Movie.fromJsonArray(movieJsonArray))
                    movieAdapter.notifyDataSetChanged()
                    Log.i(TAG, "Movie list $movies")
                } catch (e: JSONException) {
                    Log.e(TAG, "Encountered exception $e")
                }


                for (i in 0 until movies.size) {
                    val TRAILER_URL = TRAILER_URL_1 + movies[i].movieId.toString() + TRAILER_URL_2
                    client.get(TRAILER_URL, object: JsonHttpResponseHandler() {
                        override fun onFailure(
                            statusCode: Int,
                            headers: Headers?,
                            response: String?,
                            throwable: Throwable?
                        ) {
                            Log.e(TAG, "onFailure $statusCode")
                        }

                        override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                            Log.i(TAG, "onSuccess: JSON data $json")
                            try {
                                val trailerJsonArray = json.jsonObject.getJSONArray("youtube")
                                for (j in 0 until trailerJsonArray.length()) {
                                    val trailerJson = trailerJsonArray.getJSONObject(j)
                                    if (trailerJson.getString("name").contains("Official Trailer")) {
                                        trailers[movies[i].movieId] = trailerJsonArray.getJSONObject(j).getString("source")
                                        break
                                    }
                                }
                                movieAdapter.notifyDataSetChanged()
                                Log.i(TAG, "Trailer list $trailers")
                            } catch (e: JSONException) {
                                Log.e(TAG, "Encountered exception $e")
                            }
                        }
                    })
                }


            }
        })


    }
}