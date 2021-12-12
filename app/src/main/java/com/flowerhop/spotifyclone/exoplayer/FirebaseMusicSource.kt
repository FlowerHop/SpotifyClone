package com.flowerhop.spotifyclone.exoplayer

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import androidx.core.net.toUri
import com.flowerhop.spotifyclone.data.remote.MusicDatabase
import com.flowerhop.spotifyclone.exoplayer.State.*
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirebaseMusicSource @Inject constructor(
    private val musicDatabase: MusicDatabase
){
    var songs = emptyList<MediaMetadataCompat>()

    suspend fun fetchMediaData() = withContext(Dispatchers.IO) {
        state = Initializing
        val allSongs = musicDatabase.getAllSongs()
        songs = allSongs.map { song ->
            MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "Fake Artist")
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, song.mediaId)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.title)
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, song.title)
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, song.downloadUrl)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, song.thumbnailUrl)
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, song.thumbnailUrl)
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, "Fake subtitle")
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, "Fake description")
                .build()
        }

        state = Initialized
    }

    fun asMediaSource(dataSourceFactory: DefaultDataSourceFactory): ConcatenatingMediaSource {
        val concatenatingMediaSource = ConcatenatingMediaSource()
        songs.forEach { song ->
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(song.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI).toUri())
            concatenatingMediaSource.addMediaSource(mediaSource)
        }

        return concatenatingMediaSource
    }

    fun asMediaItems() = songs.map { song ->
        val desc = MediaDescriptionCompat.Builder()
            .setMediaUri(song.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI).toUri())
            .setTitle(song.description.title)
            .setSubtitle(song.description.subtitle)
            .setMediaId(song.description.mediaId)
            .setIconUri(song.description.iconUri)
            .build()

        MediaBrowserCompat.MediaItem(desc, 0)
    }

    private val onReadyListeners = mutableListOf<(Boolean) -> Unit>()
    private var state: State = Created
    set(value) {
        if (value == Initialized || value == Error) {
            synchronized(onReadyListeners) {
                field = value
                onReadyListeners.forEach { listener ->
                    listener(state == Initialized)
                }
            }
        } else
            field = value
    }

    fun whenReady(action: (Boolean) -> Unit): Boolean {
        if (state == Created || state == Initializing) {
            onReadyListeners += action
            return false
        }

        action(state == Initialized)
        return true
    }
}

enum class State {
    Created, Initializing, Initialized, Error
}