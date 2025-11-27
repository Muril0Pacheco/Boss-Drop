package com.murilo.bossdrop.ui.account

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.murilo.bossdrop.databinding.ActivityAccountBinding
import com.murilo.bossdrop.ui.esconderTeclado
import com.murilo.bossdrop.ui.forgotpassword.ForgotPasswordActivity

class AccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountBinding
    private val viewModel: AccountViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura a Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.title = "Conta"

        setupObservers()
        setupClickListeners()
        setupTextWatchers()
        binding.accountRootLayout.setOnClickListener {
            esconderTeclado()
        }
    }

    override fun onResume() {
        super.onResume()
        // Recarrega os dados caso o usuário volte de outra tela
        viewModel.loadCurrentUserDetails()
    }
    private fun setupTextWatchers() {
        binding.currentPasswordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Limpa o erro assim que o texto mudar
                if (binding.currentPasswordInputLayout.error != null) {
                    binding.currentPasswordInputLayout.error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
    private fun setupObservers() {
        // Observa os detalhes do usuário
        viewModel.currentUser.observe(this) { user ->
            if (user != null) {
                // Preenche os dados
                binding.usernameTextView.text = user.username
                binding.usernameTextView.setTypeface(null, android.graphics.Typeface.BOLD)
                binding.usernameEditText.setText(user.username)
                binding.emailEditText.setText(user.email)
                binding.newPasswordEditText.text?.clear()
                binding.currentPasswordEditText.text?.clear()

                if (user.providerId == "google.com") {
                    // É usuário Google: Esconde tudo, menos o username
                    binding.emailInputLayout.visibility = View.GONE
                    binding.newPasswordInputLayout.visibility = View.GONE
                    binding.currentPasswordInputLayout.visibility = View.GONE
                    binding.tvForgotPassword.visibility = View.GONE
                } else {
                    // É usuário Email/Senha: Mostra tudo
                    binding.emailInputLayout.visibility = View.VISIBLE
                    binding.newPasswordInputLayout.visibility = View.VISIBLE
                    binding.currentPasswordInputLayout.visibility = View.VISIBLE
                    binding.tvForgotPassword.visibility = View.VISIBLE
                }

            } else {
                Toast.makeText(this, "Falha ao carregar dados do usuário.", Toast.LENGTH_LONG).show()
                finish()
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.saveButton.isEnabled = !isLoading
            binding.usernameEditText.isEnabled = !isLoading
            binding.emailEditText.isEnabled = !isLoading
            binding.newPasswordEditText.isEnabled = !isLoading
            binding.currentPasswordEditText.isEnabled = !isLoading
        }

        viewModel.saveResult.observe(this) { resultPair ->
            val result = resultPair?.first
            val message = resultPair?.second

            when (result) {
                SaveResult.SUCCESS -> {
                    Toast.makeText(this, "Alterações salvas com sucesso!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                SaveResult.ERROR_WRONG_PASSWORD -> {
                    binding.currentPasswordInputLayout.error = "Senha atual incorreta ou não fornecida"
                }
                SaveResult.ERROR_INVALID_EMAIL -> {
                    binding.emailInputLayout.error = "O formato do novo e-mail é inválido"
                }
                SaveResult.ERROR_EMAIL_IN_USE -> {
                    binding.emailInputLayout.error = "Este e-mail já está em uso por outra conta"
                }
                SaveResult.ERROR_WEAK_PASSWORD -> {
                    binding.newPasswordInputLayout.error = "A nova senha é muito fraca (mínimo 6 caracteres)"
                }
                SaveResult.ERROR_GENERIC -> {
                    Toast.makeText(this, "Erro: $message", Toast.LENGTH_LONG).show()
                }
                null -> {}
            }
        }

        viewModel.passwordResetEmailSent.observe(this) { sent ->
            if (sent) {
                Toast.makeText(this, "Email de recuperação enviado!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Falha ao enviar email de recuperação.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupClickListeners() {
        binding.saveButton.setOnClickListener {
            // Limpa erros anteriores
            binding.currentPasswordInputLayout.error = null
            binding.emailInputLayout.error = null
            binding.newPasswordInputLayout.error = null
            val username = binding.usernameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val newPass = binding.newPasswordEditText.text.toString()
            val currentPass = binding.currentPasswordEditText.text.toString()

            viewModel.saveChanges(username, email, newPass, currentPass)
        }

        binding.tvForgotPassword.setOnClickListener {
            viewModel.onForgotPasswordClicked()
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }
}