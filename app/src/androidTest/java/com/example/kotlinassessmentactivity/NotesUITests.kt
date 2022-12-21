package com.example.kotlinassessmentactivity

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotesUITests {
    @get:Rule
    val activity = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun general_test() {

        Espresso.onView(withId(R.id.floatingActionButton)).perform(click())

//        Espresso.onView(withId(R.id.recycler_view)).perform(
//            RecyclerViewActions
//                .scrollToPosition<RecyclerView.ViewHolder>(1)
//        )
//
//        Espresso.onView(withText("Bcd"))
//            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}