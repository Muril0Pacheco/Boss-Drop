package com.example.bossdrop.ui.register

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.bossdrop.databinding.ActivityRegisterBinding
import com.example.bossdrop.ui.esconderTeclado

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupClickListeners()

        binding.registerRootLayout.setOnClickListener {
            esconderTeclado()
        }
    }

    private fun setupObservers() {
        viewModel.statusMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        viewModel.isLoading.observe(this) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }
    }

    private fun setupClickListeners() {
        binding.editProfileImageButton.setOnClickListener {
            viewModel.chooseProfilePhoto()
        }

        binding.registerButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val repeatPassword = binding.repeatPasswordEditText.text.toString()
            viewModel.register(username, email, password, repeatPassword)
        }

        binding.loginButton.setOnClickListener {
            finish()
        }
    }
}
