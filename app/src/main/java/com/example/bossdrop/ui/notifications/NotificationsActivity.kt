package com.example.bossdrop.ui.notifications

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bossdrop.databinding.ActivityNotificationsBinding

class NotificationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura a Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        setupListeners()
    }

    // Gerencia o clique no botão de voltar da toolbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish() // Fecha a activity e volta para a anterior
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupListeners() {
        // Listener para o switch de notificação push
        binding.pushNotificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            val status = if (isChecked) "ativadas" else "desativadas"
            Toast.makeText(this, "Notificações push $status", Toast.LENGTH_SHORT).show()
            // TODO: Salvar esta preferência do usuário (ex: em SharedPreferences)
        }

        // Listener para o switch de notificação por email
        binding.emailNotificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            val status = if (isChecked) "ativadas" else "desativadas"
            Toast.makeText(this, "Notificações por email $status", Toast.LENGTH_SHORT).show()
            // TODO: Salvar esta preferência do usuário
        }
    }

}