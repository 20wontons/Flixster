package com.example.flixster

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class InfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        val backdrop = getIntent().getStringExtra("backdrop")
        val title = getIntent().getStringExtra("title")
        val releaseDate = getIntent().getStringExtra("releaseDate")
        val rating = getIntent().getDoubleExtra("rating",0.0)
        val overview = getIntent().getStringExtra("overview")
        val position = getIntent().getIntExtra("position", 0)
        val code = getIntent().getIntExtra("code", 0)

        Glide.with(this).load(backdrop).into(findViewById<ImageView>(R.id.infoBackdrop))
        findViewById<TextView>(R.id.infoTitle).setText(title)
        findViewById<TextView>(R.id.infoReleaseDate).setText("Release Date: $releaseDate")
        findViewById<RatingBar>(R.id.infoRatingBar).setRating((rating/2.0).toFloat())
        findViewById<TextView>(R.id.infoOverview).setText(overview)

        findViewById<Button>(R.id.infoBackButton).setOnClickListener {
            this.finish()
        }
    }
}