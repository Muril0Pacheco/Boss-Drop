package com.example.bossdrop.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.example.bossdrop.ui.register.RegisterActivity
import com.example.bossdrop.ui.forgotpassword.ForgotPasswordActivity
import com.example.bossdrop.R
import com.example.bossdrop.databinding.ActivityLoginBinding
import com.example.bossdrop.ui.esconderTeclado
import com.example.bossdrop.ui.home.HomeActivity


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    private lateinit var oneTapClient: SignInClient

    companion object {
        private const val REQUEST_CODE_GOOGLE_SIGN_IN = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        oneTapClient = Identity.getSignInClient(this)

        setupObservers()
        setupClickListeners()
        setupTextWatchers()
    }

    private fun setupTextWatchers() {
        val errorCleaner = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Limpa o erro de ambos os campos
                binding.emailInputLayout.error = null
                binding.passwordInputLayout.error = null
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        binding.emailEditText.addTextChangedListener(errorCleaner)
        binding.passwordEditText.addTextChangedListener(errorCleaner)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_GOOGLE_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                handleGoogleSignInResult(data)
            } else {
                viewModel.onGoogleSignInFailed("Login com Google cancelado ou falhou.")
            }
        }
    }
    private fun handleGoogleSignInResult(data: Intent?) {
        try {
            val credential = oneTapClient.getSignInCredentialFromIntent(data)
            val idToken = credential.googleIdToken
            when {
                idToken != null -> {
                    viewModel.firebaseAuthWithGoogle(idToken)
                }
                else -> {
                    viewModel.onGoogleSignInFailed("Falha no login com Google: Token não encontrado")
                }
            }
        } catch (e: Exception) {
            viewModel.onGoogleSignInFailed("Falha no login com Google: ${e.message}")
        }
    }

    private fun setupObservers() {
        viewModel.statusMessage.observe(this) { message ->
            if (message.startsWith("Falha no login")) {
                binding.passwordInputLayout.error = "E-mail ou senha incorretos."
            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.isLoading.observe(this) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE

            binding.loginButton.isEnabled = !loading
            binding.googleSignInButton.isEnabled = !loading
            binding.registerButton.isEnabled = !loading
            binding.forgotPasswordTextView.isEnabled = !loading

            if (loading) {
                esconderTeclado()
            }
        }

        viewModel.navigateToHome.observe(this) { shouldNavigate ->
            if (shouldNavigate) {
                navigateToHome()
                viewModel.onHomeNavigated()
            }
        }
    }

    private fun setupClickListeners() {
        binding.mainLayout.setOnClickListener {
            esconderTeclado()
        }

        binding.loginButton.setOnClickListener {
            // Limpa erros antigos antes de validar
            binding.emailInputLayout.error = null
            binding.passwordInputLayout.error = null

            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (validateInputs(email, password)) {
                esconderTeclado()
                viewModel.login(email, password)
            }
        }

        binding.registerButton.setOnClickListener {
            viewModel.goToRegister()
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.forgotPasswordTextView.setOnClickListener {
            viewModel.forgotPassword()
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        binding.googleSignInButton.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            binding.emailInputLayout.error = "Por favor, preencha o e-mail"
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailInputLayout.error = "Por favor, insira um email válido"
            return false
        }
        if (password.isEmpty()) {
            binding.passwordInputLayout.error = "Por favor, preencha a senha"
            return false
        }

        return true
    }

    private fun signInWithGoogle() {
        val signInRequest = com.google.android.gms.auth.api.identity.BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()

        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                try {
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender,
                        REQUEST_CODE_GOOGLE_SIGN_IN,
                        null, 0, 0, 0, null
                    )
                } catch (e: Exception) {
                    viewModel.onGoogleSignInFailed("Erro ao iniciar login: ${e.message}")
                }
            }
            .addOnFailureListener { exception ->
                viewModel.onGoogleSignInFailed("Falha ao iniciar login com Google: ${exception.message}")
            }
    }
    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}