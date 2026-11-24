package com.cancreative.rawggames

import android.app.Application
import com.cancreative.rawggames.data.GameRepository
import com.cancreative.rawggames.data.local.AppDatabase
import com.cancreative.rawggames.data.remote.RawgClient

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // Kept swappable so UI tests can install a fake repository before launching the activity.
        if (ServiceLocator.repository == null) {
            ServiceLocator.repository = GameRepository(
                RawgClient(BuildConfig.RAWG_API_KEY),
                AppDatabase.get(this).favoriteDao(),
            )
        }
    }
}

object ServiceLocator {
    var repository: GameRepository? = null
    // Set by UI tests; the injected fake backend makes the missing-API-key guard moot.
    var isTestBackend = false
}
