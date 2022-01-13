package com.example.flixster

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

private const val TAG = "InfoActivity"
class InfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        val backdrop = getIntent().getStringExtra("backdrop")
        val poster = getIntent().getStringExtra("poster")
        val trailer = getIntent().getStringExtra("trailer")
        val title = getIntent().getStringExtra("title")
        val releaseDate = getIntent().getStringExtra("releaseDate")
        val rating = getIntent().getDoubleExtra("rating",0.0)
        val overview = getIntent().getStringExtra("overview")
        val position = getIntent().getIntExtra("position", 0)
        val code = getIntent().getIntExtra("code", 0)

        var image: String? = ""
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            image = backdrop
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            image = poster
        }
        Glide.with(this).load(image).into(findViewById<ImageView>(R.id.infoPoster))
        findViewById<ImageView>(R.id.infoPlayButton).setOnClickListener {
            Log.i(TAG, "User clicked on play button")
            try {
                val i = Intent(this@InfoActivity, TrailerActivity::class.java)
                i.putExtra("trailer", trailer)
                i.putExtra("position", position)
                i.putExtra("code", 400)
                startActivity(i)
            } catch (noActivity: ActivityNotFoundException) {
                noActivity.printStackTrace()
            }
        }

        findViewById<TextView>(R.id.infoTitle).setText(title)
        findViewById<TextView>(R.id.infoReleaseDate).setText("Release Date: $releaseDate")
        findViewById<RatingBar>(R.id.infoRatingBar).setRating((rating/2.0).toFloat())
        findViewById<TextView>(R.id.infoOverview).setText(overview)
    }
}