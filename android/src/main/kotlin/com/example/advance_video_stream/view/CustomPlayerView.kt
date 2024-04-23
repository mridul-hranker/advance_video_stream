package com.example.advance_video_stream.view

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.format.DateUtils
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.advance_video_stream.R
import com.example.advance_video_stream.libre_tube.VideoResolution
import com.example.advance_video_stream.libre_tube.round
import com.example.advance_video_stream.libre_tube.updateParameters
import com.google.android.material.dialog.MaterialAlertDialogBuilder

@UnstableApi
class CustomPlayerView(context: Context, attrs: AttributeSet?) : PlayerView(context, attrs), AdapterView.OnItemSelectedListener {
    private val TAG = "CustomPlayerView"

    private var position: TextView = findViewById(R.id.position)
    private var duration: TextView = findViewById(R.id.duration)

    private var playPauseBTN: ImageButton = findViewById(R.id.playPauseBTN)
    private var toggleResize: ImageButton = findViewById(R.id.toggle_resize)

    private var spinnerQuality: Spinner = findViewById(R.id.spinner_quality)
    private var spinnerSpeed: Spinner = findViewById(R.id.spinner_speed)
    val topBarTextVideoTitle: TextView = findViewById(R.id.top_bar_text_video_title)

    private val runnableHandler = Handler(Looper.getMainLooper())

    private var isLive: Boolean = false

    private val qualityList = mutableListOf<VideoResolution>()
    private val speedList = listOf(0.5f, 0.75f, 1f, 1.25f, 1.5f, 1.75f, 2f)

    private lateinit var defaultTrackSelector: DefaultTrackSelector

