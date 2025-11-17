package com.example.bossdrop.ui.register

import android.content.Intent
import android.os.Bundle
import android.text.Editable // ◀️ --- IMPORT NECESSÁRIO
import android.text.TextWatcher // ◀️ --- IMPORT NECESSÁRIO
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.bossdrop.databinding.ActivityRegisterBinding
import com.example.bossdrop.ui.login.LoginActivity
import com.example.bossdrop.ui.esconderTeclado

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        setupObservers()
        setupTextWatchers()
    }

    private fun setupTextWatchers() {
        // Um listener que limpa todos os erros
        val errorCleaner = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.usernameInputLayout.error = null
                binding.emailInputLayout.error = null
                binding.passwordInputLayout.error = null
                binding.repeatPasswordInputLayout.error = null
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        binding.usernameEditText.addTextChangedListener(errorCleaner)
        binding.emailEditText.addTextChangedListener(errorCleaner)
        binding.passwordEditText.addTextChangedListener(errorCleaner)
        binding.repeatPasswordEditText.addTextChangedListener(errorCleaner)
    }

    private fun setupClickListeners() {
        binding.registerButton.setOnClickListener {
            binding.emailInputLayout.error = null
            binding.passwordInputLayout.error = null

            val username = binding.usernameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString()
            val confirmPassword = binding.repeatPasswordEditText.text.toString()

            esconderTeclado()
            viewModel.register(username, email, password, confirmPassword)
        }

        binding.loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.registerButton.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.registerButton.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            }
            binding.loginButton.isEnabled = !isLoading
        }

        viewModel.registrationResult.observe(this) { (resultType, message) ->
            when (resultType) {
                RegisterResultType.SUCCESS -> {
                    Toast.makeText(this, "Cadastro realizado com sucesso! Faça login.", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                RegisterResultType.ERROR_WEAK_PASSWORD -> {
                    // Mostra o erro no campo de senha
                    binding.passwordInputLayout.error = message ?: "Senha inválida"
                }
                RegisterResultType.ERROR_EMAIL_IN_USE -> {
                    // Mostra o erro no campo de email
                    binding.emailInputLayout.error = "Este e-mail já está em uso"
                }
                RegisterResultType.ERROR_GENERIC -> {
                    Toast.makeText(this, message ?: "Erro desconhecido", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}