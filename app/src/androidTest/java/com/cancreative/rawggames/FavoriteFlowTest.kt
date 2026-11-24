package com.cancreative.rawggames

import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cancreative.rawggames.data.GameRepository
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

// Fake backend: no network, no real DB.
@RunWith(AndroidJUnit4::class)
class FavoriteFlowTest {

    @Before
    fun installFakeBackend() {
        ServiceLocator.repository = GameRepository(FakeApi(), FakeDao())
        ServiceLocator.isTestBackend = true
    }

    @Test
    fun addFavoriteThenDeleteIt() {
        ActivityScenario.launch(MainActivity::class.java).use {
            await(withText("Hades"))
            onView(withText("Hades")).perform(click())

            await(withId(R.id.toggle_favorite))
            onView(withId(R.id.toggle_favorite)).perform(click())
            await(withContentDescription(R.string.unfavorite))

            pressBack()
            onView(withId(R.id.tab_favorites)).perform(click())
            await(withText("Hades"))

            onView(withContentDescription(R.string.delete)).perform(click())
            await(withText(R.string.empty_favorites))
        }
    }

    private fun await(matcher: Matcher<View>, timeoutMs: Long = 5_000) {
        val end = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < end) {
            if (runCatching { onView(matcher).check(matches(isDisplayed())) }.isSuccess) return
            Thread.sleep(100)
        }
        onView(matcher).check(matches(isDisplayed()))
    }
}
