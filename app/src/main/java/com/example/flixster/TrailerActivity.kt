package com.example.flixster

import com.google.android.youtube.player.YouTubeBaseActivity
import android.os.Bundle
import android.view.View
import com.example.flixster.R
import com.google.android.youtube.player.YouTubePlayerView
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubeInitializationResult

// TODO: Haven't addressed the issue of what to do when trailer does not exist
class TrailerActivity : YouTubeBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trailer)

        val trailer = getIntent().getStringExtra("trailer")

        val youTubePlayerView = findViewById<View>(R.id.player) as YouTubePlayerView
        youTubePlayerView.initialize("AIzaSyAgY_UzUg1pmE-lj6BdJQT-3afbUbOLx9o",
            object : YouTubePlayer.OnInitializedListener {
                override fun onInitializationSuccess(
                    provider: YouTubePlayer.Provider,
                    youTubePlayer: YouTubePlayer, b: Boolean
                ) {
                    youTubePlayer.cueVideo(trailer)
                }

                override fun onInitializationFailure(
                    provider: YouTubePlayer.Provider,
                    youTubeInitializationResult: YouTubeInitializationResult
                ) {
                }
            })
    }
}