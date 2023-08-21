package com.peacemaker.android.courselearn.ui.util

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView

class VideoPlayerManager(context: Context, lifecycle: Lifecycle) : DefaultLifecycleObserver {

    private val exoPlayer = ExoPlayer.Builder(context)
        .setMediaSourceFactory(DefaultMediaSourceFactory(context))
        .build()

    init {
        lifecycle.addObserver(this)
    }

    // Private function to prepare the player and play
    private fun prepareAndPlay(mediaItem: MediaItem) {
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    // Populate the playlist and prepare the player
    fun setPlaylist(playerView: PlayerView, videoUrls: List<String>, isLoading: ((Boolean) -> Unit?)? =null) {
        playerView.player = exoPlayer
        val mediaItems = videoUrls.map { url ->
            MediaItem.fromUri(url)
        }
        // Show loading indicator while preparing
        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    isLoading?.invoke(false)
                }
            }
            override fun onPlaybackStateChanged(state: Int) {
                // Show loading indicator during buffering
                if (state == Player.STATE_BUFFERING) {
                    isLoading?.invoke(true)
                } else {
                    isLoading?.invoke(false)
                }
            }
        })
        exoPlayer.setMediaItems(mediaItems)
        exoPlayer.prepare()
    }

    // Set a new video URL from the list based on ID and play
    fun setNewVideoUrlAndPlayById(playerView: PlayerView, videoUrls: List<String>, videoId: Int,isLoading:(Boolean)->Unit) {
        playerView.player = exoPlayer
        val videoUrl = videoUrls.getOrNull(videoId)
        if (videoUrl != null) {
            // Show loading indicator while preparing
            isLoading.invoke(true)
            exoPlayer.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    if (isPlaying) {
                       isLoading.invoke(false)
                    }
                }

                override fun onPlaybackStateChanged(state: Int) {
                    // Show loading indicator during buffering
                    if (state == Player.STATE_BUFFERING) {
                        isLoading.invoke(true)
                    } else {
                        isLoading.invoke(false)
                    }
                }
            })

            val mediaItem = MediaItem.fromUri(videoUrl)
            prepareAndPlay(mediaItem)
        }
    }

    // Set and play a video URL from the list based on index
    fun setVideoUrlIdAndPlay(index: Int?, isLoading: (Boolean) -> Unit) {
        if (index in 0 until exoPlayer.mediaItemCount) {
            val mediaItem = exoPlayer.getMediaItemAt(index!!)
            // Show loading indicator while preparing
            isLoading.invoke(true)
            exoPlayer.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    if (isPlaying) {
                       isLoading.invoke(false)
                    }
                }

                override fun onPlaybackStateChanged(state: Int) {
                    // Show loading indicator during buffering
                    if (state == Player.STATE_BUFFERING) {
                        isLoading.invoke(true)
                    } else {
                        isLoading.invoke(false)
                    }
                }
            })
            prepareAndPlay(mediaItem)
        }
    }

    // Play the video
    fun play() {
        exoPlayer.play()
    }

    // Pause the video
    fun pause() {
        exoPlayer.pause()
    }

    fun resume(){
        exoPlayer
    }

    // Toggle play/pause
    fun playPause(isPlaying:(Boolean)->Unit) {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
            isPlaying.invoke(true)
        } else {
            exoPlayer.play()
            isPlaying.invoke(false)
        }
    }


    // Release the player when it's no longer needed
    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        exoPlayer.release()
    }
}
