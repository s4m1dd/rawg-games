package com.cancreative.rawggames.ui.list

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cancreative.rawggames.BuildConfig
import com.cancreative.rawggames.MainActivity
import com.cancreative.rawggames.R
import com.cancreative.rawggames.ui.GameAdapter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameListFragment : Fragment(R.layout.fragment_game_list) {

    private val vm: GameListViewModel by viewModels()
    private val adapter = GameAdapter(onClick = { game -> (activity as? MainActivity)?.openDetail(game.id) })
    private var searchJob: Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val search = view.findViewById<EditText>(R.id.search)
        val list = view.findViewById<RecyclerView>(R.id.list)
        val progress = view.findViewById<ProgressBar>(R.id.progress)
        val message = view.findViewById<TextView>(R.id.message)

        list.layoutManager = LinearLayoutManager(requireContext())
        list.adapter = adapter

        if (BuildConfig.RAWG_API_KEY.isBlank()) {
            progress.visibility = View.GONE
            message.text = getString(R.string.no_api_key)
            message.visibility = View.VISIBLE
            return
        }

        list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 && !rv.canScrollVertically(1)) vm.loadMore()
            }
        })
        message.setOnClickListener { vm.retry() }

        search.doAfterTextChanged { text ->
            searchJob?.cancel()
            searchJob = viewLifecycleOwner.lifecycleScope.launch {
                delay(350)
                vm.search(text?.toString())
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.state.collect { state ->
                    adapter.submitList(state.games)
                    progress.visibility = if (state.loading) View.VISIBLE else View.GONE
                    message.visibility = if (state.error != null || state.games.isEmpty() && !state.loading) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                    message.text = state.error ?: getString(R.string.empty_games)
                }
            }
        }

        vm.start()
    }
}
