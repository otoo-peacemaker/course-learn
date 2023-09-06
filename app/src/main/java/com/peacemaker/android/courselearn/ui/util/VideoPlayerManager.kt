package com.peacemaker.android.courselearn.ui.util

import android.content.Context
import android.net.Uri
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultDataSourceFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.MediaSourceEventListener
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import com.peacemaker.android.courselearn.R

class VideoPlayerManager(context: Context, lifecycle: Lifecycle) : DefaultLifecycleObserver {
    private var playbackPosition = 0L
    private var playWhenReady = true
    private var isVideoPlaying = false


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
    fun setPlaylist(playerView: PlayerView, videoUrls: MutableList<String?>, isLoading: ((Boolean) -> Unit?)? =null) {
        playerView.player = exoPlayer
        val mediaItems = videoUrls.map { url ->
            MediaItem.fromUri(url!!)
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
    // Populate the playlist and prepare the player
    fun setPlayUrlAndPlay(playerView: PlayerView, videoUrl: String?, button: TextView ,isLoading: ((Boolean) -> Unit?)? =null) {
        playerView.player=exoPlayer
        val mediaItem = MediaItem.fromUri(videoUrl.toString())
        // Show loading indicator while preparing
        updateButtonIcon(button, R.drawable.ico_course_pause)
        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    isLoading?.invoke(false)
                    updateButtonIcon(button, R.drawable.ico_course_pause)
                }else {
                    updateButtonIcon(button, R.drawable.ico_play)
                }
            }
            override fun onPlaybackStateChanged(state: Int) {
                // Show loading indicator during buffering
                if (state == Player.STATE_BUFFERING) {
                    isLoading?.invoke(true)
                    updateButtonIcon(button, R.drawable.ico_course_pause)
                } else {
                    isLoading?.invoke(false)
                    updateButtonIcon(button, R.drawable.ico_play)
                }
            }
        })
        prepareAndPlay(mediaItem)
    }
    // Set a new video URL from the list based on ID and play
    fun setNewVideoUrlAndPlayById(
        videoUrls: MutableList<String?>, videoId: Int,
        isLoading:(Boolean)->Unit) {
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
    fun setVideoUrlIdAndPlay(index: Int?, button: TextView,isLoading: (Boolean) -> Unit) {
        if (index in 0 until exoPlayer.mediaItemCount) {
            val mediaItem = exoPlayer.getMediaItemAt(index!!)
            // Show loading indicator while preparing
            isLoading.invoke(true)
            updateButtonIcon(button, R.drawable.ico_course_pause)
            exoPlayer.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    if (isPlaying) {
                        updateButtonIcon(button, R.drawable.ico_course_pause)
                       isLoading.invoke(false)
                    }
                }

                override fun onPlaybackStateChanged(state: Int) {
                    // Show loading indicator during buffering
                    if (state == Player.STATE_BUFFERING) {
                        isLoading.invoke(true)
                        updateButtonIcon(button, R.drawable.ico_course_pause)
                    } else {
                        isLoading.invoke(false)
                        updateButtonIcon(button, R.drawable.ico_play)
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

    // Pause the video if it is currently playing and save playback position
    fun pause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.playWhenReady = false
            playbackPosition = exoPlayer.currentPosition
            isVideoPlaying = false
        }
    }

    // Resume the video from the saved playback position
    fun resume() {
        if (!isVideoPlaying) {
            exoPlayer.seekTo(playbackPosition)
            exoPlayer.playWhenReady = true
            isVideoPlaying = true
        }
    }

    fun isPlaying():Boolean{
        return exoPlayer.isPlaying
    }

    fun isLoading():Boolean{
        return exoPlayer.isLoading
    }

    fun stopAndRelease(){
        exoPlayer.stop()
        exoPlayer.release()
    }

    // Toggle play/pause
    fun playPause(button: TextView) {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
            updateButtonIcon(button, R.drawable.ico_play)
        } else {
            exoPlayer.play()
            updateButtonIcon(button, R.drawable.ico_course_pause)
        }
    }

    private fun updateButtonIcon(button: TextView, iconResId: Int) {
        button.setBackgroundResource(if (iconResId == R.drawable.ico_play) R.drawable.ico_play else R.drawable.ico_course_pause)
        button.setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0, 0)
    }

    private fun togglePlayPause(textView: TextView,listener: (Unit)->Unit) {
        isVideoPlaying = !isVideoPlaying
        textView.setOnClickListener {
            listener.invoke(
                if (isVideoPlaying) {
                    textView.setBackgroundResource(R.drawable.ico_course_pause)
                    textView.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ico_course_pause, 0, 0, 0
                    )
                    // Implement logic to start playback here
                } else {
                    textView.setBackgroundResource(R.drawable.ico_play)
                    textView.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ico_play, 0, 0, 0
                    )
                    // Implement logic to pause playback here
                }
            )

        }
    }

    // Release the player when it's no longer needed
    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        exoPlayer.release()
    }
}
