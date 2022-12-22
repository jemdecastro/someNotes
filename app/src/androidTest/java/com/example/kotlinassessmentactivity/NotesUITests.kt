package com.example.kotlinassessmentactivity

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matcher
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class NotesUITests {
    @get:Rule
    val activity = ActivityScenarioRule(MainActivity::class.java)

    private val addButton = R.id.floating_add_button
    private val deleteButton = R.id.floating_delete_button
    private val recyclerView = R.id.recycler_view
    private val noteTitle = R.id.note_title
    private val noteText = R.id.note_text

    private var testTimestamp = 0L
    private var testName = ""

    @Test
    fun test_1_add() {
        testName = "Add"

        // Move to add/edit fragment
        onView(withId(addButton)).perform(click())

        // Test input
        testTimestamp = System.currentTimeMillis()
        onView(withId(noteTitle)).perform(click(), typeText("$testName Title $testTimestamp"))
        onView(withId(noteText)).perform(click(), typeText("$testName Note $testTimestamp"))

        // Press back to hide softKeyboard
        onView(isRoot()).perform(pressBack())
        // Press back to go in recyclerView
        onView(isRoot()).perform(pressBack())

        // Check if added on first item
        onView(withId(recyclerView)).perform(
            scrollToPosition<RecyclerView.ViewHolder>(0)
        )
        onView(withText("$testName Title $testTimestamp")).check(matches(isDisplayed()))
        onView(withText("$testName Note $testTimestamp")).check(matches(isDisplayed()))
    }

    @Test
    fun test_2_update() {
        testName = "Update"

        // Click an item to move to add/edit fragment
        onView(withId(recyclerView))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, clickItemWithId(R.id.note_title) ));

        // Test input - Append if existing
        testTimestamp = System.currentTimeMillis()
        onView(withId(noteTitle)).perform(click(), clearText(), typeText("$testName Title $testTimestamp"))
        onView(withId(noteText)).perform(click(), clearText(), typeText("$testName Note $testTimestamp"))

        // Press back to hide softKeyboard
        onView(isRoot()).perform(pressBack())
        // Press back to go in recyclerView
        onView(isRoot()).perform(pressBack())

        // Check if note is updated
        onView(withId(recyclerView)).perform(
            scrollToPosition<RecyclerView.ViewHolder>(0)
        )
        onView(withText("$testName Title $testTimestamp")).check(matches(isDisplayed()))
        onView(withText("$testName Note $testTimestamp")).check(matches(isDisplayed()))
    }

    @Test
    fun test_3_delete() {
        testName = "Delete"

        // Click an item to move to add/edit fragment
        onView(withId(recyclerView))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, clickItemWithId(R.id.note_title) ));

        // Test input - Append if existing
        onView(withId(noteTitle)).perform(click())
        onView(withId(noteText)).perform(click())

        // Press back to hide softKeyboard
        onView(isRoot()).perform(pressBack())
        // Click delete button
        onView(withId(deleteButton)).perform(click())
        // Click YES button on dialog
        onView(withText("YES")).perform(click())

        // Check if the note is delete and 'No Notes yet.' is displayed
        onView(withText("No Notes yet.")).check(matches(isDisplayed()))
    }
}

fun clickItemWithId(id: Int): ViewAction {
    return object : ViewAction {
        override fun getConstraints(): Matcher<View>? {
            return null
        }

        override fun getDescription(): String {
            return "Click on a child view with specified id."
        }

        override fun perform(uiController: UiController, view: View) {
            val v = view.findViewById<View>(id) as View
            v.performClick()
        }
    }
}