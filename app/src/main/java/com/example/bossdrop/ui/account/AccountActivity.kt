package com.example.bossdrop.ui.account

import com.example.bossdrop.R
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bossdrop.databinding.ActivityAccountBinding

class AccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura a Toolbar para ter o botão de voltar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        setupClickListeners()
        setupBottomNavigation()
    }

    // Gerencia o clique no botão de voltar da toolbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish() // Fecha a tela atual
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupClickListeners() {
        binding.changeUsername.setOnClickListener {
            // TODO: Abrir dialog ou nova tela para alterar o nome de usuário
            Toast.makeText(this, "Alterar nome de usuário", Toast.LENGTH_SHORT).show()
        }

        binding.changeEmail.setOnClickListener {
            // TODO: Abrir dialog ou nova tela para alterar o email
            Toast.makeText(this, "Alterar email", Toast.LENGTH_SHORT).show()
        }

        binding.changePassword.setOnClickListener {
            // TODO: Abrir a tela de "Criar nova senha" ou similar
            Toast.makeText(this, "Alterar senha", Toast.LENGTH_SHORT).show()
        }

        binding.saveButton.setOnClickListener {
            // TODO: Implementar a lógica para salvar as alterações
            Toast.makeText(this, "Alterações salvas!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupBottomNavigation() {
        // Deixa o item de "Home" selecionado por padrão, ou pode ser nenhum
        binding.bottomNavigation.selectedItemId = R.id.navigation_home

        // Adicione a lógica de navegação para os outros itens, se necessário
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            // ... (copie a lógica de navegação das outras telas)
            true
        }
    }
}