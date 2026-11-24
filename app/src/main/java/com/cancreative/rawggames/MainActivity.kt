package com.cancreative.rawggames

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.cancreative.rawggames.ui.favorite.FavoriteFragment
import com.cancreative.rawggames.ui.list.GameListFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private val bottomNav by lazy { findViewById<BottomNavigationView>(R.id.bottom_nav) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNav.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.tab_favorites -> FavoriteFragment()
                else -> GameListFragment()
            }
            show(fragment)
            true
        }
        if (savedInstanceState == null) {
            bottomNav.selectedItemId = R.id.tab_games
        }
    }

    fun openDetail(gameId: Int) {
        showDetail(gameId)
    }

    private fun showDetail(gameId: Int) {
        supportFragmentManager.commit {
            replace(
                R.id.container,
                com.cancreative.rawggames.ui.detail.GameDetailFragment.newInstance(gameId),
            )
            addToBackStack(null)
        }
    }

    private fun show(fragment: Fragment) {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        }
        supportFragmentManager.commit { replace(R.id.container, fragment) }
    }
}