    fun initialize(isLive: Boolean = false, exoPlayer: ExoPlayer) {
        Log.d(TAG, "initialize: called")
        this.isLive = isLive
        if (isLive) {
            duration.visibility = View.GONE
        }

        player?.addListener(object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                super.onEvents(player, events)
                if (events.containsAny(Player.EVENT_PLAYBACK_STATE_CHANGED, Player.EVENT_IS_PLAYING_CHANGED, Player.EVENT_PLAY_WHEN_READY_CHANGED)) {
                    playPauseBTN.setImageResource(getPlayPauseActionIcon(player))

                    if (qualityList.isEmpty()) {
                        val exoPlayerCurrentTracksList: List<VideoResolution> = exoPlayer.currentTracks.groups.asSequence().flatMap { group ->
                            (0 until group.length).map { group.getTrackFormat(it).height }
                        }.filter { it > 0 }.toSortedSet(compareByDescending { it }).toList().map { VideoResolution("${it}p", it) }

                        qualityList.addAll(exoPlayerCurrentTracksList)

                        if (qualityList.isNotEmpty()) {
                            qualityList.add(0, VideoResolution("Auto", Int.MAX_VALUE))
                        }

                        defaultTrackSelector = exoPlayer.trackSelector!! as DefaultTrackSelector

                        /*Log.d(TAG, "onEvents: current quality ${defaultTrackSelector.parameters.maxVideoHeight}")
                        Log.d(TAG, "onEvents: qualityList ${qualityList.size}")

                        qualityList.forEach {
                            Log.d(TAG, "onEvents: qualityList it $it")
                        }*/

                        setVideoList(defaultTrackSelector.parameters.maxVideoHeight)
                    }

                    setPlaybackSpeed()

                    // keep screen on if the video is playing
//                    keepScreenOn = player.isPlaying == true
//                    onPlayerEvent(player, events)
                }
            }
        })
    }

    fun setVideoList(currentQuality: Int) {
        val arrayAdapter = ArrayAdapter(context, R.layout.dropdown_item, qualityList.map { it.name })
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerQuality.adapter = arrayAdapter

        // Spinner click listener
        spinnerQuality.onItemSelectedListener = this

        val findItem = qualityList.find { videoResolution -> videoResolution.resolution == currentQuality }

        spinnerQuality.setSelection(qualityList.indexOf(findItem), true)
    }

    fun setPlaybackSpeed() {
        val speedUi = speedList.map { "${String.format(" % .2f", it)}x" }

        val arrayAdapter = ArrayAdapter(context, R.layout.dropdown_item, speedUi)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSpeed.adapter = arrayAdapter

        spinnerSpeed.onItemSelectedListener = this

        spinnerSpeed.setSelection(2, true)
    }

    init {
        Log.d(TAG, "init: called")

        playPauseBTN.setOnClickListener {
            if (player?.isPlaying == true || player?.playbackState == Player.STATE_ENDED) {
                player?.pause()
            } else {
                player?.play()
            }
        }

        toggleResize.setOnClickListener {
            Log.d(TAG, "setOnClickListener toggle_options: called")
            toggleResizeVideo()
        }

        findViewById<FrameLayout>(R.id.rewindBTN).setOnClickListener {
            val seekDiff = (player?.currentPosition ?: 0) - 10 * 1000
            Log.d(TAG, "setOnClickListener rewindBTN: seekDiff $seekDiff")
            player?.seekTo(seekDiff)
        }

        findViewById<FrameLayout>(R.id.forwardBTN).setOnClickListener {
            val seekDiff = (player?.currentPosition ?: 0) + 10 * 1000
            Log.d(TAG, "setOnClickListener forwardBTN: seekDiff $seekDiff")
            player?.seekTo(seekDiff)
        }

        updateCurrentPosition()
    }

    private fun updateCurrentPosition() {
        val position = player?.currentPosition?.div(1000) ?: 0
        val duration = player?.duration?.takeIf { it != C.TIME_UNSET }?.div(1000) ?: 0
        val timeLeft = duration - position

        this.position.text = if (isLive) "Live" else DateUtils.formatElapsedTime(position)
        this.duration.text = "-${DateUtils.formatElapsedTime(timeLeft)}"

        runnableHandler.postDelayed(this::updateCurrentPosition, 100)
    }

    private fun getPlayPauseActionIcon(player: Player) = when {
        player.isPlaying -> R.drawable.ic_pause
        player.playbackState == Player.STATE_ENDED -> R.drawable.ic_restart
        else -> R.drawable.ic_play
    }

    private fun toggleResizeVideo() {
        val aspectRatioModes = listOf(
            AspectRatioFrameLayout.RESIZE_MODE_FIT,
            AspectRatioFrameLayout.RESIZE_MODE_ZOOM,
            AspectRatioFrameLayout.RESIZE_MODE_FILL
        )

        player.let {
            val currentAspectRatioIndex = aspectRatioModes.indexOf(resizeMode)
            var nextAspectRatioIndex = currentAspectRatioIndex + 1
            if (nextAspectRatioIndex > aspectRatioModes.lastIndex) {
                nextAspectRatioIndex = 0
            }
            resizeMode = aspectRatioModes[nextAspectRatioIndex]
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        Log.d(TAG, "onItemSelected: view ${view}")
        Log.d(TAG, "onItemSelected: view?.id ${view?.id}")


        if (parent?.id == spinnerQuality.id) {
            defaultTrackSelector.updateParameters {

                val resolution: Int = qualityList[position].resolution

//                Log.d(TAG, "onItemSelected: updateParameters resolution $resolution")

                setMinVideoSize(Int.MIN_VALUE, resolution)
                setMaxVideoSize(Int.MAX_VALUE, resolution)
            }
        } else if (parent?.id == spinnerSpeed.id) {/*Log.d(TAG, "onItemSelected: view == spinnerSpeed")
            Log.d(TAG, "onItemSelected: view == spinnerSpeed speedList[position].round(2) ${speedList[position].round(2)}")*/
            player?.playbackParameters = PlaybackParameters(speedList[position].round(2), 1f)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }


}