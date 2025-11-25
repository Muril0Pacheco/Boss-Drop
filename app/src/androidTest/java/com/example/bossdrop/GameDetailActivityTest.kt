package com.example.bossdrop.ui.detail

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.bossdrop.R
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GameDetailActivityTest {

    // Removemos a @Rule automática porque queremos controlar o início da Activity

    @Test
    fun verificarElementosPrincipaisDaTela() {
        // 1. Criar um Intent falso com os dados que a Activity exige
        val intent = Intent(ApplicationProvider.getApplicationContext(), GameDetailActivity::class.java).apply {
            putExtra("GAME_ID", "01b9d604-0540-47a2-8153-c3c04299f32d") // ID Falso
            putExtra("GAME_TITLE", "Jogo de Teste") // Título Falso para exibir na tela
        }

        // 2. Lançar a Activity manualmente usando esse Intent
        ActivityScenario.launch<GameDetailActivity>(intent).use {

            // 3. Agora verificamos se os elementos aparecem
            // Banner
            onView(withId(R.id.gameBanner))
                .check(matches(isDisplayed()))

            // Botão Voltar
            onView(withId(R.id.backButton))
                .check(matches(isDisplayed()))

            // Botão Favorito
            onView(withId(R.id.favoriteButton))
                .check(matches(isDisplayed()))

            // Título do jogo (verificamos se o TextView está visível)
            onView(withId(R.id.gameTitle))
                .check(matches(isDisplayed()))
        }
    }

    @Test
    fun testarCliqueNoFavorito() {
        // Mesmo processo: criar intent válido
        val intent = Intent(ApplicationProvider.getApplicationContext(), GameDetailActivity::class.java).apply {
            putExtra("GAME_ID", "123")
            putExtra("GAME_TITLE", "Teste Favorito")
        }

        ActivityScenario.launch<GameDetailActivity>(intent).use {
            // Tenta clicar no botão
            onView(withId(R.id.favoriteButton))
                .perform(click())
                .check(matches(isDisplayed()))
        }
    }
}