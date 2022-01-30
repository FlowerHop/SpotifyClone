package com.flowerhop.spotifyclone.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flowerhop.spotifyclone.data.Constants.UPDATE_PLAYER_POSITION_INTERVAL
import com.flowerhop.spotifyclone.exoplayer.MusicService
import com.flowerhop.spotifyclone.exoplayer.MusicServiceConnection
import com.flowerhop.spotifyclone.exoplayer.currentPlaybackPosition
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongViewModel @Inject constructor(
    musicServiceConnection: MusicServiceConnection
): ViewModel() {

    private val playbackStateCompat = musicServiceConnection.playbackState
    private val _currSongDuration = MutableLiveData<Long>()
    val currSongDuration: LiveData<Long> = _currSongDuration

    private val _currPlayerPosition = MutableLiveData<Long>()
    val currPlayerPosition: LiveData<Long> = _currPlayerPosition

    init {
        updateCurrPlayerPosition()
    }

    private fun updateCurrPlayerPosition() {
        viewModelScope.launch {
            while (true) {
                val pos = playbackStateCompat.value?.currentPlaybackPosition
                if (currPlayerPosition.value != pos) {
                    _currPlayerPosition.postValue(pos!!)
                    _currSongDuration.postValue(MusicService.currSongDuration)
                }

                delay(UPDATE_PLAYER_POSITION_INTERVAL)
            }
        }
    }
}