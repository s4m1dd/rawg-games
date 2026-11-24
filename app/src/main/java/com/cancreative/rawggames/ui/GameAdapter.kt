package com.cancreative.rawggames.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cancreative.rawggames.R
import com.cancreative.rawggames.data.remote.Game
import com.cancreative.rawggames.util.ImageLoader

class GameAdapter(
    private val onClick: (Game) -> Unit,
    private val onDelete: ((Game) -> Unit)? = null,
) : ListAdapter<Game, GameAdapter.Holder>(Diff) {

    private object Diff : DiffUtil.ItemCallback<Game>() {
        override fun areItemsTheSame(a: Game, b: Game) = a.id == b.id
        override fun areContentsTheSame(a: Game, b: Game) = a == b
    }

    inner class Holder(view: View) : RecyclerView.ViewHolder(view) {
        private val cover: ImageView = view.findViewById(R.id.cover)
        private val name: TextView = view.findViewById(R.id.name)
        private val meta: TextView = view.findViewById(R.id.meta)
        private val delete: ImageButton = view.findViewById(R.id.delete)

        fun bind(game: Game) {
            name.text = game.name
            meta.text = itemView.context.getString(R.string.meta, game.rating, game.released.orEmpty())
            ImageLoader.load(game.backgroundImage, cover)
            itemView.setOnClickListener { onClick(game) }
            delete.visibility = if (onDelete == null) View.GONE else View.VISIBLE
            delete.setOnClickListener { onDelete?.invoke(game) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_game, parent, false))

    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(getItem(position))
}
