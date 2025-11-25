package com.example.bossdrop.ui.favorites

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.bossdrop.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoritesActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(FavoritesActivity::class.java)

    @Test
    fun verificarSeComponentesEstaoVisiveis() {
        // CORREÇÃO: Em vez de buscar qualquer texto "Favoritos" (que dá erro porque tem na barra de navegação),
        // Buscamos pelo ID do título e verificamos se ele está visível e tem o texto certo.
        onView(withId(R.id.favoritesTitleTextView))
            .check(matches(isDisplayed()))
            .check(matches(withText("Favoritos")))

        // 2. Verifica se a Lista (RecyclerView) está na tela
        onView(withId(R.id.favoritesRecyclerView))
            .check(matches(isDisplayed()))

        // 3. Verifica se o Botão Flutuante (+) está visível
        onView(withId(R.id.fabAddGame))
            .check(matches(isDisplayed()))

        // 4. Verifica se a barra de navegação inferior está visível
        onView(withId(R.id.bottomNavigation))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testarCliqueNoBotaoAdicionar() {
        // Tenta clicar no botão de Adicionar Jogo (+)
        onView(withId(R.id.fabAddGame))
            .perform(click())
    }
}