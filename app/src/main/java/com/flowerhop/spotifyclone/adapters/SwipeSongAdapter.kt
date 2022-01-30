package com.flowerhop.spotifyclone.adapters

import androidx.recyclerview.widget.AsyncListDiffer
import com.flowerhop.spotifyclone.R
import com.flowerhop.spotifyclone.data.entities.Song
import kotlinx.android.synthetic.main.list_item.view.*

class SwipeSongAdapter: BaseAdapter(R.layout.swipe_item) {
    override var differ: AsyncListDiffer<Song> = AsyncListDiffer(this, diffCallback)

    override fun onBindViewHolder(holder: BaseAdapter.SongViewHolder, position: Int) {
        val song = songs[position]
        holder.itemView.apply {
            val text = "${song.title} - Subtitle"
            tvPrimary.text = text

            setOnClickListener {
                onItemClickListener?.invoke(song)
            }
        }
    }
}