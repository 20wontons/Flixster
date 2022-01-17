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

class MainActivity : AppCompatActivity() {
    private val movies = mutableListOf<Movie>()
    private val trailers = mutableMapOf<Int, String>()
    private lateinit var rvMovies: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rvMovies = findViewById<RecyclerView>(R.id.rvMovies)

        val movieAdapter = MovieAdapter(this, movies, trailers)
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


//                for (i in 0 until movies.size) {
//                    val TRAILER_URL = TRAILERS_URL + movies[i].movieId.toString()
//                    client.get(TRAILER_URL, object: JsonHttpResponseHandler() {
//                        override fun onFailure(
//                            statusCode: Int,
//                            headers: Headers?,
//                            response: String?,
//                            throwable: Throwable?
//                        ) {
//                            Log.e(TAG, "onFailure $statusCode")
//                        }
//
//                        override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
//                            Log.i(TAG, "onSuccess: JSON data $json")
//                            try {
//                                val trailerJsonArray = json.jsonObject.getJSONArray("youtube")
//                                for (j in 0 until trailerJsonArray.length()) {
//                                    val trailerJson = trailerJsonArray.getJSONObject(j)
//                                    if (trailerJson.getString("name").contains("Official Trailer")) {
//                                        trailers[movies[i].movieId] = trailerJsonArray.getJSONObject(j).getString("source")
//                                        break
//                                    }
//                                }
//                                movieAdapter.notifyDataSetChanged()
//                                Log.i(TAG, "Trailer list $trailers")
//                            } catch (e: JSONException) {
//                                Log.e(TAG, "Encountered exception $e")
//                            }
//                        }
//                    })
//                }


            }
        })


    }
}