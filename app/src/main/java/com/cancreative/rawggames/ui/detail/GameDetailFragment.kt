package com.cancreative.rawggames.ui.detail

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.cancreative.rawggames.R
import com.cancreative.rawggames.util.ImageLoader
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class GameDetailFragment : Fragment(R.layout.fragment_game_detail) {

    private val vm: GameDetailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val gameId = requireArguments().getInt(ARG_ID)
        val cover = view.findViewById<ImageView>(R.id.cover)
        val name = view.findViewById<TextView>(R.id.name)
        val meta = view.findViewById<TextView>(R.id.meta)
        val genres = view.findViewById<TextView>(R.id.genres)
        val description = view.findViewById<TextView>(R.id.description)
        val toggle = view.findViewById<MaterialButton>(R.id.toggle_favorite)
        val progress = view.findViewById<ProgressBar>(R.id.progress)
        val message = view.findViewById<TextView>(R.id.message)

        toggle.setOnClickListener { vm.toggleFavorite() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.state.collect { state ->
                    progress.visibility = if (state.loading) View.VISIBLE else View.GONE
                    message.visibility = if (state.error != null) View.VISIBLE else View.GONE
                    message.text = state.error

                    val detail = state.detail ?: return@collect
                    name.text = detail.name
                    meta.text = getString(R.string.meta, detail.rating, detail.released.orEmpty())
                    genres.text = detail.genres.joinToString(", ")
                    description.text = detail.description
                    ImageLoader.load(detail.backgroundImage, cover)
                    toggle.visibility = View.VISIBLE
                    toggle.text = getString(if (state.isFavorite) R.string.unfavorite else R.string.favorite)
                }
            }
        }

        vm.load(gameId)
    }

    companion object {
        private const val ARG_ID = "id"

        fun newInstance(gameId: Int) = GameDetailFragment().apply {
            arguments = Bundle().apply { putInt(ARG_ID, gameId) }
        }
    }
}
