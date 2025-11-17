package com.example.bossdrop.ui.register

import android.content.Intent
import android.os.Bundle
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
    }

    private fun setupClickListeners() {
        // Botão principal de cadastro
        binding.registerButton.setOnClickListener {
            // Mapeando os IDs do XML: usernameEditText, emailEditText, etc.
            val username = binding.usernameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val confirmPassword = binding.repeatPasswordEditText.text.toString()

            esconderTeclado()

            // Adicione validação básica de UI antes de chamar o ViewModel, se necessário.
            viewModel.register(username, email, password, confirmPassword)
        }

        // Botão de navegação para a tela de Login
        binding.loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun setupObservers() {
        // Observa o estado de carregamento
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                // Esconde o botão de cadastro e mostra o ProgressBar
                binding.registerButton.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            } else {
                // Mostra o botão de cadastro e esconde o ProgressBar
                binding.registerButton.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            }

            // Desabilita o botão de redirect também
            binding.loginButton.isEnabled = !isLoading
        }

        // Observa o sucesso do cadastro
        viewModel.registrationSuccess.observe(this) { isSuccess ->
            if (isSuccess == true) {
                Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_LONG).show()
                // Redireciona para a tela de login
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        // Observa mensagens de erro
        viewModel.errorMessage.observe(this) { message ->
            if (!message.isNullOrEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
    }
}