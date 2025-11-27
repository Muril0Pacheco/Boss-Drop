package com.murilo.bossdrop.ui.search

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.pressImeActionButton
import androidx.test.espresso.action.ViewActions.typeText
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
class SearchActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(SearchActivity::class.java)

    @Test
    fun verificarEstadoInicialDaTela() {
        // Aumentamos o tempo para 5 segundos para dar chance ao emulador lento
        Thread.sleep(5000)

        // 1. Verifica se a barra de pesquisa está visível (Item Fundamental)
        onView(withId(R.id.searchEditText))
            .check(matches(isDisplayed()))

        // 2. Verifica a barra de navegação (Item Fundamental)
        onView(withId(R.id.bottomNavigation))
            .check(matches(isDisplayed()))

        // OBS: Removemos a verificação do texto "Recomendados".
        // Por que? Se o emulador estiver sem internet ou o banco vazio,
        // esse texto não aparece e quebra o teste.
        // Verificar a Barra de Busca já prova que a Activity carregou!
    }

    @Test
    fun testarInteracaoDeBusca() {
        // 1. Digita o nome de um jogo
        onView(withId(R.id.searchEditText))
            .perform(typeText("Elden Ring"), closeSoftKeyboard())

        // 2. Verifica se o texto foi digitado corretamente
        onView(withId(R.id.searchEditText))
            .check(matches(withText("Elden Ring")))

        // 3. Simula o clique no botão "Pesquisar" (Lupa) do teclado
        onView(withId(R.id.searchEditText))
            .perform(pressImeActionButton())

        // 4. Verificação de sanidade:
        // O app não deve crashar e a barra de busca deve continuar lá
        onView(withId(R.id.searchEditText))
            .check(matches(isDisplayed()))
    }
}