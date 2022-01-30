package com.flowerhop.spotifyclone.adapters

import androidx.recyclerview.widget.AsyncListDiffer
import com.bumptech.glide.RequestManager
import com.flowerhop.spotifyclone.R
import com.flowerhop.spotifyclone.data.entities.Song
import kotlinx.android.synthetic.main.list_item.view.*
import javax.inject.Inject

class SongAdapter @Inject constructor(
    private val glide: RequestManager
): BaseAdapter(R.layout.list_item) {
    override var differ: AsyncListDiffer<Song> = AsyncListDiffer(this, diffCallback)

    override fun onBindViewHolder(holder: BaseAdapter.SongViewHolder, position: Int) {
        val song = songs[position]
        holder.itemView.apply {
            tvPrimary.text = song.title
            tvSecondary.text = song.title
            glide.load(song.thumbnailUrl).into(ivItemImage)

            setOnClickListener {
                onItemClickListener?.invoke(song)
            }
        }
    }
}