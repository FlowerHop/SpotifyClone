package com.flowerhop.spotifyclone.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flowerhop.spotifyclone.R
import com.flowerhop.spotifyclone.State
import com.flowerhop.spotifyclone.adapters.SongAdapter
import com.flowerhop.spotifyclone.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment: Fragment(R.layout.frgment_home) {

    private lateinit var mainViewModel: MainViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var allSongsProgressBar: ProgressBar

    @Inject
    lateinit var songAdapter: SongAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.rvAllSongs)
        allSongsProgressBar = view.findViewById(R.id.allSongsProgressBar)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        setupRecyclerView()
        subscribeToObservers()

        songAdapter.setItemClickListener {
            mainViewModel.playOrToggleSong(it)
        }
    }

    private fun setupRecyclerView() = recyclerView.apply {
        adapter = songAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

    private fun subscribeToObservers() {
        mainViewModel.mediaItems.observe(viewLifecycleOwner) {
            when (it.state) {
                State.SUCCESS -> {
                    allSongsProgressBar.isVisible = false
                    it.data?.let { songs ->
                        songAdapter.songs = songs
                    }
                }
                State.ERROR -> Unit
                State.LOADING -> allSongsProgressBar.isVisible = true
            }
        }
    }
}