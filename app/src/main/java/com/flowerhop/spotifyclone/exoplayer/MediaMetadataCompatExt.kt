package com.flowerhop.spotifyclone.exoplayer

import android.support.v4.media.MediaMetadataCompat
import com.flowerhop.spotifyclone.data.entities.Song

fun MediaMetadataCompat.toSong(): Song? {
    return description?.let {
        Song(
            mediaId = it.mediaId ?: "",
            title = it.title.toString(),
            downloadUrl = it.mediaUri.toString(),
            thumbnailUrl = it.iconUri.toString()
        )
    }
}