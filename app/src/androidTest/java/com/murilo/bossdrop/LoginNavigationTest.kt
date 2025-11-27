package com.murilo.bossdrop.ui.login

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.murilo.bossdrop.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginNavigationTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun loginComSucesso_NavegaParaHome() {
        onView(withId(R.id.emailEditText))
            .perform(typeText("google-test@bossdrop.com"), closeSoftKeyboard())

        onView(withId(R.id.passwordEditText))
            .perform(typeText("Teste123"), closeSoftKeyboard())

        onView(withId(R.id.loginButton)).perform(click())


        Thread.sleep(4000)


        onView(withId(R.id.activePromotionsLabel))
            .check(matches(isDisplayed()))
    }
}