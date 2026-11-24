package com.cancreative.rawggames.ui.detail

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.cancreative.rawggames.R
import com.cancreative.rawggames.util.ImageLoader
import com.cancreative.rawggames.util.formatReleaseDate
import kotlinx.coroutines.launch

class GameDetailFragment : Fragment(R.layout.fragment_game_detail) {

    private val vm: GameDetailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val gameId = requireArguments().getInt(ARG_ID)
        val cover = view.findViewById<ImageView>(R.id.cover)
        val name = view.findViewById<TextView>(R.id.name)
        val developer = view.findViewById<TextView>(R.id.developer)
        val rating = view.findViewById<TextView>(R.id.rating)
        val release = view.findViewById<TextView>(R.id.release)
        val genres = view.findViewById<TextView>(R.id.genres)
        val played = view.findViewById<TextView>(R.id.played)
        val description = view.findViewById<TextView>(R.id.description)
        val toggle = view.findViewById<ImageButton>(R.id.toggle_favorite)
        val back = view.findViewById<ImageButton>(R.id.back)
        val progress = view.findViewById<ProgressBar>(R.id.progress)
        val message = view.findViewById<TextView>(R.id.message)

        back.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        toggle.setOnClickListener { vm.toggleFavorite() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.state.collect { state ->
                    progress.isVisible = state.loading
                    message.isVisible = state.error != null
                    message.text = state.error

                    val detail = state.detail ?: return@collect
                    name.text = detail.name
                    rating.text = getString(R.string.rating, detail.rating)
                    release.text = getString(R.string.release_date, formatReleaseDate(detail.released))
                    genres.text = detail.genres.joinToString(", ")
                    developer.text = detail.developers.joinToString(", ")
                    developer.isVisible = detail.developers.isNotEmpty()
                    played.text = getString(R.string.played, detail.added.toString())
                    description.text = detail.description
                    ImageLoader.load(detail.backgroundImage, cover)
                    toggle.isVisible = true
                    toggle.setImageResource(
                        if (state.isFavorite) R.drawable.ic_heart_filled else R.drawable.ic_heart
                    )
                    toggle.contentDescription =
                        getString(if (state.isFavorite) R.string.unfavorite else R.string.favorite)
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
