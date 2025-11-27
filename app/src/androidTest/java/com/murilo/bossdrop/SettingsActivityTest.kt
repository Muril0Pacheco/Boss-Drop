package com.murilo.bossdrop.ui.settings

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.murilo.bossdrop.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(SettingsActivity::class.java)

    @Test
    fun verificarElementosDoMenu() {
        // 1. Verifica se o Header com o nome do usuário está visível
        onView(withId(R.id.tvUsername))
            .check(matches(isDisplayed()))

        // 2. Verifica se as opções do menu estão presentes
        onView(withId(R.id.tvMenuAccount))
            .check(matches(isDisplayed()))
            .check(matches(withText("Conta")))

        onView(withId(R.id.tvMenuNotifications))
            .check(matches(isDisplayed()))
            .check(matches(withText("Notificações")))

        // 3. Verifica se o botão de Sair (Critical Path) está visível
        onView(withId(R.id.tvSignOut))
            .check(matches(isDisplayed()))
            .check(matches(withText("Sair da conta")))
    }

    @Test
    fun testarCliqueEmSair() {
        // Simula o clique no botão de Sair
        onView(withId(R.id.tvSignOut))
            .perform(click())
    }
}