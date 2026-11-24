package com.cancreative.rawggames.ui.favorite

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cancreative.rawggames.MainActivity
import com.cancreative.rawggames.R
import com.cancreative.rawggames.data.remote.Game
import com.cancreative.rawggames.ui.GameAdapter
import kotlinx.coroutines.launch

class FavoriteFragment : Fragment(R.layout.fragment_favorite) {

    private val vm: FavoriteViewModel by viewModels()
    private lateinit var adapter: GameAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val list = view.findViewById<RecyclerView>(R.id.list)
        val message = view.findViewById<TextView>(R.id.message)

        adapter = GameAdapter(
            onClick = { game -> (activity as? MainActivity)?.openDetail(game.id) },
            onDelete = { game -> vm.remove(game.id) },
        )
        list.layoutManager = LinearLayoutManager(requireContext())
        list.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.favorites.collect { favorites ->
                    adapter.submitList(
                        favorites.map { Game(it.id, it.name, it.backgroundImage, it.rating, it.released, emptyList()) }
                    )
                    message.visibility = if (favorites.isEmpty()) View.VISIBLE else View.GONE
                    message.text = getString(R.string.empty_favorites)
                }
            }
        }
    }
}
