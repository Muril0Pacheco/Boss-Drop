package com.example.bossdrop.ui.notifications

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bossdrop.data.repository.UserRepository
import com.example.bossdrop.databinding.ActivityNotificationsBinding

class NotificationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationsBinding
    private val userRepository = UserRepository() // Instancia o Repo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        loadCurrentState()
    }

    private fun loadCurrentState() {
        binding.pushNotificationSwitch.setOnCheckedChangeListener(null)

        userRepository.getNotificationPreference { isEnabled ->
            binding.pushNotificationSwitch.isChecked = isEnabled

            setupListeners()
        }
    }

    private fun setupListeners() {
        binding.pushNotificationSwitch.setOnCheckedChangeListener { _, isChecked ->

            userRepository.updateNotificationPreference(isChecked) { success ->
                if (success) {
                    val status = if (isChecked) "ativadas" else "desativadas"
                    Toast.makeText(this, "Notificações $status", Toast.LENGTH_SHORT).show()
                } else {
                    binding.pushNotificationSwitch.isChecked = !isChecked
                    Toast.makeText(this, "Erro ao salvar preferência.", Toast.LENGTH_SHORT).show()
                }
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
}