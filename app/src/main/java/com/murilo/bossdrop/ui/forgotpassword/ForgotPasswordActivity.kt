package com.murilo.bossdrop.ui.forgotpassword

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.murilo.bossdrop.databinding.ActivityForgotPasswordBinding
import com.murilo.bossdrop.ui.esconderTeclado

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private val viewModel: ForgotPasswordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportActionBar?.setDisplayShowTitleEnabled(true)

        setupObservers()
        setupClickListeners()

        binding.forgotRootLayout.setOnClickListener {
            esconderTeclado()
        }
    }

    private fun setupObservers() {
        viewModel.statusMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_LONG).show() // Mudei para LONG

            if (message.startsWith("Link enviado")) {
                finish() // Fecha a tela após sucesso
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.sendLinkButton.isEnabled = false
                binding.emailEditText.isEnabled = false
            } else {
                binding.progressBar.visibility = View.GONE
                binding.sendLinkButton.isEnabled = true
                binding.emailEditText.isEnabled = true
            }
        }
    }

    private fun setupClickListeners() {
        binding.sendLinkButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim() // Adiciona trim()
            viewModel.sendResetLink(email)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}