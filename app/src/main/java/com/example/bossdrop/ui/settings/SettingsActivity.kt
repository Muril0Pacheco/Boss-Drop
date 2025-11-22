package com.example.bossdrop.ui.settings

import android.content.Intent
import android.os.Bundle
import android.graphics.Typeface
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.bossdrop.databinding.ActivitySettingsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

import com.example.bossdrop.ui.privacy.PrivacyActivity
import com.example.bossdrop.ui.account.AccountActivity
import com.example.bossdrop.ui.help.HelpActivity
import com.example.bossdrop.ui.notifications.NotificationsActivity
import com.example.bossdrop.ui.login.LoginActivity
import com.example.bossdrop.R

import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: SettingsViewModel by viewModels() // Pega o ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupClickListeners()
        setupObservers()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            // Fecha a activity e volta para a tela anterior (Home)
            finish()
        }
    }

    // 1. Reporta os cliques para o ViewModel
    private fun setupClickListeners() {
        binding.tvMenuAccount.setOnClickListener { viewModel.onAccountClicked() }
        binding.tvMenuNotifications.setOnClickListener { viewModel.onNotificationsClicked() }
        binding.tvMenuPrivacy.setOnClickListener { viewModel.onPrivacyClicked() }
        binding.tvMenuHelp.setOnClickListener { viewModel.onHelpClicked() }
        binding.tvSignOut.setOnClickListener { viewModel.onSignOutClicked() }
    }

    // 2. Observa mudanças do ViewModel e atualiza a UI
    private fun setupObservers() {
        // Observa o username
        viewModel.username.observe(this) { username ->
            binding.tvUsername.text = username
            binding.tvUsername.setTypeface(null, Typeface.BOLD)
        }

        // Observa eventos de navegação
        viewModel.navigationEvent.observe(this) { navigationEvent ->
            // Se o evento não for nulo, navega
            navigationEvent?.let {
                val destination = when (it) {
                    // Substitua .java pela sua Activity real
                    SettingsNavigation.TO_ACCOUNT -> AccountActivity::class.java
                    SettingsNavigation.TO_NOTIFICATIONS -> NotificationsActivity::class.java
                    SettingsNavigation.TO_PRIVACY -> PrivacyActivity::class.java
                    SettingsNavigation.TO_HELP -> HelpActivity::class.java
                }
                startActivity(Intent(this, destination))
            }
        }

        // Observa o evento de Sair da Conta
        viewModel.signOutEvent.observe(this) { shouldSignOut ->
            if (shouldSignOut == true) {
                showSignOutConfirmationDialog()
            }
        }
    }

    private fun showSignOutConfirmationDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Sair da conta")
            .setMessage("Tem certeza que deseja sair?")
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Sair") { _, _ ->

                // 1. Desloga do Firebase Auth
                FirebaseAuth.getInstance().signOut()

                // 2. Desloga do Google Sign-In (Importante para não logar automático)
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id)) // Use o seu client_id
                    .requestEmail()
                    .build()

                GoogleSignIn.getClient(this, gso).signOut().addOnCompleteListener {
                    // 3. Navega para a tela de Login após deslogar
                    val intent = Intent(this, LoginActivity::class.java)
                    // Limpa todas as telas anteriores (Home, Settings, etc.)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finishAffinity()
                }
            }
            .show()
    }
}