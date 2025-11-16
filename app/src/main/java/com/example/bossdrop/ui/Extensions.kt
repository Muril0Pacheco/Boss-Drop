package com.example.bossdrop.ui

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

/**
 * Função de extensão para a classe Activity.
 * Esconde o teclado e limpa o foco.
 */
fun Activity.esconderTeclado() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val currentFocusedView = this.currentFocus
    currentFocusedView?.let { view ->
        imm.hideSoftInputFromWindow(view.windowToken, 0)
        view.clearFocus()
    }
}

/**
 * Função de extensão para a classe Fragment.
 * Esconde o teclado e limpa o foco.
 */
fun Fragment.esconderTeclado() {
    // Um fragment pode não estar ligado a uma activity, então checamos
    val activity = this.activity ?: return

    val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    val currentFocusedView = activity.currentFocus
    currentFocusedView?.let { view ->
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
        view.clearFocus()
    }
}