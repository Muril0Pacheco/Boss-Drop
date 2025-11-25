package com.example.bossdrop.ui.login

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.bossdrop.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    // Essa regra diz: "Abra a LoginActivity antes de cada teste"
    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun verificarSeComponentesEstaoVisiveis() {
        // Verifica se o campo de email está na tela
        onView(withId(R.id.emailEditText)).check(matches(isDisplayed()))

        // Verifica se o botão de login está na tela
        onView(withId(R.id.loginButton)).check(matches(isDisplayed()))

        // Verifica se a logo está visível
        onView(withId(R.id.logoImageView)).check(matches(isDisplayed()))
    }

    @Test
    fun tentarLoginComDadosInvalidos() {
        // 1. Digita um email qualquer
        onView(withId(R.id.emailEditText))
            .perform(typeText("email@teste.com"), closeSoftKeyboard())

        // 2. Digita uma senha qualquer
        onView(withId(R.id.passwordEditText))
            .perform(typeText("123456"), closeSoftKeyboard())

        // 3. Clica no botão Entrar
        onView(withId(R.id.loginButton)).perform(click())

        // 4. Verificação:
        // Como não estamos mockando a API aqui (é um teste real), se o login falhar,
        // o usuário deve CONTINUAR na tela de login (o botão login ainda deve estar visível).
        onView(withId(R.id.loginButton)).check(matches(isDisplayed()))
    }
}